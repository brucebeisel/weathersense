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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Bruce
 */
public class ProcessMonitor implements HealthMonitor, MonitoredProcess.Monitor {
    private static final String MONITOR_NAME = "Process Monitor";
    private final File baseDirectory;
    private final Map<String,MonitoredProcess> processes = new HashMap<>();
    private static final String COLLECTOR_ARGS[] = {"java", "-jar", "weathersense-collector-2.4.jar"};
    private final MonitoredProcess collectorProcess;
    private static final String VP2_DRIVER_ARGS[] = {"vp2driver"};
    private final MonitoredProcess vp2DriverProcess;
    private final static Logger logger = Logger.getLogger(ProcessMonitor.class.getName());

    public ProcessMonitor(String baseDirectoryName) throws IOException {
        baseDirectory = new File(baseDirectoryName);
        File logFile = new File(baseDirectory.getCanonicalPath() + File.pathSeparator + "log" + File.pathSeparator + "collector.log");
        File startDirectory = new File(baseDirectory.getCanonicalPath() + File.pathSeparator + "jars");
        collectorProcess = new MonitoredProcess("Collector", Arrays.asList(COLLECTOR_ARGS), logFile, startDirectory, this);
        processes.put(collectorProcess.getName(), collectorProcess);

        logFile = new File(baseDirectory.getCanonicalPath() + File.pathSeparator + "log" + File.pathSeparator + "VP2Driver.log");
        startDirectory = new File(baseDirectory.getCanonicalPath() + File.pathSeparator + "bin");
        vp2DriverProcess = new MonitoredProcess("VP2 Driver", Arrays.asList(VP2_DRIVER_ARGS), logFile, startDirectory, this);
        processes.put(vp2DriverProcess.getName(), vp2DriverProcess);
    }

    public void startProcesses() {
        logger.info("Starting processes...");
        final String VP2_ARGS[] = { "notepad.exe" };
        MonitoredProcess p1 = new MonitoredProcess("Notepad", Arrays.asList(VP2_ARGS), new File("log/notepad.log"), baseDirectory, this);
        processes.put(p1.getName(), p1);

        for (MonitoredProcess process : processes.values()) {
            process.launch();
        }
    }

    public void stopProcesses() {
        for (MonitoredProcess process : processes.values()) {
            process.kill();
        }
    }

    public void dumpStatus() {
        for (MonitoredProcess process : processes.values()) {
            System.out.println(process);
        }
    }

    @Override
    public boolean isHealthy() {
        boolean allRunning = true;
        for (MonitoredProcess process : processes.values()) {
            allRunning = allRunning && process.isRunning();
        }

        return allRunning;
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME;
    }

    @Override
    public String getMailMessage() {
        return null;
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
