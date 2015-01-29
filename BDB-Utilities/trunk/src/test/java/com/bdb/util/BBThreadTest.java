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
