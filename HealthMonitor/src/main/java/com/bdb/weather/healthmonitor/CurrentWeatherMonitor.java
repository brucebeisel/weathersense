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


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.bdb.piglow4j.PiGlow;
import com.bdb.piglow4j.PiGlowColor;
import com.bdb.piglow4j.PiGlowLED;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;
import java.util.ArrayList;

/**
 *
 * @author Bruce Beisel
 */
public class CurrentWeatherMonitor implements HealthMonitor, CurrentWeatherSubscriber.CurrentWeatherHandler {
    private static final String MONITOR_NAME = "Current Weather Monitor";
    private CurrentWeatherSubscriber subscriber;
    private LocalDateTime lastCurrentWeather;
    private final int toleranceSeconds;
    private final PiGlow piGlow;
    private int counter = 0;
    private List<PiGlowLED> leds;

    public static CurrentWeatherMonitor createCurrentWeatherMonitor(PiGlow piGlow, List<PiGlowLED> leds, int toleranceMinutes) {
        List<PiGlowLED> list = new ArrayList<>(leds);
        CurrentWeatherMonitor monitor = new CurrentWeatherMonitor(piGlow, list, toleranceMinutes);
        monitor.init();
        return monitor;
    }

    private CurrentWeatherMonitor(PiGlow piGlow, List<PiGlowLED> leds, int toleranceMinutes) {
        this.piGlow = piGlow;
        this.leds = leds;
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
        counter++;
        leds.forEach((led)->led.setIntensity(0));
        leds.get(0).setIntensity(PiGlowLED.MAX_INTENSITY / 2);
        leds.add(leds.remove(0));
        try {
	    piGlow.updateLEDs();
        }
        catch (IOException e) {}
    }

    @Override
    public boolean isHealthy() {
        LocalDateTime now = LocalDateTime.now();
        Duration delta = Duration.between(lastCurrentWeather, now);
        return delta.getSeconds() < toleranceSeconds;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    public String getMailMessage() {
        return null;
    }

    @Override
    public String toString() {
        return subscriber.getStatistics().toString();
    }
}
