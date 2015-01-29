package com.bdb.util.command;

/**
 * Interface to define a Command object
 * 
 * @author Bruce
 *
 */
public interface Command
{
    /**
     * Execute a command within a thread
     */
    void execute();
}
