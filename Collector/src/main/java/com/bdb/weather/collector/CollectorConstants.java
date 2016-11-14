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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Constants used by the WeatherSense collector.
 *
 * @author Bruce
 */
public final class CollectorConstants {
    /**
     * The port on which the collector listens for a connection from a driver.
     */
    public static final int COLLECTOR_PORT = 11461;
    
    /**
     * The message type token used in the non-XML protocol with the collector.
     */
    public static final String MESSAGE_TYPE_TOKEN = "message_type";
    /**
     * The initialize command
     */
    public static final String INITIALIZE_TOKEN = "initialize";
    /**
     * The format to be used to format the date of the newest archive record in the database
     */
    public static final String NEWEST_RECORD_DATE_FORMAT = "yyyyMMdd HHmm";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    
    private CollectorConstants() {
    }

    /**
     * Get the date/time formatter used internally by weather sense.
     * 
     * @return The data/time formatter
     */
    public static DateTimeFormatter dateFormatter() {
        return DATE_FORMATTER;
    }

    /**
     * Get the date formatter used internally by weather sense.
     * 
     * @return The date formatter
     */
    public static DateTimeFormatter dateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }
}