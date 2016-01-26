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
package com.bdb.weather.common;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bdb.weather.common.measurement.Depth;

/**
 * Class that holds the averages for the months.
 * 
 * @author bruce
 */
public class MonthWeatherAverages implements WeatherAverages {
    private static final int MONTHS_PER_YEAR = Month.values().length;
    private final Map<Month,WeatherAverage> averages = new HashMap<>();
    
    /**
     * Constructor with initial data.
     * 
     * @param avgs
     * @throws IllegalArgumentException
     */
    public MonthWeatherAverages(List<WeatherAverage> avgs) throws IllegalArgumentException {
        if (avgs.size() != MONTHS_PER_YEAR)
            throw new IllegalArgumentException("Month Weather Averages list must have 12 items");
        
        averages.clear();
        for (WeatherAverage avg : avgs)
            averages.put(avg.getMonth(), avg);
    }
    
    /**
     *
     * @param date
     * @return
     */
    @Override
    public WeatherAverage getAverage(LocalDate date) {
        return averages.get(date.getMonth());
    }

    /**
     *
     * @param avg
     * @param date
     */
    @Override
    public void putAverage(WeatherAverage avg, LocalDate date) {
        Month month = date.getMonth();
        averages.put(month, avg);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Collection<WeatherAverage> getAllAverages() {
        return Collections.unmodifiableCollection(averages.values());
    }

    /**
     * Calculate how much rain falls in an average calendar year
     * 
     * @return The average rainfall for a year
     */
    public Depth calculateYearlyAverageRainfall() {
        Depth yearlyAverage = new Depth(0.0);
        
        for (WeatherAverage avg : averages.values())
            yearlyAverage = (Depth)yearlyAverage.add(avg.getRainfall());
        
        return yearlyAverage;
    }

    /**
     * Calculate how much rain should have fallen in the year as of the specified date.
     * 
     * @param date The date
     * @return The amount of rain that should have fallen so far
     */
    public Depth calculateCalendarYearAverageRainfallToDate(LocalDate date) {
        return calculateWeatherYearAverageRainfallToDate(date, Month.JANUARY);
    }
    
    /**
     * Calculate how much rain should have fallen in the weather year as of the specified date.
     * 
     * @param date The date
     * @param weatherYearStartMonth  The month in which the weather year starts
     * @return The amount of rain that should have fallen so far
     */
    public Depth calculateWeatherYearAverageRainfallToDate(LocalDate date, Month weatherYearStartMonth) {
        Depth totalAverage = new Depth(0.0);
        Month curMonth = date.getMonth();

        //
        // Start with the current month, then back up until the start of the weather year
        //
        WeatherAverage avg = averages.get(curMonth);
        double rain = avg.getRainfall().get();
        double monthRatio = (double)date.getDayOfMonth() / (double)date.getMonth().minLength();
        totalAverage = (Depth)totalAverage.add(new Depth(rain * monthRatio));

        while (curMonth != weatherYearStartMonth) {
            curMonth = curMonth.minus(1);
            avg = averages.get(curMonth);
            totalAverage = (Depth)totalAverage.add(avg.getRainfall());
        }
        
        return totalAverage;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.averages);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final MonthWeatherAverages other = (MonthWeatherAverages)obj;

        return Objects.equals(this.averages, other.averages);
    }
}