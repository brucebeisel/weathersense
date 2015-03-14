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

import com.bdb.weather.common.measurement.Depth;

/**
 * Range axis for all rain plots.
 * 
 * @author Bruce
 *
 */
public final class RainRangeAxis extends NumberAxis {
    private static final long serialVersionUID = -3897750402592855081L;
    private static final String AXIS_LABEL = "Rainfall";
    private static final double UPPER_MARGIN = .2;

    /**
     * Constructor.
     */ 
    public RainRangeAxis() {
        super();
        setUpperMargin(UPPER_MARGIN);
        setRangeType(RangeType.POSITIVE);
        setNumberFormatOverride(Depth.getDefaultFormatter());
        resetLabel();
        setStandardTickUnits(new RainTickUnitSource());
    }
    
    /**
     * Resets the axis label.
     * TODO This may need to be in a base class so that the axis labels can be changed on the fly when the units preference is changed.
     */
    public void resetLabel() {
        setLabel(AXIS_LABEL + " (" + Depth.getDefaultUnit() + ")");
    }
}
