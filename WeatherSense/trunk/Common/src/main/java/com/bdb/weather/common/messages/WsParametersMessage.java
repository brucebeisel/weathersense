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
package com.bdb.weather.common.messages;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.bdb.weather.common.GeographicLocation;


/**
 *
 * @author Bruce
 */
@XmlRootElement
public class WsParametersMessage {
    @XmlElement
    private final String manufacturer;
    @XmlElement
    private final String model;
    @XmlElement
    private final String firmwareDate;
    @XmlElement
    private final String firmwareVersion;
    @XmlElement
    private final GeographicLocation location;
    @XmlElement
    private final int weatherYearStartMonth;
    @XmlElement(name="parameters", type=String.class)
    private final Map<String,String> parameters;

    private WsParametersMessage() {
        this(null, null, null, null, null, 0);
    }

    public WsParametersMessage(String manufacturer, String model, String firmwareDate, String firmwareVersion, GeographicLocation location, int weatherYearStartMonth) {
        this.location = location;
        this.manufacturer = manufacturer;
        this.model = model;
        this.firmwareDate = firmwareDate;
        this.firmwareVersion = firmwareVersion;
        this.weatherYearStartMonth = weatherYearStartMonth;
        parameters = new TreeMap<>();
    }

    public GeographicLocation getLocation() {
        return location;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getFirmwareDate() {
        return firmwareDate;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public int getWeatherYearStartMonth() {
        return weatherYearStartMonth;
    }
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public Set<String> getKeys() {
        return parameters.keySet();
    }
    public String getValueForKey(String key) {
        return parameters.get(key);
    }
}