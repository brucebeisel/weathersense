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
package com.bdb.weather.collector.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.BBThread;

/**
 * Read ASCII text from a socket and send it to the message processor. This class runs in its own thread to separate the
 * network traffic from the database processing.
 * 
 * @author Bruce
 * @since 1.0
 *
 */
public class SocketReaderThread extends BBThread {
    private final int                  port;
    private ServerSocket               serverSocket = null;
    private Socket                     socket;
    private SocketReader               reader;
    private final SocketDataProcessor  consumer;
    private boolean                    closeSocket;
    private boolean                    closeSocketRequested;
    private static final Logger        logger = Logger.getLogger(SocketReaderThread.class.getName());
    
    /**
     * Create a thread to listen on a server socket and read from the sockets that
     * connect to the server socket.
     * 
     * @param port The port on which to listen for connections
     * @param consumer Callback object for data that is read from the socket
     */
    public SocketReaderThread(int port, SocketDataProcessor consumer) {
        super("Socket Reader");
        logger.log(Level.FINE, "Creating socket reader on port " + port);
        this.port = port;
        this.consumer = consumer;
        socket = null;
        closeSocketRequested = false;
    }
    
    /**
     * Wait for a connection from the weather station console driver.
     */
    private void waitForConnection() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            logger.fine("Wating for connection on port " + port);
            socket = serverSocket.accept();
            logger.fine("Socket connection accepted");
            serverSocket.close();
            serverSocket = null;
            socket.setSoTimeout(2000);
            reader = new SocketReader(socket, consumer);
        }
        catch (IOException e) {
            try {
                logger.log(Level.WARNING, "Failed to create new socket connection", e);
                if (serverSocket != null) {
                    serverSocket.close();
                    serverSocket = null;
                }
            }
            catch (IOException e2) {
                logger.log(Level.SEVERE, "Exception caught while closing server socket in catch block", e2);
            }
        }
    }

    /**
     * Request that the socket be closed.
     */
    public void closeSocketRequest() {
        closeSocketRequested = true;
    }

    /**
     * Close the socket and clean up the field values.
     */
    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed())
                socket.close();

            socket = null;
        }
        catch (IOException e) {
            logger.warning("Caught exception while closing socket");
        }
    }
    
    /**
     * Called each time through the main loop of the thread
     */
    @Override
    public void threadStep() {
        logger.entering(SocketReaderThread.class.getName(), "threadStep");
        if (socket == null)
            waitForConnection();
        else
            closeSocket = !reader.readSocket();

        synchronized(this) {
            if (closeSocket || closeSocketRequested)
                closeSocket();

            closeSocket = false;
            closeSocketRequested = false;
        }
    }
    
    /**
     * Method called by BBThread in order to wait for an event to occur.
     */
    @Override
    public synchronized void waitForEvent() {
        // Do nothing, threadStep will wait for data
    }
    
    /**
     * Method overridden from BBThread. This call is used to wake up the thread when
     * the thread is trying to be terminated.
     */
    @Override
    public synchronized void sendEvent() {
        closeSocket();
    }
}