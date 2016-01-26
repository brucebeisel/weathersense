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
package com.bdb.weather.common;

/**
 * These values are based on Davis Vantage Pro 2. Other stations may have additional sensor station types.
 * 
 * @author Bruce
 */
public enum SensorStationType {

    /**
     *
     */
    INTEGRATED_SENSOR_STATION("ISS"),

    /**
     *
     */
    TEMPERATURE_ONLY("TEMP"),

    /**
     *
     */
    TEMPERATURE_HUMIDTY("TEMP/HUMID"),

    /**
     *
     */
    HUMIDITY_ONLY("HUMID"),

    /**
     *
     */
    LEAF("LEAF WETNESS"),

    /**
     *
     */
    SOIL("SOIL_MOISTURE"),

    /**
     *
     */
    SOIL_LEAF("LW/SM"),

    /**
     *
     */
    ANEMOMETER("AN"),

    /**
     *
     */
    RAIN("RAIN");

    private final String code;
    
    SensorStationType(String code) {
        this.code = code;
    }
    
    /**
     *
     * @return
     */
    public String getCode() {
        return code;
    }
    
    /**
     *
     * @param code
     * @return
     */
    public static SensorStationType fromCode(String code) {
        for (SensorStationType type : SensorStationType.values()) {
            if (type.code.equals(code))
                return type;
        }
        
        throw new IllegalArgumentException("Unknown SensorStationType for code (" + code + ")");
    }
}