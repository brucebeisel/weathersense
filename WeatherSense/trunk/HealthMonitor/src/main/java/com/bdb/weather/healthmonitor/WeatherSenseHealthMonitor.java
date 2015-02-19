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
package com.bdb.weather.healthmonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class WeatherSenseHealthMonitor {
    //private static final String VP2_ARGS[] = { "vp2" };
    //private static final String COLLECTOR_ARGS[] = { "java", "-jar", "weathersense-collector-2.4.jar"};
    private final CurrentWeatherMonitor cwMonitor;
    private final HistoryMonitor historyMonitor;
    private final ScheduledExecutorService executor;

    public WeatherSenseHealthMonitor() {
        cwMonitor = CurrentWeatherMonitor.createCurrentWeatherMonitor(10);
        historyMonitor = HistoryMonitor.createHistoryMonitor("192.168.0.100", 60);
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        final String VP2_ARGS[] = { "notepad.exe" };
        MonitoredProcess p1 = new MonitoredProcess(Arrays.asList(VP2_ARGS), new File("c:/weathersense/notepad.log"));
        try {
            p1.launch();
        }
        catch (IOException ex) {
            Logger.getLogger(WeatherSenseHealthMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        Runnable runnable = () -> {
            boolean hmHealth = historyMonitor.isHealthy();
            boolean cwHealth = cwMonitor.isHealthy();
            boolean healthy = hmHealth && cwHealth;
            System.out.println("CW: " + cwHealth + "  HM: " + hmHealth);
            System.out.println("WeatherSense health: " + (healthy ? "Healthy" : "Unhealthy"));
            System.out.println(cwMonitor);
        };
        executor.scheduleAtFixedRate(runnable, 10, 10, TimeUnit.SECONDS);
    }

    public static void main(String args[]) {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("health_monitor_logging.properties");
            
            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            WeatherSenseHealthMonitor monitor = new WeatherSenseHealthMonitor();
            monitor.start();
        }
        catch (IOException | SecurityException ex) {
            Logger.getLogger(WeatherSenseHealthMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}