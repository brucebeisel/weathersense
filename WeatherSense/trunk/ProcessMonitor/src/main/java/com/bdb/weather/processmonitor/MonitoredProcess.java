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
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class MonitoredProcess {
    private final ProcessBuilder builder;
    private Process process;
    private boolean running = false;
    private boolean killing = false;
    private int startCount = 0;
    private int restartCount = 0;
    private LocalDateTime lastStartTime;

    public MonitoredProcess(List<String> commandArgs, File outputFile) {
        builder = new ProcessBuilder(commandArgs);
        ProcessBuilder.Redirect redirect = ProcessBuilder.Redirect.appendTo(outputFile);
        builder.redirectErrorStream(true);
        builder.redirectOutput(redirect);
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

    public boolean launch() throws IOException {
        if (restartCount > 5)
            return false;

        process = builder.start();
        startCount++;
        restartCount++;
        lastStartTime = LocalDateTime.now();
        running = true;
        killing = false;
        Thread thread = new Thread(()-> {
            while (process.isAlive()) {
                try {
                    if (process.waitFor(10, TimeUnit.MINUTES)) {
                        running = false;
                        if (!killing)
                            launch();
                    }
                    else
                        restartCount = 0;
                }
                catch (InterruptedException | IOException ex) {
                    Logger.getLogger(MonitoredProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
        return process != null;
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
}