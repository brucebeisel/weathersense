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
package com.bdb.weather.processmonitor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class ProcessMonitor {
    private static final String VP2_ARGS[] = { "vp2" };
    private static final String COLLECTOR_ARGS[] = { "java", "-jar", "weathersense-collector-2.4.jar"};

    public static void main(String args[]) {
        try {
            MonitoredProcess p1 = new MonitoredProcess(Arrays.asList(VP2_ARGS), new File("/weathersense/logs/vp2.log"));
            MonitoredProcess p2 = new MonitoredProcess(Arrays.asList(COLLECTOR_ARGS), new File("/weathersense/logs/collector.log"));
            p1.launch();
            p2.launch();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                p1.kill();
                p2.kill();
            }));
        }
        catch (IOException ex) {
            Logger.getLogger(ProcessMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}