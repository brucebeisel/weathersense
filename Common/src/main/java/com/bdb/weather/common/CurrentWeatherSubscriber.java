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
package com.bdb.weather.common;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Bruce
 */
public class CurrentWeatherSubscriber implements Runnable {
    public interface CurrentWeatherHandler {
        void handleCurrentWeather(CurrentWeather cw);
    }
    public class CurrentWeatherStatistics implements Serializable {
        private LocalDateTime collectionStartTime;
        private LocalDateTime lastValidPacketTime;
        private int validPacketsReceived;
        private int invalidPacketsReceived;
        private int packetsReceivedThisHour;
        private DateTimeFormatter dtf;

        public CurrentWeatherStatistics() {
            collectionStartTime = LocalDateTime.now();
            validPacketsReceived = 0;
            invalidPacketsReceived = 0;
            packetsReceivedThisHour = 0;
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }

        public void receivedInvalidPacket() {
            invalidPacketsReceived++;
        }

        public void receivedValidPacket() {
            validPacketsReceived++;
            lastValidPacketTime = LocalDateTime.now();
            packetsReceivedThisHour++;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Start Time: ").append(dtf.format(collectionStartTime)).append("\n");
            sb.append("Time of Last Current Weather: ").append(dtf.format(lastValidPacketTime)).append("\n");
            sb.append(String.format("Valid Packets Received: %d, Invalid Packets Received %d\n", validPacketsReceived, invalidPacketsReceived));
            sb.append(String.format("Packets Received this Hour: %d", packetsReceivedThisHour));

            return sb.toString();
        }
    }
    
    private static final String DEFAULT_ADDRESS = "224.0.0.120";
    private static final int DEFAULT_PORT = 11461;
    private static final int RECEIVE_TIMEOUT_MILLIS = 10000;
    private final MulticastSocket socket;
    private final CurrentWeatherHandler handler;
    private Thread thread;
    private boolean exit;
    private final JAXBContext jaxbContext;
    private final Unmarshaller unmarshaller;
    private final CurrentWeatherStatistics stats;
    private static final Logger logger = Logger.getLogger(CurrentWeatherSubscriber.class.getName());
    
    public static CurrentWeatherSubscriber createSubscriber(CurrentWeatherHandler handler) {
        try {
            CurrentWeatherSubscriber subscriber = new CurrentWeatherSubscriber(handler);
            subscriber.init();
            return subscriber;
        }
        catch (IOException | JAXBException ex) {
            logger.log(Level.SEVERE, "Caught I/O exception", ex);
            return null;
        }
    }
    
    private CurrentWeatherSubscriber(CurrentWeatherHandler handler) throws IOException, JAXBException {
        stats = new CurrentWeatherStatistics();
        socket = new MulticastSocket(DEFAULT_PORT);
        socket.joinGroup (InetAddress.getByName(DEFAULT_ADDRESS));
        this.handler = handler;
        jaxbContext = JAXBContext.newInstance(com.bdb.weather.common.CurrentWeather.class,
                                              com.bdb.weather.common.measurement.LeafWetness.class,
                                              com.bdb.weather.common.measurement.SoilMoisture.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        socket.setSoTimeout(RECEIVE_TIMEOUT_MILLIS);
    }
    
    private void init() {
        exit = false;
        thread = new Thread(this);
        thread.start();
    }
    
    public void requestExit() {
        exit = true;
    }

    public CurrentWeatherStatistics getStatistics() {
        return stats;
    }

    @Override
    public void run() {
        byte[] b = new byte[10240];
        DatagramPacket packet = new DatagramPacket(b, b.length);
        
        while (!exit) {
            try {
                socket.receive(packet);
                String s = new String(b, 0, packet.getLength());
                logger.finer("UDP Packet: '" + s + "'");
                Object msg = unmarshaller.unmarshal(new StringReader(s));
                if (msg instanceof CurrentWeather) {
                    CurrentWeather cw = (CurrentWeather)msg;
                    handler.handleCurrentWeather(cw);
                    logger.log(Level.FINE, "Current weather at {0}", cw.getTime());
                    stats.receivedValidPacket();
                }
                else
                    logger.log(Level.WARNING, "Current weather UDP packet could not be demarshalled");
            }
            catch (SocketTimeoutException e2) {
                logger.log(Level.INFO, "Timeout while waiting for current weather");
            }
            catch (IOException | JAXBException e) {
                logger.log(Level.WARNING, "Caught exception while reading current weather UDP packet", e);
                stats.receivedInvalidPacket();
            }
        }
    }
}