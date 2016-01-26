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
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.TemperatureBinDuration;
import com.bdb.weather.common.ThresholdType;
import com.bdb.weather.common.measurement.Temperature;

/**
 * The class for accessing the time that the temperature bin durations.
 * 
 * @author Bruce
 *
 */
public class TemperatureBinDurationTable extends DBTable<TemperatureBinDuration> {
    /**
     * Name of the temperature bin duration table
     */
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".temperature_bin_durations";
    private static final String TEMPERATURE_BIN_TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".temperature_bins";
    private static final String DATE_COLUMN = "date";
    private static final String TEMPERATURE_BIN_ID_COLUMN = "temperature_bin_id";
    private static final String DURATION_COLUMN = "duration";
    private static final String BIN_TYPE_COLUMN = "bin_type";
    private static final String BIN_THRESHOLD_COLUMN = "threshold";
    private static final String QUERY = "select * from " + TABLE_NAME + " inner join " + TEMPERATURE_BIN_TABLE_NAME + " on " +
                                        TABLE_NAME + ".temperature_bin_id=" + TEMPERATURE_BIN_TABLE_NAME + ".bin_id";
    private static final Logger logger = Logger.getLogger(TemperatureBinDurationTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public TemperatureBinDurationTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
   
    /**
     * Add the durations by extracting the data from a summary record.
     * 
     * @param record The Summary record
     * @return  True if rows added successfully
     */
    public boolean addRowsFromSummary(SummaryRecord record) {
        return addTemperatureBinDurations(record.getTemperatureBinDurations());
    }
    
    /**
     * Retrieve the duration bins for a given day for a given station.
     * 
     * @param date The date of the data to be retrieved
     * @return The list of the temperature bin durations
     */
    public List<TemperatureBinDuration> retrieveBinsForDay(LocalDate date) {
        String clause = " where " + DATE_COLUMN + "='" + DBTable.dateFormatter().format(date) + "'";
        
        return query(clause);
    }
    
    /* (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.TemperatureBin)
     */
    @Override
    public boolean addRow(TemperatureBinDuration row) {
        List<TemperatureBinDuration> list = new ArrayList<>();
        list.add(row);
        return addTemperatureBinDurations(list);
    }

    /**
     * Add a list of temperature bin durations to the database.
     * 
     * @param list The list of durations
     * @return True if the durations were added sucessfully
     */
    public boolean addTemperatureBinDurations(List<TemperatureBinDuration> list) {
       String sql = "insert into " + TABLE_NAME + " values(?,?,?)";

        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
            for (TemperatureBinDuration row : list) {
                int n = 1;

                stmt.setDate(n++,  java.sql.Date.valueOf(row.getDate()));
                stmt.setInt(n++, row.getBinId());
                stmt.setLong(n++, row.getDuration().getSeconds());
                stmt.addBatch();
            }

            int [] results = stmt.executeBatch();
            boolean success = true;
            for (int result : results)
                success = success && result == 1;

            return success;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#deleteRow(java.lang.TemperatureBin)
     */
    @Override
    public boolean deleteRow(TemperatureBinDuration row) {
        throw new UnsupportedOperationException("TemperatureBindDuration.deleteRow() not supported");
    }

    /* (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<TemperatureBinDuration> query(String clause) {
        String sql = QUERY + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<TemperatureBinDuration> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDate date = rs.getDate(DATE_COLUMN).toLocalDate();
            int temperatureBinId = rs.getInt(TEMPERATURE_BIN_ID_COLUMN);
            Duration duration = Duration.ofSeconds(rs.getInt(DURATION_COLUMN));
            String typeString = rs.getString(BIN_TYPE_COLUMN);
            ThresholdType thresholdType = ThresholdType.valueOf(typeString);
            double threshold = rs.getDouble(BIN_THRESHOLD_COLUMN);
            
            TemperatureBin bin = new TemperatureBin(temperatureBinId, thresholdType, new Temperature(threshold, DatabaseUnits.TEMPERATURE));
            
            TemperatureBinDuration rec = new TemperatureBinDuration(bin, date, duration);
            
            return rec;
        });
        
        return list;
    }
}