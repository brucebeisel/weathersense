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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bdb.weather.common.json.DurationDeserializer;
import com.bdb.weather.common.json.DurationSerializer;
import com.bdb.weather.common.json.LocalDateDeserializer;
import com.bdb.weather.common.json.LocalDateSerializer;
import com.bdb.weather.common.json.LocalDateTimeDeserializer;
import com.bdb.weather.common.json.LocalDateTimeSerializer;
import com.bdb.weather.common.json.MeasurementDeserializer;
import com.bdb.weather.common.json.MeasurementSerializer;
import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Rainfall;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;

/**
 *
 * @author bruce
 */
public class GsonUtils {
    private GsonUtils() {}
    public static Gson gsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AngularMeasurement.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(AngularMeasurement.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Depth.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Depth.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Distance.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Distance.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Heading.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Heading.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Humidity.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Humidity.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(LeafWetness.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(LeafWetness.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Pressure.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Pressure.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Rainfall.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Rainfall.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(SoilMoisture.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(SoilMoisture.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(SolarRadiation.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(SolarRadiation.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Speed.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Speed.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Temperature.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Temperature.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(UvIndex.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(UvIndex.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        Gson gson = gsonBuilder.create();
        return gson;
    }
}