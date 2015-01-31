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
package com.bdb.util.measurement;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Class that manages minimum, maximum and average statistics for a given Measurement type.
 * 
 * @author Bruce
 *
 * @param <M> A Measurement type
 */
public class MeasurementStatistics<M extends Measurement> {
    private MeasurementExtreme<M> minimum = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MINIMUM);
    private MeasurementExtreme<M> maximum = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MAXIMUM);
    private MeasurementAverage<M> average;
    
    /**
     * Constructor that needs a zero value Measurement. This is due to the fact that you
     * cannot construct an object of the generic type (in this case a subclass of Measurement).
     * 
     * @param zeroValue A Measurement that has the value of zero
     * @throws IllegalArgumentException The constructor argument does not contain the value of 0.0
     */
    public MeasurementStatistics(M zeroValue) throws IllegalArgumentException {
        if (zeroValue.get() != 0.0)
            throw new IllegalArgumentException("Illegal constructor argument " + zeroValue.get() + " != 0.0");
        
        average = new MeasurementAverage<>(zeroValue, Duration.ZERO);
    }
    
    /**
     * Apply a value to the statistics.
     * 
     * @param value The value to apply
     * @param time The time at which the value occurred
     * @param duration The duration of the measured value
     */
    public void applyValue(M value, LocalDateTime time, Duration duration) {
        minimum.applyValue(value, time);
        maximum.applyValue(value, time);
        average.applyValue(value, duration);
    }
    
    public void applyValues(M minValue, M maxValue, M avgValue, LocalDateTime time, Duration duration) {
        minimum.applyValue(minValue, time);
        maximum.applyValue(maxValue, time);
        average.applyValue(avgValue, duration);
    }
    
    /**
     * Apply another MeasurementStatistic to this one. 
     * 
     * @param stats The statistics to apply to this object
     */
    public void applyStatistics(MeasurementStatistics<M> stats) {
        minimum.applyValue(stats.getMinimumValue(), stats.getMinimumTime());
        maximum.applyValue(stats.getMaximumValue(), stats.getMaximumTime());
        average.applyValue(stats.getAverage(), stats.getAverageDuration());
    }
    
    /**
     * Override the minimum value.
     * 
     * @param extreme The value with which to override the minimum
     * @param time The time of the measurement
     */
    public void setMinimum(M extreme, LocalDateTime time) {
        minimum = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MINIMUM, extreme, time);
    }
    
    /**
     * Get the minimum value.
     * 
     * @return The minimum value
     */
    public M getMinimumValue() {
        return minimum.getExtreme();
    }
    
    /**
     * Get the time of the minimum value.
     * 
     * @return The time of the minimum value
     */
    public LocalDateTime getMinimumTime() {
        return minimum.getExtremeTime();
    }
    
    /**
     * Override the maximum value.
     * 
     * @param extreme The value with which to override the maximum
     * @param time The time of the measurement
     */
    public void setMaximum(M extreme, LocalDateTime time) {
        maximum = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MAXIMUM, extreme, time);
    }
    
    /**
     * Get the maximum value.
     * 
     * @return The maximum value
     */
    public M getMaximumValue() {
        return maximum.getExtreme();
    }
    
    /**
     * Get the time of the maximum value.
     * 
     * @return The time of the maximum value
     */
    public LocalDateTime getMaximumTime() {
        return maximum.getExtremeTime();
    }
    
    /**
     * Override the average value.
     * 
     * @param average The average value
     * @param duration The duration used to calculate the average value
     */
    public void setAverage(M average, Duration duration) {
        this.average = new MeasurementAverage<>(average, duration);
    }
    
    /**
     * Get the average value.
     * 
     * @return The average value
     */
    public M getAverage() {
        return average.getAverage();
    }
    
    /**
     * Get the duration used to calculate the average.
     * 
     * @return The duration
     */
    public Duration getAverageDuration() {
        return average.getDuration();
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Minimum[" + minimum + "] Maximum[" + maximum + "] Average[" + average + "]";
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        
        if (this.getClass() != other.getClass())
            return false;
        
        MeasurementStatistics<M> o = (MeasurementStatistics<M>)other;
        return minimum.equals(o.minimum) && maximum.equals(o.maximum) && average.equals(o.average);
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return minimum.hashCode() + maximum.hashCode() + average.hashCode();
    }
}
