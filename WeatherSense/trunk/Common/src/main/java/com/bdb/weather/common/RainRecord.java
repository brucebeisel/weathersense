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

public class RainRecord {

    private final LocalDate date;
    private final int hour;
    private final Depth rainfall;

    public RainRecord(LocalDate startTime, int hour, Depth rainfall) {
        this.date = startTime;
        this.hour = hour;
        this.rainfall = rainfall;
    }

    public LocalDate getTime() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public Depth getRainfall() {
        return rainfall;
    }
}
