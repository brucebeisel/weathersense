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
package com.bdb.weather.collector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.weather.common.CurrentWeatherSubscriber;

/**
 * Publishes the current weather on a multicast socket. This means that the database is not needed to
 * communicate the current weather to any running user interfaces.
 * 
 * @author Bruce
 */
final class CurrentWeatherPublisher {
    private MulticastSocket socket;
    private int port;
    private InetAddress group;
    private static final Logger logger = Logger.getLogger(CurrentWeatherPublisher.class.getName());
    
    /**
     * Constructor that opens the default address and default port.
     * 
     * @throws UnknownHostException Could not initialize the Multicast socket
     * @throws IOException Could not open the port
     */
    public CurrentWeatherPublisher() throws UnknownHostException, IOException {
        this(CurrentWeatherSubscriber.DEFAULT_ADDRESS, CurrentWeatherSubscriber.DEFAULT_PORT);
    }
    
    /**
     * Constructor.
     * 
     * @param address The address on which to create the Multicast socket
     * @param port The port on which to create the Multicast socket
     * @throws UnknownHostException Could not initialize the Multicast socket
     * @throws IOException Could not open the port
     */
    public CurrentWeatherPublisher(String address, int port) throws IOException, UnknownHostException {
        socket = new MulticastSocket(port);
        socket.setTimeToLive(2);
        group = InetAddress.getByName(address);
        this.port = port;
    }
    
    /**
     * Send the current weather as XML on the Multicast socket.
     * 
     * @param xml The current weather XML
     */
    public void sendCurrentWeather(String xml) {
        try {
            byte[] b = xml.getBytes();
            DatagramPacket packet = new DatagramPacket(b, b.length, group, port);
            socket.send(packet);
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Exception while publishing current weather", e);
        }
    }
}
