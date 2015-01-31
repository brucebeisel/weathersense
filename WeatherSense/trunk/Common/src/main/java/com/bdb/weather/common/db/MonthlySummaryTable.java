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
package com.bdb.weather.common.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.measurement.Speed;

public class MonthlySummaryTable extends DBTable<SummaryRecord> {
    private static final Logger logger = Logger.getLogger(MonthlySummaryTable.class.getName());

    private static final String	TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".monthly_summary";
    private static final String MONTH_COLUMN = "month";
    private static final String TOTAL_DURATION_COLUMN = "total_duration";
    private static final String OUTDOOR_TEMP_DURATION_COLUMN = "outdoor_temp_duration";
    private static final String HIGH_OUTDOOR_TEMP_COLUMN = "high_outdoor_temp";
    private static final String HIGH_OUTDOOR_TEMP_TIME_COLUMN = "high_outdoor_temp_time";
    private static final String LOW_OUTDOOR_TEMP_COLUMN = "low_outdoor_temp";
    private static final String LOW_OUTDOOR_TEMP_TIME_COLUMN = "low_outdoor_temp_time";
    private static final String AVG_OUTDOOR_TEMP_COLUMN = "avg_outdoor_temp";
    private static final String INDOOR_TEMP_DURATION_COLUMN = "indoor_temp_duration";
    private static final String HIGH_INDOOR_TEMP_COLUMN = "high_indoor_temp";
    private static final String HIGH_INDOOR_TEMP_TIME_COLUMN = "high_indoor_temp_time";
    private static final String LOW_INDOOR_TEMP_COLUMN = "low_indoor_temp";
    private static final String LOW_INDOOR_TEMP_TIME_COLUMN = "low_indoor_temp_time";
    private static final String AVG_INDOOR_TEMP_COLUMN = "avg_indoor_temp";
    private static final String OUTDOOR_HUMID_DURATION_COLUMN = "outdoor_humid_duration";
    private static final String HIGH_OUTDOOR_HUMID_COLUMN = "high_outdoor_humid";
    private static final String HIGH_OUTDOOR_HUMID_TIME_COLUMN = "high_outdoor_humid_time";
    private static final String LOW_OUTDOOR_HUMID_COLUMN = "low_outdoor_humid";
    private static final String LOW_OUTDOOR_HUMID_TIME_COLUMN = "low_outdoor_humid_time";
    private static final String AVG_OUTDOOR_HUMID_COLUMN = "avg_outdoor_humid";
    private static final String INDOOR_HUMID_DURATION_COLUMN = "indoor_humid_duration";
    private static final String HIGH_INDOOR_HUMID_COLUMN = "high_indoor_humid";
    private static final String HIGH_INDOOR_HUMID_TIME_COLUMN = "high_indoor_humid_time";
    private static final String LOW_INDOOR_HUMID_COLUMN = "low_indoor_humid";
    private static final String LOW_INDOOR_HUMID_TIME_COLUMN = "low_indoor_humid_time";
    private static final String AVG_INDOOR_HUMID_COLUMN = "avg_indoor_humid";
    private static final String HIGH_PRESSURE_TIME_COLUMN = "high_pressure_time";
    private static final String LOW_PRESSURE_COLUMN = "low_pressure";
    private static final String LOW_PRESSURE_TIME_COLUMN = "low_pressure_time";
    private static final String AVG_PRESSURE_COLUMN = "avg_pressure";
    private static final String WIND_DURATION_COLUMN = "wind_duration";
    private static final String MAX_WIND_SPEED_COLUMN = "max_wind_speed";
    private static final String MAX_WIND_SPEED_TIME_COLUMN = "max_wind_speed_time";
    private static final String MAX_WIND_GUST_COLUMN = "max_wind_gust";
    private static final String MAX_WIND_GUST_TIME_COLUMN = "max_wind_gust_time";
    private static final String AVG_WIND_SPEED_COLUMN = "avg_wind_speed";
    private static final String RAINFALL_COLUMN = "rainfall";
    private static final String MAX_RAINFALL_RATE_COLUMN = "max_rainfall_rate";
    private static final String MAX_RAINFALL_RATE_TIME_COLUMN = "max_rainfall_rate_time";
    
    //
    // TODO add a real MonthlySummaryTable to the database and have the collector populate the table.
    // For now we will use the daily records to create monthly records.
    //
    private final DailySummaryTable dailySummaryTable;

    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public MonthlySummaryTable(DBConnection connection) {
	super(TABLE_NAME, connection);
	dailySummaryTable = new DailySummaryTable(connection);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(SummaryRecord record) {
        String stmtString = "replace into " + TABLE_NAME
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            int n = 1;
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(stmtString)) {
                stmt.setDate(n++,  java.sql.Date.valueOf(record.getDate()));
                stmt.setLong(n++, record.getDuration().getSeconds());
                //
                // Outdoor temperature
                //
                stmt.setLong(n++, record.getOutdoorTempDuration().getSeconds());
                stmt.setDouble(n++, record.getMaxOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxOutdoorTempTime()));
                stmt.setDouble(n++, record.getMinOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMinOutdoorTempTime()));
                stmt.setDouble(n++, record.getAvgOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
                //
                // Indoor temperature
                //
                stmt.setLong(n++, record.getIndoorTempDuration().getSeconds());
                stmt.setDouble(n++, record.getMaxIndoorTemp().get(DatabaseUnits.TEMPERATURE));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxIndoorTempTime()));
                stmt.setDouble(n++, record.getMinIndoorTemp().get(DatabaseUnits.TEMPERATURE));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMinIndoorTempTime()));
                stmt.setDouble(n++, record.getAvgIndoorTemp().get(DatabaseUnits.TEMPERATURE));
                //
                // Outdoor humidity
                //
                stmt.setLong(n++, record.getOutdoorHumidityDuration().getSeconds());
                stmt.setDouble(n++, record.getMaxOutdoorHumidity().get());
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxOutdoorHumidityTime()));
                stmt.setDouble(n++, record.getMinOutdoorHumidity().get());
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMinOutdoorHumidityTime()));
                stmt.setDouble(n++, record.getAvgOutdoorHumidity().get());
                //
                // Indoor humidity
                //
                stmt.setLong(n++, record.getIndoorHumidityDuration().getSeconds());
                stmt.setDouble(n++, record.getMaxIndoorHumidity().get());
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxIndoorHumidityTime()));
                stmt.setDouble(n++, record.getMinIndoorHumidity().get());
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMinIndoorHumidityTime()));
                stmt.setDouble(n++, record.getAvgIndoorHumidity().get());
                //
                // Barometric Pressure 
                //
                stmt.setLong(n++, record.getBaroPressureDuration().getSeconds());
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxBaroPressureTime()));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMinBaroPressureTime()));
                stmt.setDouble(n++, record.getAvgBaroPressure().get(DatabaseUnits.PRESSURE));
                //
                // Wind
                //
                stmt.setLong(n++, record.getWindDuration().getSeconds());
                stmt.setDouble(n++, record.getMaxWindSpeed().get(DatabaseUnits.SPEED));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxWindSpeedTime()));

                Speed gust = record.getMaxWindGust();

                if (gust != null)
                {
                    stmt.setDouble(n++, record.getMaxWindGust().get(DatabaseUnits.SPEED));
                    stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxWindGustTime()));
                }
                else
                {
                    stmt.setNull(n++, Types.DOUBLE);
                    stmt.setNull(n++, Types.TIMESTAMP);
                }

                stmt.setDouble(n++, record.getAvgWindSpeed().get(DatabaseUnits.SPEED));

                //
                // Rainfall
                //
                stmt.setDouble(n++, record.getTotalRainfall().get(DatabaseUnits.DEPTH));
                stmt.setDouble(n++, record.getMaxRainfallRate().get(DatabaseUnits.DEPTH));
                stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(record.getMaxRainfallRateTime()));

                stmt.executeUpdate();
            }

            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQLException", e);
            return false;
        }
    }

    /**
     * Retrieve monthly summaries for the specified time period.
     * 
     * @param ws The ID of the weather station
     * @param start The start of the time period
     * @param end The end of the time period
     * @param temperatureBinMgr The temperature bins
     * @return The list of monthly summary records
     */
    public List<SummaryRecord> retrieveRange(WeatherStation ws, LocalDate start, LocalDate end, TemperatureBinMgr temperatureBinMgr) {
        List<SummaryRecord> monthlyList = new ArrayList<>();
        
        start = start.withDayOfMonth(1);
        end = end.withDayOfMonth(end.lengthOfMonth());
        
        while (start.isBefore(end)) {
            LocalDate endMonth = start.withDayOfMonth(start.lengthOfMonth());
            List<SummaryRecord> dailyList = dailySummaryTable.retrieveRange(start, endMonth, ws.getWindParameters(), temperatureBinMgr);
            if (dailyList.size() > 0) {
                SummaryRecord monthlySummary = new SummaryRecord(ws.getWindParameters(), temperatureBinMgr, start);
                
                dailyList.stream().forEach((record) -> { monthlySummary.applySummaryRecord(record); });
                
                monthlyList.add(monthlySummary);
            }
            start = start.plusMonths(1);
        }   
        
        return monthlyList;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#updateRow(java.lang.Object)
     */
    @Override
    public boolean updateRow(SummaryRecord object) {
	return addRow(object);
    }
}
