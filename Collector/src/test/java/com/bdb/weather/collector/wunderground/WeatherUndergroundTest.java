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
package com.bdb.weather.collector.wunderground;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.LogManager;

import com.bdb.util.ThreadUtils;
import com.bdb.weather.common.CurrentWeather;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bruce
 */
public class WeatherUndergroundTest {
    private static int PORT = 11111;
    private Thread thread;
    private boolean respondSuccessfully = true;
    private static final String SUCCESS_RESPONSE = "HTTP/1.1 200 OK\n" +
                                                   "Content-Type: text/xml; charset=utf-8\n" +
                                                   "Content-Length: 8\n\n" +
                                                   "success\n";
    private static final String FAILURE_RESPONSE = "HTTP/1.1 200 OK\n" +
                                                   "Content-Type: text/xml; charset=utf-8\n" +
                                                   "Content-Length: 14\n\n" +
                                                   "NOT A SUCCESS\n";
    
    /**
     *
     * @throws IOException
     */
    public WeatherUndergroundTest() throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");
        
        if (is != null)
            LogManager.getLogManager().readConfiguration(is);
    }
    
    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        thread = new Thread(() -> {
            System.out.println("Creating server socket");
            Socket socket = null;
            ServerSocket server = null;
            try {
                server = new ServerSocket();
                server.setReuseAddress(true);
                SocketAddress addr = new InetSocketAddress(PORT);
                server.setSoTimeout(5000);
                server.bind(addr);

                byte buffer[] = new byte[1000];
                System.out.println("Waiting for connection");
                socket = server.accept();
                System.out.println("Accepted a socket");
                InputStream is = socket.getInputStream();
                int n = is.read(buffer, 0, buffer.length);
                String s = new String(buffer, 0, n);
                System.out.println("Read '" + s + "'");
                String response;
                if (respondSuccessfully)
                    response = SUCCESS_RESPONSE;
                else
                    response = FAILURE_RESPONSE;
                socket.getOutputStream().write(response.getBytes());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Closing sockets");
                try {
                    if (socket != null)
                        socket.close();
                    
                    if (server != null)
                        server.close();
                }
                catch (IOException ex) {
                }
            }
        });
        thread.start();
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     *
     */
    @Test
    public void testSuccessfulUpload() {
        System.out.println("successfulUpload");
        respondSuccessfully = true;
        ThreadUtils.sleep(1000);
        WeatherUnderground instance = new WeatherUnderground("http://localhost:" + PORT + "?", "station", "password");
        CurrentWeather current = new CurrentWeather();
        instance.uploadCurrentWeather(current);
        ThreadUtils.sleep(2000);
        assertEquals(1, instance.getStatistics().numSuccesses);
        assertEquals(1, instance.getStatistics().numAttempts);
        assertEquals(0, instance.getStatistics().numFailures);
        thread.interrupt();
        ThreadUtils.sleep(1000);
    }

    /**
     *
     */
    @Test
    public void testUnsuccessfulUpload() {
        System.out.println("UnsuccessfulUpload");
        respondSuccessfully = false;
        ThreadUtils.sleep(1000);
        WeatherUnderground instance = new WeatherUnderground("http://localhost:" + PORT + "?", "station", "password");
        CurrentWeather current = new CurrentWeather();
        instance.uploadCurrentWeather(current);
        ThreadUtils.sleep(2000);
        assertEquals(0, instance.getStatistics().numSuccesses);
        assertEquals(1, instance.getStatistics().numFailures);
        assertEquals(1, instance.getStatistics().numAttempts);
    }

    /**
     *
     */
    @Test
    public void testUnsuccessfulConnection() {
        System.out.println("UnsuccessfulConnection");
        respondSuccessfully = false;
        ThreadUtils.sleep(1000);
        WeatherUnderground instance = new WeatherUnderground("http://192.168.0.200:" + PORT + "?", "station", "password");
        CurrentWeather current = new CurrentWeather();
        instance.uploadCurrentWeather(current);
        ThreadUtils.sleep(10000);
        assertEquals(0, instance.getStatistics().numSuccesses);
        assertEquals(1, instance.getStatistics().numFailures);
        assertEquals(1, instance.getStatistics().numAttempts);
        thread.interrupt();
        ThreadUtils.sleep(1000);
    }

    /**
     * Test of shutdown method, of class WeatherUnderground.
     */
    //@Test
    public void testShutdown() {
        System.out.println("shutdown");
        WeatherUnderground instance = new WeatherUnderground();
        instance.shutdown();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
