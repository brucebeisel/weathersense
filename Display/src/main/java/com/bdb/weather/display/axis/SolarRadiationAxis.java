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
package com.bdb.weather.display.axis;

import org.jfree.chart.axis.NumberAxis;

import com.bdb.weather.common.measurement.SolarRadiation;

/**
 *
 * @author Bruce
 */
public class SolarRadiationAxis extends NumberAxis {
    private static final long serialVersionUID = 6214282588197865707L;
    private static final String AXIS_LABEL = "Solar Radiation";

    public static SolarRadiationAxis create() {
        SolarRadiationAxis axis = new SolarRadiationAxis();
        axis.setAutoRangeIncludesZero(true);
        axis.setNumberFormatOverride(SolarRadiation.getDefaultUnit().getFormatter());
        return axis;
    }

    /**
     * Constructor.
     */
    private SolarRadiationAxis() {
        super(AXIS_LABEL + " (" + SolarRadiation.getDefaultUnit() + ")");
    }
}