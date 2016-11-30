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

import java.time.LocalDateTime;

import com.bdb.weather.common.measurement.Temperature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bdb.weather.common.json.LocalDateTimeDeserializer;
import com.bdb.weather.common.json.LocalDateTimeSerializer;
import com.bdb.weather.common.json.MeasurementDeserializer;
import com.bdb.weather.common.json.MeasurementSerializer;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;

/**
 *
 * @author bruce
 */
public class JsonTest {
    public static void main(String args[]) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Temperature.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Temperature.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Humidity.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Humidity.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Pressure.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Pressure.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(SolarRadiation.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(SolarRadiation.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(Depth.class, new MeasurementSerializer());
        gsonBuilder.registerTypeAdapter(Depth.class, new MeasurementDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson gson = gsonBuilder.create();
        CurrentWeather cw = new CurrentWeather();
        cw.setTime(LocalDateTime.now());
        cw.setOutdoorTemperature(new Temperature(18.0));
        cw.setOutdoorHumidity(new Humidity(99.0));
        cw.setSolarRadiation(new SolarRadiation(1011));
        cw.setBaroPressure(new Pressure(1001));
        cw.setBaroTrend(WeatherTrend.STEADY);
        cw.setDayET(new Depth(1.0));
        cw.setDewPoint(new Temperature(34.0));
        cw.setForecast(Forecast.SUNNY);
        cw.setForecastRule("Forecast Rule");
        cw.setHeatIndex(new Temperature(45));
        cw.setHumidityForSensor(10, Humidity.MAX_HUMIDITY);
        cw.setHumidityForSensor(11, new Humidity(99));
        cw.setIndoorHumidity(new Humidity(45));
        cw.setIndoorTemperature(new Temperature(25));
        cw.setLeafTemperatureForSensor(20, new Temperature(45));
        String json = gson.toJson(cw);
        System.out.println(json);

        cw = gson.fromJson(json, CurrentWeather.class);
        System.out.println(cw);
    }
}