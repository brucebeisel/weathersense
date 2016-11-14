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
import java.util.ArrayList;
import java.util.List;
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
public class WindParametersTest {
    private WindParameters windParameters;
    
    public WindParametersTest() {
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
     * Test of getWindSpeedBinInterval method, of class WindParameters.
     */
    @Test
    public void testGetWindSpeedBinInterval() {
        System.out.println("getWindSpeedBinInterval");
        WindParameters instance = new WindParameters();
        Speed expResult = new Speed(5.0, Speed.Unit.MILES_PER_HOUR);
        Speed result = instance.getWindSpeedBinInterval();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNumWindSpeedBins method, of class WindParameters.
     */
    @Test
    public void testGetNumWindSpeedBins() {
        System.out.println("getNumWindSpeedBins");
        WindParameters instance = new WindParameters();
        int expResult = 5;
        int result = instance.getNumWindSpeedBins();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNumWindDirectionSlices method, of class WindParameters.
     */
    @Test
    public void testGetNumWindDirectionSlices() {
        System.out.println("getNumWindDirectionSlices");
        WindParameters instance = new WindParameters();
        int expResult = 16;
        int result = instance.getNumWindDirectionSlices();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSpeedBinList method, of class WindParameters.
     */
    @Test
    public void testGetSpeedBinList() {
        Speed bins[] = {
            new Speed(0.0),
            new Speed(5.0, Speed.Unit.MILES_PER_HOUR),
            new Speed(10.0, Speed.Unit.MILES_PER_HOUR),
            new Speed(15.0, Speed.Unit.MILES_PER_HOUR),
            new Speed(20.0, Speed.Unit.MILES_PER_HOUR),
            SpeedBin.MAX_SPEED
        };
        Speed.setDefaultUnit(Speed.Unit.MILES_PER_HOUR);
        List<SpeedBin> speedBinList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            speedBinList.add(new SpeedBin(i, bins[i], bins[i + 1]));
        }
        
        System.out.println("getSpeedBinList");
        WindParameters instance = new WindParameters(new Speed(5.0), 5, 16);
        List<SpeedBin> expResult = speedBinList;
        List<SpeedBin> result = instance.getSpeedBinList();
        assertEquals(expResult, result);
    }
}
