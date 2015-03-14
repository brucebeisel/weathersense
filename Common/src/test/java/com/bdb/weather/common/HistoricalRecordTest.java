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
import java.time.LocalDateTime;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;

/**
 *
 * @author Bruce
 */
public class HistoricalRecordTest {
    public HistoricalRecordTest() {
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
     * Test of getTime method, of class HistoricalRecord.
     */
    @Test
    public void testGetTime() {
        LocalDateTime init = LocalDateTime.now();
        
        System.out.println("getTime");
        HistoricalRecord instance = new HistoricalRecord(init);
        LocalDateTime result = instance.getTime();
        assertEquals(init, result);
        
        init = init.plusYears(1);
        assertFalse(init.equals(result));
    }

    /**
     * Test of setDuration method, of class HistoricalRecord.
     */
    @Test
    public void testDuration() {
        System.out.println("setDuration");
        Duration duration = Duration.ofSeconds(10);
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        instance.setDuration(duration);
        assertEquals(duration, instance.getDuration());
    }

    /**
     * Test of getMeasurementEntries method, of class HistoricalRecord.
     */
    //@Test
    public void testGetMeasurementEntries() {
        System.out.println("getMeasurementEntries");
        HistoricalRecord instance = null;
        Collection expResult = null;
        Collection result = instance.getMeasurementEntries();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTemperatureForSensor method, of class HistoricalRecord.
     */
    //@Test
    public void testGetTemperatureForSensor() {
        System.out.println("getTemperatureForSensor");
        int sensorId = 0;
        HistoricalRecord instance = null;
        Temperature expResult = null;
        Temperature result = instance.getTemperatureForSensor(sensorId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHumidityForSensor method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHumidityForSensor() {
        System.out.println("getHumidityForSensor");
        int sensorId = 0;
        HistoricalRecord instance = null;
        Humidity expResult = null;
        Humidity result = instance.getHumidityForSensor(sensorId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgOutdoorTemperature method, of class HistoricalRecord.
     */
    @Test
    public void testAvgOutdoorTemperature() {
        System.out.println("getAvgOutdoorTemperature");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = new Temperature(50.0);
        instance.setAvgOutdoorTemperature(expResult);
        Temperature result = instance.getAvgOutdoorTemperature();
        assertSame(expResult, result);
    }

    /**
     * Test of getLowOutdoorTemperature method, of class HistoricalRecord.
     */
    //@Test
    public void testGetLowOutdoorTemperature() {
        System.out.println("getLowOutdoorTemperature");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getLowOutdoorTemperature();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighOutdoorTemperature method, of class HistoricalRecord.
     */
   // @Test
    public void testGetHighOutdoorTemperature() {
        System.out.println("getHighOutdoorTemperature");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getHighOutdoorTemperature();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOutdoorHumidity method, of class HistoricalRecord.
     */
    //@Test
    public void testGetOutdoorHumidity() {
        System.out.println("getOutdoorHumidity");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Humidity expResult = null;
        Humidity result = instance.getOutdoorHumidity();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIndoorTemperature method, of class HistoricalRecord.
     */
    //@Test
    public void testGetIndoorTemperature() {
        System.out.println("getIndoorTemperature");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getIndoorTemperature();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIndoorHumidity method, of class HistoricalRecord.
     */
    //@Test
    public void testGetIndoorHumidity() {
        System.out.println("getIndoorHumidity");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Humidity expResult = null;
        Humidity result = instance.getIndoorHumidity();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgWind method, of class HistoricalRecord.
     */
    //@Test
    public void testGetAvgWind() {
        System.out.println("getAvgWind");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Wind expResult = null;
        Wind result = instance.getAvgWind();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighWind method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighWind() {
        System.out.println("getHighWind");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Wind expResult = null;
        Wind result = instance.getHighWind();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWindGust method, of class HistoricalRecord.
     */
    //@Test
    public void testGetWindGust() {
        System.out.println("getWindGust");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Wind expResult = null;
        Wind result = instance.getWindGust();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBaroPressure method, of class HistoricalRecord.
     */
    //@Test
    public void testGetBaroPressure() {
        System.out.println("getBaroPressure");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Pressure expResult = null;
        Pressure result = instance.getBaroPressure();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRainfall method, of class HistoricalRecord.
     */
    //@Test
    public void testGetRainfall() {
        System.out.println("getRainfall");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Depth expResult = null;
        Depth result = instance.getRainfall();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighRainfallRate method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighRainfallRate() {
        System.out.println("getHighRainfallRate");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Depth expResult = null;
        Depth result = instance.getHighRainfallRate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgUvIndex method, of class HistoricalRecord.
     */
    //@Test
    public void testGetAvgUvIndex() {
        System.out.println("getAvgUvIndex");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Float expResult = null;
        Float result = instance.getAvgUvIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighUvIndex method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighUvIndex() {
        System.out.println("getHighUvIndex");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        UvIndex expResult = null;
        UvIndex result = instance.getHighUvIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgSolarRadiation method, of class HistoricalRecord.
     */
    //@Test
    public void testGetAvgSolarRadiation() {
        System.out.println("getAvgSolarRadiation");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        SolarRadiation expResult = null;
        SolarRadiation result = instance.getAvgSolarRadiation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighSolarRadiation method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighSolarRadiation() {
        System.out.println("getHighSolarRadiation");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        SolarRadiation expResult = null;
        SolarRadiation result = instance.getHighSolarRadiation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEvapotranspiration method, of class HistoricalRecord.
     */
    //@Test
    public void testGetEvapotranspiration() {
        System.out.println("getEvapotranspiration");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Depth expResult = null;
        Depth result = instance.getEvapotranspiration();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWindChill method, of class HistoricalRecord.
     */
    //@Test
    public void testGetWindChill() {
        System.out.println("getWindChill");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getWindChill();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDewPoint method, of class HistoricalRecord.
     */
    //@Test
    public void testGetDewPoint() {
        System.out.println("getDewPoint");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getDewPoint();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeatIndex method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHeatIndex() {
        System.out.println("getHeatIndex");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Temperature expResult = null;
        Temperature result = instance.getHeatIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgWindSpeed method, of class HistoricalRecord.
     */
    //@Test
    public void testGetAvgWindSpeed() {
        System.out.println("getAvgWindSpeed");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Speed expResult = null;
        Speed result = instance.getAvgWindSpeed();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvgWindDirection method, of class HistoricalRecord.
     */
    //@Test
    public void testGetAvgWindDirection() {
        System.out.println("getAvgWindDirection");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Heading expResult = null;
        Heading result = instance.getAvgWindDirection();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighWindSpeed method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighWindSpeed() {
        System.out.println("getHighWindSpeed");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Speed expResult = null;
        Speed result = instance.getHighWindSpeed();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHighWindDirection method, of class HistoricalRecord.
     */
    //@Test
    public void testGetHighWindDirection() {
        System.out.println("getHighWindDirection");
        HistoricalRecord instance = new HistoricalRecord(LocalDateTime.now());
        Heading expResult = null;
        Heading result = instance.getHighWindDirection();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}