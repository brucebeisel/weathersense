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

import java.time.LocalDate;

import com.bdb.util.measurement.Measurement;


public class Extreme<MEASUREMENT extends Measurement, RECORDTYPE extends RecordType> {    
    private final LocalDate     date;
    private final MEASUREMENT   value;
    private final RECORDTYPE    type;
    private final LocalDate     previousDate;
    private final MEASUREMENT   previousValue;

    public Extreme(LocalDate date, RECORDTYPE type, MEASUREMENT value, int previousYear, MEASUREMENT previousValue) {
        this.date = date;
        this.type = type;
        this.value = value;
        this.previousValue = previousValue;
        this.previousDate = date.withYear(previousYear);
    }
    
    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    public LocalDate getPreviousDate() {
        return previousDate;
    }

    /**
     * @return the value
     */
    public MEASUREMENT getValue() {
        return value;
    }
    
    public MEASUREMENT getPreviousValue() {
        return previousValue;
    }
    
    /**
     * @return the type
     */
    public RECORDTYPE getType() {
        return type;
    }
}