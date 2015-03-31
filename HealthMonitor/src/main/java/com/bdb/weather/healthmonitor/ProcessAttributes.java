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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Bruce
 */
@XmlRootElement
public class ProcessAttributes {
    private String executable;
    private File startDirectory;
    private List<String> arguments;
    private Map<String,String> envs;

    public ProcessAttributes(String executableFileName) {
        executable = executableFileName;
        startDirectory = new File(".");
        arguments = new ArrayList<>();
        envs = new HashMap<>();
    }

    private String getExecutable() {
        return executable;
    }

    private File getStartDirectory() {
        return startDirectory;
    }

    private List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    private Map<String,String> getEnvironmentVariables() {
        return Collections.unmodifiableMap(envs);
    }

    public static void main(String args[]) {
        try {
            JAXBContext context = JAXBContext.newInstance(com.bdb.weather.healthmonitor.ProcessAttributes.class);
        }
        catch (JAXBException ex) {
            Logger.getLogger(ProcessAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
