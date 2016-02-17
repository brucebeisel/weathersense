/*
 * Copyright (C) 2016 bruce
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

import java.time.Duration;
import java.time.LocalDateTime;


/**
 *
 * @author bruce
 */
public class BadBattery {
    private final int sensorStationId;
    private final LocalDateTime timeDetected;
    private Duration duration = Duration.ZERO;
    private int measurementCount;

    public BadBattery(int sensorStationId, LocalDateTime timeDetected) {
        this.sensorStationId = sensorStationId;
        this.timeDetected = timeDetected;
        measurementCount = 1;
    }

    public void incrementMeasurementCount(LocalDateTime time) {
        duration = Duration.between(timeDetected, time);
        measurementCount++;
    }

    public int getSensorStationId() {
        return sensorStationId;
    }

    public LocalDateTime getTimeDetected() {
        return timeDetected;
    }

    public int getMeasurementCount() {
        return measurementCount;
    }

    public Duration getDuration() {
        return duration;
    }
}