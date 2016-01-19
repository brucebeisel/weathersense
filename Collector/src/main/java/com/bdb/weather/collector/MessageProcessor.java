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

import com.bdb.weather.collector.messages.SensorStationMessage;
import com.bdb.weather.collector.messages.SensorStationStatusMessage;
import com.bdb.weather.collector.messages.SensorMessage;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.messages.WeatherSenseMessage;
import com.bdb.weather.common.messages.WsParametersMessage;
import com.bdb.weather.collector.socket.SocketDataProcessor;

/**
 * Process messages from the weather console driver.
 * 
 * @author Bruce
 */
final class MessageProcessor implements SocketDataProcessor {
    private final char MESSAGE_TERMINATOR = '\n';
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CollectorConstants.NEWEST_RECORD_DATE_FORMAT);
    private final DateTimeFormatter dateTimeFormatter = CollectorConstants.dateTimeFormatter();
    private final Unmarshaller unmarshaller;
    private final WeatherDataWriter writer;
    private final DBConnection connection;
    private final HistoryTable historyTable;
    private final WeatherUploader weatherUploader;
    private static final Logger logger = Logger.getLogger(MessageProcessor.class.getName());

    /**
     * Constructor.
     * 
     * @param dbUrl The URL of the database
     * @param dbUser The user of the database
     * @param dbPassword The password for the database user
     * @param writer The database writer
     * @param uploader The object that uploads weather to the Internet
     * @throws JAXBException An exception occurred while de-marshaling a message
     */
    public MessageProcessor(String dbUrl, String dbUser, String dbPassword, WeatherDataWriter writer, WeatherUploader uploader) throws JAXBException {
        this.writer = writer;
        JAXBContext jaxbContext = JAXBContext.newInstance(com.bdb.weather.common.CurrentWeather.class,
                                                          com.bdb.weather.common.measurement.LeafWetness.class,
                                                          com.bdb.weather.common.measurement.SoilMoisture.class,
                                                          com.bdb.weather.common.messages.WsParametersMessage.class,
                                                          com.bdb.weather.collector.messages.SensorStationMessage.class,
                                                          com.bdb.weather.collector.messages.SensorStationStatusMessage.class,
                                                          com.bdb.weather.collector.messages.SensorMessage.class,
                                                          com.bdb.weather.common.HistoricalRecord.class);
        unmarshaller = jaxbContext.createUnmarshaller();

        connection = new DBConnection(dbUrl, dbUser, dbPassword);

        historyTable = new HistoryTable(connection);
        weatherUploader = uploader;
    }
    
    /**
     * Consume a message.
     * 
     * @param messages The messages to be consumed
     * @return A response or null if there is no response
     */
    @Override
    public String consumeMessages(List<String> messages) {
        List<HistoricalRecord> historicalRecords = new ArrayList<>();
        String response = null;
        for (String s : messages) {
            logger.log(Level.FINER, "Processing command: {0}", s);
            
            if (s.startsWith("<?xml"))
                processXmlMessage(s, historicalRecords);
            else
                response = processTextMessage(s);

        }

        if (!historicalRecords.isEmpty()) {
            logger.log(Level.FINE, "++++++++++++++ Received {0} Historical Record(s) {1} to {2} +++++++++++++",
                    new Object[]{historicalRecords.size(),
                                 dateTimeFormatter.format(historicalRecords.get(0).getTime()),
                                 dateTimeFormatter.format(historicalRecords.get(historicalRecords.size() - 1).getTime())});
            try {
                writer.addHistoricalRecords(historicalRecords);
            }
            catch (CollectException ex) {
                logger.log(Level.SEVERE, "Failed to add historical record", ex);
            }
        }

        return response;
    }

    /**
     * Find a message termination token and return its position if found.
     * 
     * @param s The string in which to look for a token
     * @return The location of the token
     */
    @Override
    public int findToken(String s) {
        int index = s.indexOf(MESSAGE_TERMINATOR);
        if (index == -1)
            return SocketDataProcessor.NO_TOKEN_FOUND;
        else
            return index;
    }

    /**
     * Process a non-XML text message.
     * 
     * @param s The message
     * 
     * @return The response to send back or null if there is no response
     */
    private String processTextMessage(String s) {
        String response = null;
        WeatherSenseMessage message = WeatherSenseMessage.parseMessage(s);

        if (message == null) {
            logger.log(Level.WARNING, "Received invalid message: {0}", s);
            return null;
        }

        switch (message.getMessageType()) {
            case CollectorConstants.INITIALIZE_TOKEN:
                //
                // Since this only happens when the collector and the console driver connect, only keep the connection
                // active for this task.
                //
                if (connection.connect()) {
                    LocalDateTime time = historyTable.findNewestRecordTime();
                    if (time == null)
                        time = LocalDate.now().minusYears(1).atStartOfDay();

                    response = dateFormatter.format(time);
                    connection.close();
                }
                break;
        }

        return response;
    }

    /**
     * Process an XML message.
     * 
     * @param s The XML message
     * 
     * @return The response to send back or null if there is no response
     */
    private void processXmlMessage(String s, List<HistoricalRecord> historicalRecords) {
        try {
            logger.fine("Received XML message");
            logger.log(Level.FINER, "XML message {0}", s.substring(0, 10));
            Object msg = unmarshaller.unmarshal(new StringReader(s));
            if (msg instanceof SensorMessage) {
                logger.fine("&&&&&&&&&&&&& Sensor Message &&&&&&&&&&&&&&&&&");
                writer.updateSensorList(((SensorMessage)msg).getSensorList());
            }
            else if (msg instanceof HistoricalRecord) {
                HistoricalRecord rec = (HistoricalRecord)msg;
                historicalRecords.add(rec);
            }
            else if (msg instanceof CurrentWeather) {
                logger.fine("################ Received Current Weather ################");
                CurrentWeather cw = (CurrentWeather)msg;
                writer.setCurrentWeather(cw, s);
                weatherUploader.uploadCurrentWeather(cw);
            }
            else if (msg instanceof WsParametersMessage) {
                logger.fine("**************** Received Weather Station Parameters **************");
                writer.setWeatherStationParameters((WsParametersMessage)msg);
            }
            else if (msg instanceof SensorStationMessage) {
                logger.fine("$$$$$$$$$$$$$$$$$$ Sensor Station Message $$$$$$$$$$$$$$$$");
                writer.updateSensorStationList(((SensorStationMessage)msg).getSensorStationList());
            }
            else if (msg instanceof SensorStationStatusMessage) {
                logger.fine("!!!!!!!!!!!!! Sensor Station Status Message !!!!!!!!!!!!!!");
                writer.updateSensorStationStatus(((SensorStationStatusMessage)msg).getSensorStationStatusList());
            }
            else
                logger.log(Level.SEVERE, "Received unknown message type {0}", msg.getClass().getName());
        }
        catch (JAXBException ex) {
            logger.log(Level.SEVERE, "Failed to unmarshal XML message", ex);
        }
    }
}