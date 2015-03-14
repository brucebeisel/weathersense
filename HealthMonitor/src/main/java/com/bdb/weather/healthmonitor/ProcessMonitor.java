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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class ProcessMonitor implements HealthMonitor, MonitoredProcess.Monitor {
    private final static Logger logger = Logger.getLogger(ProcessMonitor.class.getName());
    private Map<String,MonitoredProcess> processes = new HashMap<>();

    public ProcessMonitor() {
    }

    public void startProcesses() {
        final String VP2_ARGS[] = { "notepad.exe" };
        MonitoredProcess p1 = new MonitoredProcess("Notepad", Arrays.asList(VP2_ARGS), new File("c:/weathersense/notepad.log"), this);
        processes.put(p1.getName(), p1);
        p1.launch();
    }

    public void stopProcesses() {
    }

    public void dumpStatus() {
        for (MonitoredProcess process : processes.values()) {
            System.out.println(process);
        }

    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public void processExited(MonitoredProcess process, int exitCode) {
    }

    @Override
    public void processFinished(MonitoredProcess process) {
    }

    @Override
    public void processFailed(MonitoredProcess process) {
    }

    @Override
    public void processStarted(MonitoredProcess process) {
    }
}
