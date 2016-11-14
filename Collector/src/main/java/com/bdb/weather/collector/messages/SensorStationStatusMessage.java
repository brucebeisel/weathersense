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
package com.bdb.weather.collector.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bdb.weather.common.SensorStationStatus;

/**
 * Class for the sensor station status message.
 * 
 * @author Bruce
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SensorStationStatusMessage {
    @XmlElement(name="sensorStationStatus", type=SensorStationStatus.class)
    private final List<SensorStationStatus> sensorStationStatusList;
    
    /**
     * Constructor.
     * 
     * @param sensorStationStatusList A list of sensor station statuses
     */
    public SensorStationStatusMessage(List<SensorStationStatus> sensorStationStatusList) {
        this.sensorStationStatusList = new ArrayList<>();
        this.sensorStationStatusList.addAll(sensorStationStatusList);
    }
    
    /**
     * No argument constructor required by JAXB.
     */
    private SensorStationStatusMessage() {
        sensorStationStatusList = new ArrayList<>();
    }
    
    /**
     * Get the list of sensor station statuses.
     * 
     * @return The list
     */
    public List<SensorStationStatus> getSensorStationStatusList() {
        return Collections.unmodifiableList(sensorStationStatusList);
    }
    
    /**
     * Set the list of sensor station statuses
     * 
     * @param sensorStationStatusList The list
     */
    public void setSensorStationStatusList(List<SensorStationStatus> sensorStationStatusList) {
        this.sensorStationStatusList.clear();
        this.sensorStationStatusList.addAll(sensorStationStatusList);
    }

    @Override
    public String toString() {
        String s = "";
        s = sensorStationStatusList.stream().map((status) -> status.toString() + "\n").reduce(s, String::concat);

        return s;
    }
}
