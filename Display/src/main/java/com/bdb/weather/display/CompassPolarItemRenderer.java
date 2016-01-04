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
package com.bdb.weather.display;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author Bruce
 */
public class CompassPolarItemRenderer extends DefaultPolarItemRenderer {
    private static final NumberTick TICKS[] = {
        new NumberTick(Double.valueOf(0.0),   "N",   TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(22.5),  "NNE", TextAnchor.BOTTOM_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(45.0),  "NE",  TextAnchor.BOTTOM_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(67.5),  "ENE", TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(90.0),  "E",   TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(112.5), "ESE", TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(135.0), "SE",  TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(157.5), "SSE", TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(180.0), "S",   TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(202.5), "SSW", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(225.0), "SW",  TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(247.5), "WSW", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(270.0), "W",   TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(292.5), "WNW", TextAnchor.BOTTOM_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(315.0), "NW",  TextAnchor.BOTTOM_RIGHT, TextAnchor.CENTER, 0.0),
        new NumberTick(Double.valueOf(337.5), "NNW", TextAnchor.BOTTOM_RIGHT, TextAnchor.CENTER, 0.0)
    };

    /**
     * Creates a new instance of DayWindDirRenderer
     */
    public CompassPolarItemRenderer() {
    }
    
    @Override
    public void drawAngularGridLines(Graphics2D g2, PolarPlot plot, List ticks, Rectangle2D dataArea) {
        super.drawAngularGridLines(g2, plot, Arrays.asList(TICKS), dataArea);
    }
}