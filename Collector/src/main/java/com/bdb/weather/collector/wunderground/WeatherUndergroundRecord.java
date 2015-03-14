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
package com.bdb.weather.collector.wunderground;

import java.time.temporal.TemporalAccessor;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author Bruce
 */
public class WeatherUndergroundRecord {
    public String stationId;
    public String password;
    public boolean rapidFire;
    public TemporalAccessor time;
    public Heading windDir;
    public Speed windSpeedMph;
    public Heading windGustDir;
    public Speed windGustMph;
    public Speed windSpeedMphAvg2Min;
    public Heading windDirAvg2Min;
    public Speed windGust10Min;
    public Heading winGustDir10Min;
    public Humidity outdoorHumidity;
    public Temperature dewPoint;
    public Temperature outdoorTemp;
    public Pressure barometer;
    public Temperature indoorTemp;
    public Humidity indoorHumidity;
    public Depth rainRate;
    public Depth rainToday;
    public SolarRadiation solarRadiation;
}
