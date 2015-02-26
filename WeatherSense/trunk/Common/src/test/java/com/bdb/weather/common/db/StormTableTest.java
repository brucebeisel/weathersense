/*
 * Copyright (C) 2015 Bruce
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
import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Storm;
import com.bdb.weather.common.measurement.Depth;

/**
 *
 * @author Bruce
 */
public class StormTableTest {
    static private DBConnection dbcon;
    private static DatabaseUtilities utils;
    
    public StormTableTest() {
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
    }
    
    @After
    public void tearDown() {
        utils.dropHistoryTables();
    }

    /**
     * Test of query method, of class StormTable.
     */
    @Test
    public void testQuery() {
        System.out.println("query");
        StormTable instance = new StormTable(dbcon);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(12);
        Storm storm = new Storm(startTime, endTime, new Depth(1.0));
        instance.addRow(storm);

        List<Storm> result = instance.query();
        assertEquals(1, result.size());
        storm = result.get(0);
        assertEquals(startTime, storm.getStartTime());
        assertEquals(endTime, storm.getEndTime());
    }

    /**
     * Test of retrieveLatestStorm method, of class StormTable.
     */
    //@Test
    public void testRetrieveLatestStorm() {
        System.out.println("retrieveLatestStorm");
        StormTable instance = null;
        Storm expResult = null;
        Storm result = instance.retrieveLatestStorm();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateRow method, of class StormTable.
     */
    @Test
    public void testUpdateRow() {
        System.out.println("updateRow");
        StormTable instance = new StormTable(dbcon);
        LocalDateTime endTime = LocalDateTime.now().minusHours(1);
        LocalDateTime startTime = endTime.minusHours(12);
        Storm storm = new Storm(startTime, endTime, new Depth(1.0));
        instance.addRow(storm);
        endTime = endTime.plusHours(1);
        storm = new Storm(startTime, endTime, new Depth(2.0));
        instance.updateRow(storm);

        List<Storm> result = instance.query();
        assertEquals(1, result.size());
        storm = result.get(0);
        assertEquals(endTime, storm.getEndTime());
        assertEquals(new Depth(2.0), storm.getStormRainfall());
    }

    /**
     * Test of saveStorm method, of class StormTable.
     */
    @Test
    public void testSaveStorm() {
        System.out.println("saveStorm");
        StormTable instance = new StormTable(dbcon);
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime endTime = now.minusHours(1);
        LocalDateTime startTime = endTime.minusHours(12);
        Storm storm = new Storm(startTime, endTime, new Depth(1.0));
        instance.addRow(storm);
        endTime = endTime.plusHours(1);
        storm = new Storm(startTime, endTime, new Depth(2.0));
        instance.saveStorm(storm);

        List<Storm> result = instance.query();
        assertEquals(1, result.size());
        storm = result.get(0);
        assertEquals(endTime, storm.getEndTime());
        assertEquals(new Depth(2.0), storm.getStormRainfall());

        storm = new Storm(startTime.plusDays(1), endTime.plusDays(1), new Depth(1.5));
        instance.saveStorm(storm);
        result = instance.query();
        assertEquals(2, result.size());
    }
    
}
