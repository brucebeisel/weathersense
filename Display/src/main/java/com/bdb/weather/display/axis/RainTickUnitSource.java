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

import java.text.NumberFormat;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;

import com.bdb.weather.common.measurement.Depth;

public class RainTickUnitSource extends TickUnits {
    private static final long serialVersionUID = -5092712597838074580L;

    public RainTickUnitSource() {
        NumberFormat format = Depth.getDefaultFormatter();

        add(new NumberTickUnit(0.01, format, 5));
        add(new NumberTickUnit(0.1, format, 5));
        add(new NumberTickUnit(1, format, 5));
        add(new NumberTickUnit(10, format, 5));
        add(new NumberTickUnit(100, format, 5));
        add(new NumberTickUnit(1000, format, 5));
        add(new NumberTickUnit(10000, format, 2));
        add(new NumberTickUnit(100000, format, 2));
        add(new NumberTickUnit(1000000, format, 2));
        add(new NumberTickUnit(10000000, format, 2));
        add(new NumberTickUnit(100000000, format, 2));
        add(new NumberTickUnit(1000000000, format, 2));
        add(new NumberTickUnit(10000000000.0, format, 2));
        add(new NumberTickUnit(100000000000.0, format, 2));

        add(new NumberTickUnit(0.25, format, 5));
        add(new NumberTickUnit(2.5, format, 5));
        add(new NumberTickUnit(25, format, 5));
        add(new NumberTickUnit(250, format, 5));
        add(new NumberTickUnit(2500, format, 5));
        add(new NumberTickUnit(25000, format, 5));
        add(new NumberTickUnit(250000, format, 5));
        add(new NumberTickUnit(2500000, format, 5));
        add(new NumberTickUnit(25000000, format, 5));
        add(new NumberTickUnit(250000000, format, 5));
        add(new NumberTickUnit(2500000000.0, format, 5));
        add(new NumberTickUnit(25000000000.0, format, 5));
        add(new NumberTickUnit(250000000000.0, format, 5));

        add(new NumberTickUnit(0.2, format, 5));
        add(new NumberTickUnit(0.4, format, 5));
        add(new NumberTickUnit(0.5, format, 5));
        add(new NumberTickUnit(5L, format, 5));
        add(new NumberTickUnit(50L, format, 5));
        add(new NumberTickUnit(500L, format, 5));
        add(new NumberTickUnit(5000L, format, 5));
        add(new NumberTickUnit(50000L, format, 5));
        add(new NumberTickUnit(500000L, format, 5));
        add(new NumberTickUnit(5000000L, format, 5));
        add(new NumberTickUnit(50000000L, format, 5));
        add(new NumberTickUnit(500000000L, format, 5));
        add(new NumberTickUnit(5000000000L, format, 5));
        add(new NumberTickUnit(50000000000L, format, 5));
        add(new NumberTickUnit(500000000000L, format, 5));

    }
}
