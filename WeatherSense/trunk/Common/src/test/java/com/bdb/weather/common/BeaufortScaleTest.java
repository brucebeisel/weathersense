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
import junit.framework.TestCase;
import org.junit.Test;

public class BeaufortScaleTest extends TestCase
{

    @Test
    public void testLookupForce()
    {
        if (BeaufortScale.lookupForce(new Speed(0.0, Speed.Unit.MILES_PER_HOUR)).getForce() != 0)
            fail("Incorrect force returned for speed of 0.0");
        
        if (BeaufortScale.lookupForce(new Speed(100.0, Speed.Unit.MILES_PER_HOUR)).getForce() != 12)
            fail("Incorrect force returned for speed of 0.0");
        
        try
        {
            BeaufortScale.lookupForce(new Speed(500.0, Speed.Unit.MILES_PER_HOUR));
            fail("Wind speed of 500 MPH did not throw exception");
        }
        catch (IllegalArgumentException e)
        {
        }
        
        try
        {
            BeaufortScale.lookupForce(new Speed(-1.0, Speed.Unit.MILES_PER_HOUR));
            fail("Wind speed of -1.0 MPH did not throw exception");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

}
