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

import com.bdb.util.measurement.Measurement;

/**
 * Class the represents a measurement of distance.
 * 
 * @author Bruce
 *
 */
public class Distance extends Depth {
    private static final long serialVersionUID = 6125056244763863987L;
    
    static {
        Measurement.setDefaultUnit(Distance.class, Unit.METERS);
    }
    
    /**
     * @return The current default unit for the distance measurement
     */
    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(Distance.class);
    }

    /**
     * Set the default unit for the distance measurement
     * 
     * @param unit The new default unit
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Distance.class, unit);
    }
    
    /**
     * Constructor that uses the default unit
     * 
     * @param distance The value of this measurement
     */
    public Distance(double distance) {
        this(distance, getDefaultUnit());
    }
    
    public Distance(int distance) {
        this(distance, getDefaultUnit());
    }
    
    /**
     * Constructor that requires a unit parameter
     * 
     * @param distance The value of this measurement
     * @param unit The units of the specified value
     */
    public Distance(double distance, Unit unit) {
        super(distance, unit);
    }
    
    @SuppressWarnings("unused")
	private Distance() {
        this(0.0);
    }

}
