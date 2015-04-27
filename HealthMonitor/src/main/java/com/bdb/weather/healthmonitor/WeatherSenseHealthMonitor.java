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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bdb.piglow4j.PiGlow;
import com.bdb.piglow4j.PiGlowAnimation;
import com.bdb.piglow4j.PiGlowAnimator;
import com.bdb.piglow4j.PiGlowBlinker;
import com.bdb.piglow4j.PiGlowColor;
import com.bdb.piglow4j.PiGlowLED;
import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 *
 * @author Bruce
 */
public class WeatherSenseHealthMonitor implements Runnable {
    private final CurrentWeatherMonitor cwMonitor;
    private final HistoryMonitor historyMonitor;
    private final ProcessMonitor processMonitor;
    private final ScheduledExecutorService executor;
    private final PiGlow piglow;
    private final PiGlowAnimator animator;
    private final PiGlowAnimation healthyAnimation;
    private final List<HealthMonitor> monitors = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(WeatherSenseHealthMonitor.class.getName());

    /**
     * Constructor.
     * 
     * @param baseDirectory The base directory where the weathersense software is installed
     * @param dbHost The host where the database server is running
     * @param realPiGlow Whether the real PiGlow board is being used or the software simulator
     * 
     * @throws IOException Processes could not be start or the specified directories could not be found
     */
    public WeatherSenseHealthMonitor(String baseDirectory, String dbHost, boolean realPiGlow) throws IOException {
        cwMonitor = CurrentWeatherMonitor.createCurrentWeatherMonitor(10);
        historyMonitor = HistoryMonitor.createHistoryMonitor(dbHost, 6);
        processMonitor = new ProcessMonitor(baseDirectory);
        monitors.add(cwMonitor);
        monitors.add(historyMonitor);
        monitors.add(processMonitor);

        if (!realPiGlow)
            I2CFactory.setFactory(new I2CFactoryProviderSwing());

        executor = Executors.newSingleThreadScheduledExecutor();
        piglow = PiGlow.getInstance();
        
        animator = new PiGlowAnimator(piglow);
        healthyAnimation = new PiGlowBlinker(0, 1000, 10000, 5, 255, 25, true, true, 10000, PiGlowLED.colorLEDs(PiGlowColor.GREEN));
        animator.addAnimation(healthyAnimation);
    }

    /**
     * Start the processes and the monitors.
     */
    public void start() {
        processMonitor.startProcesses();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
        animator.start();
    }

    /**
     * Stop the processes and the monitors.
     */
    public void stop() {
        try {
            animator.stop();
            animator.waitForTermination(500);
        }
        catch (InterruptedException ex) {
            logger.log(Level.INFO, "Timed out waiting for PiGlow animator to terminator");
        }
            
        try {
            executor.shutdownNow();
            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ex) {
            logger.log(Level.INFO, "Timed out waiting for executor to terminate");
        }
    }

    /**
     * Call back that is run every monitoring cycle.
     */
    @Override
    public void run() {
        boolean healthy = true;
        for (HealthMonitor monitor : monitors) {
            healthy = healthy && monitor.isHealthy();
            logger.info(monitor.getMonitorName() + " is " + (monitor.isHealthy() ? "Healthy" : "Unhealthy"));
        }

        logger.info("WeatherSense health: " + (healthy ? "Healthy" : "Unhealthy"));
        logger.info("" + cwMonitor);
        processMonitor.dumpStatus();
    }

    public static void main(String args[]) {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("health_monitor_logging.properties");
            
            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            boolean realPiGlow = true;
            String baseDirectory = "/weathersense";
            String dbHost = "127.0.0.1";
                    
            for (String arg : args) {
                switch (arg) {
                    case "-g":
                        realPiGlow = false;
                        break;

                    case "-h":
                        System.out.println("Usage: WeatherSenseHealthMonitor [-g] [-h] [base directory]");
                        System.out.println("where: -g Run PiGlow GUI simulator");
                        System.out.println("       -h Print this message");
                        System.out.println("        base directory - The directory where the weathersense installation starts");
                        System.exit(0);
                        break;

                    default:
                        baseDirectory = arg;
                        break;
                }
            }

            WeatherSenseHealthMonitor monitor = new WeatherSenseHealthMonitor(baseDirectory, dbHost, realPiGlow);
            monitor.start();
        }
        catch (IOException | SecurityException ex) {
            Logger.getLogger(WeatherSenseHealthMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}