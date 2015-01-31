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
package com.bdb.weather.common.measurement;

import java.text.NumberFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bruce
 */
public class PressureTest {
    
    public PressureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of adjustForAltitude method, of class Pressure.
     */
    @Test
    public void testAdjustForAltitude_Distance() {
        System.out.println("adjustForAltitude");
        Distance altitude = new Distance(8.23, Distance.Unit.METERS);
        Pressure instance = new Pressure(1000.0);
        Pressure expResult = new Pressure(1001.0);
        Pressure result = instance.adjustForAltitude(altitude, true);
        assertEquals(expResult, result);
        Pressure expResult2 = new Pressure(999.0);
        Pressure result2 = instance.adjustForAltitude(altitude, false);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getDefaultUnit method, of class Pressure.
     */
    @Test
    public void testGetDefaultUnit() {
        System.out.println("getDefaultUnit");
        Pressure.Unit expResult = Pressure.Unit.MILLIBAR;
        Pressure.Unit result = Pressure.getDefaultUnit();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultUnit method, of class Pressure.
     */
    @Test
    public void testSetDefaultUnit() {
        System.out.println("setDefaultUnit");
        Pressure.Unit unit = Pressure.Unit.IN_HG;
        Pressure.setDefaultUnit(unit);
        Pressure.Unit result = Pressure.getDefaultUnit();
        assertEquals(Pressure.Unit.IN_HG, result);
        Pressure.setDefaultUnit(Pressure.Unit.MILLIBAR);
    }

    /**
     * Test of getDefaultFormatter method, of class Pressure.
     */
    @Test
    public void testGetDefaultFormatter() {
        System.out.println("getDefaultFormatter");
        Pressure.setDefaultUnit(Pressure.Unit.MILLIBAR);
        NumberFormat result = Pressure.getDefaultFormatter();
        String s = result.format(new Pressure(999.9).get());
        assertEquals("1000", s);
    }
}