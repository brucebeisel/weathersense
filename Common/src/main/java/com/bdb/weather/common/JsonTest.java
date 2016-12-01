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

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.bdb.weather.common.measurement.Temperature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bdb.weather.common.json.LocalDateDeserializer;
import com.bdb.weather.common.json.LocalDateSerializer;
import com.bdb.weather.common.json.LocalDateTimeDeserializer;
import com.bdb.weather.common.json.LocalDateTimeSerializer;
import com.bdb.weather.common.json.MeasurementDeserializer;
import com.bdb.weather.common.json.MeasurementSerializer;
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
import com.bdb.weather.common.measurement.UvIndex;

/**
 *
 * @author bruce
 */
public class JsonTest {
    public static void main(String args[]) {
        GsonBuilder gsonBuilder = new GsonBuilder();
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
        Gson gson = gsonBuilder.create();
        CurrentWeather cw = new CurrentWeather();
        cw.setTime(LocalDateTime.now());
        cw.setOutdoorTemperature(new Temperature(18.0));
        cw.setOutdoorHumidity(new Humidity(99.0));
        cw.setSolarRadiation(new SolarRadiation(1011));
        cw.setBaroPressure(new Pressure(1001));
        cw.setBaroTrend(WeatherTrend.STEADY);
        cw.setDayET(new Depth(1.0));
        cw.setMonthET(new Depth(20.0));
        cw.setYearET(new Depth(300.0));
        cw.setDewPoint(new Temperature(34.0));
        cw.setForecast(Forecast.SUNNY);
        cw.setForecastRule("Forecast Rule");
        cw.setHeatIndex(new Temperature(45));
        cw.setWindChill(new Temperature(5));
        cw.setThsw(new Temperature(7));
        cw.setIndoorHumidity(new Humidity(45));
        cw.setIndoorTemperature(new Temperature(25));
        cw.setWindGust(new Wind(new Speed(20), new Heading(178)));
        cw.setWindSpeed10MinAvg(new Speed(11));
        cw.setWindSpeed2MinAvg(new Speed(12));
        cw.setWind(new Wind(new Speed(10), new Heading(180)));
        cw.setWindDir2(new Heading(45));
        cw.setWindDir3(new Heading(50));
        cw.setWindDir4(new Heading(55));
        cw.setWindDir5(new Heading(60));
        cw.setRain15Minute(new Depth(.02));
        cw.setRainHour(new Depth(.06));
        cw.setRain24Hour(new Depth(1.2));
        cw.setRainToday(new Depth(1.0));
        cw.setRainMonth(new Depth(4.3));
        cw.setRainRate(new Depth(2.3));
        cw.setRainWeatherYear(new Depth(12.12));
        cw.setRainCalendarYear(new Depth(10.9));
        cw.setStormStart(LocalDate.now());
        cw.setStormRain(new Depth(1.2));
        cw.setTemperatureForSensor(10, new Temperature(28));
        cw.setHumidityForSensor(11, new Humidity(99));
        cw.setLeafWetnessForSensor(22, LeafWetness.MAX_LEAF_WETNESS);
        cw.setLeafTemperatureForSensor(20, new Temperature(45));
        String json = gson.toJson(cw);
        System.out.println("" + json.length() + " " +json);

        HistoricalRecord h = new HistoricalRecord(LocalDateTime.now());
        h.setAvgOutdoorTemperature(new Temperature(20));
        String hjson = gson.toJson(h);
        System.out.println("" + hjson.length() + " " + hjson);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(com.bdb.weather.common.CurrentWeather.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(cw, sw);
            System.out.println("" + sw.getBuffer().length() + " " +sw.getBuffer());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        cw = gson.fromJson(json, CurrentWeather.class);
        System.out.println(cw);
    }
}