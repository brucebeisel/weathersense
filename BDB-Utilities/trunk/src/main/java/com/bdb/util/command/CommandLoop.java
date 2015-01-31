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
package com.bdb.util.command;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a main loop function for a thread that implements a Command pattern
 *
 * @author Bruce
 */
public class CommandLoop implements Runnable, CommandReceiver {
    private final ArrayBlockingQueue<Command> m_queue = new ArrayBlockingQueue<>(10000);
    private boolean m_exit = false;
    private final String m_name;
    private static final Logger s_log = Logger.getLogger(CommandLoop.class.getName());

    /**
     * Constructor
     *
     * @param name The name of the main loop. This name is used in logger output
     */
    public CommandLoop(String name) {
        m_name = name;
    }

    /**
     * Get the name of the main loop
     *
     * @return The name
     */
    public String getName() {
        return m_name;
    }

    /**
     * The main loop. This loop will continue to run until an exit is requested by calling
     * <code>requestExit</code>
     *
     */
    public void mainLoop() {
        s_log.log(Level.FINE, "Entering main loop - {0}", m_name);

        while (!m_exit)
            try {
                //
                // Wait for a Command object to be pushed onto the queue
                //
                s_log.fine("Waiting for new command");
                Command command = m_queue.take();
                s_log.fine("Got command off the queue");
                command.execute();
            }
            catch (InterruptedException | RuntimeException e) {
                s_log.log(Level.SEVERE, "Caught unexpected exception", e);
            }
    }

    /**
     * Push a command onto the command queue so that it can be processed by the command loop
     *
     * @param command The command object to be pushed onto the queue
     * @return True if the command was queued for execution
     */
    @Override
    public boolean sendCommand(Command command) {
        return m_queue.offer(command); 
    }

    /**
     * Method called by the command object built in
     * <code>requestExit</code> method
     *
     */
    public void handleExit() {
        m_exit = true;
    }

    /**
     * Request that this command loop exit
     *
     */
    @Override
    public void sendExitCommand() {
        try {
            s_log.log(Level.FINE, "Requesting exit of command loop ''{0}''", m_name);
            Method method = this.getClass().getMethod("handleExit");
            Command cmd = new CommandTemplate(this, method, null);
            sendCommand(cmd);
        }
        catch (NoSuchMethodException | SecurityException e) {
            s_log.log(Level.WARNING, " Main loop '" + m_name + "' caught unexpected exception", e);
        }
    }
    
    /**
     * Flush the command queue
     */
    public void flushCommandQueue() {
        m_queue.clear();
    }

    /**
     * Entry point for the Runnable, calling
     * <code>mainLoop</code> directly will have the same effect without the extra thread.
     */
    @Override
    public void run() {
        mainLoop();
    }
}
