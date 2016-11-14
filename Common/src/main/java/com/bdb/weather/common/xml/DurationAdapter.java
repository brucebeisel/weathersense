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
package com.bdb.weather.common.xml;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter for converting a Duration to/from XML
 * :
 * @author Bruce
 */
public class DurationAdapter extends XmlAdapter<String, Duration> {
    private static final Logger logger = Logger.getLogger(LocalDateTimeAdapter.class.getName());
 
    @Override
    public Duration unmarshal(String v) throws Exception {
        try {
            long seconds = Long.parseLong(v);
            return Duration.ofSeconds(seconds);
        }
        catch (DateTimeParseException e) {
            logger.log(Level.SEVERE, "Failed to unmarshal Duration", e);
            return null;
        }
    }
 
    @Override
    public String marshal(Duration v) throws Exception {
        return "" + v.getSeconds();
    }
    
}
