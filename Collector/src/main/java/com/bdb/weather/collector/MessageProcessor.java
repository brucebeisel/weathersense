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

import com.bdb.weather.collector.messages.SensorStationMessage;
import com.bdb.weather.collector.messages.SensorStationStatusMessage;
import com.bdb.weather.collector.messages.SensorMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.bdb.util.StringUtils;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.messages.WeatherSenseMessage;
import com.bdb.weather.common.messages.WsParametersMessage;
import com.bdb.weather.collector.socket.SocketDataProcessor;
import com.bdb.weather.common.GsonUtils;

/**
 * Process messages from the weather console driver.
 * 
 * @author Bruce
 */
final class MessageProcessor implements SocketDataProcessor {
    private final char MESSAGE_TERMINATOR = '\n';
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CollectorConstants.NEWEST_RECORD_DATE_FORMAT);
    private final DateTimeFormatter dateTimeFormatter = CollectorConstants.dateTimeFormatter();
    private final WeatherDataWriter writer;
    private final DBConnection connection;
    private final HistoryTable historyTable;
    private final WeatherUploader weatherUploader;
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(MessageProcessor.class.getName());

    /**
     * Constructor.
     * 
     * @param dbUrl The URL of the database
     * @param dbUser The user of the database
     * @param dbPassword The password for the database user
     * @param writer The database writer
     * @param uploader The object that uploads weather to the Internet
     */
    public MessageProcessor(String dbUrl, String dbUser, String dbPassword, WeatherDataWriter writer, WeatherUploader uploader) {
        this.writer = writer;
        gson = GsonUtils.gsonBuilder();
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
            logger.log(Level.FINER, "Processing message: {0}", s);
            
            List<String> tokens = StringUtils.tokenize(s);
            //
            // A 2 token message with the second token starting with a "}" is
            // a JSON message.
            //
            if (tokens.size() == 2 && tokens.get(1).charAt(0) == '{')
                processJsonMessage(tokens.get(0), tokens.get(1), historicalRecords);
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
     * Process a JSON message.
     * 
     * @param messageName The message name the determines the JSON content
     * @param json The JSON text
     */
    private void processJsonMessage(String messageName, String json, List<HistoricalRecord> historicalRecords) throws JsonParseException {
        logger.fine("Received JSON message: " + messageName);

        switch (messageName) {
            case "sensor":
                logger.fine("&&&&&&&&&&&&& Sensor Message &&&&&&&&&&&&&&&&&");
                SensorMessage sensorMessage = gson.fromJson(json, SensorMessage.class);
                writer.updateSensorList(sensorMessage.getSensorList());
                break;

            case "historical":
                HistoricalRecord rec = gson.fromJson(json, HistoricalRecord.class);
                historicalRecords.add(rec);
                break;

            case "current-weather":
                logger.fine("################ Received Current Weather ################");
                CurrentWeather cw = gson.fromJson(json, CurrentWeather.class);
                writer.setCurrentWeather(cw, json);
                weatherUploader.uploadCurrentWeather(cw);
                break;

            case "weather-station-parameters":
                logger.fine("**************** Received Weather Station Parameters **************");
                WsParametersMessage wsp = gson.fromJson(json, WsParametersMessage.class);
                writer.setWeatherStationParameters(wsp);
                break;

            case "sensor-station":
                logger.fine("$$$$$$$$$$$$$$$$$$ Sensor Station Message $$$$$$$$$$$$$$$$");
                SensorStationMessage ssm = gson.fromJson(json, SensorStationMessage.class);
                writer.updateSensorStationList(ssm.getSensorStationList());
                break;

            case "sensor-station-status":
                logger.fine("!!!!!!!!!!!!! Sensor Station Status Message !!!!!!!!!!!!!!");
                SensorStationStatusMessage sssm = gson.fromJson(json, SensorStationStatusMessage.class);
                writer.updateSensorStationStatus(sssm.getSensorStationStatusList());
                break;

            default:
                logger.log(Level.SEVERE, "Received unknown message type {0}", messageName);
        }
    }
}