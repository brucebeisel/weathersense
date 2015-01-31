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
package com.bdb.weather.display;

import java.time.LocalDate;

/**
 * Enum to apply a date delta to another date
 * 
 * @author Bruce
 *
 */
public enum DateDelta {
    PREVIOUS_DAY("Previous Day", LocalDate::plusDays, -1),
    PREVIOUS_WEEK("Previous Week", LocalDate::plusDays, -7),
    PREVIOUS_MONTH("Previous Month", LocalDate::plusMonths, -1),
    PREVIOUS_YEAR("Previous Year", LocalDate::plusYears, -1),
    CUSTOM("Custom", LocalDate::plusDays, 0);
  
    private final String displayName;
    private final DateManipulator<LocalDate> function;
    private final long    delta;
    
    /**
     * Constructor.
     * 
     * @param name The name of the delta element
     * @param field The field to adjust for the delta
     * @param delta The amount to apply to the delta
     */
    private DateDelta(String name, DateManipulator<LocalDate> function, long delta) {
        this.displayName = name;
        this.function = function;
        this.delta = delta;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    public LocalDate applyDelta(LocalDate time) {
        return function.manipulate(time, delta);
    }
    public interface DateManipulator<D> {
        public D manipulate(D time, long offset);
    };
}
