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


import java.time.LocalDateTime;


/**
 *
 * @author Bruce
 */
public class SensorStationStatus {
    private int sensorStationId;
    private LocalDateTime time;
    private Float batteryVoltage;
    private Boolean batteryOk;
    private Integer linkQuality;
    
    /**
     *
     * @param id
     * @param time
     * @param voltage
     * @param battery
     * @param link
     */
    public SensorStationStatus(int id, LocalDateTime time, Float voltage, Boolean battery, Integer link) {
        sensorStationId = id;
        this.time = time;
        batteryVoltage = voltage;
        batteryOk = battery;
        linkQuality = link;
    }

    private SensorStationStatus() {
        this(0, LocalDateTime.now(), null, null, null);
    }

    /**
     *
     * @return
     */
    public int getSensorStationId() {
        return sensorStationId;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     *
     * @return
     */
    public Float getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     *
     * @return
     */
    public Boolean isBatteryOk() {
        return batteryOk;
    }

    /**
     *
     * @return
     */
    public Integer getLinkQuality() {
        return linkQuality;
    }

    @Override
    public String toString() {
        return String.format("ID = %d  Voltage = %f  Battery OK = %s Link Quality = %d",
                      sensorStationId, batteryVoltage, batteryOk, linkQuality);
    }
}
