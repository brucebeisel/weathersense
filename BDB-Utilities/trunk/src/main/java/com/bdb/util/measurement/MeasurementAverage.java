package com.bdb.util.measurement;

import java.time.Duration;
import java.util.Objects;



/**
 * Keep a running average of a measurement. 
 * 
 * @author Bruce
 * @param <M> The type of measurement that is being averaged
 *
 */
public class MeasurementAverage<M extends Measurement> {
    private M           value;
    private Duration    duration;
    
    /**
     * Constructs a Measurement Average with a seed value and duration
     * 
     * @param value The first data point with a given duration
     * @param duration The duration of the data point
     */
    public MeasurementAverage(M value, Duration duration) {
        this.value = value;
        this.duration = duration;
    }
    
    /**
     * Apply another value to the average
     * 
     * @param currentValue A data sample to add to the average
     * @param valueDuration The duration of this data sample
     */
    public void applyValue(M currentValue, Duration valueDuration) {
        //
        // Do no apply null values. Null values are invalid and therefore ignored.
        //
        if (currentValue == null)
            return;

        double tDuration = (double)duration.getSeconds();
        double vDuration = (double)valueDuration.getSeconds();
        if (value != null) {
            double total = (value.get() * tDuration) + (currentValue.get() * vDuration);
            duration = duration.plus(valueDuration);
            double avg = total / (double)duration.getSeconds();
            value = (M)value.newMeasurement(avg);
        }
    }
    
    /**
     * Apply another average to this average
     * 
     * @param avg The other average to apply to this average
     */
    public void applyValue(MeasurementAverage<M> avg) {
        applyValue(avg.value, avg.duration);
    }
    
    /**
     * Return the average
     * 
     * @return The average
     */
    public M getAverage() {
        return value;
    }
    
    /**
     * Get the duration that was used to calculate the average.
     * 
     * @return The duration
     */
    public Duration getDuration() {
        return duration;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Average: " + value.toString() + " Duration: " + duration;
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
        
        MeasurementAverage<M> o = (MeasurementAverage<M>)other;
        return duration.equals(o.duration) && value.equals(o.value);
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode() + (Objects.hashCode(duration));
    }
}
