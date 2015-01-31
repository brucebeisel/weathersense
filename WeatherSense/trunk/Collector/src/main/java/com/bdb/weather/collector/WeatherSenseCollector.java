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
package com.bdb.weather.collector;

import com.bdb.weather.collector.wunderground.WeatherUnderground;
import com.bdb.weather.collector.socket.SocketReaderThread;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.bdb.weather.common.db.DatabaseConstants;

/**
 * Both the main() and the Remote Object
 *
 * @author Bruce
 * @since 1.0
 *
 */
public final class WeatherSenseCollector {
    /**
     * The main program for the WeatherSense Collector
     *
     * @param args The command line arguments
     */
    public static void main(String args[]) {
        Logger logger = Logger.getLogger(WeatherSenseCollector.class.getName());

        String dbHost = "localhost";

        if (args.length > 0)
            dbHost = args[0];

        int dbPort = Integer.parseInt(DatabaseConstants.DATABASE_PORT);

        if (args.length > 1)
            dbPort = Integer.parseInt(args[1]);

        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            Properties properties = new Properties();
            is = ClassLoader.getSystemClassLoader().getResourceAsStream("com/bdb/weathersense/weathersense_collector.properties");

            if (is != null)
                properties.load(is);

            String findMissingDataProp = properties.getProperty("weathersense.find_missing_data", "false");

            boolean findMissingData = findMissingDataProp.equals("true");

            String dbName = properties.getProperty("weathersense.dbname", DatabaseConstants.DATABASE_NAME);

            String dbUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, dbHost, dbPort, dbName);

            CollectorDbWriter writer = new CollectorDbWriter(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);
            WeatherUnderground weatherUnderground = new WeatherUnderground();
            MessageProcessor processor = new MessageProcessor(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD, writer, weatherUnderground);
            SocketReaderThread socketReaderThread = new SocketReaderThread(CollectorConstants.COLLECTOR_PORT, processor);
            DopplerRadarManager doppler = new DopplerRadarManager(writer);
            DataMonitor dataMonitor = new DataMonitor(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD, doppler, weatherUnderground, writer);

            //
            // Start the socket reader for the name/value interface
            //
            writer.init(socketReaderThread, findMissingData);
            dataMonitor.init();
            socketReaderThread.start();
        }
        catch (IOException | SecurityException | NumberFormatException | SQLException | JAXBException e) {
            logger.log(Level.SEVERE, "Exception during initialization. Exiting", e);
            System.exit(1);
        }
    }
}
