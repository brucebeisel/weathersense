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

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author bruce
 */
public class SensorStation {
    @XmlElement
    private String name;
    @XmlElement
    private SensorStationType type;
    @XmlElement
    private int sensorStationId;
    
    /**
     *
     * @param sensorStationId
     * @param type
     */
    public SensorStation(int sensorStationId, SensorStationType type) {
        this(sensorStationId, type, "Sensor Station " + sensorStationId);
    }

    /**
     *
     * @param sensorStationId
     * @param type
     * @param name
     */
    public SensorStation(int sensorStationId, SensorStationType type, String name) {
        this.name = name;
        this.type = type;
        this.sensorStationId = sensorStationId;
    }

    private SensorStation() {
        this(1, SensorStationType.INTEGRATED_SENSOR_STATION, "");
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public SensorStationType getType() {
        return type;
    }
    
    /**
     *
     * @return
     */
    public int getSensorStationId() {
        return sensorStationId;
    }
}