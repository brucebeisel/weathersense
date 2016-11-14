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
package com.bdb.weather.display.sensors;

import java.util.List;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.db.SensorTable;

/**
 *
 * @author Bruce
 */
public class SensorPanel extends BorderPane {
    private final TableView<Sensor> table = new TableView<>();
    private final SensorTable sensorTable;

    public SensorPanel(DBConnection connection) {
        sensorTable = new SensorTable(connection);
        List<Sensor> sensors = sensorTable.getSensorList();

        TableColumn<Sensor,Number> idColumn = new TableColumn<>("Sensor ID");
        idColumn.setCellValueFactory((rec) -> new ReadOnlyIntegerWrapper(rec.getValue().getSensorId()));
        table.getColumns().add(idColumn);

        TableColumn<Sensor,SensorType> typeColumn = new TableColumn<>("Sensor Type");
        typeColumn.setCellValueFactory((rec) -> new ReadOnlyObjectWrapper<>(rec.getValue().getType()));
        table.getColumns().add(typeColumn);

        TableColumn<Sensor,String> nameColumn = new TableColumn<>("Sensor Name");
        nameColumn.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(rec.getValue().getName()));
        table.getColumns().add(nameColumn);

        table.setItems(FXCollections.observableList(sensors));

        this.setCenter(table);
    }
}