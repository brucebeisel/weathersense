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
import java.time.LocalDateTime;
import java.util.List;
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
    }
    private final ProcessBuilder builder;
    private Process process;
    private boolean running = false;
    private boolean killing = false;
    private int startCount = 0;
    private int restartCount = 0;
    private LocalDateTime lastStartTime;
    private Thread monitor;
    private final Monitor callback;
    private final static Logger logger = Logger.getLogger(MonitoredProcess.class.getName());

    public MonitoredProcess(List<String> commandArgs, File outputFile, Monitor callback) {
        builder = new ProcessBuilder(commandArgs);
        ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(outputFile);
        builder.redirectErrorStream(true);
        builder.redirectOutput(redirect);
        monitor = null;
        this.callback = callback;
    }

    public boolean isRunning() {
        return running;
    }

    public int getStartCount() {
        return startCount;
    }

    public int getRestartCount() {
        return restartCount;
    }

    public void processDied() {
        logger.log(Level.INFO, "Process died. Restart count = " + restartCount);
        callback.processExited(this, process.exitValue());
    }

    public boolean launch() {
        monitor = new Thread(this);

        try {
            process = builder.start();
            startCount++;
            restartCount++;
            running = true;
            killing = false;
            if (process != null) {
                monitor.start();
                lastStartTime = LocalDateTime.now();
                return true;
            }
        }
        catch (IOException e) {

        }
        return false;
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
        while (process.isAlive()) {
            try {
                if (process.waitFor(10, TimeUnit.MINUTES)) {
                    running = false;
                    processDied();
                }
                else
                    restartCount = 0;
            }
            catch (InterruptedException ex) {
                Logger.getLogger(MonitoredProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}