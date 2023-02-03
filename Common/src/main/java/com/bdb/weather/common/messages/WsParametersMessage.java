/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common.messages;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bdb.weather.common.GeographicLocation;


/**
 * Class representing the weather station parameters message.
 *
 * @author Bruce
 */
public class WsParametersMessage {
    private final String manufacturer;
    private final String model;
    private final String firmwareDate;
    private final String firmwareVersion;
    private final GeographicLocation location;
    private final int weatherYearStartMonth;
    private final Map<String,String> parameters;

    private WsParametersMessage() {
        this(null, null, null, null, null, 0);
    }

    /**
     * Create a message from the provided parameters.
     * 
     * @param manufacturer The manufacturer parameter
     * @param model The model parameter
     * @param firmwareDate The firmware date parameter
     * @param firmwareVersion The firmware version parameter
     * @param location The location parameter
     * @param weatherYearStartMonth  The weather year start month parameter
     */
    public WsParametersMessage(String manufacturer, String model, String firmwareDate, String firmwareVersion, GeographicLocation location, int weatherYearStartMonth) {
        this.location = location;
        this.manufacturer = manufacturer;
        this.model = model;
        this.firmwareDate = firmwareDate;
        this.firmwareVersion = firmwareVersion;
        this.weatherYearStartMonth = weatherYearStartMonth;
        parameters = new TreeMap<>();
    }

    /**
     * Get the location.
     * 
     * @return The location
     */
    public GeographicLocation getLocation() {
        return location;
    }

    /**
     * Get the manufacturer.
     * 
     * @return The manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Get the model name.
     * 
     * @return The model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Get the firmware date.
     * 
     * @return The firmware date
     */
    public String getFirmwareDate() {
        return firmwareDate;
    }

    /**
     * Get the firmware version.
     * 
     * @return The firmware version
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Get the weather year start month.
     * 
     * @return The weather year start month
     */
    public int getWeatherYearStartMonth() {
        return weatherYearStartMonth;
    }

    /**
     * Add a non-standard parameter.
     * 
     * @param key The key of the parameter
     * @param value The value of the parameter
     */
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    /**
     * Get the parameter keys.
     * 
     * @return The collection of keys
     */
    public Set<String> getKeys() {
        return parameters.keySet();
    }

    /**
     * Get the value given a key.
     * 
     * @param key The key with which to look up the value
     * @return The value or null if the key does not exist
     */
    public String getValueForKey(String key) {
        return parameters.get(key);
    }
}
