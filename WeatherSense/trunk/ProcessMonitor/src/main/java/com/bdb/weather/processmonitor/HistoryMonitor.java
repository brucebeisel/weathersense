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
package com.bdb.weather.processmonitor;

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

import com.bdb.weather.common.db.DatabaseConstants;

/**
 *
 * @author Bruce Beisel
 */
public class HistoryMonitor implements HealthMonitor {
    private Connection connection;
    private PreparedStatement statement;
    private int toleranceSeconds;
    private final String SQL = "select max(date) from " + DatabaseConstants.DATABASE_NAME + ".history";
    private static final Logger logger = Logger.getLogger(HistoryMonitor.class.getName());

    public static HistoryMonitor createHistoryMonitor(String host, int toleranceMinutes) {
        HistoryMonitor monitor = new HistoryMonitor(toleranceMinutes);
        if (monitor.init(host))
            return monitor;
        else
            return null;
    }

    private HistoryMonitor(int toleranceMinutes) {
        this.toleranceSeconds = toleranceMinutes * 60;
    }

    private boolean init(String host) {
        try {
            String url = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, host, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);
            connection = DriverManager.getConnection(url, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);
            statement = connection.prepareStatement(SQL);
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to open database", ex);
            return false;
        }

        return true;
    }

    @Override
    public boolean isHealthy() {
        try (ResultSet rs = statement.executeQuery()) {
            if (!rs.first())
                return false;

            Timestamp ts = rs.getTimestamp(1);
            LocalDateTime time = ts.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            Duration delta = Duration.between(time, now);
            return delta.getSeconds() < toleranceSeconds;

        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to query history table for max date");
            return false;
        }
    }
}