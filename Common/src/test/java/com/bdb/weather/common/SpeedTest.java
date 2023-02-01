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

import org.junit.Assert;
import org.junit.Test;

import com.bdb.weather.common.measurement.Speed;

public class SpeedTest {

    @Test
    public void testSpeedDouble() {
        Speed s = new Speed(10);
        
        Assert.assertEquals(10.0, s.get(Speed.getDefaultUnit()), .1);

        System.out.println("" + s.get(Speed.Unit.MILES_PER_HOUR) + Speed.Unit.MILES_PER_HOUR);

        Speed.setDefaultUnit(Speed.Unit.MILES_PER_HOUR);

        System.out.println(s);

        System.out.println("" + s.get(Speed.Unit.KNOTS) + Speed.Unit.KNOTS);
        System.out.println("" + s.get(Speed.Unit.KM_PER_HOUR) + Speed.Unit.KM_PER_HOUR);
    }

}
