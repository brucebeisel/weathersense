/*
 * Copyright (C) 2015 bruce
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
package com.bdb.weather.display.stripchart;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author bruce
 */
public class StripChartManager extends FlowPane {
    @FXML private TableView<StripChartPreferences.StripChartEntry> savedStripChartTable;

    @SuppressWarnings("LeakingThisInConstructor")
    public StripChartManager() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StripChartManager.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        StripChartPreferences prefs = StripChartPreferences.getInstance();
        savedStripChartTable.setItems(prefs.getSaveStripCharts());

    }

    public void saveStripChart() {

    }

    public void launchStripChart() {

    }
}