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

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author bruce
 */
public class SpeedBinDuration {
    private final int speedBinIndex;
    private final LocalDate date;
    private final int  headingIndex;
    private Duration duration;
    
    /**
     *
     * @param speedBinIndex
     * @param headingIndex
     * @param date
     */
    public SpeedBinDuration(int speedBinIndex, int headingIndex, LocalDate date) {
        this(speedBinIndex, headingIndex, date, Duration.ZERO);
    }
    
    /**
     *
     * @param speedBinIndex
     * @param headingIndex
     * @param date
     * @param duration
     */
    public SpeedBinDuration(int speedBinIndex, int headingIndex, LocalDate date, Duration duration) {
        this.speedBinIndex = speedBinIndex;
        this.headingIndex = headingIndex;
        this.date = date;
        this.duration = duration;
    }
    
    /**
     *
     * @return
     */
    public int getWindSpeedBinIndex() {
        return speedBinIndex;
    }
    
    /**
     *
     * @return
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     *
     * @return
     */
    public int getHeadingIndex() {
        return headingIndex;
    }
    
    /**
     *
     * @return
     */
    public Duration getDuration() {
        return duration;
    }
    
    /**
     *
     * @param duration
     */
    public void addDuration(Duration duration) {
        this.duration = this.duration.plus(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final SpeedBinDuration other = (SpeedBinDuration)obj;
        if (speedBinIndex != other.speedBinIndex)
            return false;

        if (!Objects.equals(this.date, other.date))
            return false;

        if (this.headingIndex != other.headingIndex)
            return false;

        return Objects.equals(this.duration, other.duration);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.speedBinIndex;
        hash = 29 * hash + Objects.hashCode(this.date);
        hash = 29 * hash + this.headingIndex;
        hash = 29 * hash + Objects.hashCode(this.duration);
        return hash;
    }
}