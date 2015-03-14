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
import org.jfree.data.RangeType;

import com.bdb.weather.common.measurement.Speed;

/**
 * Range axis for wind speed plots.
 * 
 * @author Bruce
 *
 */
public final class WindSpeedRangeAxis extends NumberAxis {
    private static final long serialVersionUID = -8023810246249463147L;
    private static final String AXIS_LABEL = "Wind Speed";

    /**
     * Constructor.
     */
    public WindSpeedRangeAxis() {
        super(AXIS_LABEL + " (" + Speed.getDefaultUnit() + ")");
        setNumberFormatOverride(Speed.getDefaultFormatter());
        setAutoRangeIncludesZero(true);
        setRangeType(RangeType.POSITIVE);
    }
}
