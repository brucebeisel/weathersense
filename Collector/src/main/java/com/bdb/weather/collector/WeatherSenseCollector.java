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
package com.bdb.weather.collector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.bdb.weather.collector.socket.SocketReaderThread;
import com.bdb.weather.common.db.DatabaseConstants;

/**
 * The class containing main for the WeatherSense collector.
 *
 * @author Bruce
 * @since 1.0
 *
 */
final class WeatherSenseCollector {
    private static final String LOGGING_PROPERTY_FILE = "collector_logging.properties";
    private static final String COLLECTOR_PROPERTY_FILE = "com/bdb/weathersense/weathersense_collector.properties";
    private static final String DATABASE_HOST_PROPERTY = "weathersense.database-host";
    //private static final String DATABASE_PORT_PROPERTY = "weathersense.database-port";
    private static final String HELP_ARG = "-h";

    /**
     * The main program for the WeatherSense Collector
     *
     * @param args The command line arguments
     */
    public static void main(String args[]) {
        Logger logger = Logger.getLogger(WeatherSenseCollector.class.getName());

        try {
            //
            // Load the resources first. The configuration precedence is:
            //    1. Property file
            //    2. Command line argument 
            //    3. Defaults
            //
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(LOGGING_PROPERTY_FILE);

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            Properties properties = new Properties();
            is = ClassLoader.getSystemClassLoader().getResourceAsStream(COLLECTOR_PROPERTY_FILE);

            if (is != null)
                properties.load(is);


            String dbServer = DatabaseConstants.DATABASE_SERVER;
            String dbHost = DatabaseConstants.DATABASE_HOST;
            String dbName = DatabaseConstants.DATABASE_NAME;
            String dbPort = DatabaseConstants.DATABASE_PORT;

            if (args.length > 0 && args[0].equals(HELP_ARG)) {
                System.out.println("Usage: java WeatherSenseCollector [database server [database host [database port]]]");
                System.exit(0);
            }
               
            if (args.length > 0)
                dbServer = args[0];

            if (args.length > 1)
                dbHost = args[1];

            if (args.length > 2)
                dbPort = args[2];

            dbHost = properties.getProperty(DATABASE_HOST_PROPERTY, dbHost);

            //String prop = properties.getProperty(DATABASE_PORT_PROPERTY, dbPort);

            String dbUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, dbServer, dbHost, dbPort, dbName);

            String findMissingDataProp = properties.getProperty("weathersense.find_missing_data", "false");

            boolean findMissingData = findMissingDataProp.equals("true");

            CollectorDbWriter writer = new CollectorDbWriter(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);
            MessageProcessor processor = new MessageProcessor(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD, writer);
            SocketReaderThread socketReaderThread = new SocketReaderThread(CollectorConstants.COLLECTOR_PORT, processor);
            DopplerRadarManager doppler = new DopplerRadarManager(writer);
            DataMonitor dataMonitor = new DataMonitor(dbUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD, doppler, writer);

            //
            // Start the socket reader for the XML interface
            //
            if (!writer.init(socketReaderThread, findMissingData)) {
                logger.log(Level.SEVERE, "Failed to initialize database writer");
                System.exit(1);
            }
            dataMonitor.init();
            socketReaderThread.start();
        }
        catch (IOException | SecurityException | NumberFormatException | SQLException | JAXBException e) {
            logger.log(Level.SEVERE, "Exception during initialization. Exiting", e);
            System.exit(2);
        }
    }
}
