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
    private static final Logger logger = Logger.getLogger(WeatherSenseHealthMonitor.class.getName());
    private final PiGlowAnimator animator;
    private PiGlowAnimation healthyAnimation;

    public WeatherSenseHealthMonitor() {
        cwMonitor = CurrentWeatherMonitor.createCurrentWeatherMonitor(10);
        historyMonitor = HistoryMonitor.createHistoryMonitor("127.0.0.1", 6);
        processMonitor = new ProcessMonitor();
        executor = Executors.newSingleThreadScheduledExecutor();
        I2CFactory.setFactory(new I2CFactoryProviderSwing());
        piglow = PiGlow.getInstance();
        
        animator = new PiGlowAnimator(piglow);
        healthyAnimation = new PiGlowBlinker(0, 1000, 200, 5, 255, 25, true, true, 10000, PiGlowLED.colorLEDs(PiGlowColor.GREEN));
        animator.addAnimation(healthyAnimation);
    }

    public void start() {
        processMonitor.startProcesses();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
        animator.start();
    }

    @Override
    public void run() {
        boolean hmHealth = historyMonitor.isHealthy();
        boolean cwHealth = cwMonitor.isHealthy();
        boolean healthy = hmHealth && cwHealth;
        logger.info("CW: " + cwHealth + "  HM: " + hmHealth);
        logger.info("WeatherSense health: " + (healthy ? "Healthy" : "Unhealthy"));
        logger.info("" + cwMonitor);
        processMonitor.dumpStatus();
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