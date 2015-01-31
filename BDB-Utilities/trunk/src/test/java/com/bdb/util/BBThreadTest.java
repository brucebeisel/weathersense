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
package com.bdb.util;

import junit.framework.*;

class BBTestThread extends BBThread
{
    public BBTestThread(String name)
    {
        super(name);
    }
    
    public BBTestThread(String name, long interval)
    {
            super(name, interval);
    }
    
    public void threadStep()
    {
       
    }
}

public class BBThreadTest extends TestCase
{

    public BBThreadTest(String arg0)
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testBBThreadString()
    {
 
        BBThread t = new BBTestThread("test thread");
        
        if (!t.getName().equals("test thread"))
            fail("Thread name did not get set properly");
    }

    public void testBBThreadStringLong()
    {
        
    }

    public void testIsTerminating()
    {
        BBThread t = new BBTestThread("test thread", 5000);
        
        t.start();
        
        ThreadUtils.sleep(500);
        
        if (t.isTerminating())
            fail("Terminating flag is set");
        
        t.requestTermination();
        
        if (!t.isTerminating())
            fail("Terminating flag is not set");
    }

    public void testRequestTermination()
    {
        BBThread t = new BBTestThread("test thread", 5000);

        t.start();

        t.requestTermination();
        
        ThreadUtils.sleep(500);
        
        if (t.getState() != Thread.State.TERMINATED)
            fail("Terminated thread has not terminated");
    }

}
