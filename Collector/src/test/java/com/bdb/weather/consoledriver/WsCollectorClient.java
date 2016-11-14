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
package com.bdb.weather.consoledriver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.Pair;

import com.bdb.weather.collector.CollectorConstants;
import com.bdb.weather.common.messages.WeatherSenseMessage;

/**
 *
 * @author Bruce
 */
public class WsCollectorClient {
    private final static int READ_TIMEOUT = 10000;
    private final int port;
    private Socket socket;
    private static final Logger logger = Logger.getLogger(WsCollectorClient.class.getName());
    
    /**
     *
     * @param port
     * @throws IOException
     */
    public WsCollectorClient(int port) throws IOException {
        this.port = port;
    }
    
    /**
     *
     * @return
     */
    public Pair<Boolean,LocalDateTime> connect() {
        if (socket != null) {
            logger.info("Ignoring connect request, already connected");
            return new Pair<>(true, null);
        }

        try {
            SocketAddress sa = new InetSocketAddress("localhost", port);
            socket = new Socket();
            socket.connect(sa);
            socket.setSoTimeout(READ_TIMEOUT);
            LocalDateTime c = readNewestHistoricalRecordTime();

            if (c == null) {
                closeConnection();
                return new Pair<>(false, null);
            }
            return new Pair<>(true, c);
        }
        catch (IOException e) {
            logger.warning("Error connecting to collector");
            closeConnection();
            return new Pair<>(false, null);
        }
    }
    
    /**
     *
     * @return
     */
    public boolean isConnected() {
        return socket != null;
    }

    private void closeConnection() {
        if (socket == null)
            return;
        
        try {
            socket.close();
        }
        catch (IOException e) {

        }
        socket = null;
    }
    
    private LocalDateTime readNewestHistoricalRecordTime() {
        try {
            String command = new WeatherSenseMessage(CollectorConstants.INITIALIZE_TOKEN).buildMessage();
            sendMessage(command);
            
            byte buffer[] = new byte[1024];
            int bytes = socket.getInputStream().read(buffer);
            if (bytes > 0) {
                String s = new String(buffer);
                DateTimeFormatter sdf = DateTimeFormatter.ofPattern(CollectorConstants.NEWEST_RECORD_DATE_FORMAT);
                LocalDateTime c = LocalDateTime.from(sdf.parse(s));
                logger.info("Newest record time: " + sdf.format(c));
                return c;
            }
            else
                return null;
        }
        catch (IOException | DateTimeParseException e) {
            logger.log(Level.WARNING, "Error getting newest historical record");
            return null;
        }
    }
    
    /**
     *
     * @param s
     * @return
     */
    public boolean sendMessage(String s) {
        if (socket == null)
            return false;
        
        if (!s.endsWith("\n"))
            s += "\n";
        
        logger.log(Level.FINE, "Sending ''{0}''", s);
        try {
            socket.getOutputStream().write(s.getBytes());
            return true;
        }
        catch (IOException e) {
            closeConnection();
            System.out.println(e);
            return false;
        }
    }
}
