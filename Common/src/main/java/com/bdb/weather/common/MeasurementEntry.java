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

import com.bdb.util.measurement.Measurement;

/**
 *
 * @author Bruce
 * @param <T>
 */
public class MeasurementEntry<T extends Measurement> {
    private int sensorId;
    private SensorType sensorType;
    private T measurement;

    /**
     * Constructor.
     * 
     * @param sensorId The ID of the sensor from which this measurement was obtained
     * @param sensorType The type of sensor from which this measurement was obtained
     * @param measurement The measurement
     */
    public MeasurementEntry(int sensorId, SensorType sensorType, T measurement) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.measurement = measurement;
    }
    
    /**
     * Constructor.
     */
    private MeasurementEntry() {
    }
    
    /**
     * Get the sensor ID.
     * 
     * @return The sensor ID
     */
    public int getSensorId() {
        return sensorId;
    }
    
    /**
     * Get the sensor type.
     * 
     * @return The sensor type
     */
    public SensorType getSensorType() {
        return sensorType;
    }

    /**
     * Get the measurement.
     * 
     * @return The measurement
     */
    public T getMeasurement() {
        return measurement;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.sensorId;
        hash = 67 * hash + Objects.hashCode(this.sensorType);
        hash = 67 * hash + Objects.hashCode(this.measurement);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final MeasurementEntry<T> other = (MeasurementEntry<T>)obj;
        if (this.sensorId != other.sensorId)
            return false;

        if (this.sensorType != other.sensorType)
            return false;

        return Objects.equals(this.measurement, other.measurement);
    }
}