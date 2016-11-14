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

import com.bdb.util.measurement.Measurement;

/**
 * Class that contains an extreme (also called a record)
 * @author bruce
 * @param <MEASUREMENT> The type of measurement for which this is an extreme
 * @param <RECORDTYPE> The type of record as maximum or minimum
 */
public class Extreme<MEASUREMENT extends Measurement, RECORDTYPE extends RecordType> {    
    private final LocalDate     date;
    private final MEASUREMENT   value;
    private final RECORDTYPE    type;
    private final LocalDate     previousDate;
    private final MEASUREMENT   previousValue;

    /**
     * Constructor.
     * 
     * @param date The date of the extreme
     * @param type The type of the extreme
     * @param value The value of the extreme measurement
     * @param previousYear The year of the previous extreme that this one exceeded
     * @param previousValue The previous value that this one exceeded
     */
    public Extreme(LocalDate date, RECORDTYPE type, MEASUREMENT value, int previousYear, MEASUREMENT previousValue) {
        this.date = date;
        this.type = type;
        this.value = value;
        this.previousValue = previousValue;
        this.previousDate = date.withYear(previousYear);
    }
    
    /**
     * Get the date of the extreme.
     * 
     * @return the date of the extreme
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Get the date of the previous extreme value.
     * 
     * @return The date of the previous extreme
     */
    public LocalDate getPreviousDate() {
        return previousDate;
    }

    /**
     * Get the value of the extreme.
     * 
     * @return The extreme value
     */
    public MEASUREMENT getValue() {
        return value;
    }
    
    /**
     * Get the value of the previous extreme.
     * 
     * @return The previous extreme value
     */
    public MEASUREMENT getPreviousValue() {
        return previousValue;
    }
    
    /**
     * Get the type of extreme.
     * 
     * @return The extreme type
     */
    public RECORDTYPE getType() {
        return type;
    }
}