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

import java.time.LocalDateTime;
import java.util.List;

import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;
import com.bdb.weather.common.BadBattery;

import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.db.SensorStationStatusTable;
import com.bdb.weather.display.DisplayConstants;

/**
 *
 * @author Bruce
 */
public class SensorStationPanel extends BorderPane {
    private final TableView<SensorStationStatus> table;
    private final SensorStationStatusTable sensorStationStatusTable;

    public SensorStationPanel(DBConnection connection) {
        table = new TableView<>();
        sensorStationStatusTable = new SensorStationStatusTable(connection);

        TableColumn<SensorStationStatus,String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(DisplayConstants.formatDateTime(rec.getValue().getTime())));
        table.getColumns().add(timeColumn);

        TableColumn<SensorStationStatus,Number> idColumn = new TableColumn<>("Senaor Station ID");
        idColumn.setCellValueFactory((rec) -> new ReadOnlyIntegerWrapper(rec.getValue().getSensorStationId()));
        table.getColumns().add(idColumn);

        TableColumn<SensorStationStatus,Number> voltageColumn = new TableColumn<>("Battery Voltage");
        voltageColumn.setCellValueFactory((rec) -> new ReadOnlyFloatWrapper(rec.getValue().getBatteryVoltage() == null ? 99.9F : rec.getValue().getBatteryVoltage()));
        table.getColumns().add(voltageColumn);

        TableColumn<SensorStationStatus,String> batteryColumn = new TableColumn<>("Battery Status");
        batteryColumn.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(rec.getValue().isBatteryOk() ? "Good" : "Bad"));
        table.getColumns().add(batteryColumn);

        TableColumn<SensorStationStatus,Number> linkQualityColumn = new TableColumn<>("Link Quality");
        linkQualityColumn.setCellValueFactory((rec) -> new ReadOnlyIntegerWrapper(rec.getValue().getLinkQuality() == null ? 999 : rec.getValue().getLinkQuality()));
        table.getColumns().add(linkQualityColumn);

        this.setCenter(table);
    }

    public void loadData() {
        LocalDateTime time = LocalDateTime.now().minusDays(30);
        String clause = "where time>'" + DBTable.dateTimeFormatter().format(time) + "'";
        List<SensorStationStatus> stations = sensorStationStatusTable.query(clause);

        table.setItems(FXCollections.observableList(stations));

        List<BadBattery> badBatteryList = sensorStationStatusTable.retrieveBadBatteryRecords();
        for (BadBattery status : badBatteryList) {
            System.out.println("Station: " + status.getSensorStationId() +
                               " Time: " + DBTable.dateTimeFormatter().format(status.getTimeDetected()) +
                               " Measurements: " + status.getMeasurementCount() +
                               " Duration: " + status.getDuration().getSeconds());
        }
    }
}