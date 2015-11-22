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
package com.bdb.weather.display.current;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.RainPlot;
import com.bdb.weather.display.RainPlot.RainEntry;

/**
 * Panel to display the current weather rain. This panel contains a bar graph showing all of the
 * rain data points from the last 24 hours then the rainfall amounts for:
 *     1. Last hour
 *     2. Today
 *     3. Yesterday
 *     4. This month
 *     5. Last month
 *     6. This season and the 3 previous seasons
 *     7. YTD
 *     8. Weather YTD
 * 
 * @author Bruce
 *
 */
public class CurrentWeatherRainPanel extends BorderPane {
    private final CurrentWeatherRainBucketsPanel bucketsPanel;
    private final RainPlot                       rainPlot;
    
    /**
     * Constructor.
     * 
     * @param ws The weather station for which this graph will display rain data
     * @param thisMonthAverage The seasons average rain for this month
     * @param lastMonthAverage The seasonal average rain for last month
     * @param yearlyAverage The seasonal average for the year
     * @param weatherYearAverageToDate The amount of rain that should have fallen so far this weather year
     * @param calendarYearAverageToDate  The amount of rain that should have fallen so far this time year
     */
    @SuppressWarnings("serial")
    public CurrentWeatherRainPanel(WeatherStation ws, Depth thisMonthAverage, Depth lastMonthAverage, Depth yearlyAverage, Depth calendarYearAverageToDate, Depth weatherYearAverageToDate) {
        rainPlot = new RainPlot();
        Node panel = rainPlot.getComponent();
        this.setCenter(panel);
        bucketsPanel = new CurrentWeatherRainBucketsPanel(ws, thisMonthAverage, lastMonthAverage, yearlyAverage, weatherYearAverageToDate, calendarYearAverageToDate);
        this.setBottom(bucketsPanel.getComponent());
    }
    
    /**
     * Return the swing component that contains the graph.
     * 
     * @return The swing container
     */
    public Node getComponent() {
        return this;
    }
    
    /**
     * Load the rain data.
     * 
     * @param ytd The amount of rain since Jan 1
     * @param fiscal The amount of rain this fiscal rain year
     * @param thisMonth The amount of rain this month
     * @param lastMonth The amount of rain last month
     * @param last24Hour The amount of rain in the last 24 hours
     * @param today The amount of rain since midnight
     * @param hour The amount of rain in the last 60 minutes
     * @param list The list of historical records for the rainfall graph.
     */
    public void setRainData(Depth ytd, Depth fiscal, Depth thisMonth, Depth lastMonth, Depth last24Hour, Depth today, Depth hour, List<RainEntry> list) {
        bucketsPanel.setRainData(ytd, fiscal, thisMonth, lastMonth, last24Hour, today, hour);
        rainPlot.setRainData(list);
    }
}