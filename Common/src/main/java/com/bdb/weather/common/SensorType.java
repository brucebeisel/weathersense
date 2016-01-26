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
 *
 * @author bruce
 */
public enum SensorType {

    /**
     *
     */
    THERMOMETER("TH"),

    /**
     *
     */
    ULTRAVIOLET("UV"),

    /**
     *
     */
    ANEMOMETER("AN"),

    /**
     *
     */
    LEAF_WETNESS("LW"),

    /**
     *
     */
    LEAF_TEMPERATURE("LT"),

    /**
     *
     */
    SOLAR_RADIATION("SR"),

    /**
     *
     */
    SOIL_MOISTURE("SM"),

    /**
     *
     */
    SOIL_TEMPERATURE("ST"),

    /**
     *
     */
    HYGROMETER("HY"),

    /**
     *
     */
    RAIN_COLLECTOR("RC"),

    /**
     *
     */
    BAROMETER("BO");
    
    private final String code;
    
    SensorType(String code) {
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
    public static SensorType fromCode(String code) {
        for (SensorType type : SensorType.values()) {
            if (type.code.equals(code))
                return type;
        }
        
        throw new IllegalArgumentException("Unknown SensorType for code (" + code + ")");
    }
}