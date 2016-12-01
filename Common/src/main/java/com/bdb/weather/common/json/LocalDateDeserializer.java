/*
 * Copyright (C) 2016 bruce
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
package com.bdb.weather.common.json;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 *
 * @author bruce
 */
public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    private static final Logger logger = Logger.getLogger(LocalDateTimeDeserializer.class.getName());
 
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            LocalDate time = LocalDate.parse(json.getAsString(), LocalDateSerializer.FORMATTER);
            return time;
        }
        catch (DateTimeParseException e) {
            logger.log(Level.SEVERE, "Failed to deserialize LocalDateTime", e);
            return null;
        }
    }
}