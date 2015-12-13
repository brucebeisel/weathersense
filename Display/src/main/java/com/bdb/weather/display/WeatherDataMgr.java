/*
 * Copyright (C) 2015 bruce
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
import java.util.List;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.common.measurement.Depth;

/**
 * The purpose of this class is to retrieve the data needed to refresh the screens.
 * The threading is left to the calling class. This should be called from the same
 * thread every time. If a different thread calls the refresh method a warning will be issued.
 *
 * @author Bruce
 */
public final class WeatherDataMgr {
    private boolean needHourRain = false;
    private boolean needTodayRain = false;
    private boolean need24HourRain = false;
    private boolean needThisMonthRain = false;
    private boolean needWeatherYearRain = false;
    private boolean needCalendarYearRain = true;
    private DBConnection connection;
    private HistoryTable historyTable;
    private DailySummaryTable summaryTable;
    private WeatherStationTable wsTable;
    private TemperatureBinMgr temperatureBinMgr;
    private Month weatherYearStartMonth;
    private Depth calendarYearRainfallAtMidnight;
    private Depth weatherYearRainfallAtMidnight;
    private Depth thisMonthRainAtMidnight;
    private Depth lastMonthRain;
    private Depth hourRain;
    private Depth ytdRain;
    private Depth todayRain;
    private Depth last24HourRain;
    private Depth weatherYearRain;
    private Depth thisMonthRain;
    private LocalDate today;
    private List<HistoricalRecord> recentData;
    private SummaryRecord todaysSummary;
    private Thread thread = null;
    private static WeatherDataMgr instance;
    private static Logger logger = Logger.getLogger(WeatherDataMgr.class.getName());

    static {
        instance = new WeatherDataMgr();
    }

    public static WeatherDataMgr getInstance() {
        return instance;
    }

    private WeatherDataMgr() {
    }

    public void initialize(DBConnection connection, Month weatherStartMonth) {
        this.connection = connection;
        this.weatherYearStartMonth = weatherStartMonth;
        historyTable = new HistoryTable(connection);
        summaryTable = new DailySummaryTable(connection);
        wsTable = new WeatherStationTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);
        refreshData();
    }

    public void refreshData() {
        Thread currentThread = Thread.currentThread();

        if (thread == null) {
            thread = currentThread;
            today = LocalDate.now();
        }
        else if (thread != currentThread)
            logger.warning("Refreshing data on a different thread");

        WeatherStation ws = wsTable.getWeatherStation();
        temperatureBinMgr.refresh();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = now.minusHours(25);
        recentData = historyTable.queryRecordsForTimePeriod(start, now);

        if (needHourRain) {
            LocalDateTime hourBegin = now.minusHours(1);
            hourRain = historyTable.rainTotal(hourBegin, now);
        }
        
        if (need24HourRain) {
            start = now.minusHours(24);
            last24HourRain = historyTable.rainTotal(start, now);
        }
        
        if (needTodayRain) {
            start = now.toLocalDate().atStartOfDay();
            todayRain = historyTable.rainTotal(start, now);
        }
        
        LocalDateTime yearBegin = LocalDate.now().withDayOfYear(1).atStartOfDay();
        if (needCalendarYearRain) {
            ytdRain = historyTable.rainTotal(yearBegin, now);
        }

        if (needWeatherYearRain) {
            yearBegin = yearBegin.withMonth(weatherYearStartMonth.getValue());

            if (now.getMonth().getValue() < weatherYearStartMonth.getValue())
                yearBegin = yearBegin.minusYears(1);

            weatherYearRain = historyTable.rainTotal(yearBegin, now);
        }
        
        if (needThisMonthRain) {
            LocalDate nowDate = LocalDate.now();
            LocalDate monthBegin = nowDate.withDayOfMonth(1);
            thisMonthRain = historyTable.rainTotal(monthBegin.atStartOfDay(), now);
        }
        
        LocalDate monthBegin = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        LocalDate monthEnd = monthBegin.plusDays(monthBegin.lengthOfMonth());
        lastMonthRain = historyTable.rainTotal(monthBegin.atStartOfDay(), monthEnd.atTime(23, 59, 59));


        //recentData.stream().filter(

        todaysSummary = summaryTable.retrieveTodaysSummary(ws.getWindParameters(), temperatureBinMgr);
    }

    public void fillInCurrentWeather(CurrentWeather cw) {

    }
}
