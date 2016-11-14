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
package com.bdb.weather.consoledriver;

import java.util.Calendar;

import com.bdb.util.measurement.Measurement;

/**
 *
 * @author Bruce
 * @param <T>
 */
public abstract class MeasurementGenerator<T extends Measurement> {
    private T measurement;
    private final Calendar time;
    private final int intervalSeconds;
    
    /**
     *
     * @param initValue
     * @param time
     * @param intervalSeconds
     */
    protected MeasurementGenerator(T initValue, Calendar time, int intervalSeconds) {
        measurement = initValue;
        this.time = (Calendar)time.clone();
        this.intervalSeconds = intervalSeconds;
    }
    
    /**
     *
     * @param measurement
     */
    protected void setMeasurement(T measurement) {
        this.measurement = measurement;
    }
    
    /**
     *
     * @return
     */
    public T getMeasurement() {
        return measurement;
    }
    
    /**
     *
     * @return
     */
    public T nextValue() {
        time.add(Calendar.SECOND, intervalSeconds);
        measurement = nextValue(measurement, time, intervalSeconds);
        return measurement;
    }
    
    /**
     *
     * @param currentValue
     * @param time
     * @param intervalSeconds
     * @return
     */
    protected abstract T nextValue(T currentValue, Calendar time, int intervalSeconds);
}
