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
package com.bdb.weather.common.messages;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.Pair;

/**
 *
 * @author Bruce
 */
public class WeatherSenseMessage {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
    private static final String MESSAGE_TYPE_NAME = "message_type";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String TOKEN_TERMINATOR = ";";
    private static final String MESSAGE_TERMINATOR = "\n";
    private final String messageType;
    private final List<Pair<String,String>> tokens;
    private static final Logger logger = Logger.getLogger(WeatherSenseMessage.class.getName());
    
    public WeatherSenseMessage(String messageType) {
        tokens = new ArrayList<>();
        this.messageType = messageType;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void addToken(String name, String value) {
        tokens.add(new Pair<>(name, value));
    }
    
    public String buildMessage() {
        StringBuilder sb = new StringBuilder(MESSAGE_TYPE_NAME);
        sb.append(NAME_VALUE_SEPARATOR).append(messageType).append(TOKEN_TERMINATOR);
        tokens.stream().forEach((pair) -> {
            sb.append(pair.first).append(NAME_VALUE_SEPARATOR).append(pair.second).append(TOKEN_TERMINATOR);
        });
        
        sb.append(MESSAGE_TERMINATOR);
        return sb.toString();
    }
    
    public List<Pair<String,String>> getTokens() {
        return Collections.unmodifiableList(tokens);
    }
    
    public static WeatherSenseMessage parseMessage(String messageString) {
        String stringTokens[] = messageString.split(";");
        WeatherSenseMessage message = null;

        for (String token : stringTokens) {
            int equalsIndex = token.indexOf('=');
            if (equalsIndex == -1) {
                logger.log(Level.SEVERE, "Name/Value pair missing '=' sign. (" + message + ")");
                return null;
            }
            String name = token.substring(0, equalsIndex);
            String value = token.substring(equalsIndex + 1);
            if (message == null) {
                if (!name.equals(MESSAGE_TYPE_NAME)) {
                    logger.log(Level.SEVERE, "First token (" + name + ") of message is not " + MESSAGE_TYPE_NAME);
                    return null;
                }
                else
                    message = new WeatherSenseMessage(value);
            }
            else
                message.addToken(name, value);
        }
        
        return message;
    }
    
    public static int extractIndex(String token) throws ParseException {
        int leftBracketIndex = token.indexOf("[");
        int rightBracketIndex = token.indexOf("]");
        if (leftBracketIndex == -1 || rightBracketIndex == -1)
            throw new ParseException("Malformed index token in historical record (" + token + ")", -1);
                    
        String indexString = token.substring(leftBracketIndex + 1, rightBracketIndex);
        return Integer.parseInt(indexString);
    }
}
