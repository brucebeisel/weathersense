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

import java.time.LocalDate;
import java.util.Objects;

import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.MeasurementStatistics;

/**
 *
 * @author beisbr
 * @param <M>
 */
public class MeasurementStatisticsEntry<M extends Measurement> {
    private final LocalDate date;
    private final int sensorId;
    private final SensorType sensorType;
    private final MeasurementStatistics<M> stats;

    /**
     *
     * @param date
     * @param sensorId
     * @param sensorType
     * @param stats
     */
    public MeasurementStatisticsEntry(LocalDate date, int sensorId, SensorType sensorType, MeasurementStatistics<M> stats) {
        this.date = date;
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.stats = stats;
    }

    /**
     *
     * @return
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public int getSensorId() {
        return sensorId;
    }

    /**
     *
     * @return
     */
    public SensorType getSensorType() {
        return sensorType;
    }

    /**
     *
     * @return
     */
    public MeasurementStatistics<M> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "Measurement Stats Entry:  Sensor ID: " + sensorId + " Sensor Type: " + sensorType + " Stats: " + stats;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.date);
        hash = 37 * hash + this.sensorId;
        hash = 37 * hash + Objects.hashCode(this.sensorType);
        hash = 37 * hash + Objects.hashCode(this.stats);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        @SuppressWarnings("unchecked")
		final MeasurementStatisticsEntry<M> other = (MeasurementStatisticsEntry<M>)obj;
        if (!Objects.equals(this.date, other.date))
            return false;

        if (this.sensorId != other.sensorId)
            return false;

        if (this.sensorType != other.sensorType)
            return false;

        return Objects.equals(this.stats, other.stats);
    }
}