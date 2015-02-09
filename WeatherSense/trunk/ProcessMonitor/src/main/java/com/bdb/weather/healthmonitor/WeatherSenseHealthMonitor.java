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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        Runnable runnable = () -> {
            boolean hmHealth = historyMonitor.isHealthy();
            boolean cwHealth = cwMonitor.isHealthy();
            boolean healthy = hmHealth && cwHealth;
            System.out.println("CW: " + cwHealth + "  HM: " + hmHealth);
            System.out.println("WeatherSense health: " + (healthy ? "Healthy" : "Unhealthy"));
        };
        executor.scheduleAtFixedRate(runnable, 10, 10, TimeUnit.SECONDS);
    }

    public static void main(String args[]) {
            /*
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
            Logger.getLogger(WeatherSenseHealthMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
                    */
        WeatherSenseHealthMonitor monitor = new WeatherSenseHealthMonitor();
        monitor.start();
    }
}