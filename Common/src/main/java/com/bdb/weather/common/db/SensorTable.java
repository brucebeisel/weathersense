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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorType;

public class SensorTable extends DBTable<Sensor> {
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".sensors";
    private static final String SENSOR_ID_COLUMN = "sensor_id";
    private static final String NAME_COLUMN = "name";
    private static final String TYPE_COLUMN = "type";
    private static final Logger logger = Logger.getLogger(SensorTable.class.getName());
    
    public SensorTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<Sensor> query(String clause) {
        String  sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<Sensor> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            String name = rs.getString(NAME_COLUMN);
            SensorType type = SensorType.fromCode(rs.getString(TYPE_COLUMN));
            int sensorId = rs.getInt(SENSOR_ID_COLUMN);
            
            return new Sensor(sensorId, type, name);
        });
        
        return list;
    }
    
    @Override
    public boolean addRow(Sensor sensor) {
        String sql = "insert into " + TABLE_NAME +
                     " (" + SENSOR_ID_COLUMN + "," + TYPE_COLUMN + "," + NAME_COLUMN + ") " +
                     "values (?,?,?)";
                                      
        try {
            PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql);
            stmt.setInt(1, sensor.getSensorId());
            stmt.setString(2, sensor.getType().getCode());
            stmt.setString(3, sensor.getName());
            int rv = stmt.executeUpdate();
            return rv == 1;
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, "add row failed", ex);
        }
        return false;
    }
    
    @Override
    public boolean updateRow(Sensor sensor) {
            String sql = "update " + TABLE_NAME + " set " +
                                      NAME_COLUMN + "=?," +
                                      TYPE_COLUMN + "=?" +
                                      " where " + SENSOR_ID_COLUMN + "=?";
        try {
            PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql);
            stmt.setString(1, sensor.getName());
            stmt.setString(2, sensor.getType().getCode());
            stmt.setInt(3, sensor.getSensorId());
            int rv = stmt.executeUpdate();
            return rv == 1;
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, "Update row failed", ex);
        }
        return false;
    }
    
    /**
     * Update all of the sensors in the list, inserting or modifying.
     * 
     * @param sensorList The list containing the sensors.
     */
    public void updateSensorList(List<Sensor> sensorList) {
        getConnection().startTransaction();
        sensorList.stream().filter((sensor) -> (!recordExists(sensor.getSensorId()))).forEach((sensor) -> {
            addRow(sensor);
        });
        getConnection().endTransaction();
    }
    
    /**
     * Convenience method to get all of the sensors.
     * 
     * @return The list of sensors
     */
    public List<Sensor> getSensorList() {
        return query(null);
    }
    
    /**
     * See if a specific summary record exists for a weather station.
     *
     * @param date The date for which to check for existence
     * @return Whether the record exists
     */
    private boolean recordExists(int sensorId) {
        String sql = "select * from " + TABLE_NAME + " where " + SENSOR_ID_COLUMN + "=" + sensorId;

        List<Boolean> list = executeQuery(sql, (ResultSet rs, Object... args) -> true);

        if (list.size() > 0)
            return list.get(0);
        else
            return false;
    }
}
