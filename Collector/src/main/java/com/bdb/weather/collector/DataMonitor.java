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

import com.bdb.weather.collector.wunderground.WeatherUnderground;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CollectorCommand;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.CollectorCommandsTable;
import com.bdb.weather.common.db.WeatherStationTable;

/**
 * Class that monitors various data for change and performs the proper operation when a change is detected.
 * This class has its own database connection because it runs in its own thread.
 *
 * @author Bruce
 */
public class DataMonitor {
    private static final int INTERVAL_MILLIS = 60000;
    private final DBConnection connection;
    private final ScheduledExecutorService executor;
    private final WeatherStationTable wsTable;
    private final DopplerRadarManager dopplerMgr;
    private final WeatherUnderground weatherUnderground;
    private final CollectorCommandsTable collectorCommandsTable;
    private final WeatherDataWriter writer;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CollectorCommand.COMMAND_DATE_FORMAT);
    private static final Logger logger = Logger.getLogger(DataMonitor.class.getName());

    /**
     * Constructor.
     * 
     * @param dbUrl The URL with which to connect to the database
     * @param user The user with which to log into the database
     * @param password The password for the database user
     * @param dpm The Doppler Radar manager object that retrieves the Doppler radar images from the Internet
     * @param wu The weather underground object that write the weather data to the weather underground web site
     * @param writer The database writer thread that does all of the writing to the database
     */
    public DataMonitor(String dbUrl, String user, String password, DopplerRadarManager dpm, WeatherUnderground wu, WeatherDataWriter writer) {
        connection = new DBConnection(dbUrl, user, password);
        executor = Executors.newSingleThreadScheduledExecutor();
        wsTable = new WeatherStationTable(connection);
        collectorCommandsTable = new CollectorCommandsTable(connection);
        dopplerMgr = dpm;
        weatherUnderground = wu;
        this.writer = writer;
    }

    /**
     * Initialize the executor thread, this version is only used for testing.
     * 
     * @param monitorIntervalMillis The interval at which the database is checked for changes
     */
    public void init(int monitorIntervalMillis) {
        executor.scheduleAtFixedRate(() -> {
            checkForDatabaseChanges();
        }, 0, monitorIntervalMillis, TimeUnit.MILLISECONDS);

    }

    public void init() {
        this.init(INTERVAL_MILLIS);
    }

    public void shutdown() {
        try {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Timed out waiting for data monitor to terminate", ex);
        }
    }

    /**
     * Check if any changes have occurred.
     */
    private void checkForDatabaseChanges() {
        logger.log(Level.FINE, "Checking for changes to weather station record");
        connection.connect();
        WeatherStation ws = wsTable.getWeatherStation();

        if (ws == null) {
            logger.log(Level.INFO, "Weather station record does not exist");
        }
        else {
            checkForDopplerUrlChanges(ws.getDopplerRadarUrl());
            if (!ws.getWeatherUndergroundStationId().isEmpty())
                weatherUnderground.setStationParameters(ws.getWeatherUndergroundStationId(), ws.getWeatherUndergroundPassword());
            checkForCollectorCommands();
        }
        connection.close();
    }

    /**
     * Check if there have been any changes to the Doppler Radar image URL.
     * 
     * @param urlString The URL from the weather station table
     */
    private void checkForDopplerUrlChanges(String urlString) {
        try {
            if (!urlString.isEmpty())
                dopplerMgr.setDopplerUrl(new URL(urlString));
        }
        catch (MalformedURLException e) {
            logger.log(Level.WARNING, "Bad doppler radar URL: {0}", urlString);
        }
    }

    /**
     * Check the Collector Commands table for any new commands.
     */
    private void checkForCollectorCommands() {
        List<CollectorCommand> commands = collectorCommandsTable.retrieveNewCommands();

        logger.fine("There are " + commands.size() + " collector commands to process");

        //
        // Process each command
        //
        for (CollectorCommand command : commands) {
            String tokens[] = command.getCommand().split(" ");
            switch (tokens[0]) {
                case CollectorCommand.SUMMARIZE_COMMAND:
                    if (tokens[1].equals(CollectorCommand.SUMMARIZE_ALL))
                        writer.summarizeDay(null);
                    else {
                        try {
                            LocalDate date = LocalDate.parse(tokens[1], dateFormatter);
                            writer.summarizeDay(date);
                        }
                        catch (DateTimeParseException e) {
                            Logger.getLogger(DataMonitor.class.getName()).log(Level.SEVERE, "Invalidate date in collector command", e);
                        }
                    }
                    break;

                default:
                    logger.info("Deleting invalid collector command in database: " + tokens[0]);
                    collectorCommandsTable.deleteRow(command);
                    break;
            }

            collectorCommandsTable.updateCommandState(command.getId(), CollectorCommand.CollectorCommandState.COMPLETE);
        }
    }
}