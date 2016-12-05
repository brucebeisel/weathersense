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
package com.bdb.weather.common;

import java.util.Objects;

/**
 * This class represents a sensor that is attached to a weather station. The standard sensors that come with a
 * professional weather stations are pre-assigned IDs. All other IDs are assigned in groups of ID depending on
 * the sensor type.
 * 
 * @author Bruce
 */
public class Sensor {

    /**
     *
     */
    public static final int LAST_STANDARD_SENSOR_ID = 99;
    /**
     * The base ID of extra thermometer sensors
     */
    public static final int THERMOMETER_BASE_SENSOR_ID = 100;
    /**
     * The base ID of extra hygrometer sensors
     */
    public static final int HYGROMETER_BASE_SENSOR_ID = 200;
    /**
     * The base ID of leaf wetness sensors
     */
    public static final int LEAF_WETNESS_BASE_SENSOR_ID = 400;
    /**
     * The base ID of leaf temperature sensors
     */
    public static final int LEAF_TEMPERATURE_BASE_SENSOR_ID = 500;
    /**
     * The base ID of soil moisture sensors
     */
    public static final int SOIL_MOISTURE_BASE_SENSOR_ID = 600;
    /**
     * The base ID of soil temperature sensors
     */
    public static final int SOIL_TEMPERATURE_BASE_SENSOR_ID = 700;
    /**
     * Statically assigned sensor ID for the barometer
     */
    public static final int BAROMETER_SENSOR_ID = 1;
    /**
     * Statically assigned sensor ID for the wind sensor
     */
    public static final int WIND_SENSOR_ID = 2;
    /**
     * Statically assigned sensor ID for the UV sensor
     */
    public static final int UV_SENSOR_ID = 3;
    /**
     * Statically assigned sensor ID for the solar radiation sensor
     */
    public static final int SOLAR_RADIATION_SENSOR_ID = 4;
    /**
     * Statically assigned sensor ID for the rain collector
     */
    public static final int RAIN_COLLECTOR_SENSOR_ID = 5;
    /**
     * Statically assigned sensor ID for the indoor thermometer
     */
    public static final int INDOOR_THERMOMETER_SENSOR_ID = 6;
    /**
     * Statically assigned sensor ID for the outdoor thermometer
     */
    public static final int OUTDOOR_THERMOMETER_SENSOR_ID = 7;
    /**
     * Statically assigned sensor ID for the indoor hygrometer
     */
    public static final int INDOOR_HYGROMETER_SENSOR_ID = 8;
    /**
     * Statically assigned sensor ID for the outdoor hygrometer
     */
    public static final int OUTDOOR_HYGROMETER_SENSOR_ID = 9;
    private int sensorId;
    private String name;
    private SensorType type;
    static final String SENSOR_NAMES[] = {
        "",
        "Barometer",
        "Wind",
        "Ultraviolet",
        "Solar Radiation",
        "Rain Guage",
        "Indoor Thermometer",
        "Outdoor Thermometer",
        "Indoor Hygrometer",
        "Outdoor Hygrometer"
    };

    /**
     * Constructor.
     * 
     * @param sensorId The sensor ID
     * @param type The type of sensor
     */
    public Sensor(int sensorId, SensorType type) {
        this(sensorId, type, null);
    }
    
    private Sensor() {
    }
    
    /**
     * Constructor
     *
     * @param sensorId The sensor ID
     * @param type The type of sensor
     * @param name The name of the sensor
     */
    public Sensor(int sensorId, SensorType type, String name) {
        this.sensorId = sensorId;
        this.type = type;
        this.name = name;
    }

    /**
     * Get the ID of the sensor
     * 
     * @return The ID of the sensor
     */
    public int getSensorId() {
        return sensorId;
    }

    /**
     * Get the name of the sensor.
     * 
     * @return The name of the sensor
     */
    public String getName() {
        if (name == null || name.isEmpty()) {
            if (sensorId < SENSOR_NAMES.length)
                return SENSOR_NAMES[sensorId];
            else
                return "Sensor " + sensorId;
        }
        else
            return name;
    }

    /**
     * Get the sensor's type.
     * 
     * @return The sensor's type
     */
    public SensorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("ID = %d  Name = '%s'  Type = %s", sensorId, name, type);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.sensorId;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Sensor other = (Sensor)obj;
        if (this.sensorId != other.sensorId)
            return false;

        if (!Objects.equals(this.name, other.name))
            return false;

        return this.type == other.type;
    }
}