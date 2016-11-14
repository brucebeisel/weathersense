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
package com.bdb.weather.collector.socket;

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
public class SocketReaderThreadTest {
    
    /**
     *
     */
    public SocketReaderThreadTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of closeSocketRequest method, of class SocketReaderThread.
     */
    //@Test
    public void testCloseSocketRequest() {
        System.out.println("closeSocketRequest");
        SocketReaderThread instance = null;
        instance.closeSocketRequest();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of threadStep method, of class SocketReaderThread.
     */
    //@Test
    public void testThreadStep() {
        System.out.println("threadStep");
        SocketReaderThread instance = null;
        instance.threadStep();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of waitForEvent method, of class SocketReaderThread.
     */
    //@Test
    public void testWaitForEvent() {
        System.out.println("waitForEvent");
        SocketReaderThread instance = null;
        instance.waitForEvent();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendEvent method, of class SocketReaderThread.
     */
   // @Test
    public void testSendEvent() {
        System.out.println("sendEvent");
        SocketReaderThread instance = null;
        instance.sendEvent();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
