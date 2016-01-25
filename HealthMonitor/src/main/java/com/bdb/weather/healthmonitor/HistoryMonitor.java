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
package com.bdb.weather.healthmonitor;

import com.bdb.piglow4j.PiGlowLED;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBTable;
import com.bdb.weather.common.db.DatabaseConstants;
import java.util.List;

/**
 *
 * @author Bruce Beisel
 */
public class HistoryMonitor implements HealthMonitor {
    private static final String MONITOR_NAME = "History Monitor";
    private final String host;
    private Connection connection = null;
    private PreparedStatement historyStatement;
    private PreparedStatement sensorStationStatusStatement;
    private final int toleranceSeconds;
    private final List<PiGlowLED> leds;
    private final String HISTORY_SQL = "select max(date) from " + DatabaseConstants.DATABASE_NAME + ".history";
    private final String SENSOR_STATION_MAX_TIME_SQL = "select max(time) from " + DatabaseConstants.DATABASE_NAME + ".sensor_station_status";
    private static final Logger logger = Logger.getLogger(HistoryMonitor.class.getName());

    public static HistoryMonitor createHistoryMonitor(String host, List<PiGlowLED> leds, int toleranceMinutes) {
        HistoryMonitor monitor = new HistoryMonitor(host, leds, toleranceMinutes);
        if (monitor.init(host))
            return monitor;
        else
            return null;
    }

    private HistoryMonitor(String host, List<PiGlowLED> leds, int toleranceMinutes) {
        this.host = host;
	this.leds = leds;
        this.toleranceSeconds = toleranceMinutes * 60;
    }

    private boolean init(String host) {
        try {
            String url = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, host, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);
            connection = DriverManager.getConnection(url, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);
            historyStatement = connection.prepareStatement(HISTORY_SQL);
            sensorStationStatusStatement = connection.prepareStatement(SENSOR_STATION_MAX_TIME_SQL);
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to open database", ex);
            return false;
        }

        return true;
    }

    private boolean checkHistory() throws SQLException {
        logger.fine("Checking if history table is up to date");
        try (ResultSet rs = historyStatement.executeQuery()) {
            if (!rs.first()) {
                logger.warning("Failed to query history table");
                throw new SQLException("Unexpected empty history table");
            }

            Timestamp ts = rs.getTimestamp(1);
            LocalDateTime time = ts.toLocalDateTime();
            logger.log(Level.FINE, "History table time: {0}", DBTable.dateTimeFormatter().format(time));
            LocalDateTime now = LocalDateTime.now();
            Duration delta = Duration.between(time, now);
	    long minutes = delta.toMinutes();
	    leds.forEach((led)->led.setIntensity(0));
	    for (int i = 0; i < leds.size(); i++) {
		if (Math.pow(2.0, i) <= minutes)
		    leds.get(i).setIntensity(PiGlowLED.MAX_INTENSITY / 2);
	    }
            return delta.getSeconds() < toleranceSeconds;
        }
    }

    private boolean checkBatteries() throws SQLException {
        logger.fine("Checking if batteries are good");
        LocalDateTime time = LocalDateTime.now();
        try (ResultSet rs = sensorStationStatusStatement.executeQuery()) {
            if (!rs.first()) {
                logger.warning("Failed to query sensor station status table");
                throw new SQLException("Unexpected empty sensor station status table");
            }

            Timestamp ts = rs.getTimestamp(1);
            time = ts.toLocalDateTime();
        }


        String select = "select sensor_station_id,battery_ok from " + DatabaseConstants.DATABASE_NAME + ".sensor_station_status" +
                        " where time='" + DBTable.dateTimeFormatter().format(time) + "'";


        boolean batteriesOK = true;
        try (PreparedStatement selectStatement = connection.prepareStatement(select);
             ResultSet rs = selectStatement.executeQuery()) {
            if (!rs.first()) {
                logger.warning("Failed to query sensor station status table");
                throw new SQLException("Unexpected empty sensor station status table");
            }

            do {
                int sensorStationId = rs.getInt(1);
                boolean batteryOK = rs.getBoolean(2);
                batteriesOK = batteriesOK && batteryOK;

                if (!batteryOK) 
                    logger.log(Level.INFO, "Battery for sensor station {0} is going bad", sensorStationId);
                else
                    logger.log(Level.FINE, "Battery for sensor station {0} is good", sensorStationId);
            } while (rs.next());
        }

        return batteriesOK;
    }

    @Override
    public boolean isHealthy() {
        try {
            if (connection == null)
                init(host);
            
            boolean historyHealth = checkHistory();
            boolean batteryHealth = checkBatteries();
            
            return historyHealth && batteryHealth;
        }
        catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, "Failed to get history or battery status", ex);
                connection.close();
                connection = null;
                return false;
            }
            catch (SQLException ex1) {
                logger.log(Level.INFO, "Failed to close database connection", ex1);
            }
        }

        return false;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    public String getMailMessage() {
        return null;
    }
}
