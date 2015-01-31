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
package com.bdb.weather.collector;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.DatabaseUtilities;
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
public class SummarizerTest {
    
    static private DBConnection dbcon;
    private final List<HistoricalRecord> historyList = new ArrayList<>();
    private static DatabaseUtilities utils;

    public SummarizerTest() throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");
        
        if (is != null)
            LogManager.getLogManager().readConfiguration(is);
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
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of refresh method, of class Summarizer.
     */
    //@Test
    public void testRefresh() throws Exception {
        System.out.println("refresh");
        Summarizer instance = null;
        instance.refresh();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSummary method, of class Summarizer.
     */
    //@Test
    public void testUpdateSummary() throws Exception {
        System.out.println("updateSummary");
        LocalDate date = null;
        Summarizer instance = null;
        instance.updateSummary(date);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of catchup method, of class Summarizer.
     */
    //@Test
    public void testCatchup() throws Exception {
        System.out.println("catchup");
        Summarizer instance = null;
        instance.catchup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
