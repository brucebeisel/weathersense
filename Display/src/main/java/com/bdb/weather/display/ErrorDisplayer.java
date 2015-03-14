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
package com.bdb.weather.display;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Bruce
 */
public class ErrorDisplayer {

    private static final ErrorDisplayer instance;

    static {
        instance = new ErrorDisplayer();
    }

    private ErrorDisplayer() {
    }

    public static ErrorDisplayer getInstance() {
        return instance;
    }

    public void displayMessageLater(final String message, final int messageType) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                JOptionPane.showMessageDialog(null, message, "Internal Error", messageType);
            });
        }
        catch (InterruptedException | InvocationTargetException e) {
            // Can't do anything about these exceptions
        }
    }
    
    public void displayInformation(String message) {
        JOptionPane.showMessageDialog(null, message, "Internal Error", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displayWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "Internal Error", JOptionPane.WARNING_MESSAGE);
    }
    
    public void displayError(String message) {
        JOptionPane.showMessageDialog(null, message, "Internal Error", JOptionPane.ERROR_MESSAGE);
    }
}