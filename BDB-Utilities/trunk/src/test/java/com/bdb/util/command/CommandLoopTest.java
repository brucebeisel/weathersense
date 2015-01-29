package com.bdb.util.command;

import java.lang.reflect.*;

import com.bdb.util.*;

import junit.framework.*;

public class CommandLoopTest extends TestCase
{
    private static int RETURN_VALUE = 25;
 
    public CommandLoopTest(String arg0)
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

    public void testCommandLoop()
    {
        CommandLoop loop = new CommandLoop("loop");
        
        String name = loop.getName();
        
        if (!name.equals("loop"))
            fail("Wrong name was retured - '" + name + "'");
    }
    
    public void handler()
    {
    }
    
    public void handler2(int i)
    {   
        if (i != 12)
            fail("Incorrect argument passed the command handler - " + i);
    }
    
    public int handler3()
    {
        return RETURN_VALUE;
    }

    public void testMainLoop()
    {
        CommandLoop loop = new CommandLoop("loop");
        Thread t = new Thread(loop);
        t.start();
        
        try
        {
            Method m = this.getClass().getMethod("handler");
            CommandTemplate cmd = new CommandTemplate(this, m, null);
            loop.sendCommand(cmd);
            cmd.waitForInvocation();
            
            if (cmd.getInvocationState() != CommandTemplate.State.NORMAL_RETURN)
                fail("Method handler was not called");
            
            m = this.getClass().getMethod("handler2", int.class);
            Object args[] = new Object[1];
            args[0] = new Integer(12);
            cmd = new CommandTemplate(this, m, args);
            loop.sendCommand(cmd);
            cmd.waitForInvocation();
            
            if (cmd.getInvocationState() != CommandTemplate.State.NORMAL_RETURN)
                fail("Method handler2 was not called");
            
            m = this.getClass().getMethod("handler3");
            
            cmd = new CommandTemplate(this, m, null);
            loop.sendCommand(cmd);
            cmd.waitForInvocation();
            Pair<CommandTemplate.State,Object> pair = cmd.getStateAndReturnValue();
            if (pair.first != CommandTemplate.State.NORMAL_RETURN)
            {
                fail("Command did not indicate that the method has been called");
            }
            else if (((Integer)pair.second).intValue() != RETURN_VALUE)
                fail("Command return value was not correct");
 
            m = this.getClass().getMethod("handler2", int.class);
            args = new Object[1];
            args[0] = new String("hello");
            cmd = new CommandTemplate(this, m, args);
            loop.sendCommand(cmd);
            cmd.waitForInvocation();
            
            if (cmd.getInvocationState() != CommandTemplate.State.EXCEPTION_THROWN)
                fail("Command handler with mismatched argument types did not throw an exception"); 
            else if (cmd.getException() == null)
                fail("Exception was not passed through to the command");
            else if (!(cmd.getException() instanceof IllegalArgumentException))
                fail("Expected exception not an IllegalArgumentException " + cmd.getException().getClass().getName());
            

            loop.sendExitCommand();
            
            ThreadUtils.sleep(500);
            
            if (t.isAlive())
                fail("Thread is still alive after requesting an exit");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
 
    }
}