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
    /**
     * The weather station ID
     */
    public String stationId;
    /**
     * The weather underground password
     */
    public String password;
    /**
     * Whether the rapid fire protocol is being used
     */
    public boolean rapidFire;
    /**
     * The time of this observation
     */
    public TemporalAccessor time;
    /**
     * The direction of the wind
     */
    public Heading windDir;
    /**
     * The speed of the wind
     */
    public Speed windSpeedMph;
    /**
     * The direction of the wind gust
     */
    public Heading windGustDir;
    /**
     * The speed of the wind gust
     */
    public Speed windGustMph;
    /**
     * The average wind speed over the last 2 minutes
     */
    public Speed windSpeedMphAvg2Min;
    /**
     * The average wind direction over the last 2 minutes
     */
    public Heading windDirAvg2Min;
    /**
     * The maximum wind gust over the last 10 minutes
     */
    public Speed windGust10Min;
    /**
     * The average wind gust direction over the last 10 minutes
     */
    public Heading winGustDir10Min;
    /**
     * The current outdoor humidity
     */
    public Humidity outdoorHumidity;
    /**
     * The current dew point
     */
    public Temperature dewPoint;
    /**
     * The current outdoor temperature
     */
    public Temperature outdoorTemp;
    /**
     * The current barometer reading
     */
    public Pressure barometer;
    /**
     * The current indoor temperature
     */
    public Temperature indoorTemp;
    /**
     * The current indoor humidity
     */
    public Humidity indoorHumidity;
    /**
     * The current rainfall rate in inches per hour
     */
    public Depth rainRate;
    /**
     * The amount of rainfall since midnight
     */
    public Depth rainToday;
    /**
     * The current solar radiation reading
     */
    public SolarRadiation solarRadiation;
}
