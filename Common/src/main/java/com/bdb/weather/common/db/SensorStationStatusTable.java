/* 
 * Copyright (C) 2016 Bruce Beisel
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
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.BadBattery;

/**
 * Class that provides access to the sensor station status table.
 *
 * @author Bruce
 */
public class SensorStationStatusTable extends DBTable<SensorStationStatus> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".sensor_station_status";
    private static final String SENSOR_ID_COLUMN = "sensor_station_id";
    private static final String TIME_COLUMN = "time";
    private static final String BATTERY_VOLTAGE_COLUMN = "battery_voltage";
    private static final String BATTERY_OK_COLUMN = "battery_ok";
    private static final String LINK_QUALITY_COLUMN = "link_quality_percentage";
    private static final Logger logger = Logger.getLogger(SensorStationStatusTable.class.getName());
    
    /**
     * Constructor.
     * 
     * @param connection The connection to the database server
     */
    public SensorStationStatusTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<SensorStationStatus> query(String clause) {
        String  sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<SensorStationStatus> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            int sensorStationId = rs.getInt(SENSOR_ID_COLUMN);
            LocalDateTime time = rs.getTimestamp(TIME_COLUMN).toLocalDateTime();
            
            Float voltageObject;
            float voltage = rs.getFloat(BATTERY_VOLTAGE_COLUMN);
            if (rs.wasNull())
                voltageObject = null;
            else
                voltageObject = voltage;
            
            Boolean batteryOkObject;
            boolean batteryOk = rs.getBoolean(BATTERY_OK_COLUMN);
            if (rs.wasNull())
                batteryOkObject = null;
            else
                batteryOkObject = batteryOk;
            
            Integer linkQualityObject;
            int linkQuality = rs.getInt(LINK_QUALITY_COLUMN);
            if (rs.wasNull())
                linkQualityObject = null;
            else
                linkQualityObject = linkQuality;
            
            return new SensorStationStatus(sensorStationId, time, voltageObject, batteryOkObject, linkQualityObject);
        });
        
        return list;
    }
    
    @Override
    public boolean addRow(SensorStationStatus record) {
        String sql = "insert into " + TABLE_NAME + " values (?,?,?,?,?)";
                                      
        try {
            int rv;
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
                stmt.setInt(1, record.getSensorStationId());
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(record.getTime()));

                Float v = record.getBatteryVoltage();
                if (v != null)
                    stmt.setFloat(3, v);
                else
                    stmt.setNull(3, Types.FLOAT);

                Boolean b = record.isBatteryOk();
                if (b != null)
                    stmt.setBoolean(4, b);
                else
                    stmt.setNull(4, Types.BOOLEAN);

                Integer i = record.getLinkQuality();
                if (i != null)
                    stmt.setInt(5, i);
                else
                    stmt.setNull(5, Types.INTEGER);

                rv = stmt.executeUpdate();
            }
            return rv == 1;
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, "add row failed", ex);
        }
        return false;
    }

    /**
     * Get the newest status for all of the sensor stations.
     * 
     * @return The list of sensor status statuses
     */
    public List<SensorStationStatus> getLatestSensorStationStatus() {
        String sql = "select max(" + TIME_COLUMN + ") from " + TABLE_NAME;
                     
        List<LocalDateTime> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDateTime time = null;
            java.sql.Timestamp ts = rs.getTimestamp(1);
            if (ts != null)
                time = ts.toLocalDateTime();
            return time;
        });

        if (list.isEmpty())
            return null;

        LocalDateTime time = list.get(0);

        String whereClause = " where " + TIME_COLUMN + "='" + DBTable.dateTimeFormatter().format(time) + "'";
        List<SensorStationStatus> statusList = query(whereClause);

        return statusList;
    }

    /**
     * Get the station status records where the battery was first detected to not be OK.
     * 
     * @return The list of sensor station records where the battery is not OK
     */
    public List<BadBattery> retrieveBadBatteryRecords() {
        String whereClause = " where " + BATTERY_OK_COLUMN + "=false";
        List<SensorStationStatus> candidateStatusList = query(whereClause);
        List<BadBattery> statusList = new ArrayList<>();
        if (candidateStatusList.isEmpty())
            return statusList;

        //
        // Trim the list to just the records when the battery first went bad.
        //
        statusList.add(new BadBattery(candidateStatusList.get(0).getSensorStationId(), candidateStatusList.get(0).getTime()));
        for (int i = 1; i < candidateStatusList.size(); i++) {
            SensorStationStatus first = candidateStatusList.get(i - 1);
            SensorStationStatus second = candidateStatusList.get(i);

            if (first.getSensorStationId() != second.getSensorStationId())
                statusList.add(new BadBattery(second.getSensorStationId(), second.getTime()));
            else {
                Duration d = Duration.between(first.getTime(), second.getTime());
                if (d.getSeconds() > 86400)
                    statusList.add(new BadBattery(second.getSensorStationId(), second.getTime()));
                else
                    statusList.get(statusList.size() - 1).incrementMeasurementCount(second.getTime());
            }
        }
        return statusList;
    }

    /**
     * Update the sensor station status for each sensor station in the list.
     * 
     * @param list The list of sensor station status
     */
    public void updateSensorStationStatus(List<SensorStationStatus> list) {
        getConnection().startTransaction();
        list.stream().forEach((sensorStationStatus) -> {
            addRow(sensorStationStatus);
        });
        getConnection().endTransaction();
    }
}