/*
 * Copyright (C) 2016 bruce
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

/**
 *
 * @author bruce
 */
public class Rainfall extends Depth {
    /**
     * Construct a Depth measurement object using the default units.
     * @param rainfall The value to initialize the rainfall measurement
     */
    public Rainfall(double rainfall) {
        this(rainfall, getDefaultUnit());
    }

    /**
     * Construct a Depth measurement object using the specified unit.
     * 
     * @param rainfall The value for the measurement
     * @param unit The unit of the value
     */
    public Rainfall(double rainfall, Unit unit) {
        super(rainfall, unit);
    }
    
    private Rainfall() {
        this(0.0);
    }
}