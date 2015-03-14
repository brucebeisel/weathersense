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

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import com.bdb.util.measurement.Measurement;

/**
 *
 * @author Bruce
 * @param <T>
 */
public class MeasurementEntry<T extends Measurement> {
    @XmlElement
    private int sensorId;
    @XmlElement
    private SensorType sensorType;
    @XmlElement
    private T measurement;

    public MeasurementEntry(int sensorId, SensorType sensorType, T measurement) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.measurement = measurement;
    }
    
    private MeasurementEntry() {
    }
    
    public int getSensorId() {
        return sensorId;
    }
    
    public SensorType getSensorType() {
        return sensorType;
    }

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
