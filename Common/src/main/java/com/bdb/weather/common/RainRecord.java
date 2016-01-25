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

import com.bdb.weather.common.measurement.Depth;

/**
 * A class to hold the amount of rainfall for an hour.
 * 
 * @author bruce
 */
public class RainRecord {
    private final LocalDate date;
    private final int hour;
    private final Depth rainfall;

    /**
     * Constructor.
     * 
     * @param date The date this rain fell
     * @param hour The hour this rain fell
     * @param rainfall The amount of rain for the hour
     */
    public RainRecord(LocalDate date, int hour, Depth rainfall) {
        this.date = date;
        this.hour = hour;
        this.rainfall = rainfall;
    }

    /**
     * Get the date of the rain.
     * 
     * @return The date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Get the hour of the rainfall.
     * 
     * @return The hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * Get the amount of rainfall.
     * 
     * @return The rainfall amount
     */
    public Depth getRainfall() {
        return rainfall;
    }
}
