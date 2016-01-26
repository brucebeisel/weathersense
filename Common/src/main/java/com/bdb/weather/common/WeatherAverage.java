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

import java.time.Month;
import java.util.Objects;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Container class that holds the temperature and rainfall averages for a given month.
 * 
 * @author Bruce
 *
 */
public class WeatherAverage implements Comparable<WeatherAverage> {

    /**
     *
     */
    public enum Interval {

        /**
         *
         */
        DAILY,

        /**
         *
         */
        MONTHLY,

        /**
         *
         */
        YEARLY,

        /**
         *
         */
        CUSTOM
    };
    
    private final Interval interval;
    private final Month    month;
    private final int      day;
    private final Month    endMonth;
    private final int      endDay;
    private Temperature    highTemperature;
    private Temperature    lowTemperature;
    private Temperature    meanTemperature;
    private Depth          rainfall;
    
    /**
     * Constructor.
     * 
     * @param location The location for which these averages apply
     * @param month The month (1 - 12)
     */
    private WeatherAverage(Interval interval, Month month, int day, Month endMonth, int endDay, Temperature highTemperature, Temperature lowTemperature, Temperature meanTemperature, Depth rainfall) {
        this.interval = interval;
        this.month = month;
        this.day = day;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.highTemperature = highTemperature;
        this.lowTemperature = lowTemperature;
        this.meanTemperature = meanTemperature;
        this.rainfall = rainfall;
    }
    
    /**
     *
     * @param highTemperature
     * @param lowTemperature
     * @param meanTemperature
     * @param rainfall
     */
    public WeatherAverage(Temperature highTemperature, Temperature lowTemperature, Temperature meanTemperature, Depth rainfall) {
        this(Interval.YEARLY, Month.JANUARY, 0, Month.JANUARY, 0, highTemperature, lowTemperature, meanTemperature, rainfall);
    }
    
    /**
     *
     * @param month
     * @param highTemperature
     * @param lowTemperature
     * @param meanTemperature
     * @param rainfall
     */
    public WeatherAverage(Month month, Temperature highTemperature, Temperature lowTemperature, Temperature meanTemperature, Depth rainfall) {
        this(Interval.MONTHLY, month, 0, month, 0, highTemperature, lowTemperature, meanTemperature, rainfall);
    }
    
    /**
     *
     * @param month
     * @param day
     * @param highTemperature
     * @param lowTemperature
     * @param meanTemperature
     * @param rainfall
     */
    public WeatherAverage(Month month, int day, Temperature highTemperature, Temperature lowTemperature, Temperature meanTemperature, Depth rainfall) {
        this(Interval.DAILY, month, day, month, 0, highTemperature, lowTemperature, meanTemperature, rainfall);
    }
    
    /**
     *
     * @param month
     * @param day
     * @param endMonth
     * @param endDay
     * @param highTemperature
     * @param lowTemperature
     * @param meanTemperature
     * @param rainfall
     */
    public WeatherAverage(Month month, int day, Month endMonth, int endDay, Temperature highTemperature, Temperature lowTemperature, Temperature meanTemperature, Depth rainfall) {
        this(Interval.CUSTOM, month, day, endMonth, endDay, highTemperature, lowTemperature, meanTemperature, rainfall);
    }

    /**
     * Get the interval over which these averages apply.
     * 
     * @return The interval
     */
    public Interval getInterval() {
        return interval;
    }
    
    /**
     * Get the month for which these averages apply.
     * 
     * @return The month (1 - 12)
     */
    public Month getMonth() throws IllegalStateException {
        if (interval == Interval.YEARLY)
            throw new IllegalStateException("Cannot get the month for a YEARLY average");
       
        return month;
    }
    
    /**
     * Get the day for which these averages apply.
     * 
     * @return The day (1 - 31)
     */
    public int getDay() throws IllegalStateException {
        if (interval != Interval.DAILY)
            throw new IllegalStateException("Cannot get the day for an average that is not a DAILY average");
        
        return day;
    }
    
    /**
     *
     * @return
     * @throws IllegalStateException
     */
    public Month getEndMonth() throws IllegalStateException {
    	if (interval != Interval.CUSTOM)
    		throw new IllegalStateException("Cannot get end month for an average that is not a CUSTOM average");
    	
    	return endMonth;
    }
    
    /**
     *
     * @return
     * @throws IllegalStateException
     */
    public int getEndDay() throws IllegalStateException {
    	if (interval != Interval.CUSTOM)
    		throw new IllegalStateException("Cannot get end day for an average this is not a CUSTOM average");
    	
    	return endDay;
    }
    
    /**
     * Get the average high temperature for this location/month.
     * 
     * @return The average high temperature
     */
    public Temperature getHighTemperature() {
        return highTemperature;
    }
    
    /**
     *
     * @param value
     */
    public void setHighTemperature(Temperature value) {
        highTemperature = value;
    }
    
    /**
     * Get the average low temperature for this location/month.
     * 
     * @return The average low temperature
     */
    public Temperature getLowTemperature() {
        return lowTemperature;
    }
    
    /**
     *
     * @param value
     */
    public void setLowTemperature(Temperature value) {
        lowTemperature = value;
    }
    
    /**
     * Get the average mean temperature for this location/month. Yes, this
     * is an average of averages.
     * 
     * @return The average mean temperature
     */
    public Temperature getMeanTemperature() {
        return meanTemperature;
    }
    
    /**
     *
     * @param value
     */
    public void setMeanTemperature(Temperature value) {
        meanTemperature = value;
    }
    
    /**
     * Get the average rainfall for this location/month.
     * 
     * @return The average rainfall
     */
    public Depth getRainfall() {
        return rainfall;
    }

    /**
     *
     * @param value
     */
    public void setRainfall(Depth value) {
        rainfall = value;
    }
    
    @Override
    public int compareTo(WeatherAverage o) {
        if (o.month != this.month)
            return this.month.compareTo(o.month);
        else
            return this.day - o.day;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.interval);
        hash = 59 * hash + Objects.hashCode(this.month);
        hash = 59 * hash + this.day;
        hash = 59 * hash + Objects.hashCode(this.endMonth);
        hash = 59 * hash + Objects.hashCode(this.highTemperature);
        hash = 59 * hash + Objects.hashCode(this.lowTemperature);
        hash = 59 * hash + Objects.hashCode(this.meanTemperature);
        hash = 59 * hash + Objects.hashCode(this.rainfall);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final WeatherAverage other = (WeatherAverage)obj;
        if (this.interval != other.interval)
            return false;

        if (this.month != other.month)
            return false;

        if (this.day != other.day)
            return false;

        if (this.endMonth != other.endMonth)
            return false;

        if (this.endDay != other.endDay)
            return false;

        if (!Objects.equals(this.highTemperature, other.highTemperature))
            return false;

        if (!Objects.equals(this.lowTemperature, other.lowTemperature))
            return false;

        if (!Objects.equals(this.meanTemperature, other.meanTemperature))
            return false;

        return Objects.equals(this.rainfall, other.rainfall);
    }
}