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
package com.bdb.weather.common.measurement;

import java.io.Serializable;

/**
 * Class the represents a UV index value.
 *
 * @author Bruce
 */
public class UvIndex implements Serializable {
    /**
     * The minimum UV index value
     */
    public static final int MIN_UV_INDEX = 0;
    /**
     * The maximum UV index value
     */
    public static final int MAX_UV_INDEX = 15;
    
    private static final UvIndex UV_INDEX_VALUES[] = new UvIndex[MAX_UV_INDEX + 1];
    static {
        for (int i = MIN_UV_INDEX; i <= MAX_UV_INDEX; i++)
            UV_INDEX_VALUES[i] = new UvIndex(i);
    }
    
    private final int index;
    
    /**
     * Get a UvIndex from a integer value.
     * 
     * @param uvIndex The integer equivalent of a UV index
     * @return The UV index
     */
    public static UvIndex getUvIndex(int uvIndex) {
        if (uvIndex < MIN_UV_INDEX || uvIndex > MAX_UV_INDEX)
            throw new IllegalArgumentException(String.format("UV Index must be between %d and %d (inclusive)", MIN_UV_INDEX, MAX_UV_INDEX));
        
        return UV_INDEX_VALUES[uvIndex];
    }

    /**
     * Constructor.
     * 
     * @param index The integer equivalent of a UV index
     */
    private UvIndex(int index) {
        if (index < MIN_UV_INDEX || index > MAX_UV_INDEX)
            throw new IllegalArgumentException(String.format("UV Index must be between %d and %d (inclusive)", MIN_UV_INDEX, MAX_UV_INDEX));
        
        this.index = index;
    }
    
    private UvIndex() {
        this(MIN_UV_INDEX);
    }
    
    /**
     * Get the index value.
     * 
     * @return The index
     */
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "" + index;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        final UvIndex other = (UvIndex)obj;
        
        return this.index == other.index;
    }
}