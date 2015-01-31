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

import com.bdb.weather.common.measurement.Temperature;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TemperatureBinDurationTest {

    @Test
    public void testTemperatureBinDurationStringCalendarTemperatureThresholdType() {
        Temperature.setDefaultUnit(Temperature.Unit.FAHRENHEIT);
        TemperatureBin bin = new TemperatureBin(0, ThresholdType.ABOVE_THRESHOLD, new Temperature(80.0));
        TemperatureBinDuration tbd = new TemperatureBinDuration(bin, LocalDate.now());
        assertEquals(tbd.getDuration(), Duration.ZERO);
    }
    
    @Test
    public void testTemperatureBinDurationStringCalendarTemperatureThresholdTypeInt() {
        Temperature.setDefaultUnit(Temperature.Unit.FAHRENHEIT);
        TemperatureBin bin = new TemperatureBin(0, ThresholdType.ABOVE_THRESHOLD, new Temperature(80.0));
        TemperatureBinDuration tbd = new TemperatureBinDuration(bin, LocalDate.now(), Duration.ofSeconds(100));
        assertEquals(tbd.getDuration(), Duration.ofSeconds(100));
    }

    @Test
    public void testAddDurationIntTemperature() {
        Temperature.setDefaultUnit(Temperature.Unit.FAHRENHEIT);
        TemperatureBin bin = new TemperatureBin(0, ThresholdType.ABOVE_THRESHOLD, new Temperature(80.0));
        TemperatureBinDuration tbd = new TemperatureBinDuration(bin, LocalDate.now());
        
        tbd.addDuration(new Temperature(70.0), Duration.ofSeconds(10));
        
        assertEquals(tbd.getDuration(), Duration.ZERO);
        
        tbd.addDuration(new Temperature(80.0), Duration.ofSeconds(10));
        
        assertEquals(tbd.getDuration(), Duration.ofSeconds(10));
        
        tbd.addDuration(new Temperature(90.0), Duration.ofSeconds(20));
        
        assertEquals(tbd.getDuration(), Duration.ofSeconds(30));
        
        bin = new TemperatureBin(0, ThresholdType.BELOW_THRESHOLD, new Temperature(80.0));
        tbd = new TemperatureBinDuration(bin, LocalDate.now());
    
        tbd.addDuration(new Temperature(90.0), Duration.ofSeconds(20));
        
        assertEquals(tbd.getDuration(), Duration.ZERO);
        
        tbd.addDuration(new Temperature(80.0), Duration.ofSeconds(20));
        
        assertEquals(tbd.getDuration(), Duration.ZERO);
        
        tbd.addDuration(new Temperature(79.0), Duration.ofSeconds(20));
        
        assertEquals(tbd.getDuration(), Duration.ofSeconds(20));
    }
}