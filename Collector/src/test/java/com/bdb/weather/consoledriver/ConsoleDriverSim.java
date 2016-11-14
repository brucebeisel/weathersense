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
package com.bdb.weather.consoledriver;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.bdb.util.Pair;
import com.bdb.util.ThreadUtils;

import com.bdb.weather.collector.CollectorConstants;
import com.bdb.weather.collector.messages.SensorMessage;
import com.bdb.weather.collector.messages.SensorStationStatusMessage;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.messages.WsParametersMessage;

/**
 *
 * @author Bruce
 */
public class ConsoleDriverSim {
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;
    private final WsCollectorClient client;
    private Thread thread;
    private boolean exit;
    private boolean sensorMessageSent = true;
    private LocalDateTime newestRecordTime;
    private final DataGenerator dataGen = new DataGenerator();
    private static final Logger logger = Logger.getLogger(ConsoleDriverSim.class.getName());
    
    /**
     *
     * @param client
     * @throws JAXBException
     */
    public ConsoleDriverSim(WsCollectorClient client) throws JAXBException {
        this.client = client;
        JAXBContext jaxbContext = JAXBContext.newInstance(com.bdb.weather.common.CurrentWeather.class,
                                                          com.bdb.weather.common.measurement.LeafWetness.class,
                                                          com.bdb.weather.common.measurement.SoilMoisture.class,
                                                          com.bdb.weather.collector.messages.SensorMessage.class,
                                                          com.bdb.weather.collector.messages.SensorStationMessage.class,
                                                          com.bdb.weather.collector.messages.SensorStationStatusMessage.class,
                                                          com.bdb.weather.common.messages.WsParametersMessage.class,
                                                          com.bdb.weather.common.GeographicLocation.class,
                                                          com.bdb.weather.common.HistoricalRecord.class);
        
        marshaller = jaxbContext.createMarshaller();
        unmarshaller = jaxbContext.createUnmarshaller();
    }
    
    private void simLoop() {
        exit = false;

        while (!exit) {
            if (!client.isConnected()) {
                sensorMessageSent = false;
                Pair<Boolean,LocalDateTime> connectResult = client.connect();

                //
                // first == true mean that we are connected to the collector.
                // first == true and second == null means that we were already connected
                // first == true and second != null means that we just connected and received a lastest record response
                //
                if (!connectResult.first)
                    ThreadUtils.sleep(1000);
                else if (connectResult.second != null) {
                    sensorMessageSent = false;
                    newestRecordTime = connectResult.second;
                }
            }

            if (client.isConnected()) {
                if (!sensorMessageSent) {
                    WsParametersMessage wsMessage = dataGen.getWeatherStationParametersMessage();
                    sendMessage(wsMessage);
                    SensorMessage msg = dataGen.getSensorMessage();
                    sendMessage(msg);
                    sensorMessageSent = true;
                }
                logger.fine("Sending current weather");
                CurrentWeather cw = dataGen.currentWeather();
                sendMessage(cw);

                HistoricalRecord record;
                do {
                    record = dataGen.getNextHistoricalRecord(newestRecordTime);
                    if (record != null) {
                        logger.log(Level.FINE, "Sending historical with time {0}", SimpleDateFormat.getDateTimeInstance().format(record.getTime()));
                        if (!sendMessage(record))
                            break;
                        newestRecordTime = record.getTime();
                    }
                } while (record != null);

                ThreadUtils.sleep(5000);
            }
        }
    }
    
    /**
     *
     */
    public void start() {
        thread = new Thread(() -> simLoop());
        thread.start();
    }
    
    /**
     *
     */
    public void stop() {
        exit = true;
        try {
            thread.join();
        }
        catch (InterruptedException ex) {
            logger.log(Level.INFO, "Interrupted while joining the thread", ex);
        }
    }    
    
    private boolean sendMessage(Object msg) {
        try {
            logger.log(Level.FINE, "Sending message: {0}", msg.getClass().getName());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(msg, System.out);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            StringWriter sw = new StringWriter();
            marshaller.marshal(msg, sw);
            return client.sendMessage(sw.toString());

        }
        catch (JAXBException e) {
            logger.log(Level.SEVERE, "Could not marshal the message", e);
            return false;
        }
    }
    
    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            LogManager.getLogManager().readConfiguration(ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties"));
            WsCollectorClient client = new WsCollectorClient(CollectorConstants.COLLECTOR_PORT);
            ConsoleDriverSim sim = new ConsoleDriverSim(client);

            LocalDateTime c = LocalDateTime.now().minusMinutes(10);
            List<SensorStationStatus> list = new ArrayList<>();
            list.add(new SensorStationStatus(1, LocalDateTime.now(), null, null, null));
            list.add(new SensorStationStatus(2, LocalDateTime.now(), null, true, null));
            list.add(new SensorStationStatus(3, LocalDateTime.now(), null, true, 90));
            SensorStationStatusMessage msg = new SensorStationStatusMessage(list);
            msg.setSensorStationStatusList(list);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(msg, sw);
            System.out.println(sw.toString());
            StringReader sr = new StringReader(sw.getBuffer().toString());
            msg = (SensorStationStatusMessage)unmarshaller.unmarshal(sr);
            System.out.println(msg);
            sim.start();
        }
        catch (IOException | JAXBException e) {
            logger.log(Level.SEVERE, "Simulator Error", e);
        }
    }
}