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
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
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
    
    private static final String DEFAULT_ADDRESS = "224.0.0.120";
    private static final int DEFAULT_PORT = 11461;
    private static final int RECEIVE_TIMEOUT_MILLIS = 2000;
    private final MulticastSocket socket;
    private final CurrentWeatherHandler handler;
    private Thread thread;
    private boolean exit;
    private final JAXBContext jaxbContext;
    private final Unmarshaller unmarshaller;
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

    @Override
    public void run() {
        byte[] b = new byte[10240];
        DatagramPacket packet = new DatagramPacket(b, b.length);
        
        while (!exit) {
            try {
                socket.receive(packet);
                String s = new String(b, 0, packet.getLength());
                packet.setLength(b.length);
                Object msg = unmarshaller.unmarshal(new StringReader(s));
                if (msg instanceof CurrentWeather) {
                    logger.info("Current weather: '" + s + "'");
                    handler.handleCurrentWeather((CurrentWeather)msg);
                }
                else
                    logger.log(Level.WARNING, "Current weather UDP packet could not be demarshalled");
            }
            catch (SocketTimeoutException e2) {
            }
            catch (IOException | JAXBException e) {
                    logger.log(Level.WARNING, "Caught exception while reading current weather UDP packet", e);
            }
            finally {
                synchronized(this) {
                    notifyAll();
                }
            }
        }
    }
}