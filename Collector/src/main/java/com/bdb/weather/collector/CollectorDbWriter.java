/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.weather.collector;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.collector.socket.SocketReaderThread;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.Storm;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.DopplerRadarTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.SensorStationStatusTable;
import com.bdb.weather.common.db.SensorStationTable;
import com.bdb.weather.common.db.SensorTable;
import com.bdb.weather.common.db.StormDopplerRadarTable;
import com.bdb.weather.common.db.StormTable;
import com.bdb.weather.common.db.WeatherStationParametersTable;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.messages.WsParametersMessage;

/**
 * This class performs all database write operators for the WeatherSense collector. It runs in its own thread and implements
 * a command pattern to allow other threads to store data in the database.
 * 
 * @author Bruce
 */
public class CollectorDbWriter implements WeatherDataWriter, Runnable {
    private static final int MAX_MISSING_DATA_ERRORS_ALLOWED = 3;
    private static final int CONNECTION_RETRY_INTERVAL_MILLIS = 10000;
    private static final int SUMMARIZER_DELAY = 10000;
    private final DBConnection connection;
    private final ExecutorService executor;
    private final Thread dbConnectionThread;
    private final HistoryTable historyTable;
    private final SensorTable sensorTable;
    private final SensorStationTable sensorStationTable;
    private final SensorStationStatusTable sensorStationStatusTable;
    private final DopplerRadarTable dopplerTable;
    private final StormDopplerRadarTable stormDopplerTable;
    private final WeatherStationTable wsTable;
    private final WeatherStationParametersTable wsParamsTable;
    private final StormTable stormTable;
    private final DailySummaryTable dailySummaryTable;
    private LocalDateTime lastSavedHistoricalRecordTime = null;
    private LocalDateTime lastBadHistoricalTime = null;
    private int missingDataCount = 0;
    private final Summarizer summarizer;
    private SocketReaderThread socketReader;
    private final CurrentWeatherPublisher cwPublisher;
    private final List<SummarizeTimerTask> timerTasks = new ArrayList<>();
    private final Timer timer = new Timer();
    private final Statistics statistics;
    private LocalDateTime stormStartTime = null;
    private static final Logger logger = Logger.getLogger(CollectorDbWriter.class.getName());

    /**
     * Constructor.
     * 
     * @param dbUrl The URL of the database
     * @param dbUser The database user name
     * @param dbPassword The password for the database user
     * 
     * @throws UnknownHostException The database server host could not be found
     * @throws IOException The current weather publisher should not establish its multi-cast UDP channel
     */
    public CollectorDbWriter(String dbUrl, String dbUser, String dbPassword) throws IOException {
        connection = new DBConnection(dbUrl, dbUser, dbPassword);
        statistics = new Statistics();
        executor = Executors.newSingleThreadExecutor();
        dbConnectionThread = new Thread(connection);

        historyTable = new HistoryTable(connection);
        sensorTable = new SensorTable(connection);
        sensorStationTable = new SensorStationTable(connection);
        sensorStationStatusTable = new SensorStationStatusTable(connection);
        dopplerTable = new DopplerRadarTable(connection);
        stormDopplerTable = new StormDopplerRadarTable(connection);
        wsTable = new WeatherStationTable(connection);
        wsParamsTable = new WeatherStationParametersTable(connection);
        dailySummaryTable = new DailySummaryTable(connection);
        stormTable = new StormTable(connection);

        summarizer = new Summarizer(connection, this);
        cwPublisher = new CurrentWeatherPublisher();
    }

    /**
     * Initialize the object.
     * 
     * @param socketReader The socket reader that will read data from the weather console driver
     * @param runMissingDataFinder Whether to run the missing data finder
     * @throws SQLException  Could not set the automatic commit parameter on the database connection
     */
    public void init(SocketReaderThread socketReader, boolean runMissingDataFinder) throws SQLException {
        Jmx.registerMBean(this);
        Runtime.getRuntime().addShutdownHook(new Thread(this));
        this.socketReader = socketReader;

        connection.connect();
        connection.getConnection().setAutoCommit(true);

        if (runMissingDataFinder)
            findMissingData();

        dbConnectionThread.start();

        executor.execute(() -> initializeStormStart());

        summarizer.catchup();
    }

    public void initializeStormStart() {
        Storm storm = stormTable.retrieveLatestStorm();
        if (storm != null && storm.isStormActive())
            stormStartTime = storm.getStartTime();
        else
            stormStartTime = null;

    }

    /**
     * Add a historical record to the database
     *
     * @param records The historical records
     *
     */
    @Override
    public void addHistoricalRecords(List<HistoricalRecord> records) {
        if (records.isEmpty())
            return;

        logger.info("Received " + records.size() + " records with time range " +
                    CollectorConstants.dateTimeFormatter().format(records.get(0).getTime()) +
                    " to " + CollectorConstants.dateTimeFormatter().format(records.get(records.size() - 1).getTime()));
        
        List<HistoricalRecord> localList = new ArrayList<>(records);
        executor.execute(() -> handleAddHistoricalRecords(localList));
    }
    
    /**
     * Add a new historical record
     *
     * @param records The historical records to add
     *
     */
    public void handleAddHistoricalRecords(List<HistoricalRecord> records) {
        boolean missingRecord = false;
        logger.info("Checking for gaps in " + records.size() + " records with time range " +
                    CollectorConstants.dateTimeFormatter().format(records.get(0).getTime()) +
                    " to " + CollectorConstants.dateTimeFormatter().format(records.get(records.size() - 1).getTime()));

        for (HistoricalRecord record : records) {
            logger.fine(String.format("Checking historical record at time %s", DBTable.dateTimeFormatter().format(record.getTime())));

            //
            // Make sure there are no gaps in the historical record table. If a gap is detected
            // close down the connection with the reader. This will cause the reader to reconnect
            // and hopefully get the collection back on track.
            //
            if (lastSavedHistoricalRecordTime == null)
                lastSavedHistoricalRecordTime = historyTable.timeOfRecordBefore(record.getTime());
            
            Duration delta;
            if (lastSavedHistoricalRecordTime != null)
                delta = Duration.between(record.getTime(), lastSavedHistoricalRecordTime);
            else
                delta = record.getDuration();

            //
            // If there is a gap in the data then don't save the record right now. If after a number of errors
            // we must assume that there is a problem with the data being sent from the weather station and just save the data.
            // A gap is defined as being greater than a twice the duration. This allows for minor glitches in the historical record
            // times.
            //
            /*
            if (delta.getSeconds() >= 2 * record.getDuration().getSeconds()) {
                missingRecord = true;
                logger.log(Level.WARNING, "Records seem to be missing. Missing Data Count = {0}", missingDataCount);
                logger.warning(String.format("DB Time = %s New record time = %s Delta (seconds) = %d New record duration = %d",
                                    CollectorConstants.dateTimeFormatter().format(lastSavedHistoricalRecordTime),
                                    CollectorConstants.dateTimeFormatter().format(record.getTime()),
                                    delta.getSeconds(),
                                    record.getDuration().getSeconds()));

                if (lastBadHistoricalTime == null)
                    lastBadHistoricalTime = record.getTime();

                if (lastBadHistoricalTime.equals(record.getTime())) { 
                    logger.info("Incrementing missing data counter^^^^^^^^^^^^^^^");
                    missingDataCount++;
                }
                else {
                    missingDataCount = 0;
                    lastBadHistoricalTime = record.getTime();
                }

                if (missingDataCount < MAX_MISSING_DATA_ERRORS_ALLOWED) {
                    //
                    // This will cause the socket to close and force a reconnect from the driver. Flushing the queue will clean out
                    // any other historical records that were read with the record being processed. This will cause a clean start with
                    // the driver.
                    //
                    logger.info("Closing socket due to missing data");
//                    socketReader.closeSocketRequest();
    //                executor.clear(); TODO how do we clear the executors queue?
                }
                else
                    missingRecord = false;
            }
            else
                lastSavedHistoricalRecordTime = record.getTime();
            */
        }

        if (!missingRecord)
            performAddHistoricalRecords(records);
        else
            logger.severe("Skipping adding historical records due to gap int records");
    }

    /**
     * Send the received historical record to the collector thread.
     * 
     * @param record The historical record
     */
    private void performAddHistoricalRecords(List<HistoricalRecord> records) {
        if (historyTable.addHistoricalRecords(records)) {
            logger.info("Added historical " + records.size() + " records with time range " +
                        CollectorConstants.dateTimeFormatter().format(records.get(0).getTime()) +
                        " to " + CollectorConstants.dateTimeFormatter().format(records.get(records.size() - 1).getTime()));

            for (HistoricalRecord record : records) {
                //
                // Add the new record and schedule an summary update if the addition is successful
                //
                LocalDateTime time = record.getTime();
                for (SummarizeTimerTask tt : timerTasks) {
                    if (tt.isDuplicate(time.toLocalDate())) {
                        tt.cancel();
                        timerTasks.remove(tt);
                        break;
                    }
                }

                LocalDate date = record.getTime().toLocalDate();
                SummarizeTimerTask task = new SummarizeTimerTask(date, executor, ()->handleSummarizeCommand(date));
                timerTasks.add(task);
                timer.schedule(task, SUMMARIZER_DELAY);
            }

            lastSavedHistoricalRecordTime = records.get(records.size() - 1).getTime();
            missingDataCount = 0;
            lastBadHistoricalTime = null;
            statistics.currentWeatherRecordCount++;
            statistics.lastCurrentWeatherTime = lastSavedHistoricalRecordTime;
        }
        else {
            logger.warning("Failed to store historical record");
        }
    }

    /**
     * Send a list of sensors to the collector thread.
     * 
     * @param sensorList The list of sensors
     */
    @Override
    public void updateSensorList(List<Sensor> sensorList) {
        logger.log(Level.FINE, "Received sensor list");
        executor.execute(() -> handleUpdateSensorList(sensorList));
    }

    /**
     * Update the sensor list table
     * 
     * @param sensorList The list of sensors
     */
    public void handleUpdateSensorList(List<Sensor> sensorList) {
        logger.fine("Updating sensors");
        sensorTable.updateSensorList(sensorList);
    }

    /**
     * Send the list of sensor stations to the collector thread.
     * 
     * @param sensorStationList The list of sensor stations
     */
    @Override
    public void updateSensorStationList(List<SensorStation> sensorStationList) {
        logger.log(Level.FINE, "Received sensor station list");
        executor.execute(() -> handleUpdateSensorStationList(sensorStationList));
    }

    /**
     * Update the sensor station table
     * 
     * @param sensorStationList The list of sensors
     */
    public void handleUpdateSensorStationList(List<SensorStation> sensorStationList) {
        logger.fine("Updating sensor stations");
        if (!sensorStationTable.updateSensorStationList(sensorStationList))
            handleConnectCommand();
    }

    /**
     * Send a list of sensor station statuses to the collector thread.
     * 
     * @param list The list of sensor station statuses
     */
    @Override
    public void updateSensorStationStatus(List<SensorStationStatus> list) {
        logger.log(Level.FINE, "Received sensor station status");
        executor.execute(() -> handleUpdateSensorStationStatus(list));
    }

    /**
     * Update the sensor station statuses.
     * 
     * @param list The list of sensor station statuses
     */
    public void handleUpdateSensorStationStatus(List<SensorStationStatus> list) {
        logger.fine("Updating sensor stations");
        sensorStationStatusTable.updateSensorStationStatus(list);
    }

    /**
     * Send the weather station parameters to the collector thread.
     * 
     * @param parameters The weather station parameters
     */
    @Override
    public void setWeatherStationParameters(WsParametersMessage parameters) {
        executor.execute(() -> handleUpdateWeatherStationParameters(parameters));
    }

    /**
     * Write the weather station parameters to the database.
     * 
     * @param parameters The weather station parameters
     */
    public void handleUpdateWeatherStationParameters(WsParametersMessage parameters) {
        WeatherStation ws = wsTable.getWeatherStation();
        if (ws == null)
            ws = new WeatherStation();

        if (parameters.getLocation() != null)
            ws.setGeographicLocation(parameters.getLocation());

        if (parameters.getManufacturer() != null)
            ws.setManufacturer(parameters.getManufacturer());

        if (parameters.getModel() != null)
            ws.setModel(parameters.getModel());

        if (parameters.getFirmwareDate() != null)
            ws.setFirmwareDate(parameters.getFirmwareDate());

        if (parameters.getFirmwareVersion() != null)
            ws.setFirmwareVersion(parameters.getFirmwareVersion());

        ws.setWeatherYearStartMonth(Month.of(parameters.getWeatherYearStartMonth()));

        if (!wsTable.updateRow(ws))
            logger.log(Level.WARNING, "Weather Station record not updated with data from weather station console");

        wsParamsTable.setParameters(parameters);
    }

    /**
     * Set the current weather.
     *
     * @param weather The current weather data
     * @param xml The XML version of the current weather
     */
    @Override
    public void setCurrentWeather(CurrentWeather weather, String xml) {
        executor.execute(() -> handleUpdateCurrentWeather(weather, xml));
    }

    /**
     * Update the current storm.
     * 
     */
    private void updateStorm(LocalDate stormStartDate, Depth stormRain) {
        //
        // A non-null stormStartDate means there is an active storm.
        //
        if (stormStartDate != null) {
            //
            // If this is the first time that the storm was detected find the actual
            // start of the storm as opposed to just the start date as reported by
            // the weather station.
            //
            if (stormStartTime == null)
                stormStartTime = findStormStartTime(stormStartDate);

            Storm storm = new Storm(stormStartTime, stormRain);
            if (!stormTable.saveStorm(storm))
                logger.log(Level.SEVERE, "Failed to save storm");
        }
        else if (stormStartTime != null) {
            Storm storm = stormTable.retrieveLatestStorm();
            if (storm.getStartTime().equals(stormStartTime)) {
                List<HistoricalRecord> records = historyTable.retrieveRainRecords(stormStartTime, LocalDateTime.now());
                //
                // When the storm ends, trim the doppler radar images to the times during the storm. The Vantage Pro 2 does
                // not end the storm for 24 hours after the last rainfall so there are 24 hours of no rain doppler images that
                // are not needed.
                //
                if (!records.isEmpty()) {
                    storm = new Storm(stormStartTime, records.get(records.size() - 1).getTime(), storm.getStormRainfall());
                    if (!stormTable.saveStorm(storm))
                        logger.log(Level.SEVERE, "Failed to save storm");

                    stormDopplerTable.trimStormRadarImages(storm.getStartTime(), storm.getEndTime());
                }
                else
                    logger.warning("No rainfall during active storm. Ending storm.");
            }
            else {
                logger.warning("Storm being monitored does not match latest storm in the database. Ending storm.");
            }
            stormStartTime = null;
        }

    }

    private LocalDateTime findStormStartTime(LocalDate startDay) {
        LocalDateTime startOfStormDay = startDay.atStartOfDay();
        List<HistoricalRecord> records = historyTable.retrieveRainRecords(startOfStormDay, LocalDateTime.now());
        if (!records.isEmpty())
            return records.get(0).getTime();
        else
            return startOfStormDay;
    }

    /**
     * Handle a current weather update
     *
     * @param weather The current weather data
     * @param xml The XML string received from the console driver
     */
    public void handleUpdateCurrentWeather(CurrentWeather weather, String xml) {
        logger.info("Received current weather at time " + CollectorConstants.dateTimeFormatter().format(weather.getTime()));
        cwPublisher.sendCurrentWeather(xml);

        updateStorm(weather.getStormStart(), weather.getStormRain());
    }

    /**
     * 
     * Request that the database be closed and reopened
     */
    @Override
    public void requestDbConnection() {
        executor.execute(() -> handleConnectCommand());
    }

    /**
     * Send a Doppler radar image to the collector thread.
     * 
     * @param dri The Doppler radar image from the Internet
     */
    @Override
    public void addDopplerRadarImage(DopplerRadarImage dri) {
        executor.execute(() -> handleDopplerRadarUpdate(dri));
    }

    /**
     * Add the Doppler radar image to the database.
     * 
     * @param dri The Doppler radar image to be added to the database
     */
    public void handleDopplerRadarUpdate(DopplerRadarImage dri) {
        logger.fine("Check for new Doppler Radar images");
        if (!dopplerTable.addRadarImage(dri)) {
            logger.warning("Error saving Doppler radar image");
            connection.close();
        }
        else
            logger.info("Added Doppler radar image");

        //
        // If a storm is active then add this image to the storm radar images
        //
        if (stormStartTime != null) {
            stormDopplerTable.addRadarImage(stormStartTime, dri);
            logger.info("Added Doppler radar image for storm");
        }
    }
    
    /**
     * Handle the command to calculate the summary data for the given date.
     *
     * @param day The date for which the summary will be calculated
     */
    public void handleSummarizeCommand(LocalDate day) {
        try {
            logger.finer(String.format("Calculating summary for date %s", CollectorConstants.dateFormatter().format(day)));

            if (summarizer != null)
                summarizer.updateSummary(day);
            else
                logger.warning("Skipping summarizer, weather station not initialized yet");
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Error", e);
            connection.close();
            requestDbConnection();
        }
    }
    /**
     * Handle the database connection command.
     */
    public void handleConnectCommand() {
        connection.close();
        if (connection.connect()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    requestDbConnection();
                }
            }, CONNECTION_RETRY_INTERVAL_MILLIS);
        }
    }

    /**
     * Send a command to summarize a particular day to the collector thread.
     * 
     * @param day The day to summarize
     */
    @Override
    public void summarizeDay(LocalDate day) {
        executor.execute(() -> handleSummarizeDay(day));
    }

    /**
     * Actually perform the summarizing.
     * 
     * @param day The day to summarize or summarize the entire database if null
     */
    public void handleSummarizeDay(LocalDate day) {
        try {
        if (day == null) {
            dailySummaryTable.deleteAllRows();
            summarizer.catchup();
        }
        else
            summarizer.updateSummary(day);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL exception while summarizing", e);
        }
    }

    /**
     * Find any missing data in the history table
     *
     * @return True if any data is mission
     */
    private boolean findMissingData() {
        logger.fine("Looking for missing data");
        boolean missingDataFound = false;

        List<HistoricalRecord> recs;
        LocalDateTime time = historyTable.findOldestRecordTime();

        if (time == null)
            return true;

        do {
            logger.fine(String.format("Looking for missing data on %s", DBTable.dateFormatter().format(time)));

            recs = historyTable.queryRecordsForDay(time.toLocalDate());

            if (recs.size() > 0) {

                for (HistoricalRecord rec : recs) {
                    LocalDateTime older = historyTable.timeOfRecordBefore(rec.getTime());

                    if (older == null)
                        continue;

                    Duration delta = Duration.between(rec.getTime(), older);
                    if (!delta.equals(rec.getDuration())) {
                        logger.warning(String.format("Found missing data - Record 1: %s Record 2: %s Delta of %d != to duration of %d",
                                                    DBTable.dateTimeFormatter().format(rec.getTime()),
                                                    DBTable.dateTimeFormatter().format(older),
                                                    delta, rec.getDuration()));
                        missingDataFound = true;
                    }
                }

                time = time.plusDays(1);
            }
        } while (recs.size() > 0);

        return missingDataFound;
    }

    /**
     * Entry point for shutdown hook thread.
     */
    @Override
    public void run() {
        logger.info("Shutting down");
        Jmx.unregisterMBean();
        executor.shutdownNow();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @SuppressWarnings("serial")
    public class Statistics implements Serializable {
        int currentWeatherRecordCount;
        LocalDateTime lastCurrentWeatherTime;
        int historicalRecordCount;
        LocalDateTime lastHistoricalRecordTime; 

        @Override
        public String toString() {
            String s = "Number of current weather records received: " + currentWeatherRecordCount + "\n" +
                       "Number of historical records received: " + historicalRecordCount;

            return s;
        }
    }

    public static class Jmx implements CollectorWriterMBean {
        private final CollectorDbWriter writer;
        private static ObjectName objectName;

        private static void registerMBean(CollectorDbWriter writer) {
            try {
                Jmx jmx = new Jmx(writer);
                StandardMBean mbean = new StandardMBean(jmx, CollectorWriterMBean.class, false);
                objectName = new ObjectName("com.bdb.weather:type=Collector");
                ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, objectName);
            }
            catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) { 

            }
        }

        private static void unregisterMBean() {
            try {
                ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException | MBeanRegistrationException ex) {
                Logger.getLogger(CollectorDbWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public Jmx(CollectorDbWriter writer) {
            this.writer = writer;
        }

        @Override
        public Statistics getStatistics() {
            return writer.getStatistics();
        }

        @Override
        public int getCurrentWeatherRecordCount() {
            return writer.getStatistics().currentWeatherRecordCount;
        }

    }

    public interface CollectorWriterMBean {
        public Statistics getStatistics();
        public int getCurrentWeatherRecordCount();
    }
}