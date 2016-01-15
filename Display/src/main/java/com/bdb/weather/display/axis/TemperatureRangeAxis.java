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
package com.bdb.weather.display.axis;

import org.jfree.chart.axis.NumberAxis;

import com.bdb.weather.common.measurement.Temperature;

/**
 * Range axis for temperature plots that always adds an upper and lower margin.
 * 
 * @author Bruce
 *
 */
public final class TemperatureRangeAxis extends NumberAxis {
    private static final String AXIS_LABEL = "Temperature";
    private static final double INITIAL_UPPER_MARGIN = .2;
    private static final double INITIAL_LOWER_MARGIN = .2;

    /**
     * Constructor.
     */
    public TemperatureRangeAxis() {
        super(AXIS_LABEL + " (" + Temperature.getDefaultUnit() + ")");
        
        setUpperMargin(INITIAL_UPPER_MARGIN);
        setLowerMargin(INITIAL_LOWER_MARGIN);
        setAutoRangeIncludesZero(false);
        setNumberFormatOverride(Temperature.getDefaultUnit().getFormatter());
    }
}
