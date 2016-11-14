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
package com.bdb.weather.filereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bdb.util.ThreadUtils;
import com.bdb.weather.collector.CollectException;
import com.bdb.weather.collector.Collector;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;

/**
 * Class that controls the reading of the weather history file.
 * 
 * @author Bruce
 * @since 1.0
 */
public class WeatherFileReader
{
    private HistoryFileReader	m_reader;
    private File	        m_file;
    private String		m_model;
    private Collector           m_collector;
    private String		m_collectorName;
    private Registry            m_registry;
    private int                 m_registryPort;
    private static final Logger m_logger = Logger.getLogger(WeatherFileReader.class.getName());
    private SimpleDateFormat    m_sdf = new SimpleDateFormat();
    private static final long	COLLECT_DELAY_MILLIS = 15000;
    private static final long	CONNECT_DELAY_MILLIS = 10000;

    /**
     * Constructor
     * 
     * @param filename The file to read
     * @param model The model of the weather station, this will be used to create a HistoryFileReader
     * @param collectorName The name of the collector to connect to using RMI
     * @param registryPort The registry port to use to find the RMI object
     * @throws FileNotFoundException The history file was not found
     */
    public WeatherFileReader(String filename, String model, String collectorName, int registryPort) throws FileNotFoundException
    {
        m_logger.fine("Constructing. File = " + filename + " Model = " + model + " RMI Object = " + collectorName);

        m_file = new File(filename);
        m_model = model;
        m_reader = WeatherFileReaderFactory.weatherFileReader(model, new File(filename));
        m_collectorName = collectorName;
        m_registryPort = registryPort;

    }

    public String getModel()
    {
        return m_model;
    }

    public File getFile()
    {
        return m_file;
    }

    /**
     * Connect to the WeatherSense collector using RMI
     */
    private void connectToCollector()
    {
        while (true)
        {
            try
            {
                //
                // If we don't have an RMI registry yet, create one.
                //
                if (m_registry == null)
                    m_registry = LocateRegistry.getRegistry(m_registryPort);

                m_logger.info("Attempting to find collector remote object");
                m_collector = (Collector)m_registry.lookup(m_collectorName);
                m_logger.info("Found remote object");
                return;
            }
            catch (RemoteException e1)
            {
                m_logger.log(Level.SEVERE, "Could not find WeatherSense remote object", e1);
            }
            catch (NotBoundException e2)
            {
                m_logger.log(Level.SEVERE, "Could not find WeatherSense remote object", e2);
            }

            m_logger.info("Waiting 10 seconds before next remote object lookup");

            ThreadUtils.sleep(CONNECT_DELAY_MILLIS);

        }
    }

    /**
     * Send the current weather to the collector
     * 
     * @param rec A historical record (this needs to be changed to a current weather record)
     * @throws RemoteException Error communicating with the collector
     * @throws CollectException The collector reported an error
     */
    private void sendCurrentWeather(HistoricalRecord rec) throws RemoteException, CollectException
    {
        CurrentWeather weather = new CurrentWeather();
        weather.setIndoorHumidity(rec.getIndoorHumidity());
        weather.setIndoorTemperature(rec.getIndoorTemperature());
        weather.setOutdoorHumidity(rec.getOutdoorHumidity());
        weather.setOutdoorTemperature(rec.getAvgOutdoorTemperature());
        weather.setTime(rec.getTime());
        weather.setWind(rec.getAvgWind());
        m_logger.info("Sending current weather data");
        m_collector.setCurrentWeather(weather);
    }

    /**
     * The main loop of the program
     */
    public void mainLoop()
    {
        //
        // This forever loops keeps the main loop going even when an exception is caught
        //
        while (true)
        {
            try
            {
                //
                // Connect to the collector and get the date of the newest record in its database.
                // The file reader will send all data that is in the history file that is older than
                // this date.
                //
                connectToCollector();

                Calendar lastDataTime = m_collector.getNewestHistoricalDataTime();;

                if (lastDataTime != null)
                    m_logger.fine("Last data time is " + m_sdf.format(lastDataTime.getTime()));
                else
                    m_logger.fine("Database is empty");

                m_reader.openHistoryFile();

                HistoricalRecord record = m_reader.readNextRecord(lastDataTime);

                //
                // This forever loop keeps reading the file until an exception is thrown
                //
                while (true)
                {
                    if (record == null)
                    {
                        m_logger.finer("Waiting for " + COLLECT_DELAY_MILLIS / 1000 + " seconds before attempting to read history file again");
                        m_reader.closeHistoryFile();
                        ThreadUtils.sleep(COLLECT_DELAY_MILLIS);
                        m_reader.openHistoryFile();
                    }
                    else
                    {
                        m_logger.info("Sending record: " + record);
                        m_collector.addHistoricalRecord(record);
                        //
                        // TODO This is a temporary solution, load the current weather from the historical record
                        //
                        sendCurrentWeather(record);
                    }

                    record = m_reader.readNextRecord();
                }
            }
            catch (Exception e)
            {
                m_logger.log(Level.WARNING, "Caught exception during main loop. Discarding remote object and resetting", e);
            }
            finally
            {
                m_collector = null;
                m_registry = null;
                m_reader.closeHistoryFile();
            }
        }
    }

    /**
     * Main program for the WeatherSense file reader. This program will read any file created by a weather station
     * provided there is a reader for the weather station model.
     * 
     * @param args The arguments to the program
     */
    public static final void main(String args[])
    {
        if (args.length < 2)
        {
            System.err.println("Usage: WeatherFileReader <weather station model> <history file> [collector name] [RMI registry port]");
            System.exit(1);
        }

        try
        {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("reader_logging.properties");

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);
            
            String collectorName = Collector.COLLECTOR_NAME;
            int registryPort = Registry.REGISTRY_PORT;
            
            if (args.length > 3)
                collectorName = args[3];
            
            if (args.length > 4)
                registryPort = Integer.parseInt(args[4]);

            WeatherFileReader reader = new WeatherFileReader(args[1], args[0], collectorName, registryPort);

            reader.mainLoop();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Could not find weather file '" + args[2] + "'");
            System.exit(1);
        }
        catch (IOException e2)
        {
            System.out.println("Could not open log properties file");
        }
    }
}
