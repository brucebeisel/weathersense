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
package com.bdb.weather.common.db;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;

/**
 *
 * @author beisbr
 */
public class HistoryTableTest {
    static private DBConnection dbcon;
    private final List<HistoricalRecord> historyList = new ArrayList<>();
    private static DatabaseUtilities utils;

    public HistoryTableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        dbcon = DatabaseUtilities.connectToDerby();
        dbcon.connect();
        utils = new DatabaseUtilities(dbcon);
    }

    @AfterClass
    public static void tearDownClass() {
        dbcon.close();
    }

    @Before
    public void setUp() throws SQLException {
        utils.createHistoryTables();
        LocalDateTime c = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        double highTemp = 22.0;
        double lowTemp = 19.0;
        double avgTemp = 20;
        double pressure = 1010.0;
        float inHumidity = 65.0F;
        float outHumidity = 50.0F;
        double inTemp = 16;
        double sensorTemp = 38;
        double rain = 3.0;
        double rainRate = 5.0;
        float heading = 180.0F;
        double speed = 1.0;
        LocalDateTime ht = c.toLocalDate().atStartOfDay();
        for (int i = 0; i < 20; i++) {
            HistoricalRecord rec = new HistoricalRecord(ht);
            rec.setDuration(Duration.ofSeconds(300));
            rec.setAvgOutdoorTemperature(new Temperature(avgTemp));
            rec.setHighOutdoorTemperature(new Temperature(highTemp));
            rec.setLowOutdoorTemperature(new Temperature(lowTemp));
            rec.setBaroPressure(new Pressure(pressure));
            rec.setOutdoorHumidity(new Humidity(outHumidity));
            rec.setIndoorHumidity(new Humidity(inHumidity));
            rec.setIndoorTemperature(new Temperature(inTemp));
            rec.setRainfall(new Depth(rain));
            rec.setAvgUvIndex(5.5f);
            rec.setHighUvIndex(UvIndex.getUvIndex(6));
            rec.setAvgSolarRadiation(new SolarRadiation(1.0));
            rec.setHighSolarRadiation(new SolarRadiation(9.0));
            rec.setEvapotranspiration(new Depth(20.0));
            if (rain > 0.0)
                rain -= 1.0;
            rec.setHighRainfallRate(new Depth(rainRate));
            if (rainRate > 0.0)
                rainRate -= 1.0;
            rec.setAvgWind(new Wind(new Speed(speed), new Heading(heading)));
            rec.setHighWind(new Wind(new Speed(speed * 2), new Heading(heading - 10.0F)));
            speed += 1.0;
            heading += 4.0;
            rec.setTemperatureForSensor(1, new Temperature(sensorTemp));
            rec.setHumidityForSensor(2, new Humidity(95.0));
            rec.setLeafWetnessForSensor(3, new LeafWetness(3));
            rec.setSoilMoistureForSensor(4, new SoilMoisture(5.0));
            highTemp += 1.0;
            lowTemp += 1.0;
            avgTemp += 1.0;
            pressure += .5;
            inHumidity += 1.0;
            outHumidity += 2.0;
            inTemp += 1.5;
            sensorTemp -= 1.0;
            historyList.add(rec);
            ht = ht.plusSeconds(300);
	}
    }

    @After
    public void tearDown() {
        utils.dropHistoryTables();
    }

    /**
     * Test of findNewestRecordTime method, of class HistoryTable.
     */
    @Test
    public void testFindNewestRecordTime() {
        System.out.println("findNewestRecordTime");
        HistoryTable instance = new HistoryTable(dbcon);
        LocalDateTime result = instance.findNewestRecordTime();
        assertNull(result);
	LocalDateTime c = LocalDateTime.now();
	HistoricalRecord history = new HistoricalRecord(c);
	instance.addRow(history);
	result = instance.findNewestRecordTime();
	assertEquals(c, result);
    }

    /**
     * Test of findOldestRecordTime method, of class HistoryTable.
     */
    @Test
    public void testFindOldestRecordTime() {
        System.out.println("findOldestRecordTime");
        HistoryTable instance = new HistoryTable(dbcon);
        LocalDateTime result = instance.findOldestRecordTime();
        assertNull(result);
	LocalDateTime oldestTime = LocalDateTime.now().minusMonths(1);
	HistoricalRecord history = new HistoricalRecord(oldestTime);
	instance.addRow(history);
	history = new HistoricalRecord(LocalDateTime.now());
	instance.addRow(history);
	result = instance.findOldestRecordTime();
	assertEquals(oldestTime, result);
    }

    /**
     * Test of dataRange method, of class HistoryTable.
     */
    @Test
    public void testDataRange() {
        System.out.println("dataRange");
        HistoryTable instance = new HistoryTable(dbcon);
	LocalDateTime oldestTime = LocalDateTime.now().minusMonths(1);
	HistoricalRecord history = new HistoricalRecord(oldestTime);
	instance.addRow(history);
        LocalDateTime newestTime = LocalDateTime.now();
	history = new HistoricalRecord(newestTime);
	instance.addRow(history);
	DateRange range = instance.dataRange();
	assertEquals(oldestTime, range.getStart());
	assertEquals(newestTime, range.getEnd());
    }

    /**
     * Test of timeOfRecordBefore method, of class HistoryTable.
     */
    @Test
    public void testTimeOfRecordBefore() {
        System.out.println("timeOfRecordBefore");
        HistoryTable instance = new HistoryTable(dbcon);
        for (HistoricalRecord rec : historyList) {
            boolean ret = instance.addRow(rec);
            assertTrue(ret);
        }
        LocalDateTime expResult = historyList.get(historyList.size() - 2).getTime();
        LocalDateTime c = historyList.get(historyList.size() - 1).getTime();
        LocalDateTime result = instance.timeOfRecordBefore(c);
        assertEquals(expResult, result);
        
        c = c.minusYears(1);
        result = instance.timeOfRecordBefore(c);
        assertNull(result);
    }

    /**
     * Test of queryRecordsForDay method, of class HistoryTable.
     */
    @Test
    public void testQueryRecordsForDay() {
        System.out.println("queryRecordsForDay");
        LocalDateTime day = historyList.get(0).getTime();
        HistoryTable instance = new HistoryTable(dbcon);
        for (HistoricalRecord rec : historyList) {
            boolean ret = instance.addRow(rec);
            assertTrue(ret);
        }
        List<HistoricalRecord> expResult = historyList;
        List<HistoricalRecord> result = instance.queryRecordsForDay(day.toLocalDate());
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult, result);
    }

    /**
     * Test of queryRecordsForTimePeriod method, of class HistoryTable.
     */
    @Test
    public void testQueryRecordsForTimePeriod() {
        System.out.println("queryRecordsForTimePeriod");
        HistoryTable instance = new HistoryTable(dbcon);
        for (HistoricalRecord rec : historyList) {
            boolean ret = instance.addRow(rec);
            assertTrue(ret);
        }
        List<HistoricalRecord> result = instance.queryRecordsForTimePeriod(historyList.get(0).getTime(),
                                                                           historyList.get(historyList.size() - 1).getTime());
        assertEquals(historyList, result);
    }

    /**
     * Test of numRecordsForDay method, of class HistoryTable.
     */
    @Test
    public void testNumRecordsForDay() {
        System.out.println("numRecordsForDay");
        final int NUM_RECORDS = 20;
        HistoryTable instance = new HistoryTable(dbcon);
	LocalDateTime c = LocalDateTime.now().withHour(6);
        for (int i = 0; i < NUM_RECORDS; i++) {
            c = c.minusMinutes(1);
            HistoricalRecord history = new HistoricalRecord(c);
            instance.addRow(history);
        }
        int expResult = NUM_RECORDS;
        int result = instance.numRecordsForDay(c.toLocalDate());
        assertEquals(expResult, result);
    }

    /**
     * Test of query method, of class HistoryTable.
     */
    //@Test
    public void testQuery() {
        System.out.println("query");
        String clause = "";
        HistoryTable instance = null;
        List expResult = null;
        List result = instance.query(clause);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addRow method, of class HistoryTable.
     */
    @Test
    public void testAddRow() {
        System.out.println("addRow");
        HistoricalRecord expResult = historyList.get(0);
        HistoryTable instance = new HistoryTable(dbcon);
        boolean ret = instance.addRow(expResult);
        assertTrue(ret);

        HistoricalRecord result = instance.query().get(0);
        assertEquals(expResult, result);
    }

    /**
     * Test of addRow method, of class HistoryTable.
     */
    @Test
    public void testAddRowPerformance() {
        System.out.println("addRowPerformance");
        HistoryTable instance = new HistoryTable(dbcon);
        long t1;
        long t0 = System.nanoTime();
        for (HistoricalRecord expResult : historyList) {
            instance.addRow(expResult);
        }
        t1 = System.nanoTime();
        long deltaInd = t1 - t0;
        System.out.println("" + deltaInd + " ns Individual adds");
        instance.deleteAllRows();
        
        t0 = System.nanoTime();
        instance.addHistoricalRecords(historyList);
        t1 = System.nanoTime();
        long deltaBlock = t1 - t0;
        System.out.println("" + deltaBlock + " ns Block adds");
        System.out.println("Ind - Block = " + ((float)(deltaInd - deltaBlock) / 1000000.0f) + " ms");
    }

    /**
     * Test of rainTotal method, of class HistoryTable.
     */
    @Test
    public void testRainTotal() {
        System.out.println("rainTotal");
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now().minusDays(14);
        HistoryTable instance = new HistoryTable(dbcon);
        Depth expResult = new Depth(0.0);
        Depth result = instance.rainTotal(start, end);
        assertEquals(expResult, result);

        double expRain = 0.0;
        for (HistoricalRecord rec : historyList) {
            boolean ret = instance.addRow(rec);
            expRain += rec.getRainfall().get();
            assertTrue(ret);
        }
        Depth resultRain = instance.rainTotal(historyList.get(0).getTime(), historyList.get(historyList.size() - 1).getTime());
        assertEquals(expRain, resultRain.get(), .1);

        end = end.minusMonths(1);
        try {
            result = instance.rainTotal(start, end);
            assertTrue(false);
        }
        catch (IllegalArgumentException e){}
    }
}