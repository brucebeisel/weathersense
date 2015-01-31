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

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import junit.framework.TestCase;

import com.bdb.util.ThreadUtils;

public class SocketReaderThreadTest extends TestCase
{

    public SocketReaderThreadTest(String arg0)
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testSendEvent()
    {
        //fail("Not yet implemented");
    }

    public void testWaitForEvent()
    {
        //fail("Not yet implemented");
    }

    public void testThreadStep()
    {
        //fail("Not yet implemented");
    }

//    public void testSocketReaderThread()
//    {
//        SocketReaderThread t;
//        try
//        {
//            t = new SocketReaderThread(14000, null);
//            t.start();
//            ThreadUtils.sleep(1000);
//            SocketChannel channel = SocketChannel.open(new InetSocketAddress(14000));
//            channel.socket().getOutputStream().write((new String("Hello world\n")).getBytes());
//
//            t.requestTermination();
//            ThreadUtils.sleep(1000);
//            if (t.isAlive())
//                fail("Thread did not terminate");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            fail("Exception thrown");
//        }
//       
//        
//    }

}
