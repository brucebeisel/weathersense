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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.SpeedBinDuration;

/**
 * A table that contains the duration that wind was blowing in a direction at a speed.
 * This data is used to create the wind rose.
 * 
 * @author Bruce
 *
 */
public class WindSpeedBinDurationTable extends DBTable<SpeedBinDuration> {
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".wind_speed_bin_durations";
    private static final String DATE_COLUMN = "date";
    private static final String WIND_HEADING_INDEX_COLUMN = "wind_heading_index";
    private static final String WIND_SPEED_BIN_INDEX_COLUMN = "wind_speed_bin_index";
    private static final String DURATION_COLUMN = "duration";
    private static final Logger logger = Logger.getLogger("Dummy");
    
    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public WindSpeedBinDurationTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
    
    /**
     * Retrieve the duration of time the wind blew at a heading, a speed on a date.
     * 
     * @param date The date to query for
     * @param headingIndex The index of the heading (0 is north)
     * @param speedBinIndex The index of the wind speed bin 
     * @return The duration the wind blew with the given parameters
     */
    public SpeedBinDuration retrieveDurationForBin(LocalDate date, int headingIndex, int speedBinIndex) {     
        String clause = " where " + 
                        DATE_COLUMN + "='" + DBTable.dateFormatter().format(date) + "' and " +
                        WIND_HEADING_INDEX_COLUMN + "=" + headingIndex + " and " +
                        WIND_SPEED_BIN_INDEX_COLUMN + "=" + speedBinIndex;
        
        return query(clause).get(0);
    }
    
    /**
     * Retrieve the list of bins for a heading and date.
     * 
     * @param date The date to query for
     * @param headingIndex The heading index for which to query
     * @return The list of durations
     */
    public List<SpeedBinDuration> retrieveDurationsForSlice(LocalDate date, int headingIndex) {     
        String clause = " where " +
                        DATE_COLUMN + "='" + DBTable.dateFormatter().format(date) + "' and " +
                        WIND_HEADING_INDEX_COLUMN + "=" + headingIndex;
        
        return query(clause);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<SpeedBinDuration> query(String clause) {
        String sql = "select * from " + TABLE_NAME;
        
        if (clause != null)
            sql += clause;
        
        List<SpeedBinDuration> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            int windSpeedBinIndex = rs.getInt(WIND_SPEED_BIN_INDEX_COLUMN);
            LocalDate date = rs.getDate(DATE_COLUMN).toLocalDate();
            int headingIndex = rs.getInt(WIND_HEADING_INDEX_COLUMN);
            Duration duration = Duration.ofSeconds(rs.getInt(DURATION_COLUMN));
            int speedBinIndex = rs.getRow() - 1;
            
            SpeedBinDuration rec = new SpeedBinDuration(speedBinIndex, headingIndex, date, duration);
            
            return rec;
        });

        return list;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(SpeedBinDuration row) {
        String sql = "insert into " + TABLE_NAME + " values(?,?,?,?)";

	try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
	    int n = 1;

	    stmt.setDate(n++,  java.sql.Date.valueOf(row.getDate()));
	    stmt.setInt(n++, row.getHeadingIndex());
	    stmt.setInt(n++, row.getWindSpeedBinIndex());
	    stmt.setLong(n++, row.getDuration().getSeconds());
	    return stmt.executeUpdate() == 1;
	}
	catch (SQLException e) {
	    logger.log(Level.SEVERE, "Caught SQL Exception", e);
	    return false;
	}
    }
}