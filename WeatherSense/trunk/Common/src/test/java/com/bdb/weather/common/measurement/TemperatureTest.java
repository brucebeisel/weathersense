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
public class TemperatureTest {
    
    public TemperatureTest() {
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
     * Test of getDefaultUnit method, of class Temperature.
     */
    @Test
    public void testGetDefaultUnit() {
        System.out.println("getDefaultUnit");
        Temperature.Unit expResult = Temperature.Unit.CELSIUS;
        Temperature.Unit result = Temperature.getDefaultUnit();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultUnit method, of class Temperature.
     */
    @Test
    public void testSetDefaultUnit() {
        System.out.println("setDefaultUnit");
        Temperature.Unit unit = Temperature.Unit.FAHRENHEIT;
        Temperature.setDefaultUnit(unit);
        Temperature.Unit unit2 = Temperature.getDefaultUnit();
        assertEquals(unit, unit2);
        Temperature.setDefaultUnit(Temperature.Unit.CELSIUS);
    }

    /**
     * Test of getDefaultFormatter method, of class Temperature.
     */
    @Test
    public void testGetDefaultFormatter() {
        System.out.println("getDefaultFormatter");
        Temperature t = new Temperature(10.0);
        NumberFormat formatter = Temperature.getDefaultFormatter();
        String s = formatter.format(t.get());
        assertEquals("10.0", s);

        s = Temperature.Unit.CELSIUS.getFormatterWithUnit().format(t.get());
        assertEquals("10.0 C", s);

        s = Temperature.Unit.CELSIUS.toString();
        assertEquals("\260C", s);
    }

    @Test
    public void testTemperatureUnit() {
	double val = Temperature.Unit.FAHRENHEIT.fromUnit(-40.0);
	assertEquals(-40.0, val, .01);

	val = Temperature.Unit.FAHRENHEIT.fromUnit(32.0);
	assertEquals(0.0, val, .01);

	val = Temperature.Unit.KELVIN.fromUnit(0);
	assertEquals(-273.0, val, .01);

	val = Temperature.Unit.CELSIUS.fromUnit(100.0);
	assertEquals(100.0, val, .01);

	val = Temperature.Unit.FAHRENHEIT.toUnit(0.0);
	assertEquals(32.0, val, .01);

	val = Temperature.Unit.KELVIN.toUnit(0.0);
	assertEquals(273.0, val, .01);

	val = Temperature.Unit.CELSIUS.toUnit(100.0);
	assertEquals(100.0, val, .01);

        
    }
}