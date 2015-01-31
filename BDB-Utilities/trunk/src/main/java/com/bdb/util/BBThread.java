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

import java.util.logging.*;

/**
 * Class that provides main loop logic for a thread. There are three hooks that can
 * be overridden. An init hook and a cleanup hook that are optional and a processing
 * hook that must be overridden by the derived class.
 * 
 * @author Bruce
 *
 */
public abstract class BBThread extends Thread implements BBRunnable
{
    private boolean terminate = false;
    private long    intervalMillis;
    private static final Logger logger = Logger.getLogger(BBThread.class.getName());
    
    /**
     * Constructor
     *
     * @param threadName The name of the thread. This is passed along to the base class constructor
     */
    public BBThread(String threadName) {
        this(threadName, 0);
    }

    /**
     * Constructor
     *
     * @param threadName The name of the thread. This is passed along to the base class constructor
     * @param intervalMillis How often the thread should wake up. A value of zero will cause the thread to sleep until it is
     * notified.
     */
    public BBThread(String threadName, long intervalMillis) {
        super(threadName);
        this.intervalMillis = intervalMillis;
    }

    /**
     * Entry point for the thread
     */
    @Override
    public final void run() {
        logger.fine(String.format("Starting thread %s", getName()));
        initThread();
        mainLoop();
        cleanupThread();
        logger.fine(String.format("Exiting thread %s", getName()));
    }

    /**
     * Checks whether this thread is scheduled for termination
     *
     * @return True of the thread is scheduled for termination or has terminated
     */
    public boolean isTerminating() {
        return terminate;
    }

    /**
     * Request that the thread terminate. Calling this method does not mean that the thread will terminate. That is up to the
     * logic in the
     * <code>threadStep()</code> method.
     */
    @Override
    public final void requestTermination() {
        terminate = true;
        sendEvent();
    }

    /**
     * Send an event that will wake up this thread. This method is synchronized because the default implementation uses the
     * Object.wait() as the technique of pausing the thread waiting for something to occur.
     *
     */
    public synchronized void sendEvent() {
        notifyAll();
    }

    /**
     * Empty implementation of the initThread() method.
     *
     */
    public void initThread() {
    }

    /**
     * The default main loop. This method will only return if the thread has been asked to terminated. All exceptions are caught
     * and logged as warnings.
     *
     */
    public void mainLoop() {
        while (!terminate)
            try {
                threadStep();
                waitForEvent();
            }
            catch (Exception e) {
                logger.log(Level.WARNING, "Caught exception on thread " + getName(), e);
            }
    }

    /**
     * The method that waits for some event before waking the thread. The default implementation is a combination of a timer and
     * the Object notification pattern. This method can be overridden if a different event, such as I/O, is needed.
     *
     * @throws Exception Generic exception so that any classes that override this method can throw exceptions
     */
    public void waitForEvent() throws Exception {
        synchronized (this) {
            if (!terminate)
                wait(intervalMillis);
        }
    }

    /**
     * A single step in a thread.
     *
     * @throws Exception
     */
    public abstract void threadStep() throws Exception;

    /**
     * Empty implementation of the cleanupThread() method
     *
     */
    public void cleanupThread() {
    }
}
