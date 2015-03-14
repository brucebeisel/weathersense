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
package com.bdb.weather.common.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Bruce
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.LENIENT);
    private static final Logger logger = Logger.getLogger(LocalDateAdapter.class.getName());
 
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        try {
            String dateString = v.substring(0, 10);
            LocalDate time = LocalDate.parse(dateString, formatter);
            return time;
        }
        catch (DateTimeParseException e) {
            logger.log(Level.SEVERE, "Failed to unmarshal LocalDate", e);
            return null;
        }
    }
 
    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.toString();
    }
}
