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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.ThresholdType;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.WindParameters;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
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
public class DailySummaryTableTest {
    static private DBConnection dbcon;
    static private WindParameters windParams;
    static private DatabaseUtilities utils;

    private List<SummaryRecord> summaryList = new ArrayList<>();
    private static TemperatureBinMgr temperatureBinMgr;

    public DailySummaryTableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        dbcon = DatabaseUtilities.connectToDerby();
        utils = new DatabaseUtilities(dbcon);
        temperatureBinMgr = new TemperatureBinMgr(dbcon);
	windParams = new WindParameters(new Speed(5.0, Speed.Unit.MILES_PER_HOUR), 5, 16);
    }
    
    @AfterClass
    public static void tearDownClass() {
        dbcon.close();
    }
    
    @Before
    public void setUp() throws SQLException {
        utils.createDailySummaryTables();

        List<TemperatureBin> tempBinList = new ArrayList<>();
	tempBinList.add(new TemperatureBin(0, ThresholdType.ABOVE_THRESHOLD, new Temperature(0.0)));
        temperatureBinMgr.replaceBins(tempBinList);

	LocalDate c = LocalDate.now();
	for (int j = 0; j < 20; j++) {
	    double highTemp = 22.0;
	    double lowTemp = 19.0;
	    double avgTemp = 20;
	    double pressure = 1010.0;
	    float inHumidity = 25;
	    float outHumidity = 10;
	    double inTemp = 16;
	    double sensorTemp = 38;
	    double rain = 3.0;
	    double rainRate = 5.0;
	    float heading = 180.0F;
	    double speed = 1.0;
	    SummaryRecord summary = new SummaryRecord(windParams, temperatureBinMgr, c);
	    LocalDateTime ht = c.atStartOfDay();
	    for (int i = 0; i < 288; i++) {
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
		if (rain > 0.0)
		    rain -= 1.0;
		rec.setHighRainfallRate(new Depth(rainRate));
		if (rainRate > 0.0)
		    rainRate -= 1.0;
		rec.setAvgWind(new Wind(new Speed(speed), new Heading(heading)));
		rec.setHighWind(new Wind(new Speed(speed * 2), new Heading(heading - 10.0F)));
		speed += .10;
		heading += 4.0;
                if (heading >= 360)
                    heading = 10;
		rec.setTemperatureForSensor(1, new Temperature(sensorTemp));
		summary.applyHistoricalRecord(rec);
		highTemp += .10;
		lowTemp += .10;
		avgTemp += .10;
		pressure += .5;
		inHumidity += .10;
		outHumidity += .20;
		inTemp += .15;
		sensorTemp -= .11;
		ht = ht.plusSeconds(300);
	    }
	    summaryList.add(summary);
	    c = c.plusDays(1);
	}
    }
    
    @After
    public void tearDown() {
        utils.dropDailySummaryTables();
    }

    /**
     * Test of findNewestRecord method, of class DailySummaryTable.
     */
    @Test
    public void testFindNewestRecord() {
        System.out.println("findNewestRecord");
        DailySummaryTable instance = new DailySummaryTable(dbcon);
	boolean result = true;

        for (SummaryRecord record : summaryList)
	    result = result && instance.addRow(record);

        assertEquals(true, result);

        LocalDate start = summaryList.get(0).getDate();
        LocalDate end = summaryList.get(summaryList.size() - 1).getDate();

        LocalDate newest = instance.findNewestRecord();

	assertEquals(end, newest);
    }

    /**
     * Test of retrieveTodaysSummary method, of class DailySummaryTable.
     */
    @Test
    public void testRetrieveTodaysSummary() {
        System.out.println("retrieveTodaysSummary");
        SummaryRecord expRecord = summaryList.get(0);
        DailySummaryTable instance = new DailySummaryTable(dbcon);
        boolean result = instance.addRow(expRecord);
        assertEquals(true, result);

        SummaryRecord record = instance.retrieveTodaysSummary(windParams, temperatureBinMgr);
        assertEquals(expRecord, record);
    }

    /**
     * Test of retrieveRange method, of class DailySummaryTable.
     */
    @Test
    public void testRetrieveRange() {
        System.out.println("retrieveRange");
        DailySummaryTable instance = new DailySummaryTable(dbcon);
	boolean result = true;

        for (SummaryRecord record : summaryList)
	    result &= instance.addRow(record);

        assertEquals(true, result);

        LocalDate start = summaryList.get(0).getDate();
        LocalDate end = summaryList.get(summaryList.size() - 1).getDate();

        List<SummaryRecord> list = instance.retrieveRange(start, end, windParams, temperatureBinMgr);
	assertEquals(summaryList.size(), list.size());
    }

    /**
     * Test of addRow method, of class DailySummaryTable.
     */
    @Test
    public void testAddRow() {
        System.out.println("addRow");
        SummaryRecord record = summaryList.get(0);
	System.out.println(record);
        DailySummaryTable instance = new DailySummaryTable(dbcon);
        boolean result = instance.addRow(record);
        assertTrue(result);

	SummaryRecord rsummary = instance.retrieveSummaryForDate(record.getDate(), windParams, temperatureBinMgr);
	System.out.println(rsummary);
        result = instance.addRow(record);
        assertTrue(result);
	rsummary = instance.retrieveSummaryForDate(record.getDate(), windParams, temperatureBinMgr);
	System.out.println(rsummary);

    }
}