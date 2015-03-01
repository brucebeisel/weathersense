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
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.Storm;
import com.bdb.weather.common.measurement.Depth;

/**
 * Class that interfaces with the storm SQL table.
 *
 * @author Bruce
 */
public class StormTable extends DBTable<Storm> {
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".storms";
    private static final String STORM_START_COLUMN = "storm_start";
    private static final String STORM_END_COLUMN = "storm_end";
    private static final String TOTAL_RAINFALL_COLUMN = "total_rainfall";
    private static final Logger logger = Logger.getLogger(StormTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public StormTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<Storm> query(String clause) {
        String  sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<Storm> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDateTime startTime = rs.getTimestamp(STORM_START_COLUMN).toLocalDateTime();
            Timestamp ts = rs.getTimestamp(STORM_END_COLUMN);
            LocalDateTime endTime = null;
            if (ts != null)
                endTime = ts.toLocalDateTime();

            double rv = rs.getDouble(TOTAL_RAINFALL_COLUMN);
            
            return new Storm(startTime, endTime, new Depth(rv, DatabaseUnits.DEPTH));
        });
        
        return list;
    }

    /**
     * Retrieve the newest storm from the database.
     * 
     * @return The latest storm
     */
    public Storm retrieveLatestStorm() {
        List<Storm> storms = query(" order by " + STORM_START_COLUMN);
        if (storms.isEmpty())
            return null;
        else
            return storms.get(storms.size() - 1);
    }

    @Override
    public boolean addRow(Storm record) {
        String sql = "insert into " + TABLE_NAME + " values (?,?,?)";
                                      
        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(record.getStartTime()));
            if (record.getEndTime() != null)
                stmt.setTimestamp(2, Timestamp.valueOf(record.getEndTime()));
            else
                stmt.setNull(2, Types.TIMESTAMP);

            stmt.setDouble(3, record.getStormRainfall().get(DatabaseUnits.DEPTH));

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving storm data.", e);
            return false;
        }
    }

    @Override
    public boolean updateRow(Storm record) {
        String sql = "update " + TABLE_NAME + " set " + STORM_END_COLUMN + "=?," + TOTAL_RAINFALL_COLUMN + "=? where " + STORM_START_COLUMN + "=?";

        try {
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
                if (record.getEndTime() != null)
                    stmt.setTimestamp(1, Timestamp.valueOf(record.getEndTime()));
                else
                    stmt.setNull(1, Types.TIMESTAMP);

                stmt.setDouble(2, record.getStormRainfall().get(DatabaseUnits.DEPTH));
                stmt.setTimestamp(3, Timestamp.valueOf(record.getStartTime()));
                return stmt.executeUpdate() == 1;
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving storm data.", e);
            return false;
        }
    }

    public boolean saveStorm(Storm storm) {
        if (!recordExists(storm.getStartTime()))
            return addRow(storm);
        else
            return updateRow(storm);
    }

    /**
     * Check if a storm with the specified start date already exists in the database.
     * 
     * @param startTime The start time of the storm to check for existence
     * @return True if the storm exists, else false.
     */
    private boolean recordExists(LocalDateTime startTime) {
        String clause = "where " + STORM_START_COLUMN + "='" + DBTable.dateTimeFormatter().format(startTime) + "'";
        List<Storm> storms = query(clause);
        return !storms.isEmpty();
    }
}