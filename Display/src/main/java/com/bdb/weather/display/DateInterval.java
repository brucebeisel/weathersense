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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import com.bdb.util.Season;

import com.bdb.weather.common.DateRange;

public enum DateInterval {

    TODAY("Today") {
        @Override
        public DateRange range() {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDateTime.now();
            return new DateRange(start, end);
        }
    },
    YESTERDAY("Yesterday") {
        @Override
        public DateRange range() {
            LocalDateTime start = LocalDate.now().atStartOfDay().minusDays(1);
            LocalDateTime end = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 23, 59, 59);
            return new DateRange(start, end);
        }
    },
    THIS_MONTH("This Month") {
        @Override
        public DateRange range() {
            LocalDateTime start = LocalDate.now().atStartOfDay().withDayOfMonth(1);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);
            return new DateRange(start, end);
        }
    },
    LAST_MONTH("Last Month") {
        @Override
        public DateRange range() {
            LocalDateTime start = LocalDate.now().atStartOfDay().withDayOfMonth(1).minusMonths(1);
            LocalDateTime end = start.plusMonths(1).minusSeconds(1);
            return new DateRange(start, end);
        }
    },
    LAST_30_DAYS("Last 30 Days") {
        @Override
        public DateRange range() {
            LocalDateTime end = LocalDate.now().atStartOfDay().minusDays(1);
            LocalDateTime start = end.minusDays(29);
            return new DateRange(start, end);
        }
    },
    THIS_SEASON("This Season") {
        @Override
        public DateRange range() {
            LocalDate date = LocalDate.now();
            Season season = Season.whichSeason(date);
            LocalDate start = season.seasonStart(date);
            LocalDate end = season.seasonEnd(date);
            return new DateRange(start.atStartOfDay(), end.atStartOfDay());
        }
    },
    LAST_SEASON("Last Season") {
        @Override
        public DateRange range() {
            LocalDate date = LocalDate.now();
            Season season = Season.currentSeason();
            LocalDate previousSeasonEnd = season.seasonStart(date).minusDays(1);
            season = season.previousSeason(date);
            LocalDate previous = season.seasonStart(previousSeasonEnd);
            LocalDate start = season.seasonStart(previous);
            LocalDate end = season.seasonEnd(previous);
            return new DateRange(start.atStartOfDay(), end.atStartOfDay());
        }
    },
    THIS_YEAR("This Year") {
        @Override
        public DateRange range() {
            LocalDateTime now = LocalDateTime.now().minusDays(1);
            LocalDateTime start = LocalDateTime.of(now.getYear(), Month.JANUARY, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
            return new DateRange(start, end);
        }
    },
    LAST_12_MONTHS("Last 12 Months") {
        @Override
        public DateRange range() {
            LocalDateTime now = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
            LocalDateTime start = end.minusMonths(12).withHour(0).withMinute(0).withSecond(0);
            return new DateRange(start, end);
        }
    },
    LAST_YEAR("Last Year") {
        @Override
        public DateRange range() {
            LocalDateTime start = LocalDateTime.of(LocalDate.now().getYear() - 1, Month.JANUARY, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(start.getYear(), Month.DECEMBER, Month.DECEMBER.maxLength(), 23, 59, 59);
            return new DateRange(start, end);
        }
    },
    CUSTOM("Custom Dates") {
        @Override
        public DateRange range() {
            LocalDateTime now = LocalDateTime.now();
            return new DateRange(now, now);
        }
    };

    private final String displayName;

    public static DateInterval[] availableIntervals() {
        return DateInterval.values();
    }

    private DateInterval(String display) {
        displayName = display;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public abstract DateRange range();
}
