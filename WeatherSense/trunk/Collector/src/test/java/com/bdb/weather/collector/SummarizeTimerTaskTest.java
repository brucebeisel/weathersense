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

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.bdb.util.command.Command;
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
public class SummarizeTimerTaskTest {
    private Executor executor;
    private Runnable command;
    
    public SummarizeTimerTaskTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        executor = Executors.newSingleThreadExecutor();
        command = ()->{}; 
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isDuplicate method, of class SummarizeTimerTask.
     */
    @Test
    public void testIsDuplicate() {
        System.out.println("isDuplicate");
        LocalDate other = LocalDate.of(1961, Month.JANUARY, 14);
        SummarizeTimerTask instance = new SummarizeTimerTask(LocalDate.of(1961, Month.JANUARY, 14), executor, command);
        boolean result = instance.isDuplicate(other);
        assertTrue(result);
    }
}
