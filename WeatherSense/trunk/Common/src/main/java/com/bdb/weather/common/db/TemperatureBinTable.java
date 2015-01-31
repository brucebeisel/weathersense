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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.ThresholdType;
import com.bdb.weather.common.measurement.Temperature;

/**
 * The temperature bin table contains ranges of temperatures for which durations will be calculated.
 * This allows the user to see how long the temperature was above 70 degrees or below 0 degrees.
 * 
 * @author Bruce
 *
 */
public class TemperatureBinTable extends DBTable<TemperatureBin> {
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".temperature_bins";
    
    private static final String BIN_ID_COLUMN = "bin_id";
    private static final String BIN_TYPE_COLUMN = "bin_type";
    private static final String BIN_THRESHOLD_COLUMN = "threshold";
    private static final Logger logger = Logger.getLogger(TemperatureBinTable.class.getName());
    
    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public TemperatureBinTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
    
    /**
     * Retrieve the temperature bins for a weather station.
     * 
     * @return The temperature bins from the database
     */
    public List<TemperatureBin> retrieveBins() {
        List<TemperatureBin> list = query();
        return list;
    }
    
    /**
     * Save the temperature bins to the database.
     * 
     * @param bins The list of temperature bins to store in the database
     * @return True if the bins were added successfully 
     */
    public boolean saveBins(Collection<TemperatureBin> bins) {
        boolean success = true;
        String sql = "delete from " + TABLE_NAME;

        if (getConnection().executeUpdate(sql) != -1) {
            if (bins.size() > 0) {
                for (TemperatureBin bin : bins)
                    success = success && addRow(bin);
            }
        }
        else
            success = false;
        
        return success;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(TemperatureBin row) {
        boolean success = false;
        logger.fine("Adding row to TemperatureBin table: " + row);
        
        try {
            String stmtString = "insert into " + TABLE_NAME + " values(?,?,?)";
            PreparedStatement stmt = getConnection().getConnection().prepareStatement(stmtString);
            stmt.setInt(1, row.getBinId());
            stmt.setString(2, row.getThresholdType().toString());
            stmt.setDouble(3, row.getThreshold().get(DatabaseUnits.TEMPERATURE));

            success = executeAddRow(stmt);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return success;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<TemperatureBin> query(String clause) {
       String sql = "select * from " + TABLE_NAME + " ";
        
        if (clause != null)
            sql += clause;
        
        List<TemperatureBin> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            int binId = rs.getInt(BIN_ID_COLUMN);
            String typeString = rs.getString(BIN_TYPE_COLUMN);
            double threshold = rs.getDouble(BIN_THRESHOLD_COLUMN);
            
            ThresholdType thresholdType = ThresholdType.valueOf(typeString);
            
            TemperatureBin bin = new TemperatureBin(binId, thresholdType, new Temperature(threshold, DatabaseUnits.TEMPERATURE));
            
            return bin;
       });
        
        Collections.sort(list);
 
        return list;
    }
}