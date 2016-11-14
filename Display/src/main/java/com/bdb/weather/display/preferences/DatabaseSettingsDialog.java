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
package com.bdb.weather.display.preferences;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class DatabaseSettingsDialog extends GridPane {
    private final UserPreferences prefs = UserPreferences.getInstance();
    private final TextField       databaseHost = new TextField();
    private final TextField       databasePort = new TextField();
    
    //protected static ResourceBundle s_localizationResources = ResourceBundle.getBundle("com.bdb.weathersense.Localization");
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DatabaseSettingsDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DatabaseSettingsDialog.class.getResource("/fxml/DatabaseSettingsPane.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        databaseHost.setText(prefs.getDbHostPref());
        databaseHost.setText(prefs.getDbPortPref());
    }

    @FXML
    private void saveSettings() {
        prefs.putDbHostPref(databaseHost.getText());
        prefs.putDbPortPref(databasePort.getText());
        prefs.sync();
        cancel();
    }

    @FXML
    private void cancel() {
        ((Stage)getScene().getWindow()).close();
    }
}