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
package com.bdb.weather.healthmonitor;


import java.time.Duration;
import java.time.LocalDateTime;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;

/**
 *
 * @author Bruce Beisel
 */
public class CurrentWeatherMonitor implements HealthMonitor, CurrentWeatherSubscriber.CurrentWeatherHandler {
    private CurrentWeatherSubscriber subscriber;
    private LocalDateTime lastCurrentWeather;
    private final int toleranceSeconds;

    public static CurrentWeatherMonitor createCurrentWeatherMonitor(int toleranceMinutes) {
        CurrentWeatherMonitor monitor = new CurrentWeatherMonitor(toleranceMinutes);
        monitor.init();
        return monitor;
    }

    private CurrentWeatherMonitor(int toleranceMinutes) {
        this.toleranceSeconds = toleranceMinutes * 60;
        lastCurrentWeather = LocalDateTime.now();
    }

    public void terminate() {
        subscriber.requestExit();
    }

    private void init() {
        subscriber = CurrentWeatherSubscriber.createSubscriber(this);
        lastCurrentWeather = LocalDateTime.now();
    }

    @Override
    public void handleCurrentWeather(CurrentWeather cw) {
        lastCurrentWeather = cw.getTime();
    }

    @Override
    public boolean isHealthy() {
        LocalDateTime now = LocalDateTime.now();
        Duration delta = Duration.between(lastCurrentWeather, now);
        return delta.getSeconds() < toleranceSeconds;
    }

    @Override
    public String toString() {
        return subscriber.getStatistics().toString();
    }
}