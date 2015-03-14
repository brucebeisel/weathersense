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
package com.bdb.weather.common;

import javax.xml.bind.annotation.XmlElement;

public class SensorStation {
    @XmlElement
    private String name;
    @XmlElement
    private SensorStationType type;
    @XmlElement
    private int sensorStationId;
    
    public SensorStation(int sensorStationId, SensorStationType type) {
        this(sensorStationId, type, "Sensor Station " + sensorStationId);
    }

    public SensorStation(int sensorStationId, SensorStationType type, String name) {
        this.name = name;
        this.type = type;
        this.sensorStationId = sensorStationId;
    }

    private SensorStation() {
        this(1, SensorStationType.INTEGRATED_SENSOR_STATION, "");
    }
    
    public String getName() {
        return name;
    }

    public SensorStationType getType() {
        return type;
    }
    
    public int getSensorStationId() {
        return sensorStationId;
    }
}