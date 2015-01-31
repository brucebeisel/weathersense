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

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.db.SensorStationStatusTable;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DisplayConstants;

/**
 *
 * @author Bruce
 */
public class SensorStationPanel implements ComponentContainer {
    private final JComponent panel;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final SensorStationStatusTable sensorStationStatusTable;
    private static final String COLUMN_HEADINGS[] = {
        "Time", "Sensor Station ID", "Battery Voltage", "Battery Status", "Link Quality"
    };

    public SensorStationPanel(DBConnection connection) {
        panel = new JPanel(new BorderLayout());
        sensorStationStatusTable = new SensorStationStatusTable(connection);
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(COLUMN_HEADINGS);
        table = new JTable(tableModel);
        JScrollPane pane = new JScrollPane(table);
        panel.add(pane, BorderLayout.CENTER);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    public void loadData() {
        List<SensorStationStatus> stations = sensorStationStatusTable.query();
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
    }
}
