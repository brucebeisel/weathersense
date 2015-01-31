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

import java.lang.reflect.*;
import java.util.logging.*;

import com.bdb.util.*;

/**
 * A Command class that uses reflection in order to avoid the need to define separate command classes. If there is interest in the
 * return value or the whether the method has been called, then do not send the same object to multiple CommandLoops.
 *
 * TODO: There is no indication that there was an attempt to call the method, but an exception was thrown. Do we need multiple
 * states? PRE_CALL, EXCEPTION_THROWN, NORMAL_RETURN?
 *
 * @author Bruce
 *
 */
public class CommandTemplate implements Command {
    private final Object object;
    private final Method method;
    private Object args[];
    private Object returnValue;
    private State invocationState;
    private Exception exception;
    private static final Logger logger = Logger.getLogger(CommandTemplate.class.getName());

    public enum State {
        PRE_CALL,         // The method has not been called yet
        EXCEPTION_THROWN, // The method was called, but an exception was thrown
        NORMAL_RETURN     // The method returned normally
    }

    /**
     * Constructor
     *
     * @param object The object on which the <code>method</code> will be called
     * @param method The method that will be called
     * @param args The arguments that will be passed to the method
     */
    public CommandTemplate(Object object, Method method, Object args[]) {
        this.object = object;
        this.method = method;
        invocationState = State.PRE_CALL;
        setArgs(args);

    }

    /**
     * Used to reset the command back to its initial state. This method should be called before a CommandTemplate object is sent
     * to a CommandReceiver a second time and the sending thread is interested in the state of the command.
     *
     */
    public synchronized void reset() {
        invocationState = State.PRE_CALL;
    }

    /**
     * Set the arguments that are used when the method is invoked
     *
     * @param args The arguments that will be passed to the method
     */
    public final void setArgs(Object args[]) {
        if (args != null) {
            args = new Object[args.length];
            System.arraycopy(this.args, 0, args, 0, args.length);
        }
    }

    /**
     * Execute the command by invoking the method with the arguments
     */
    @Override
    public synchronized void execute() {
        try {
            logger.finer("Invoking command");
            exception = null;
            invocationState = State.PRE_CALL;
            returnValue = null;
            returnValue = method.invoke(object, args);
            invocationState = State.NORMAL_RETURN;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            invocationState = State.EXCEPTION_THROWN;
            exception = e;
            logger.log(Level.SEVERE, "Error invoking command", e);
        }
        finally {
            logger.finer("Notifying...");
            notifyAll();
        }
    }

    /**
     * Returns the state of the method invocation
     *
     * @return The state of the method invocation
     */
    public synchronized State getInvocationState() {
        return invocationState;
    }

    /**
     * Get the return value from the method invoked in the command. This return value is only valid if
     * <code>hasBeenInvoked()</code> return true.
     *
     * @return A pair of values, the first indicating whether the method has been called, the second contains the return value of
     * the <code>Method.invoke()</code> method
     * @see java.lang.Method
     */
    public synchronized Pair<State, Object> getStateAndReturnValue() {
        return new Pair<>(invocationState, returnValue);
    }

    public synchronized Object getReturnValue() {
        return returnValue;
    }

    /**
     * Accessor for the exception that was thrown
     *
     * @return The exception that was thrown or null if not exception was thrown or the method has not been called yet
     */
    public synchronized Exception getException() {
        return exception;
    }

    /**
     * Method that blocks until the method in this CommandTemplate is called
     *
     * @return True if the method was called successfully, false if an exception was thrown
     *
     * @throws InterruptedException The <code>wait()</code> method threw this exception
     */
    public synchronized State waitForInvocation() throws InterruptedException {
        while (invocationState == State.PRE_CALL) {
            logger.fine("Waiting for method invocation");
            wait();
        }

        return invocationState;
    }
}
