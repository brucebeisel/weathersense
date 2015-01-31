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
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.LogManager;

import com.bdb.util.ThreadUtils;
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
public class SocketReaderTest implements SocketDataProcessor {
    private ServerSocket server;
    private Socket socket;
    private int tokenCount;
    
    public SocketReaderTest() throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");
        
        if (is != null)
            LogManager.getLogManager().readConfiguration(is);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException, InterruptedException {
        tokenCount = 0;
    }
    
    @After
    public void tearDown() throws IOException {
        socket.close();
        server.close();
    }

    public void init(int testNumber) throws IOException {
        server = new ServerSocket(1961);
        createClient(testNumber);
        try { Thread.sleep(250); } catch (InterruptedException ex) { }
        socket = server.accept();
        socket.setSoTimeout(1000);
    }

    @Test
    public void testReadSocket() throws IOException {
        System.out.println("readSocket");
        init(1);
        SocketReader instance = new SocketReader(socket, this);
        boolean expResult = true;
        boolean result = instance.readSocket();
        assertEquals(expResult, result);
        assertEquals(1, tokenCount);
    }

    @Test
    public void testMultipleTokenReadSocket() throws IOException {
        System.out.println("multipleTokenReadSocket");
        init(2);
        SocketReader instance = new SocketReader(socket, this);
        boolean expResult = true;
        boolean result = instance.readSocket();
        assertEquals(expResult, result);
        assertEquals(6, tokenCount);
    }

    @Test
    public void testMultipleWrites() throws IOException {
        System.out.println("multipleTokenReadSocket");
        init(3);
        SocketReader instance = new SocketReader(socket, this);
        boolean expResult = true;
        boolean result = instance.readSocket();
        ThreadUtils.sleep(300);
        result = instance.readSocket();
        assertEquals(expResult, result);
        assertEquals(1, tokenCount);
    }

    private void createClient(final int testNumber) {
        Thread t = new Thread(()-> {
            try {
                Socket socket = new Socket("localhost", 1961);
                if (testNumber == 1) {
                    String msg = "Hello;";
                    socket.getOutputStream().write(msg.getBytes());
                }
                else if (testNumber == 2) {
                    String msg = "Hello;World;This;is;a;test;";
                    socket.getOutputStream().write(msg.getBytes());
                }
                else if (testNumber == 3) {
                    String msg = "Hello World.";
                    socket.getOutputStream().write(msg.getBytes());
                    ThreadUtils.sleep(300);
                    msg = " This is a test;";
                    socket.getOutputStream().write(msg.getBytes());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        t.start();

    }

    @Override
    public String consumeMessages(List<String> messages) {
        for (String s : messages) {
            System.out.println("Consuming '" + s + "'");
            tokenCount++;
        }
        return null;
    }

    @Override
    public int findToken(String s) {
        return s.indexOf(';');
    }
    
}
