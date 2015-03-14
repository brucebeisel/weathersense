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

import java.time.Duration;
import java.time.LocalDate;

import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;

import java.util.List;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

public class WindSliceTest {
    private static WindParameters wp;
    private static List<SpeedBin> speedBins;

    @BeforeClass
    public static void beforeClass() {
        wp = new WindParameters(new Speed(2.5, Speed.Unit.MILES_PER_HOUR), 5, 16);
        speedBins = wp.getSpeedBinList();
    }

    @Test
    public void testInSlice() {
        WindSlice ws = new WindSlice(LocalDate.now(), 1, speedBins, wp);
        
        Assert.assertTrue(ws.inSlice(new Heading(22.5F)));
        Assert.assertTrue(ws.inSlice(new Heading(22.5F - 11.25F)));
        Assert.assertTrue(ws.inSlice(new Heading(22.5F + 11.24F)));
        Assert.assertFalse(ws.inSlice(new Heading(22.5F + 11.25F)));
        Assert.assertFalse(ws.inSlice(new Heading(22.5F - 11.251F)));
    }

    @Test
    public void testApplyWindSlice() {
        
    }

    @Test
    public void testApplyWindSample() {
        Speed speed = new Speed(1.0);
        WindSlice ws = new WindSlice(LocalDate.now(), 1, speedBins, wp);
        ws.applyWindSample(Duration.ofSeconds(300), new Wind(speed, new Heading(22.5f)));
        assertEquals(ws.getSpeedBinDuration(speed).getDuration(), Duration.ofSeconds(300));
    }
}
