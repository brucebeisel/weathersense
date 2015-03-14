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
public class HeadingTest {
    
    public HeadingTest() {
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
     * Test of get method, of class Heading.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Heading instance = new Heading(0.0);
        double expResult = 0.0;
        double result = instance.get();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of headingForSlice method, of class Heading.
     */
    @Test
    public void testHeadingForSlice() {
        System.out.println("headingForSlice");
        int numSlices = 16;
        for (int i = 0; i < numSlices; i++) {
            Heading expResult = new Heading((float)i * 22.5F);
            Heading result = Heading.headingForSlice(i, numSlices);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of isBetween method, of class Heading.
     */
    //@Test
    public void testIsBetween_3args_1() {
        System.out.println("isBetween");
        Heading low = null;
        Heading high = null;
        Heading.CompareMode mode = null;
        Heading instance = null;
        boolean expResult = false;
        boolean result = instance.isBetween(low, high, mode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isBetween method, of class Heading.
     */
    //@Test
    public void testIsBetween_3args_2() {
        System.out.println("isBetween");
        Heading center = null;
        float arcLength = 0.0F;
        Heading.CompareMode mode = null;
        Heading instance = null;
        boolean expResult = false;
        boolean result = instance.isBetween(center, arcLength, mode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isBetween method, of class Heading.
     */
    //@Test
    public void testIsBetween_3args_3() {
        System.out.println("isBetween");
        float low = 0.0F;
        float high = 0.0F;
        Heading.CompareMode mode = null;
        Heading instance = null;
        boolean expResult = false;
        boolean result = instance.isBetween(low, high, mode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
