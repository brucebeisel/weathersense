/* 
 * Copyright (C) 2016 Bruce Beisel
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

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    public void displayMessageLater(final String message, Alert.AlertType alertType) {
	Platform.runLater(() -> {;
	    Alert alert = new Alert(alertType, message, ButtonType.OK);
	    alert.showAndWait();
	});
    }
    
    public void displayInformation(String message) {
	Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
	alert.showAndWait();
    }
    
    public void displayWarning(String message) {
	Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
	alert.showAndWait();
    }
    
    public void displayError(String message) {
	Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
	alert.showAndWait();
    }
}