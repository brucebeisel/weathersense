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

import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Distance;

/**
 * Class that specifies a location on Earth.
 * 
 * @author Bruce
 *
 */
public class GeographicLocation {
    @XmlElement
    private final AngularMeasurement latitude;
    @XmlElement
    private final AngularMeasurement longitude;
    @XmlElement
    private final Distance           altitude;      // Above Sea Level
    
    /**
     * Constructor.
     * 
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param altitude The altitude above sea level of the location
     */
    public GeographicLocation(AngularMeasurement latitude, AngularMeasurement longitude, Distance altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    private GeographicLocation() {
        this(null, null, null);
    }
    
    /**
     * Constructor that defaults the altitude to 0.
     * 
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     */
    public GeographicLocation(AngularMeasurement latitude, AngularMeasurement longitude) {
        this(latitude, longitude, new Distance(0.0));
    }

    /**
     * @return the latitude
     */
    public AngularMeasurement getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public AngularMeasurement getLongitude() {
        return longitude;
    }

    /**
     * @return the altitude
     */
    public Distance getAltitude() {
        return altitude;
    }
}
