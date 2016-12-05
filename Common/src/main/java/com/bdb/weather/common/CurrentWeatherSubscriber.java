/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A class to subscribe to the current weather multicast UDP current weather packet and process the data in a separate thread.
 *
 * @author Bruce
 */
public class CurrentWeatherSubscriber implements Runnable {
    /**
     * Interface for classes to implement to receive the current weather.
     */
    public interface CurrentWeatherHandler {
        /**
         * Handle the current weather.
         * 
         * @param cw The current weather
         */
        void handleCurrentWeather(CurrentWeather cw);
    }
    
    /**
     *
     */
    public static final String DEFAULT_ADDRESS = "224.0.0.120";

    /**
     *
     */
    public static final int DEFAULT_PORT = 11461;
    private static final int RECEIVE_TIMEOUT_MILLIS = 2000;
    private final MulticastSocket socket;
    private final CurrentWeatherHandler handler;
    private Thread thread;
    private boolean exit;
    private final Gson gson;
    private final CurrentWeatherStatistics stats;
    private static final Logger logger = Logger.getLogger(CurrentWeatherSubscriber.class.getName());
    
    /**
     * Factory method for creating a current weather subscriber.
     * 
     * @param handler The handler that will process the current weather
     * 
     * @return The created subscriber
     */
    public static CurrentWeatherSubscriber createSubscriber(CurrentWeatherHandler handler) {
        try {
            CurrentWeatherSubscriber subscriber = new CurrentWeatherSubscriber(handler);
            subscriber.init();
            return subscriber;
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Caught I/O exception", ex);
            return null;
        }
    }
    
    /**
     * Private constructor.
     * 
     * @param handler The handler to process the current weather
     * @throws IOException The multicast socket could not be created
     */
    private CurrentWeatherSubscriber(CurrentWeatherHandler handler) throws IOException {
        stats = new CurrentWeatherStatistics();
        socket = new MulticastSocket(DEFAULT_PORT);
        socket.joinGroup (InetAddress.getByName(DEFAULT_ADDRESS));
        this.handler = handler;
        gson = GsonUtils.gsonBuilder();
        socket.setSoTimeout(RECEIVE_TIMEOUT_MILLIS);
    }
    
    /**
     * Initialize the subscriber.
     */
    private void init() {
        exit = false;
        thread = new Thread(this);
	thread.setName("current-weather-thread");
        thread.start();
    }
    
    /**
     * Request that the current weather subscriber thread exits.
     */
    public void requestExit() {
        exit = true;
    }

    /**
     * Get the statistics about the current weather data.
     * 
     * @return The statistics
     */
    public CurrentWeatherStatistics getStatistics() {
        return stats;
    }

    /**
     * Thread entry point.
     */
    @Override
    public void run() {
        byte[] b = new byte[10240];
        DatagramPacket packet = new DatagramPacket(b, b.length);
        
        while (!exit) {
            try {
                socket.receive(packet);
                String s = new String(b, 0, packet.getLength());
                logger.log(Level.FINER, "UDP Packet: {0}", s);
                CurrentWeather cw = gson.fromJson(s, CurrentWeather.class);
                handler.handleCurrentWeather(cw);
                logger.log(Level.FINE, "Current weather at {0}", cw.getTime());
                stats.receivedValidPacket();
            }
            catch (JsonSyntaxException e1) {
                logger.log(Level.WARNING, "Current weather packet could not be converted from JSON", e1);
            }
            catch (SocketTimeoutException e2) {
                logger.log(Level.INFO, "Timeout while waiting for current weather");
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "Caught exception while reading current weather UDP packet", e);
                stats.receivedInvalidPacket();
            }
        }
    }

    /**
     * The statistics collected about the current weather processing.
     */
    public static class CurrentWeatherStatistics implements Serializable {
        private final LocalDateTime collectionStartTime;
        private LocalDateTime lastValidPacketTime;
        private int validPacketsReceived;
        private int invalidPacketsReceived;
        private int packetsReceivedThisHour;
        private final DateTimeFormatter dtf;

        /**
         * Constructor.
         */
        public CurrentWeatherStatistics() {
            collectionStartTime = LocalDateTime.now();
            validPacketsReceived = 0;
            invalidPacketsReceived = 0;
            packetsReceivedThisHour = 0;
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }

        /**
         * Increment the invalid packet count.
         */
        public void receivedInvalidPacket() {
            invalidPacketsReceived++;
            packetsReceivedThisHour++;
        }

        /**
         * Increments the valid packet count.
         */
        public void receivedValidPacket() {
            validPacketsReceived++;
            lastValidPacketTime = LocalDateTime.now();
            packetsReceivedThisHour++;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Start Time: ").append(dtf.format(collectionStartTime)).append("\n");
            sb.append("Time of Last Current Weather: ");
	    if (lastValidPacketTime != null)
		sb.append(dtf.format(lastValidPacketTime)).append("\n");
	    else
		sb.append("N/A\n");

            sb.append(String.format("Valid Packets Received: %d, Invalid Packets Received %d%n", validPacketsReceived, invalidPacketsReceived));
            sb.append(String.format("Packets Received this Hour: %d", packetsReceivedThisHour));

            return sb.toString();
        }
    }
}