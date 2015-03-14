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

import com.bdb.weather.common.measurement.Speed;

/**
 * Beaufort Force class. A set of these classes make up the entire Beaufort scale.
 *
 * @author Bruce
 *
 */
public class BeaufortForce {
    private final int force;
    private final Speed minSpeed; // Inclusive
    private final Speed maxSpeed; // Exclusive
    private final String classification;
    private final String landDescription;
    private final String seaDescription;

    /**
     * Constructor.
     *
     * @param force The force number
     * @param minSpeed The minimum wind speed at which this force applies
     * @param maxSpeed The maximum wind speed at which this force applies
     * @param classification Description of the force classification
     * @param landDescription The description of the effect of this force on land
     * @param seaDescription The description of the effect of this force on the sea
     */
    public BeaufortForce(int force, Speed minSpeed, Speed maxSpeed, String classification, String landDescription, String seaDescription) {
        this.force = force;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.classification = classification;
        this.landDescription = landDescription;
        this.seaDescription = seaDescription;
    }

    public String getClassification() {
        return classification;
    }

    public String getLandDescription() {
        return landDescription;
    }

    public Speed getMaxSpeed() {
        return maxSpeed;
    }

    public Speed getMinSpeed() {
        return minSpeed;
    }

    public int getForce() {
        return force;
    }

    public String getSeaDescription() {
        return seaDescription;
    }

    /**
     * Test if the specified wind speed falls within this Beaufort force.
     *
     * @param speed The speed to test
     *
     * @return True if the specified speed is within this force
     */
    public boolean isInForceBand(Speed speed) {
        return speed.compareTo(minSpeed) >= 0 && speed.compareTo(maxSpeed) < 0;
    }
}
