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

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;

/**
 * Class that holds both the speed of the wind and its direction.
 * 
 * @author bruce
 */
public class Wind implements Serializable {
    private static final long serialVersionUID = -8433403188400671214L;
    @XmlElement
    private final Speed speed;
    @XmlElement
    private final Heading direction;

    /**
     * Constructor.
     * 
     * @param speed The speed of the wind
     * @param direction The direction of the wind
     */
    public Wind(Speed speed, Heading direction) {
        this.speed = speed;
        this.direction = direction;
    }
    
    /**
     * Constructor.
     */
    private Wind() {
        this(new Speed(0.0), new Heading(0.0F));
    }

    /**
     * Get the wind speed.
     * 
     * @return The wind speed
     */
    public Speed getSpeed() {
        return speed;
    }

    /**
     * Get the wind direction.
     * 
     * @return The wind direction
     */
    public Heading getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "From " + direction + " at " + speed;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.speed);
        hash = 41 * hash + Objects.hashCode(this.direction);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Wind other = (Wind)obj;
        if (!Objects.equals(this.speed, other.speed))
            return false;

        return Objects.equals(this.direction, other.direction);
    }
}
