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
package com.bdb.weather.display;

import java.time.LocalDate;

public interface ViewLauncher {
    void launchTodayView();
    void launchCurrentWeatherView();
    void launchDaySummaryView(LocalDate day);
    void launchDailySummariesView(LocalDate start, LocalDate end);
    void launchDailySummariesView(DateInterval interval);
    void launchMonthlySummariesView(DateInterval interval);
    void launchHistoricalFreePlotView();
    void launchDailyFreePlotView();
    void launchMonthlyFreePlotView();
    void launchHistoryEditor();
    void launchStripChart();
    void launchStormView();
}
