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

import org.junit.Test;

import com.bdb.weather.common.measurement.AngularMeasurement;

import junit.framework.Assert;

public class AngularMeasurementTest {

    @Test
    public void testGetDefaultUnit() {
        Assert.assertEquals(AngularMeasurement.Unit.DEGREES, AngularMeasurement.getDefaultUnit());       
    }

    @Test
    public void testSetDefaultUnitUnit() {
        AngularMeasurement.setDefaultUnit(AngularMeasurement.Unit.RADIANS);
        Assert.assertEquals(AngularMeasurement.Unit.RADIANS, AngularMeasurement.getDefaultUnit());
        AngularMeasurement.setDefaultUnit(AngularMeasurement.Unit.DEGREES);
    }

    @Test
    public void testAngularMeasurementDouble() {
        AngularMeasurement angle = new AngularMeasurement(180.0);       
        Assert.assertEquals(Math.PI, angle.get(AngularMeasurement.Unit.RADIANS));
    }

    @Test
    public void testAngularMeasurementDoubleUnit() {
        AngularMeasurement angle = new AngularMeasurement(Math.PI, AngularMeasurement.Unit.RADIANS);      
        Assert.assertEquals(180.0, angle.get());
    }

}
