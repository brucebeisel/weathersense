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
package com.bdb.weather.common.db;

import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.Unit;
import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Defines the constants for the units used to store various measurements in the database.
 *
 * @author Bruce
 *
 */
public final class DatabaseUnits {
    /**
     * Speed unit for database storage
     */
    public static final Speed.Unit SPEED = Speed.Unit.METERS_PER_SEC;
    /**
     * Depth unit for database storage
     */
    public static final Depth.Unit DEPTH = Depth.Unit.MILLIMETERS;
    /**
     * Pressure unit for database storage
     */
    public static final Pressure.Unit PRESSURE = Pressure.Unit.MILLIBAR;
    /**
     * Temperature unit for database storage
     */
    public static final Temperature.Unit TEMPERATURE = Temperature.Unit.CELSIUS;
    /**
     * Altitude unit for database storage
     */
    public static final Depth.Unit ALTITUDE = Depth.Unit.METERS;
    /**
     * Latitude/Longitude unit for database storage
     */
    public static final AngularMeasurement.Unit LAT_LON = AngularMeasurement.Unit.DEGREES;
    /**
     * Soil Moisture unit for database storage
     */
    public static final SoilMoisture.Unit SOIL_MOISTURE = SoilMoisture.Unit.CENTIBAR;
    /**
     * Leaf Wetness unit for database storage
     */
    public static final LeafWetness.Unit LEAF_WETNESS = LeafWetness.Unit.WETNESS;

    private DatabaseUnits() {
        // Only static methods
    }

    /**
     * Get the database storage unit for the provided value.
     * 
     * @param measurement The measure to use to lookup the database storage unit
     * @return The storage unit for the database
     */
    public static Unit getDatabaseUnit(Measurement measurement) {
        if (measurement instanceof Temperature)
            return TEMPERATURE;
        else if (measurement instanceof Humidity)
            return Humidity.Unit.RELATIVE_HUMIDITY;
        else if (measurement instanceof Speed)
            return SPEED;
        else if (measurement instanceof Depth)
            return DEPTH;
        else if (measurement instanceof Pressure)
            return PRESSURE;
        else if (measurement instanceof SoilMoisture)
            return SOIL_MOISTURE;
        else if (measurement instanceof LeafWetness)
            return LEAF_WETNESS;
        else
            throw new UnsupportedOperationException("Measurement type not found");
    }
}