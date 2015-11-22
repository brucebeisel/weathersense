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


import javafx.scene.Node;

import javafx.scene.layout.TilePane;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.WeatherStation;

/**
 * A class that contains a number of rain bucket widgets.
 * 
 * @author Bruce
 *
 */
public class CurrentWeatherRainBucketsPanel extends TilePane {
    private final RainBucketNode    lastHour;
    private final RainBucketNode    today;
    private final RainBucketNode    last24Hours;
    private final RainBucketNode    currentMonth;
    private final RainBucketNode    lastMonth;
    private final RainBucketNode    currentYear;
    private final RainBucketNode    weatherYear;
    
    /**
     * Constructor.
     * 
     * @param ws The weather station record that contains minimums and maximums
     * @param thisMonthAverage The seasons average rain for this month
     * @param lastMonthAverage The seasonal average rain for last month
     * @param yearlyAverage The seasonal average for the year
     * @param weatherYearAverageToDate The amount of rain that should have fallen so far this weather year
     * @param calendarYearAverageToDate  The amount of rain that should have fallen so far this calendar year
     */
    public CurrentWeatherRainBucketsPanel(WeatherStation ws, Depth thisMonthAverage, Depth lastMonthAverage, Depth yearlyAverage, Depth weatherYearAverageToDate, Depth calendarYearAverageToDate) {
        lastHour = new RainBucketNode("Last Hour", ws.getDailyRainMax());
        today = new RainBucketNode("Today", ws.getDailyRainMax());
        last24Hours = new RainBucketNode("24 Hours", ws.getDailyRainMax());
        currentMonth = new RainBucketNode("Month", ws.getMonthlyRainMax(), thisMonthAverage);
        lastMonth = new RainBucketNode("Last Month", ws.getMonthlyRainMax(), lastMonthAverage);
        currentYear = new RainBucketNode("YTD", ws.getYearlyRainMax(), yearlyAverage, calendarYearAverageToDate);
        weatherYear = new RainBucketNode("Weather Year", ws.getYearlyRainMax(), yearlyAverage, weatherYearAverageToDate);
        
        this.getChildren().add(lastHour.getComponent());
        this.getChildren().add(today.getComponent());
        this.getChildren().add(last24Hours.getComponent());
        this.getChildren().add(currentMonth.getComponent());
        this.getChildren().add(lastMonth.getComponent());
        this.getChildren().add(currentYear.getComponent());
        this.getChildren().add(weatherYear.getComponent());
    }
    
    /**
     * Get the swing component that contains the rain buckets.
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
     * @param last24Hours The amount of rain in the last 24 hours
     * @param today The amount of rain since midnight
     * @param hour The amount of rain in the last 60 minutes
     */
    public void setRainData(Depth ytd, Depth fiscal, Depth thisMonth, Depth lastMonth, Depth last24Hours, Depth today, Depth hour) {
        this.currentYear.setValue(ytd.get());
        this.weatherYear.setValue(fiscal.get());
        this.currentMonth.setValue(thisMonth.get());
        this.lastMonth.setValue(lastMonth.get());
        this.lastHour.setValue(hour.get());
        this.last24Hours.setValue(last24Hours.get());
        this.today.setValue(today.get());
    }
}
