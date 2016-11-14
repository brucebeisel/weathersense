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
package com.bdb.weather.display;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * @author Bruce
 *
 */
public final class WeatherSenseConstants {
    public static final double GUST_SHAPE_WIDTH = 2.0;
    public static final double GUST_SHAPE_HEIGHT = 2.0;
    public static final double GUST_SHAPE_X = -1.0;
    public static final double GUST_SHAPE_Y = -1.0;
    
    private WeatherSenseConstants() {}
    
    public static void configureGustRenderer(XYLineAndShapeRenderer renderer, int index) {
        renderer.setSeriesShape(index,  new Rectangle2D.Double(GUST_SHAPE_X, GUST_SHAPE_Y, GUST_SHAPE_WIDTH, GUST_SHAPE_HEIGHT));
        renderer.setSeriesLinesVisible(index, false);
        renderer.setSeriesShapesVisible(index, true);
    }
}
