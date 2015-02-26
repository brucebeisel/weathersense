/*
 * Copyright (C) 2015 Bruce
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
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class ProcessMonitor implements HealthMonitor, MonitoredProcess.Monitor {
    private final Executor executor;
    private final static Logger logger = Logger.getLogger(ProcessMonitor.class.getName());

    public ProcessMonitor() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void startProcesses() {
        final String VP2_ARGS[] = { "notepad.exe" };
        MonitoredProcess p1 = new MonitoredProcess(Arrays.asList(VP2_ARGS), new File("c:/weathersense/notepad.log"), this);
        p1.launch();
    }

    public void stopProcesses() {
    }

    private void handleProcessExited(MonitoredProcess process, int exitCode) {
        process.launch();
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public void processExited(MonitoredProcess process, int exitCode) {
        executor.execute(()->handleProcessExited(process, exitCode));
    }
}
