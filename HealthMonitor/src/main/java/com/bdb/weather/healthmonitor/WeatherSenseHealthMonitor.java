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
import com.bdb.piglow4j.PiGlowArm;
import com.bdb.piglow4j.PiGlowBlinker;
import com.bdb.piglow4j.PiGlowColor;
import com.bdb.piglow4j.PiGlowLED;
import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;
import java.util.Collections;

/**
 * The LEDs are used for the following:
 * LEFT   WHITE
 * LEFT   BLUE     Flash 2 Hz when battery low
 * LEFT   GREEN    Flash 1 Hz while healthy
 * LEFT   YELLOW   Flash 1 Hz when degraded
 * LEFT   ORANGE   
 * LEFT   RED      Flash 1 Hz when unhealthy
 * 
 * RIGHT  WHITE    Archive 1 minute old
 * RIGHT  BLUE     Archive 2 minutes old
 * RIGHT  GREEN    Archive 4 minutes old
 * RIGHT  YELLOW   Archive 8 minutes old
 * RIGHT  ORANGE   Archive 16 minutes old
 * RIGHT  RED      Archive 32 minutes old
 * 
 * TOP    WHITE    Current Weather dancer
 * TOP    BLUE     Current Weather dancer
 * TOP    GREEN    Current Weather dancer
 * TOP    YELLOW   Current Weather dancer
 * TOP    ORANGE   Current Weather dancer
 * TOP    RED      Current Weather dancer
 * 
 * @author Bruce
 */
public class WeatherSenseHealthMonitor implements Runnable {
    private boolean initialized = false;
    private CurrentWeatherMonitor cwMonitor;
    private HistoryMonitor historyMonitor;
    private ProcessMonitor processMonitor;
    private final ScheduledExecutorService executor;
    private PiGlow piglow;
    private PiGlowAnimator healthyAnimator;
    private List<HealthMonitor> monitors = new ArrayList<>();
    private PiGlowAnimation cwUnhealthyAnimation;
    private PiGlowAnimation badBatteryAnimation;
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
	executor = Executors.newSingleThreadScheduledExecutor();
	    
	try {
	    if (!realPiGlow)
		I2CFactory.setFactory(new I2CFactoryProviderSwing());

	    piglow = PiGlow.getInstance();

	    List<PiGlowLED> cwLedList = PiGlowLED.armLEDs(PiGlowArm.TOP);

	    cwMonitor = CurrentWeatherMonitor.createCurrentWeatherMonitor(piglow, cwLedList, 1);
	    cwUnhealthyAnimation = new PiGlowBlinker(0, 1000, 0, 100, Integer.MAX_VALUE, cwLedList);
	    cwUnhealthyAnimation.setEnabled(false);

	    badBatteryAnimation = new PiGlowBlinker(0, 500, 0, 100, Integer.MAX_VALUE, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.BLUE));
	    //badBatteryAnimation.setEnabled(false);

	    List<PiGlowLED> leds = new ArrayList<>(PiGlowLED.armLEDs(PiGlowArm.RIGHT));
	    Collections.reverse(leds);

	    historyMonitor = HistoryMonitor.createHistoryMonitor(dbHost, leds, 6);
	    if (historyMonitor == null) {
		logger.log(Level.SEVERE, "Failed to create history monitor");
		throw new NullPointerException("history monitor");
	    }
	    processMonitor = new ProcessMonitor(baseDirectory);
	    monitors.add(cwMonitor);
	    monitors.add(historyMonitor);
	    monitors.add(processMonitor);

	    healthyAnimator = new PiGlowAnimator(piglow);
	    healthyAnimator.addAnimation(new PiGlowBlinker(0, 1000, 0, 100, Integer.MAX_VALUE, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.GREEN)));
	    healthyAnimator.addAnimation(cwUnhealthyAnimation);
	    healthyAnimator.addAnimation(badBatteryAnimation);
	    initialized = true;
	}
	catch (Exception e) {
	    PiGlowAnimator animator = new PiGlowAnimator(PiGlow.getInstance());
	    animator.addAnimation(new PiGlowBlinker(0, 250, 0, 100, Integer.MAX_VALUE, PiGlowLED.allLEDs()));
	    animator.start();
	}
    }

    /**
     * Start the processes and the monitors.
     */
    public void start() {
	if (initialized) {
	    //processMonitor.startProcesses();
	    executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
	    healthyAnimator.start();
	}
    }

    /**
     * Stop the processes and the monitors.
     */
    public void stop() {
        try {
            healthyAnimator.stop();
            healthyAnimator.waitForTermination(500);
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
	//
	// Without this try/catch block there is no feedback if an exception is thrown.
	// In addition the repeating timer will stop if an uncaught exception is propogated up.
	//
	try {
	    logger.info("Checking health");
	    boolean healthy = true;
	    for (HealthMonitor monitor : monitors) {
		logger.log(Level.INFO, "Checking health of {0}", monitor.getMonitorName());
		healthy = healthy && monitor.isHealthy();
		logger.log(Level.INFO, "{0} is {1}", new Object[]{monitor.getMonitorName(), monitor.isHealthy() ? "Healthy" : "Unhealthy"});
	    }

	    logger.log(Level.INFO, "WeatherSense health: {0}", (healthy ? "Healthy" : "Unhealthy"));
	    logger.log(Level.INFO, "{0}", cwMonitor);
	    cwUnhealthyAnimation.setEnabled(!cwMonitor.isHealthy());
	    //processMonitor.dumpStatus();
	    /*
	    if (healthy) {
		if (!healthyAnimator.isRunning()) {
		    unhealthyAnimator.stop();
		    healthyAnimator.start();
		}
	    }
	    else {
		if (!unhealthyAnimator.isRunning()) {
		    healthyAnimator.stop();
		    unhealthyAnimator.start();
		}
	    }
		    */
	}
	catch (Exception e) {
	    logger.log(Level.SEVERE, "Caught unexpected exception ", e);
	}
    }

    public static void main(String args[]) {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("health_monitor_logging.properties");
            
            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            boolean realPiGlow = true;
            String baseDirectory = "/weathersense";
            String dbHost = "192.168.1.100";
                    
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
