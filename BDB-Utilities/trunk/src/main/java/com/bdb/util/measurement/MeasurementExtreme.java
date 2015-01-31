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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Class to manage an extreme Measurement value with the time of occurrence.
 * @author Bruce
 *
 * @param <M> The type of Measurement for this extreme value
 */
public final class MeasurementExtreme<M extends Measurement> {
    public enum ExtremeType { MAXIMUM, MINIMUM };
    private M             extreme;
    private LocalDateTime time;
    private final ExtremeType   type;
    
    /**
     * Constructor.
     * 
     * @param type The type of extreme, maximum or minimum
     */
    public MeasurementExtreme(ExtremeType type) {
        this.type = type;
    }
    
    /**
     * Constructor that sets the initial extreme value and time.
     * 
     * @param type The type of extreme value
     * @param extreme The initial extreme value
     * @param time The time of the extreme value
     */
    public MeasurementExtreme(ExtremeType type, M extreme, LocalDateTime time) {
        this(type);
        applyValue(extreme, time);
    }
    
    /**
     * Set the extreme to the given value and time.
     * 
     * @param extreme The new extreme value 
     * @param time The time of the extreme value
     */
    private void setExtreme(M extreme, LocalDateTime time) {
        this.extreme = extreme;
        this.time = time;

    }
    
    /**
     * Apply a value to the extreme.
     * 
     * @param value The value to apply to the extreme
     * @param time The time of the value
     */
    public void applyValue(M value, LocalDateTime time) {
        if (value == null)
            return;
        
        if (extreme == null)
            setExtreme(value, time);
        else if (type == ExtremeType.MINIMUM && value.compareTo(extreme) < 0)
            setExtreme(value, time);
        else if (type == ExtremeType.MAXIMUM && value.compareTo(extreme) > 0)
            setExtreme(value, time);
    }
    
    /**
     * Get the current extreme value.
     * 
     * @return The current extreme
     */
    public M getExtreme() {
        return extreme;
    }
    
    /**
     * Get the time of the extreme value.
     * 
     * @return The time of the extreme value
     */
    public LocalDateTime getExtremeTime() {
        return time;
    }
    
    /**
     * Get the type of this extreme object.
     * 
     * @return The type of the extreme object
     */
    public ExtremeType getType() {
        return type;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Type: " + type +
               "  Extreme: " + extreme +
               "  Time: " + DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(time);
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;

        if (this.getClass() != object.getClass())
            return false;
        
        MeasurementExtreme<M> o = (MeasurementExtreme<M>)object;
        if (type != o.type)
            return false;
        
        if (extreme == null && o.extreme == null)
            return true;
        else if (extreme == null || o.extreme == null)
            return false;

        return extreme.equals(o.extreme) && time.equals(o.time);
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = type.hashCode();
        if (extreme != null)
            hashCode += extreme.hashCode() + time.hashCode();
        
        return hashCode;
    }
}