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
package com.bdb.weather.common;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.bdb.util.measurement.Measurement;
import com.bdb.weather.common.measurement.Temperature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 *
 * @author bruce
 */
public class JsonTest {
    static class MeasurementSerializer implements JsonSerializer<Measurement> {
        @Override
        public JsonElement serialize(Measurement src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
    
    public static void main(String args[]) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Temperature.class, new MeasurementSerializer());
        Gson gson = gsonBuilder.create();
        CurrentWeather cw = new CurrentWeather();
        cw.setTime(LocalDateTime.now());
        cw.setOutdoorTemperature(new Temperature(18.0));
        String json = gson.toJson(cw);
        System.out.println(json);
    }
}