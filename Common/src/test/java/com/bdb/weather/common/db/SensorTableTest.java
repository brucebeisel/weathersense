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
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorType;

/**
 *
 * @author Bruce
 */
public class SensorTableTest {
    static private DBConnection dbcon;
    private static DatabaseUtilities utils;

    public SensorTableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        dbcon = DatabaseUtilities.connectToDerby();
        utils = new DatabaseUtilities(dbcon);
    }
    
    @AfterClass
    public static void tearDownClass() {
        dbcon.close();
    }
    
    @Before
    public void setUp() throws SQLException {
        utils.createSensorTable();
    }
    
    @After
    public void tearDown() {
        utils.dropSensorTable();
    }

    /**
     * Test of query method, of class SensorTable.
     */
    @Test
    public void testQuery() {
        System.out.println("query");
        Sensor sensor = new Sensor(1, SensorType.HYGROMETER, "wine cellar");
        SensorTable instance = new SensorTable(dbcon);
        boolean result = instance.addRow(sensor);
        assertTrue(result);

        List<Sensor> list = instance.query();
        assertTrue(list.size() == 1);
        assertEquals(sensor, list.get(0));
    }

    /**
     * Test of addRow method, of class SensorTable.
     */
    @Test
    public void testAddRow() {
        System.out.println("addRow");
        Sensor sensor = new Sensor(1, SensorType.HYGROMETER, "wine cellar");
        SensorTable instance = new SensorTable(dbcon);
        boolean result = instance.addRow(sensor);
        assertTrue(result);
    }

    /**
     * Test of updateRow method, of class SensorTable.
     */
    @Test
    public void testUpdateRow() {
        System.out.println("updateRow");
        Sensor sensor = new Sensor(1, SensorType.HYGROMETER, "wine cellar");
        SensorTable instance = new SensorTable(dbcon);
        boolean result = instance.addRow(sensor);
        assertTrue(result);
        sensor = new Sensor(1, SensorType.HYGROMETER, "pool");
        result = instance.updateRow(sensor);
        assertTrue(result);
        List<Sensor> list = instance.query();
        assertTrue(list.size() == 1);
        assertEquals(sensor, list.get(0));
    }
}