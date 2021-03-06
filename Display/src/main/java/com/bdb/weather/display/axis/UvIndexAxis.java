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

/**
 *
 * @author Bruce
 */
public class UvIndexAxis extends NumberAxis {
    private static final long serialVersionUID = -9000606094616067096L;
    private static final String AXIS_LABEL = "UV Index";

    public static UvIndexAxis create() {
        UvIndexAxis axis = new UvIndexAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setRange(0F, 12F);
        return axis;
    }

    private UvIndexAxis() {
        super(AXIS_LABEL);
    }
}