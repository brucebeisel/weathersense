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

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.WeatherStation;

/**
 * A class that contains a number of rain bucket widgets.
 * 
 * @author Bruce
 *
 */
public class CurrentWeatherRainBucketsPanel {
    private final JComponent    component = new JPanel(new GridLayout(1,0));
    private final RainBucket    lastHour;
    private final RainBucket    today;
    private final RainBucket    last24Hours;
    private final RainBucket    currentMonth;
    private final RainBucket    lastMonth;
    private final RainBucket    currentYear;
    private final RainBucket    weatherYear;
    
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
        lastHour = new RainBucket("Last Hour", ws.getDailyRainMax());
        today = new RainBucket("Today", ws.getDailyRainMax());
        last24Hours = new RainBucket("24 Hours", ws.getDailyRainMax());
        currentMonth = new RainBucket("Month", ws.getMonthlyRainMax(), thisMonthAverage);
        lastMonth = new RainBucket("Last Month", ws.getMonthlyRainMax(), lastMonthAverage);
        currentYear = new RainBucket("YTD", ws.getYearlyRainMax(), yearlyAverage, calendarYearAverageToDate);
        weatherYear = new RainBucket("Weather Year", ws.getYearlyRainMax(), yearlyAverage, weatherYearAverageToDate);
        
        component.add(lastHour.getComponent());
        component.add(today.getComponent());
        component.add(last24Hours.getComponent());
        component.add(currentMonth.getComponent());
        component.add(lastMonth.getComponent());
        component.add(currentYear.getComponent());
        component.add(weatherYear.getComponent());
    }
    
    /**
     * Get the swing component that contains the rain buckets.
     * 
     * @return The swing container
     */
    public JComponent getComponent() {
        return component;
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
