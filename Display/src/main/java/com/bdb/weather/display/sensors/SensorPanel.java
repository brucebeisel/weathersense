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

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.db.SensorTable;
import com.bdb.weather.display.ComponentContainer;

/**
 *
 * @author Bruce
 */
public class SensorPanel extends BorderPane implements ComponentContainer {
    private TableView<Sensor> table;
    private SensorTable sensorTable;
    private static final String COLUMN_HEADINGS[] = {
        "Sensor ID", "Sensor Type", "Sensor Name", ""
    };

    public SensorPanel(DBConnection connection) {
        sensorTable = new SensorTable(connection);
        List<Sensor> sensors = sensorTable.getSensorList();
        /*
        model.setColumnIdentifiers(COLUMN_HEADINGS);
        model.setRowCount(sensors.size());
        
        for (int i = 0; i < sensors.size(); i++) {
            model.setValueAt(sensors.get(i).getSensorId(), i, 0);
            model.setValueAt(sensors.get(i).getType(), i, 1);
            model.setValueAt(sensors.get(i).getName(), i, 2);
            model.setValueAt("Change Name", i, 3);
        }

        table = new TableView<>();
        TableColumn column = table.getColumn("");
        column.setCellRenderer((JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int col) -> {
            JButton button = new JButton(value.toString());
            return button;
        });


        this.setCenter(table);
*/
    }

    @Override
    public Node getComponent() {
        return this;
    }
}
