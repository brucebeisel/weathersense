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
package com.bdb.weather.common;

import java.time.Month;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author bruce
 */
public class DailyRecords implements Cloneable, Comparable<DailyRecords> {
    private final Month month;
    private final int   day;
    private Temperature maxHighTemperature;
    private int         maxHighTemperatureYear;
    private Temperature minHighTemperature;
    private int         minHighTemperatureYear;
    private Temperature maxLowTemperature;
    private int         maxLowTemperatureYear;
    private Temperature minLowTemperature;
    private int         minLowTemperatureYear;
    private Depth       rainfall;
    private int         rainfallYear;

    /**
     *
     * @param month
     * @param day
     */
    public DailyRecords(Month month, int day) {
        this.month = month;
        this.day = day;
    }

    /**
     *
     * @return
     */
    public Month getMonth() {
        return month;
    }

    /**
     *
     * @return
     */
    public int getDay() {
        return day;
    }

    /**
     *
     * @return
     */
    public Temperature getMaxHighTemperature() {
        return maxHighTemperature;
    }

    /**
     *
     * @param t
     * @param year
     */
    public void setMaxHighTemperature(Temperature t, int year) {
        maxHighTemperature = t;
        maxHighTemperatureYear = year;
    }

    /**
     *
     * @return
     */
    public int getMaxHighTemperatureYear() {
        return maxHighTemperatureYear;
    }

    /**
     *
     * @return
     */
    public Temperature getMinHighTemperature() {
        return minHighTemperature;
    }

    /**
     *
     * @param t
     * @param year
     */
    public void setMinHighTemperature(Temperature t, int year) {
        minHighTemperature = t;
        minHighTemperatureYear = year;
    }

    /**
     *
     * @return
     */
    public int getMinHighTemperatureYear() {
        return minHighTemperatureYear;
    }

    /**
     *
     * @return
     */
    public Temperature getMaxLowTemperature() {
        return maxLowTemperature;
    }

    /**
     *
     * @param t
     * @param year
     */
    public void setMaxLowTemperature(Temperature t, int year) {
        maxLowTemperature = t;
        maxLowTemperatureYear = year;
    }

    /**
     *
     * @return
     */
    public int getMaxLowTemperatureYear() {
        return maxLowTemperatureYear;
    }

    /**
     *
     * @return
     */
    public Temperature getMinLowTemperature() {
        return minLowTemperature;
    }

    /**
     *
     * @param t
     * @param year
     */
    public void setMinLowTemperature(Temperature t, int year) {
        minLowTemperature = t;
        minLowTemperatureYear = year;
    }

    /**
     *
     * @return
     */
    public int getMinLowTemperatureYear() {
        return minLowTemperatureYear;
    }

    /**
     *
     * @return
     */
    public Depth getRainfall() {
        return rainfall;
    }

    /**
     *
     * @param d
     * @param year
     */
    public void setRainfall(Depth d, int year) {
        rainfall = d;
        rainfallYear = year;
    }

    /**
     *
     * @return
     */
    public int getRainfallYear() {
        return rainfallYear;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object object = null;

        try {
            object = super.clone();
        }
        catch (CloneNotSupportedException e) {
            // Ignore, cannot happen
        }

        return object;
    }

    @Override
    public int compareTo(DailyRecords o) {
        if (month != o.month)
            return month.compareTo(o.month);
        else
            return day - o.day;
    }
}