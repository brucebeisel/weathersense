package com.bdb.util;

public final class ThreadUtils
{
    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            
        }
    }
    
    public static void sleep(long millis, int nanos)
    {
        try
        {
            Thread.sleep(millis, nanos);
        }
        catch (InterruptedException e)
        {
            
        }
    }
}
