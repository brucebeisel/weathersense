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

import com.bdb.weather.common.measurement.Temperature;

import com.google.gson.Gson;

import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.messages.WsParametersMessage;

/**
 *
 * @author bruce
 */
public class JsonTest {
    public static void main(String args[]) {
        Gson gson = GsonUtils.gsonBuilder();
        CurrentWeather cw = new CurrentWeather();
        cw.setTime(LocalDateTime.now().withNano(0));
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
        cw.setSoilMoistureForSensor(23, new SoilMoisture(2.0));
        cw.setSoilTemperatureForSensor(24, new Temperature(2.0));
        String json = gson.toJson(cw);
        System.out.println("" + json.length() + " " +json);

        HistoricalRecord h = new HistoricalRecord(LocalDateTime.now());
        h.setDuration(Duration.ofSeconds(300));
        h.setAvgOutdoorTemperature(new Temperature(20));
        h.setHighOutdoorTemperature(new Temperature(22));
        h.setLowOutdoorTemperature(new Temperature(18));
        h.setOutdoorHumidity(new Humidity(18));
        h.setIndoorTemperature(new Temperature(18));
        h.setIndoorHumidity(new Humidity(50));
        h.setAvgWind(new Wind(new Speed(10.0), new Heading(180)));
        h.setHighWind(new Wind(new Speed(12.0), new Heading(175)));
        h.setWindGust(new Wind(new Speed(14.0), new Heading(185)));
        h.setBaroPressure(new Pressure(1001));
        h.setAvgUvIndex(8.8F);
        //h.setHighUvIndex(UvIndex.getUvIndex(7));
        h.setAvgSolarRadiation(new SolarRadiation(400.0));
        h.setHighSolarRadiation(new SolarRadiation(500.0));
        h.setEvapotranspiration(new Depth(.2));
        h.setRainfall(new Depth(.1));
        h.setHighRainfallRate(new Depth(1.1));

        h.setTemperatureForSensor(100, new Temperature(10.0));
        h.setHumidityForSensor(101, new Humidity(48.0));
        h.setLeafWetnessForSensor(102, LeafWetness.MAX_LEAF_WETNESS);
        h.setSoilMoistureForSensor(103, new SoilMoisture(2));


        String hjson = gson.toJson(h);
        System.out.println("" + hjson.length() + " " + hjson);

        CurrentWeather cw2 = gson.fromJson(json, CurrentWeather.class);
        System.out.println(cw);
        System.out.println(cw2);
        boolean equal = cw.equals(cw2);
        System.out.println("Equal: " + equal);

        GeographicLocation location = new GeographicLocation(new AngularMeasurement(30.0), new AngularMeasurement(-60), new Distance(22.5));
        WsParametersMessage msg = new WsParametersMessage("manufacturer", "model",
                                                          "firmwareDate", "firmwareVersion",
                                                          location , 6);
        msg.addParameter("key1", "value1");
        msg.addParameter("key2", "value2");
        String json3 = gson.toJson(msg);
        System.out.println(json3);
        WsParametersMessage msg2 = gson.fromJson(json3, WsParametersMessage.class);
        equal = msg.equals(msg2);
        System.out.println("Msg Equals: " + equal);

        SensorStationMessage ssm;
        SensorStation ss = new SensorStation(1, SensorStationType.INTEGRATED_SENSOR_STATION, "ISS");
    }
}