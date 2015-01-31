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

public class BeaufortForceTest extends TestCase
{

    @Test
    public void testIsInForceBand()
    {
        BeaufortForce force = new BeaufortForce(1, new Speed(1.0), new Speed(5.0), "", "", "");
        
        if (force.isInForceBand(new Speed(.9)))
            fail(".9 is not in range");
        
        if (!force.isInForceBand(new Speed(1.0)))
            fail("1.0 should be in range");
        
        if (!force.isInForceBand(new Speed(4.9)))
            fail("4.9 should be in range");
        
        if (force.isInForceBand(new Speed(5.0)))
            fail("5.0 should not be in range");
    }
}
