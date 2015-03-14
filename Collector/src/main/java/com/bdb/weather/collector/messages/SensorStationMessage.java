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
package com.bdb.weather.collector.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bdb.weather.common.SensorStation;

/**
 * Class that holds a sensor station message.
 * 
 * @author Bruce
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SensorStationMessage {
    @XmlElement(name="sensorStation", type=SensorStation.class)
    private final List<SensorStation> sensorStationList;
    
    /**
     * Constructor.
     * 
     * @param sensorStations The list of sensor stations
     */
    public SensorStationMessage(List<SensorStation> sensorStations) {
        sensorStationList = new ArrayList<>();
        sensorStationList.addAll(sensorStations);
    }
    
    /**
     * The no argument constructor required by JAXB
     */
    private SensorStationMessage() {
        sensorStationList = new ArrayList<>();
    }
    
    /**
     * Get the sensor station list.
     * 
     * @return The sensor station list
     */
    public List<SensorStation> getSensorStationList() {
        return Collections.unmodifiableList(sensorStationList);
    }
    
    /**
     * Set the sensor station list.
     * 
     * @param sensorStations The sensor station list
     */
    public void setSensorStationList(List<SensorStation> sensorStations) {
        sensorStationList.clear();
        sensorStationList.addAll(sensorStations);
    }
}