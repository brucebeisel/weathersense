package com.bdb.util.measurement;

import java.time.LocalDateTime;

/**
 * Immutable container class for holding a measurement value and time pair. This class can be used to represent
 * a maximum value and the time that the maximum occurred.
 * 
 * @author Bruce
 *
 * @param <M> The type of measurement that must be a class that extends Measurement
 */
public class MeasurementValueTime<M extends Measurement> {
    private M        value;
    private LocalDateTime time;
    
    /**
     * Constructor.
     * 
     * @param value The value to be stored
     * @param time The time of the measurement value
     * @throws IllegalArgumentException Either value or time are null
     */
    public MeasurementValueTime(M value, LocalDateTime time) throws IllegalArgumentException {
        if (time == null)
            throw new IllegalArgumentException("Argument time cannot be null");
        
        if (value == null)
            throw new IllegalArgumentException("Argument value cannot be null");
        
        this.value = value;
        this.time = time;
    }
    
    /**
     * @return the value
     */
    public M getValue() {
        return value;
    }
    /**
     * @return the time
     */
    public LocalDateTime getTime() {
        return time;
    }
}