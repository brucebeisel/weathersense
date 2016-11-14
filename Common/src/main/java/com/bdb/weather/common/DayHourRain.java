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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.bdb.weather.common.measurement.Depth;

/**
 *
 * @author Bruce
 */
public class DayHourRain {
    private final LocalDate date;
    private final Map<Integer,Depth> hours;
    
    /**
     * Constructor.
     * 
     * @param date The date for which this object contains the hour buckets of rain
     */
    public DayHourRain(LocalDate date) {
        this.date = date;
        hours = new HashMap<>();
    }

    /**
     * Get the date.
     * 
     * @return The date
     */
    public LocalDate getDate() {
        return date;
    }
    /**
     * Put rain for a given hour.
     * 
     * @param hour The hour in which the rain fell
     * @param rain The amount of rain that fell during the hour
     */
    public void putRain(int hour, Depth rain) {
        hours.put(hour, rain);
    }

    /**
     * Get the rain for the specified hour.
     * 
     * @param hour The hour
     * @return The rainfall for the hour
     */
    public Depth getRain(int hour) {
        return hours.get(hour);
    }

    /**
     * Get the hours that are valid for this date, remembering that it could be early today or a date where DST starts.
     * 
     * @return The list of valid hours
     */
    public Set<Integer> getHourValues() {
        return hours.keySet();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.date);
        hash = 73 * hash + Objects.hashCode(this.hours);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final DayHourRain other = (DayHourRain)obj;
        
        if (!Objects.equals(this.date, other.date))
            return false;

        return Objects.equals(this.hours, other.hours);
    }
}