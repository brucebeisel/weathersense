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

import java.time.LocalDateTime;
import java.util.Objects;

import com.bdb.weather.common.measurement.Depth;

public class Storm {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Depth stormRainfall;
    
    /**
     * Constructor with all storm data.
     * 
     * @param startTime The start time of the storm
     * @param endTime The time the storm ended
     * @param rainfall The amount of rainfall of this storm
     */
    public Storm(LocalDateTime startTime, LocalDateTime endTime, Depth rainfall) {
        this.startTime = startTime;
        this.endTime = endTime;
        stormRainfall = rainfall;
    }

    /**
     * Constructor without an end time.
     * 
     * @param startTime The start time of the storm
     * @param rainfall The amount of rainfall of this storm
     */
    public Storm(LocalDateTime startTime, Depth rainfall) {
        this(startTime, null, rainfall);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public Depth getStormRainfall() {
        return stormRainfall;
    }

    /**
     * Check if the storm is still active (has not ended).
     * 
     * @return True if the storm is still active
     */
    public boolean isStormActive() {
        return endTime == null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.startTime);
        hash = 83 * hash + Objects.hashCode(this.endTime);
        hash = 83 * hash + Objects.hashCode(this.stormRainfall);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Storm other = (Storm)obj;
        if (!Objects.equals(this.startTime, other.startTime))
            return false;

        if (!Objects.equals(this.endTime, other.endTime))
            return false;

        return Objects.equals(this.stormRainfall, other.stormRainfall);
    }

}