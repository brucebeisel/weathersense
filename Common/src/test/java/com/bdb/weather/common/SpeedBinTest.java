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

import com.bdb.weather.common.measurement.Speed;
import org.junit.Assert;
import org.junit.Test;

public class SpeedBinTest {

    @Test
    public void testInSpeedBin() {
        SpeedBin bin = new SpeedBin(0, new Speed(0.0), new Speed(10.0));
        
        Assert.assertTrue(bin.inSpeedBin(new Speed(5.0)));
        Assert.assertTrue(bin.inSpeedBin(new Speed(0.1)));
        Assert.assertTrue(bin.inSpeedBin(new Speed(10.0)));
        Assert.assertFalse(bin.inSpeedBin(new Speed(0.0)));
        Assert.assertFalse(bin.inSpeedBin(new Speed(10.1)));
        
        bin = new SpeedBin(2, new Speed(10.0), SpeedBin.MAX_SPEED);
        
        Assert.assertTrue(bin.inSpeedBin(new Speed(10.1)));
        Assert.assertFalse(bin.inSpeedBin(new Speed(10.0)));
    }
        
    @Test(expected = IllegalArgumentException.class)
    public void testSpeedBinException() {
        SpeedBin bin = new SpeedBin(0, null, SpeedBin.MAX_SPEED);
        bin = new SpeedBin(0, SpeedBin.MIN_SPEED, null);
    }

}
