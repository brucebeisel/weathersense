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

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;
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
public class MonthWeatherAveragesTest {
    private static final WeatherAverage avgArray[] = {
        new WeatherAverage(Month.JANUARY, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(64.0)),
        new WeatherAverage(Month.FEBRUARY, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(58.7)),
        new WeatherAverage(Month.MARCH, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(62.7)),
        new WeatherAverage(Month.APRIL, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(22.4)),
        new WeatherAverage(Month.MAY, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(7.1)),
        new WeatherAverage(Month.JUNE, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(2.3)),
        new WeatherAverage(Month.JULY, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(1.3)),
        new WeatherAverage(Month.AUGUST, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(1.8)),
        new WeatherAverage(Month.SEPTEMBER, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(6.6)),
        new WeatherAverage(Month.OCTOBER, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(12.2)),
        new WeatherAverage(Month.NOVEMBER, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(29.7)),
        new WeatherAverage(Month.DECEMBER, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(35.3))
    };

    public MonthWeatherAveragesTest() {
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
     * Test of getAverage method, of class MonthWeatherAverages.
     */
    @Test
    public void testGetAverage() {
        System.out.println("getAverage");
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));

        LocalDate c = LocalDate.now().withMonth(Month.JANUARY.getValue());
        WeatherAverage expResult = avgArray[0];
        WeatherAverage result = instance.getAverage(c);
        assertEquals(expResult, result);

        List<WeatherAverage> list = new ArrayList<>(Arrays.asList(avgArray));
        list.remove(0);
        try {
            new MonthWeatherAverages(list);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {}
    }

    /**
     * Test of putAverage method, of class MonthWeatherAverages.
     */
    @Test
    public void testPutAverage() {
        System.out.println("putAverage");
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));

        WeatherAverage avg = new WeatherAverage(Month.DECEMBER, new Temperature(21.0), new Temperature(4.0), new Temperature(12.4), new Depth(35.3));
        LocalDate c = LocalDate.now().withMonth(Month.DECEMBER.getValue());
        instance.putAverage(avg, c);
        WeatherAverage result = instance.getAverage(c);
        assertEquals(avg, result);
    }

    /**
     * Test of getAllAverages method, of class MonthWeatherAverages.
     */
    @Test
    public void testGetAllAverages() {
        System.out.println("getAllAverages");
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));
        List<WeatherAverage> expResult = Arrays.asList(avgArray);
        List<WeatherAverage> result = new ArrayList<>(instance.getAllAverages());
        Collections.sort(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateYearlyAverageRainfall method, of class MonthWeatherAverages.
     */
    @Test
    public void testCalculateYearlyAverageRainfall() {
        System.out.println("calculateYearlyAverageRainfall");
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));
        Depth expResult = new Depth(0.0);
        for (WeatherAverage avg : avgArray) {
            expResult = expResult.add(avg.getRainfall());
        }

        Depth result = instance.calculateYearlyAverageRainfall();
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateCalendarYearAverageRainfallToDate method, of class MonthWeatherAverages.
     */
    @Test
    public void testCalculateCalendarYearAverageRainfallToDate() {
        System.out.println("calculateCalendarYearAverageRainfallToDate");
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));

        LocalDate date = LocalDate.now().withMonth(Month.FEBRUARY.getValue()).withDayOfMonth(14);
        Depth expResult = (new Depth(avgArray[1].getRainfall().get() / 2.0)).add(avgArray[0].getRainfall());
        Depth result = instance.calculateCalendarYearAverageRainfallToDate(date);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateWeatherYearAverageRainfallToDate method, of class MonthWeatherAverages.
     */
    @Test
    public void testCalculateWeatherYearAverageRainfallToDate() {
        System.out.println("calculateWeatherYearAverageRainfallToDate");

        LocalDate date = LocalDate.now().withMonth(Month.MARCH.getValue()).withDayOfMonth(31);
        MonthWeatherAverages instance = new MonthWeatherAverages(Arrays.asList(avgArray));

        Month weatherYearStartMonth = Month.JULY;
        Depth expResult = avgArray[0].getRainfall().add(avgArray[1].getRainfall()).add(avgArray[2].getRainfall()).
                          add(avgArray[6].getRainfall()).add(avgArray[7].getRainfall()).add(avgArray[8].getRainfall()).
                          add(avgArray[9].getRainfall()).add(avgArray[10].getRainfall()).add(avgArray[11].getRainfall());
        Depth result = instance.calculateWeatherYearAverageRainfallToDate(date, weatherYearStartMonth);
        assertEquals(expResult, result);
    }
}