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

import com.bdb.weather.common.measurement.Distance;
import junit.framework.Assert;

import org.junit.Test;

/**
 * This is a special JUnit test because it is the first measurement class to inherit from a class other
 * than the base "Measurement" class.
 * 
 * @author Bruce
 *
 */
public class DistanceTest {

    @Test
    public void testGetDefaultUnit() {
        Assert.assertEquals(Distance.Unit.METERS, Distance.getDefaultUnit());   
    }

    @Test
    public void testSetDefaultUnitUnit() {
        Distance.setDefaultUnit(Distance.Unit.MILLIMETERS);
        Assert.assertEquals(Distance.Unit.MILLIMETERS, Distance.getDefaultUnit());
        Distance.setDefaultUnit(Distance.Unit.METERS);
    }

    @Test
    public void testDistanceDouble() {
        Distance distance = new Distance(1.0);       
        Assert.assertEquals(1000.0, distance.get(Distance.Unit.MILLIMETERS));
    }

    @Test
    public void testDistanceDoubleUnit() {
        Distance distance = new Distance(1.0, Distance.Unit.KILOMETERS);       
        Assert.assertEquals(1000000.0, distance.get(Distance.Unit.MILLIMETERS));
    }

}
