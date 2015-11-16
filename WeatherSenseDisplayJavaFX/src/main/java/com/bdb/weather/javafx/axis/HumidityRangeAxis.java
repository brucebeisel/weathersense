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
package com.bdb.weather.javafx.axis;

import org.jfree.chart.axis.NumberAxis;

import com.bdb.weather.common.measurement.Humidity;

/**
 * Range axis for humidity plots.
 * 
 * @author Bruce
 *
 */
public final class HumidityRangeAxis extends NumberAxis {
    private static final long serialVersionUID = -6181318333459918570L;
    private static final String AXIS_LABEL = "Humidity (%)";
    
    /**
     * Constructor.
     */
    public HumidityRangeAxis() {
        super(AXIS_LABEL);
        setRange(Humidity.MIN_HUMIDITY.get(), Humidity.MAX_HUMIDITY.get());
        setNumberFormatOverride(Humidity.Unit.RELATIVE_HUMIDITY.getFormatter());
    }
}
