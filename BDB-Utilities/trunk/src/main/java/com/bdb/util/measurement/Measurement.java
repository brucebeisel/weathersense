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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlValue;

/**
 * A general class for all types of measurements (weight, distance, speed, etc.)
 * This class is immutable.
 * 
 * @author Bruce Beisel
 *
 */
public abstract class Measurement implements Comparable<Measurement>, Serializable {
    public interface DoubleMeasurementFunction<T extends Measurement, U> {
        T apply(double value, U unit);
    }

    private static final long serialVersionUID = 220604177023335181L;
    private static Map<String,Unit> defaultUnits = new TreeMap<>();

    @XmlValue
    private final double value;
    private final int comparePrecision;
    private final DoubleFunction<? extends Measurement> constructor;
    //private final DoubleMeasurementFunction constructorWithUnit;

    /**
     * Constructor
     * 
     * @param value The value of the Measurement
     * @param unit The unit from which to convert the specified value
     * @param comparePrecision The number of digits maintained in the mantissa
     * @param constructor Method reference for constructor
     */
    protected Measurement(double value, Unit unit, int comparePrecision, DoubleFunction<? extends Measurement> constructor) {
        this.value = unit.fromUnit(value);
        this.comparePrecision = comparePrecision;
        this.constructor = constructor;
        //this.constructorWithUnit = constructorWithUnit;
    }

    /**
     * Copy constructor
     * 
     * @param m The measurement to be copied
     */
    protected Measurement(Measurement m) {
        value = m.value;
        comparePrecision = m.comparePrecision;
        this.constructor = m.constructor;
    }

    /**
     * Get the value of the measurement using the current default units
     * 
     * @return The current value converted to the default units
     */
    public double get() {
        Unit unit = getDefaultUnit(getClass());
        return unit.toUnit(value);
    }

    /**
     * Get the value of the measurement using the specified unit
     * 
     * @param unit The unit to which the value will be converted
     * 
     * @return The converted value
     */
    public double get(Unit unit) {
        return unit.toUnit(value);
    }
    
    /**
     * Returns a newly constructed Measurement that is of the same type as this class.
     * This method uses introspection to call the constructor.
     * 
     * @param value The initial value of the Measurement
     * @return The new Measurement (actually a subclass of Measurement)
     */
    public Measurement newMeasurement(double value) {
        return (Measurement)constructor.apply(value);
    }
    
    public Measurement newMeasurement(double value, Unit unit) {
        try {
            Constructor<? extends Measurement> c = this.getClass().getConstructor(double.class, unit.getClass());   
            Measurement m = c.newInstance(value, unit);
            return m;
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(Measurement.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Add two measurement types and return the result in a third object of the same type.
     * 
     * @param <T> The type of Measurement class to be added
     * @param other The value to add to this
     * @return The result of the addition in a new object
     */
    public <T extends Measurement> T add(T other) {
        if (this.getClass() != other.getClass())
            throw new ClassCastException("Cannot add Measurements of different types");

        double result = this.get() + other.get();
        
        return (T)newMeasurement(result);
    }
    
    /**
     * Subtract two measurement types and return the result in a third object of the same type.
     * 
     * @param <T> The type of Measurement class to be added
     * @param other The value to add to this
     * @return The result of the addition in a new object
     */
    public <T extends Measurement> T subtract(T other) {
        if (this.getClass() != other.getClass())
            throw new ClassCastException("Cannot subtract Measurements of different types");

        double result = this.get() - other.get();
        
        return (T)newMeasurement(result);
    }

    /**
     * Format the value into a string using the specified unit
     * 
     * @param unit The unit that the value will be converted to before formatting
     * @return The formatted string
     */
    public String toString(Unit unit) {
        return unit.getFormatter().format(get(unit));
    }

    /**
     * Format a string using the default unit
     * 
     * @return The formatted string
     */
    @Override
    public String toString() {
        return toString(getDefaultUnit(getClass()));
    }

    /**
     * Implementation of Comparable interface
     * 
     * @param other The other Measurement to which to compare this Measurement
     * @return -1 if this < other, 0 if this == other and 1 if this > other
     */
    @Override
    public int compareTo(Measurement other) {
        return Double.valueOf(value).compareTo(other.value);
    }

    /**
     * Check the equality of two Measurements
     * 
     * @param other The object to compare this against
     * @return True if this and other are equal
     */
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        
        if (this.getClass() != other.getClass())
            return false;
        
        Measurement o = (Measurement)other;
        double scale = Math.pow(10.0, comparePrecision);
        long v1 = Math.round(this.value * scale);
        long v2 = Math.round(o.value * scale);
        return v1 == v2;
    }

    /**
     * Generate a hash code
     * 
     * @return The hash code
     */
    @Override
    public int hashCode() {
        double scale = Math.pow(10.0, comparePrecision);
        return (Double.valueOf(Math.round(this.value * scale) / scale)).hashCode();
    }

    /**
     * Set the default unit for the specified Measurement class
     * 
     * @param c The class for which the default unit is set
     * @param unit The new default unit
     * @throws IllegalArgumentException The specified class is not a subclass of com.bdb.util.Measurement
     */
    protected static void setDefaultUnit(Class<?> c, Unit unit)
    {
        if (unit == null)
            throw new IllegalArgumentException("Setting default unit for class " + c.getName() + " to null");

        if (Measurement.class.isAssignableFrom(c))
            defaultUnits.put(c.getName(), unit);
        else
            throw new IllegalArgumentException("Default unit must be subclass of com.bdb.util.Measurement. It is " + c.getName());
    }

    /**
     * Get the default unit for the specified Measurement class
     * 
     * @param c The class for which the default unit is retrieved
     * @return The default unit
     */
    protected static Unit getDefaultUnit(Class<?>c)
    {
        String className = c.getName();
        return defaultUnits.get(className);
    }
}
