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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import com.bdb.util.measurement.Measurement;

/**
 *
 * @author bruce
 */
public class MeasurementDeserializer implements JsonDeserializer<Measurement> {
    private static final Logger logger = Logger.getLogger(MeasurementDeserializer.class.getName());
 
    @Override
    public Measurement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            double value = json.getAsDouble();
            Class<Measurement> c = (Class<Measurement>)typeOfT;
            Measurement m = c.getConstructor(double.class).newInstance(value);
            System.out.println(typeOfT.getTypeName() + " " + m.get());
            return m;
        }
        catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            logger.log(Level.SEVERE, "Failed to deserialize " + typeOfT.getTypeName(), e);
            return null;
        }
    }
}
