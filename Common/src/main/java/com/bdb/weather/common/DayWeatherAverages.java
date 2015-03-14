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
import java.util.List;
import java.util.logging.Logger;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * @author Bruce
 *
 */
public class DayWeatherAverages implements WeatherAverages, Cloneable {
    private final DayOfYearCollection<WeatherAverage> averages = new DayOfYearCollection<>();
    private static final Logger s_logger = Logger.getLogger(DayWeatherAverages.class.getName());
    
    public DayWeatherAverages(DayOfYearCollection<WeatherAverage> averages) {
        for (WeatherAverage avg : averages.getAverages())
            this.averages.putItem(avg.getMonth(), avg.getDay(), avg);
    }
    
    public DayWeatherAverages() {
        //
        // 2001 chosen because it is not a leap year
        //
        LocalDate date = LocalDate.of(2001, Month.JANUARY, 1);
        while (date.getYear() != 2002) {
            WeatherAverage w = new WeatherAverage(date.getMonth(), date.getDayOfMonth(), new Temperature(0.0), new Temperature(0.0), new Temperature(0.0), new Depth(0.0));
            averages.addItem(date, w);
            date = date.plusDays(1);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            s_logger.info("Clone not supported");
            return null;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.common.WeatherAverages#putAverage(com.bdb.weather.common.WeatherAverage, java.util.LocalDate)
     */
    @Override
    public void putAverage(WeatherAverage avg, LocalDate date) {
        averages.addItem(date, avg);
    }
    
    /* (non-Javadoc)
     * @see com.bdb.weather.common.WeatherAverages#getAveragesFor(java.util.LocalDate)
     */
    @Override
    public WeatherAverage getAverage(LocalDate date) {
        return averages.item(date);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.common.WeatherAverages#getAllAverages()
     */
    @Override
    public List<WeatherAverage> getAllAverages() {
        return averages.getAverages();
    }

}