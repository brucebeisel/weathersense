package com.bdb.util.command;

public interface CommandReceiver
{
    /**
     * Send a command to this CommandReceiver
     * 
     * @param command The command to be sent
     * @return Indicates success of processing the command
     */
    boolean sendCommand(Command command);
    
    /**
     * Send a special "exit" command to this CommandReceiver
     */
    void sendExitCommand();
}
