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
package com.bdb.weather.admin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.collector.Summarizer;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.WeatherStationTable;

public class AdminConsole {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
        
        DBConnection connection = new DBConnection(
                String.format(DatabaseConstants.DATABASE_URL_FORMATTER,
                              DatabaseConstants.DATABASE_HOST,
                              DatabaseConstants.DATABASE_PORT,
                              DatabaseConstants.DATABASE_NAME),
                DatabaseConstants.DATABASE_DRIVER,
                DatabaseConstants.DATABASE_USER,
                DatabaseConstants.DATABASE_PASSWORD);
        
        if (!connection.connect()) {
            JOptionPane.showMessageDialog(null, "Could not connect to database", "", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        WeatherStationTable wsTable = new WeatherStationTable(connection);
        HistoryTable historyTable = new HistoryTable(connection, null);
        
        WeatherStation station = wsTable.getWeatherStation();
        
        
        if (station != null)
            ; // TODO display dialog
        else {
            JOptionPane.showMessageDialog(null, "No Weather Stations not configured", "", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        Summarizer summarizer = new Summarizer(connection, station.getWindParameters());
        
        new HistoricalDataEditor(station, summarizer, historyTable);

    }
}
