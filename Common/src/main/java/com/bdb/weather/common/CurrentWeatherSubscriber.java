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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A class to subscribe to the current weather multicast UDP current weather packet.
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
    
    public static final String DEFAULT_ADDRESS = "224.0.0.120";

    public static final int                 DEFAULT_PORT = 11461;
    private static final int                RECEIVE_TIMEOUT_MILLIS = 10000;
    private final MulticastSocket           socket;
    private final CurrentWeatherHandler     handler;
    private Thread                          thread;
    private boolean                         exit;
    private final ObjectMapper              objectMapper = new ObjectMapper();
    private final CurrentWeatherStatistics  stats;
    private static final Logger             logger = Logger.getLogger(CurrentWeatherSubscriber.class.getName());
    
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
        InetAddress mcastAddress = InetAddress.getByName(DEFAULT_ADDRESS);
        //InetSocketAddress group = new InetSocketAddress(mcastAddress, DEFAULT_PORT);
        //NetworkInterface netIf = NetworkInterface.getByName("bge0");

        socket.joinGroup (new InetSocketAddress(mcastAddress, 0), null);
        this.handler = handler;
        socket.setSoTimeout(RECEIVE_TIMEOUT_MILLIS);
    }
    
    /**
     * Initialize the subscriber.
     */
    private void init() {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
                logger.log(Level.INFO, "UDP Packet: {0}", s);
                CurrentWeather cw = objectMapper.readValue(s, CurrentWeather.class);
				handler.handleCurrentWeather(cw);
				logger.log(Level.FINE, "Current weather at {0}", cw.getTime());
				stats.receivedValidPacket();
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
	public static class CurrentWeatherStatistics {
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
