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
package com.bdb.weather.display.historyeditor;


import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.DisplayConstants;

public class HistoryEditorPanel extends BorderPane {
    private final FlowPane datePanel = new FlowPane();
    private final HistoryTable historyTable;
    private final TableView<HistoricalRecord> table = new TableView<>();
    
    public HistoryEditorPanel(WeatherStation ws, DBConnection connection) {
        historyTable = new HistoryTable(connection);
        this.setTop(datePanel);
        DatePicker dateButton = new DatePicker();
        datePanel.getChildren().add(dateButton);
        DateRange range = historyTable.dataRange();
        datePanel.getChildren().add(new Label(DisplayConstants.formatDateTime(range.getStart())));
        datePanel.getChildren().add(new Label(DisplayConstants.formatDateTime(range.getEnd())));
        
        this.setCenter(table);
    }
}