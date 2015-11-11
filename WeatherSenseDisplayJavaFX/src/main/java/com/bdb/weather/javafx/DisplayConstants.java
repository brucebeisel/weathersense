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
package com.bdb.weather.javafx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.jfree.chart.axis.CategoryLabelPositions;

public final class DisplayConstants {
    public static final String                 UNKNOWN_VALUE_STRING = "---";
    public static final String                 DATA_TAB_NAME = "Data";
    public static final String                 GRAPH_TAB_NAME = "Graph";
    public static final String                 WIND_DIR_WHEN_NO_WIND = "Calm";
    public static final CategoryLabelPositions CATEGORY_LABEL_ROTATION = CategoryLabelPositions.UP_90;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    
    private DisplayConstants() {}

    public static String formatDateTime(LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static String formatDate(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formatTime(LocalTime time) {
        return TIME_FORMATTER.format(time);
    }
}