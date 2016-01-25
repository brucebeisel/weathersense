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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class MonitoredProcess implements Runnable {
    public interface Monitor {
        void processExited(MonitoredProcess process, int exitCode);
        void processFinished(MonitoredProcess process);
        void processFailed(MonitoredProcess process);
        void processStarted(MonitoredProcess process);
    }
    private final ProcessBuilder builder;
    private Process process;
    private final String name;
    private boolean running = false;
    private boolean failed = false;
    private boolean killing = false;
    private int startCount = 0;
    private int restartCount = 0;
    private LocalDateTime lastStartTime;
    private Thread monitor;
    private final Monitor callback;
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private final static Logger logger = Logger.getLogger(MonitoredProcess.class.getName());

    public MonitoredProcess(String name, List<String> commandArgs, File outputFile, File startDirectory, Monitor callback) {
        builder = new ProcessBuilder(commandArgs);
        ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(outputFile);
        builder.redirectErrorStream(true);
        builder.redirectOutput(redirect);
        monitor = null;
        this.callback = callback;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean hasProcessFailed() {
        return failed;
    }
    public int getStartCount() {
        return startCount;
    }

    public int getRestartCount() {
        return restartCount;
    }

    public Duration uptime() {
        return Duration.between(lastStartTime, LocalDateTime.now());
    }

    private void processDied() {
        try {
            logger.log(Level.INFO, "Process {0} died. Restart count = {1}  Exit Code: {2}", new Object[]{name, restartCount, process.exitValue()});
            running = false;
            monitor.join(1000);
            if (!killing && restartCount < 5)
                launch();
            else if (killing) {
                processFinished();
            }
            else
                processFailed();
        }
        catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread wait was interrupted", ex);
        }
    }

    private void processStarted() {
        callback.processStarted(this);

    }

    private void processFailed() {
        logger.severe("Process failed");
        callback.processFailed(this);

    }

    private void processFinished() {
        callback.processFinished(this);
    }

    public boolean launch() {
        failed = false;
        running = false;
        monitor = new Thread(this);

        try {
            process = builder.start();
            startCount++;
            restartCount++;
            running = true;
            killing = false;
            processStarted();
            monitor.start();
            lastStartTime = LocalDateTime.now();
            return true;
        }
        catch (IOException e) {
            processFailed();
            return false;
        }
    }

    public boolean kill() {
        boolean success = true;
        if (!running)
            return success;

        killing = true;
        try {
            process.destroy();
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                if (!process.waitFor(2, TimeUnit.SECONDS)) {
                    Logger.getLogger(MonitoredProcess.class.getName()).log(Level.SEVERE, "Failed to kill process");
                    success = false;
                }
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(MonitoredProcess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    @Override
    public void run() {
        logger.fine("Starting thread to monitor process");
        while (process.isAlive()) {
            try {
                if (process.waitFor(10, TimeUnit.MINUTES)) {
                    running = false;
                    executor.execute(()->processDied());
                }
                else {
                    logger.log(Level.FINE, "Resetting restart count for process {0}", name);
                    restartCount = 0;
                }
            }
            catch (InterruptedException ex) {
                Logger.getLogger(MonitoredProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        logger.fine("Process Monitor thread exiting");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Running: ").append(running);
        if (running)
            sb.append(" Uptime: ").append(uptime());
        sb.append("\n");
        sb.append("Killing: ").append(killing).append("\n");
        sb.append("Total Start Count: ").append(startCount).append(" Restart Count: ").append(restartCount);

        return sb.toString();
    }
}