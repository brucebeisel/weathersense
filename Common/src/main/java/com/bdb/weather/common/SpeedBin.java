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

import com.bdb.weather.common.measurement.Speed;

/**
 * Class that defines the range of an individual speed bin. The Speed bin is used to group wind data into a number of bins. These
 * bins are then used to create the wind rose.
 *
 * @author Bruce
 *
 */
public class SpeedBin {

    /**
     *
     */
    public static final Speed MIN_SPEED = new Speed(0.0);

    /**
     *
     */
    public static final Speed MAX_SPEED = new Speed(1000.0, Speed.Unit.MILES_PER_HOUR);
    private final int index;
    private final Speed min;
    private final Speed max;

    /**
     * Constructor
     *
     * @param index
     * @param min The minimum speed of this bin (exclusive)
     * @param max The maximum speed of this bin (inclusive)
     */
    public SpeedBin(int index, Speed min, Speed max) {
        if (min == null || max == null)
            throw new IllegalArgumentException("NULL arguments not permitted");

        this.min = min;
        this.max = max;
        this.index = index;
    }

    /**
     * Whether the specified speed is within this bin
     *
     * @param s The speed
     * @return true if the speed is within the bin
     */
    public boolean inSpeedBin(Speed s) {
        return s.get() > min.get() && s.get() <= max.get();
    }

    /**
     * Get the index of the speed bin
     * 
     * @return The index of this speed bin from 0 to n
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the bins minimum speed
     *
     * @return The minimum speed
     */
    public Speed getMin() {
        return min;
    }

    /**
     * Get the bins maximum speed
     *
     * @return The maximum speed
     */
    public Speed getMax() {
        return max;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final SpeedBin other = (SpeedBin)obj;
        return other.index == index;
    }

    /**
     *
     * @return
     */
    public String speedString() {
        String s;

        if (min == MIN_SPEED)
            s = "<=" + max;
        else if (max == MAX_SPEED)
            s = ">" + min;
        else
            s = min + "-" + max;

        return s;
    }

    @Override
    public String toString() {
        String s = "Index: " + index + " Speeds: " + speedString();
        return s;
    }
}
