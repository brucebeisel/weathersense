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
package com.bdb.weather.display.summary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;

public enum SummaryInterval {
    DAY_INTERVAL(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT), DateFormat.getDateInstance(DateFormat.SHORT), Day.class, new DateTickUnit(DateTickUnitType.DAY, 1)),
    WEEK_INTERVAL(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT), DateFormat.getDateInstance(DateFormat.SHORT), Week.class, new DateTickUnit(DateTickUnitType.DAY, 7)),
    MONTH_INTERVAL(DateTimeFormatter.ofPattern("MMM yyyy"), new SimpleDateFormat("MMM yyyy"), Month.class, new DateTickUnit(DateTickUnitType.MONTH, 1)),
    YEAR_INTERVAL(DateTimeFormatter.ofPattern("yyyy"), new SimpleDateFormat("yyyy"), Year.class, new DateTickUnit(DateTickUnitType.YEAR, 1));
    
    private final DateTimeFormatter format;
    private final DateFormat        legacyFormat;
    private final Class<?>          freeChartClass;
    private final DateTickUnit      dateTickUnit;
    
    SummaryInterval(DateTimeFormatter format, DateFormat legacyFormat, Class<?> freeChartClass, DateTickUnit dateTickUnit) {
        this.format = format;
        this.legacyFormat = legacyFormat;
        this.freeChartClass = freeChartClass;
        this.dateTickUnit = dateTickUnit;
    }
    
    DateTimeFormatter getFormat() {
        return format;
    }
    
    DateFormat getLegacyFormat() {
        return legacyFormat;
    }
    
    Class<?> getFreeChartClass() {
        return freeChartClass;
    }
    
    DateTickUnit getDateTickUnit() {
        return dateTickUnit;
    }
}