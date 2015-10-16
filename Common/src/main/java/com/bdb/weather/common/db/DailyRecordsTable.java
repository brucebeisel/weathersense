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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;
import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.Unit;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.DayOfYearCollection;
import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.RainfallRecordType;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class used to access the daily records table. This table holds the records for maximum high temperature and minimum low temperature.
 * It also has columns for minimum high, maximum low and rainfall, but they are not used at this time.
 * 
 * @author Bruce
 *
 */
public class DailyRecordsTable extends DBTable<DailyRecords> {

    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".daily_records";
    private static final String MONTH_COLUMN = "month";
    private static final String DAY_COLUMN = "day";
    private static final String RECORD_MAX_HIGH_COLUMN = "record_max_high";
    private static final String RECORD_MAX_HIGH_YEAR_COLUMN = "record_max_high_year";
    private static final String RECORD_MAX_LOW_COLUMN = "record_max_low";
    private static final String RECORD_MAX_LOW_YEAR_COLUMN = "record_max_low_year";
    private static final String RECORD_MIN_HIGH_COLUMN = "record_min_high";
    private static final String RECORD_MIN_HIGH_YEAR_COLUMN = "record_min_high_year";
    private static final String RECORD_MIN_LOW_COLUMN = "record_min_low";
    private static final String RECORD_MIN_LOW_YEAR_COLUMN = "record_min_low_year";
    private static final String RECORD_RAINFALL_COLUMN = "record_rainfall";
    private static final String RECORD_RAINFALL_YEAR_COLUMN = "record_rainfall_year";
    private static final Logger logger = Logger.getLogger(DailyRecordsTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public DailyRecordsTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

    /**
     * Retrieve the records for each day of the year for a given weather station.
     * 
     * @return The records for every day of the year
     */
    public DayOfYearCollection<DailyRecords> retrieveRecordsFor() {
        List<DailyRecords> list = query(" order by " + MONTH_COLUMN + "," + DAY_COLUMN);

        DayOfYearCollection<DailyRecords> collection = new DayOfYearCollection<>();

        list.stream().forEach((records) -> {
            collection.addItem(records.getMonth(), records.getDay(), records);
        });

        return collection;
    }

    /**
     * Retrieve the records for a given day.
     * 
     * @param month The month for which the records are being retrieved (January = 1)
     * @param day The day of the month for which the records are be retrieved (1-31)
     * @return The records for the day or null if there are no entries for weather station or an error occurred
     */
    public DailyRecords retrieveRecordForDay(Month month, int day) {
        //
        // For a leap day use March 1
        //
        if (month == Month.FEBRUARY && day == 29) {
            month = Month.MARCH;
            day = 1;
        }

        List<DailyRecords> record = query(" where " + MONTH_COLUMN + "=" + month.getValue() + " and " + DAY_COLUMN + "=" + day);

        if (record.size() == 1)
            return record.get(0);
        else
            return null;
    }

    /**
     * Retrieve the records for a given day.
     * 
     * @param date The date from which the month and day of month will be determined
     * @return The records for the day or null if there are no entries for weather station or an error occurred
     */
    public DailyRecords retrieveRecordForDay(LocalDate date) {
        return retrieveRecordForDay(date.getMonth(), date.getDayOfMonth());
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<DailyRecords> query(String clause) {
        String sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<DailyRecords> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Month month = Month.of(rs.getInt(MONTH_COLUMN));
            
            DailyRecords record = new DailyRecords(month, rs.getInt(DAY_COLUMN));
            
            double temperature = rs.getDouble(RECORD_MAX_HIGH_COLUMN);
            if (!rs.wasNull())
                record.setMaxHighTemperature(new Temperature(temperature, DatabaseUnits.TEMPERATURE), rs.getInt(RECORD_MAX_HIGH_YEAR_COLUMN));
            
            temperature = rs.getDouble(RECORD_MIN_HIGH_COLUMN);
            if (!rs.wasNull())
                record.setMinHighTemperature(new Temperature(temperature, DatabaseUnits.TEMPERATURE), rs.getInt(RECORD_MIN_HIGH_YEAR_COLUMN));
            
            temperature = rs.getDouble(RECORD_MAX_LOW_COLUMN);
            if (!rs.wasNull())
                record.setMaxLowTemperature(new Temperature(temperature, DatabaseUnits.TEMPERATURE), rs.getInt(RECORD_MAX_LOW_YEAR_COLUMN));
            
            temperature = rs.getDouble(RECORD_MIN_LOW_COLUMN);
            if (!rs.wasNull())
                record.setMinLowTemperature(new Temperature(temperature, DatabaseUnits.TEMPERATURE), rs.getInt(RECORD_MIN_LOW_YEAR_COLUMN));
            
            double rainfall = rs.getDouble(RECORD_RAINFALL_COLUMN);
            if (!rs.wasNull())
                record.setRainfall(new Depth(rainfall, DatabaseUnits.DEPTH), rs.getInt(RECORD_RAINFALL_YEAR_COLUMN));
            
            return record;
        });

        return list;
    }

    /**
     * Change the record value for a given date.
     * 
     * @param extreme The extreme data
     * @param valueColumn The name of the column that contains the value
     * @param yearColumn The name of the column that contains the year of the record
     * @param unit The unit of the measurement that is stored
     * @return Whether the record was updated
     */
    private boolean saveNewRecord(Extreme<? extends Measurement, ?> extreme, String valueColumn, String yearColumn, Unit unit) {
        boolean success;
	LocalDate c = extreme.getDate();
	String sql = "update " + TABLE_NAME + " set " + valueColumn + "=?, " + yearColumn + "=?" + " where " +
		     MONTH_COLUMN + "=" + c.getMonth().getValue() + " and " + DAY_COLUMN + "=" + c.getDayOfMonth();

        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, extreme.getValue().get(unit));
            stmt.setInt(2, c.getYear());

            success = getConnection().executeUpdate(stmt) == 1;
            
            stmt.close();
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        
        return success;
    }

    /**
     * Save a new maximum high temperature record.
     * 
     * @param extreme The data for the new high temperature record
     * @return Whether the new record was saved
     */
    public boolean saveNewRecordMaximumHigh(Extreme<Temperature, TemperatureRecordType> extreme) {
        if (extreme.getType() == TemperatureRecordType.MAX_HIGH || extreme.getType() == TemperatureRecordType.MAX_HIGH_TIE)
            return saveNewRecord(extreme, RECORD_MAX_HIGH_COLUMN, RECORD_MAX_HIGH_YEAR_COLUMN, DatabaseUnits.TEMPERATURE);
        else
            throw new IllegalArgumentException("Trying to set new record maximum temperature with a " + extreme.getType());
    }

    /**
     * Save a new minimum low temperature record.
     * 
     * @param extreme The data for the new low temperature record
     * @return Whether the new record was saved
     */
    public boolean saveNewRecordMinimumLow(Extreme<Temperature, TemperatureRecordType> extreme) {
        if (extreme.getType() != TemperatureRecordType.MIN_LOW)
            throw new IllegalArgumentException("Trying to set new record minimum temperature with a " + extreme.getType());

        return saveNewRecord(extreme, RECORD_MIN_LOW_COLUMN, RECORD_MIN_LOW_YEAR_COLUMN, DatabaseUnits.TEMPERATURE);
    }

    /**
     * Save a new rainfall record.
     * 
     * @param extreme The data for the new rainfall record
     * @return Whether the new record was saved
     */
    public boolean saveNewDayRainfallMax(Extreme<Depth, RainfallRecordType> extreme) {
        if (extreme.getType() != RainfallRecordType.DAILY_RAINFALL)
            throw new IllegalArgumentException("Trying to set new record maximum temperature with a " + extreme.getType());

        return saveNewRecord(extreme, RECORD_MAX_HIGH_COLUMN, RECORD_MAX_HIGH_YEAR_COLUMN, DatabaseUnits.DEPTH);
    }
}