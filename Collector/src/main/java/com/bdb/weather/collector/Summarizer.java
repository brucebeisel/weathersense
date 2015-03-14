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

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.WindParameters;
import com.bdb.weather.common.db.DailyRecordsTable;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.TemperatureRecordTable;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class that calculates the daily summary records
 * 
 * @author Bruce
 * @since 1.0
 */
public class Summarizer
{
    private final HistoryTable           historyTable;
    private final DailySummaryTable      dailySummaryTable;
    private final DailyRecordsTable      dailyRecordsTable;
    private final TemperatureRecordTable temperatureRecordTable;
    private final WeatherStationTable    weatherStationTable;
    private WindParameters               windParameters;
    private final TemperatureBinMgr      temperatureBinMgr;
    private final WeatherDataWriter      dataWriter;
    private static final Logger          logger = Logger.getLogger(Summarizer.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The database connection to use
     * @param dataWriter The database writer thread
     */
    public Summarizer(DBConnection connection, WeatherDataWriter dataWriter)
    {
        historyTable = new HistoryTable(connection);
        dailySummaryTable = new DailySummaryTable(connection);
        temperatureRecordTable = new TemperatureRecordTable(connection);
        dailyRecordsTable = new DailyRecordsTable(connection);
        weatherStationTable = new WeatherStationTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);
        this.dataWriter = dataWriter;
    }
    
    /**
     * Load the data that is needed to summarize a day of historical records.
     */
    private void loadSupportingData() {
        temperatureBinMgr.refresh();
        WeatherStation weatherStation = weatherStationTable.getWeatherStation();
        if (weatherStation != null)
            windParameters = weatherStation.getWindParameters();
    }

    /**
     * Do a complete refresh of the daily summaries.
     * 
     * @throws SQLException A database error occurred
     */
    public void refresh() throws SQLException {
        loadSupportingData();
        dailySummaryTable.deleteAllRows();
        catchup();
    };

    /**
     * Update the summary record for the specified date. The previous day may also be calculated
     * in order to ensure that the previous day has been summarized with all of the data from that
     * day.
     * 
     * @param date The date for which the summary data will be calculated
     * 
     * @throws SQLException A database error occurred
     */
    public void updateSummary(LocalDate date) throws SQLException {        
        if (windParameters != null) {
            calculateDay(date);
        }
        else {
            logger.info("No weather station data, refreshing");
            refresh();
        }
    }

    /**
     * Calculate the summary for the specified date
     * 
     * @param date The date for which the summary data will be calculated
     * 
     * @throws SQLException A database error occurred
     */
    private void calculateDay(LocalDate date) throws SQLException {
        logger.log(Level.FINE, "Calculating summary for date {0}", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(date));

        if (windParameters == null) {
            logger.info("Skipping summarizer because there are no wind parameters specified for the weather station");
            return;
        }
        
        List<HistoricalRecord> recs = historyTable.queryRecordsForDay(date);

        if (recs.isEmpty())
            return;

        SummaryRecord dailySummary = new SummaryRecord(windParameters, temperatureBinMgr, date);

        recs.stream().forEach((rec) -> {
            dailySummary.applyHistoricalRecord(rec);
        });

        logger.log(Level.FINEST, "Calculated Daily Record:\n{0}", dailySummary);

        if (dailySummaryTable.addRow(dailySummary))
            logger.log(Level.INFO, "Performed daily summary for {0}", CollectorConstants.dateFormatter().format(dailySummary.getDate()));
        else
            logger.log(Level.WARNING, "Failed to save daily summary for {0}", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dailySummary.getDate()));

        findRecords(dailySummary, date);
    }
    
    /**
     * Find any new temperature or rain records.
     * 
     * @param dailySummary The summary record for the given date
     * @param date The date of the summary record
     * @param isEndOfDay True if all of the data for the given day has been collected
     */
    private void findRecords(SummaryRecord dailySummary, LocalDate date) {
        DailyRecords records = dailyRecordsTable.retrieveRecordForDay(date.getMonth(), date.getDayOfMonth());
        if (records == null)
            return;

        if (dailySummary.getMaxOutdoorTemp() != null) {
            Temperature t = records.getMaxHighTemperature();
            if (t == null || dailySummary.getMaxOutdoorTemp().compareTo(t) >= 0) {
                Extreme<Temperature,TemperatureRecordType> extreme = new Extreme<>(date, TemperatureRecordType.MAX_HIGH, dailySummary.getMaxOutdoorTemp(), records.getMaxHighTemperatureYear(), t);
                temperatureRecordTable.saveExtreme(extreme);
                dailyRecordsTable.saveNewRecordMaximumHigh(extreme);
            }
        }

        if (dailySummary.getMinOutdoorTemp() != null) {
            Temperature t = records.getMinLowTemperature();
            if (t == null || dailySummary.getMinOutdoorTemp().compareTo(t) <= 0) {
                Extreme<Temperature,TemperatureRecordType> extreme = new Extreme<>(date, TemperatureRecordType.MIN_LOW, dailySummary.getMinOutdoorTemp(), records.getMinLowTemperatureYear(), t);
                temperatureRecordTable.saveExtreme(extreme);
                dailyRecordsTable.saveNewRecordMinimumLow(extreme);
            }
        }
        /*
            Depth d = records.getRainfall();
            Depth rain = dailySummary.getTotalRainfall();
            if (rain.get() != 0.0 && (d == null || rain.compareTo(d) >= 0)) {
                Extreme<Depth,RainfallRecordType> extreme = new Extreme<Depth,RainfallRecordType>(date, RainfallRecordType.DAILY_RAINFALL, dailySummary.getTotalRainfall(), records.getRainfallYear(), d);
                rainfallRecordTable.saveExtreme(extreme);
                dailyRecordsTable.saveNewDayRainfallMax(extreme);
            }

            //
            // Have to wait until the end of the day because it may get warmer or colder later in the day and
            // that would override the record
            //
            if (isEndOfDay) {
                Temperature t = records.getMinHighTemperature();
                if (t == null || dailySummary.getMaxOutdoorTemp().compareTo(t) <= 0) {
                    Extreme<Temperature,TemperatureRecordType> extreme = new Extreme<Temperature,TemperatureRecordType>(date, TemperatureRecordType.MIN_HIGH, dailySummary.getMaxOutdoorTemp(), records.getMinHighTemperatureYear(), t);
                    temperatureRecordTable.saveExtreme(extreme);
                    //dailyRecordsTable.saveNewRecordHigh(extreme);
                }

                if (dailySummary.getMinOutdoorTemp().compareTo(records.getMaxLowTemperature()) >= 0) {
                    Extreme<Temperature,TemperatureRecordType> extreme = new Extreme<Temperature,TemperatureRecordType>(date, TemperatureRecordType.MAX_LOW, dailySummary.getMinOutdoorTemp(), records.getMaxLowTemperatureYear(), records.getMaxLowTemperature());
                    temperatureRecordTable.saveExtreme(extreme);
                    //dailyRecordsTable.saveNewRecordHigh(extreme);
                }
            }
         */
    }

    /**
     * Catch up on the summary calculations. Perform the calculations from the start
     * date to the end date
     * 
     * @throws SQLException An error occurred while communicating with the database
     */
    public void catchup() throws SQLException {
        loadSupportingData();
        LocalDateTime newestHistoryRecordTime = historyTable.findNewestRecordTime();
        if (newestHistoryRecordTime == null) {
            logger.info("No historical records to summarize");
            return;
        }

        //
        // Get the latest record from the summary table. If there are no record
        // then find the oldest record in the history table and start from that date
        //
        LocalDate newestSummary = dailySummaryTable.findNewestRecord();

        if (newestSummary == null) {
            logger.info("No summary records, looking for historical records");

            LocalDateTime oldestHistoryRecordTime = historyTable.findOldestRecordTime();
            newestSummary = oldestHistoryRecordTime.toLocalDate();
        }

        LocalDate currentSummary = newestSummary;
        logger.log(Level.INFO, "Catching up starting at {0}", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(currentSummary));

        //
        // Schedule the summarizer for all missing days. This could put thousands of records onto the queue,
        // but is does not happen very often so that is acceptable. Also, scheduling the calculations allows
        // for other commands to be processed by the collector while catching up.
        //
        LocalDate end = newestHistoryRecordTime.toLocalDate();
        while (!currentSummary.isAfter(end)) {
            logger.log(Level.FINE, "Scheduling summary calculation for date {0}", DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(currentSummary));
            dataWriter.summarizeDay(currentSummary);
            currentSummary = currentSummary.plusDays(1);
        }
    }
}										    