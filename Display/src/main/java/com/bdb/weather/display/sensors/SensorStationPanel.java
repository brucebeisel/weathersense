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
package com.bdb.weather.display.sensors;

import java.util.List;

import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.db.SensorStationStatusTable;

/**
 *
 * @author Bruce
 */
public class SensorStationPanel extends BorderPane {
    private final TableView<SensorStationStatus> table;
    private final SensorStationStatusTable sensorStationStatusTable;
    private static final String COLUMN_HEADINGS[] = {
        "Time", "Sensor Station ID", "Battery Voltage", "Battery Status", "Link Quality"
    };

    public SensorStationPanel(DBConnection connection) {
        table = new TableView<>();
        sensorStationStatusTable = new SensorStationStatusTable(connection);
        //tableModel.setColumnIdentifiers(COLUMN_HEADINGS);
        this.setCenter(table);
    }

    public void loadData() {
        List<SensorStationStatus> stations = sensorStationStatusTable.query();
        /*
        tableModel.setRowCount(stations.size());

        int row = 0;
        for (SensorStationStatus status : stations) {
            tableModel.setValueAt(DisplayConstants.formatDateTime(status.getTime()), row, 0);
            tableModel.setValueAt(status.getSensorStationId(), row, 1);
            tableModel.setValueAt(status.getBatteryVoltage(), row, 2);
            tableModel.setValueAt(status.isBatteryOk() ? "Good" : "Bad", row, 3);
            tableModel.setValueAt(status.getLinkQuality(), row, 4);
            row++;
        }
*/
    }
}
