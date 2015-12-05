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
package com.bdb.weather.display.historytable;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import javafx.scene.Node;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DaySelectionPanel;
import com.bdb.weather.display.DaySelectionPanel.DateChangedListener;
import com.bdb.weather.display.DisplayConstants;

/**
 * TODO Provide mechanism to export to a CSV file
 * @author Bruce
 */
public class DayHistoryTable implements DateChangedListener {
    private static final String TABLE_HEADINGS[] = {
        "Time",
        "Low Outdoor Temperature",
        "Avg Outdoor Temperature",
        "High Outdoor Temperature",
        "Outdoor Humidity",
        "Indoor Temperature",
        "Indoor Humidity",
        "Avg Wind Speed",
        "Avg Wind Direction",
        "High Wind Speed",
        "High Wind Direction",
        "Wind Gust Speed",
        "Wind Gust Direction",
        "Barometric Pressure",
        "Avg UV Index",
        "High UV Index",
        "Avg Solar Radiation",
        "High Solar Radiation",
        "ET",
        "Rainfall",
        "High Rainfall Rate"
    };
    private final JPanel               panel;
    private final JTable               dataTable;
    private final DefaultTableModel    tableModel;
    private final HistoryTable         historyTable;

    /**
     * Constructor.
     * 
     * @param connection Connection to the database
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public DayHistoryTable(DBConnection connection) {
        historyTable = new HistoryTable(connection);
        LocalDate date = LocalDate.now();
        panel = new JPanel(new BorderLayout());
        DaySelectionPanel selectionPanel = new DaySelectionPanel(date);
        selectionPanel.addDateChangedListener(this);
        panel.add(selectionPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        DefaultTableColumnModel colModel = new DefaultTableColumnModel();
        
        dataTable = new JTable();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(colModel);
        dataTable.setAutoCreateColumnsFromModel(false);

        for (int i = 0; i < TABLE_HEADINGS.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(TABLE_HEADINGS[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(TABLE_HEADINGS.length);

        //
        // Add a row sorter. TODO add custom sorters for those columns that are not sorted by ascii text, like the date
        //
        dataTable.setRowSorter(new TableRowSorter<>(tableModel));

        //
        // Insert the JTable component into a scroll pane so that we have scroll bars
        //
        JScrollPane sp = new JScrollPane(dataTable);
        panel.add(sp, BorderLayout.CENTER);

        loadData(date);
    }

    /**
     * Load the data for the specified day.
     * 
     * @param c The date for the date of data to be loaded
     */
    private void loadData(LocalDate date) {
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);

        tableModel.setRowCount(list.size());
        int row = 0;
        for (HistoricalRecord rec : list) {
            int col = 0;
            tableModel.setValueAt(DisplayConstants.formatTime(rec.getTime().toLocalTime()), row, col++);
            tableModel.setValueAt(rec.getLowOutdoorTemperature(), row, col++);
            tableModel.setValueAt(rec.getAvgOutdoorTemperature(), row, col++);
            tableModel.setValueAt(rec.getHighOutdoorTemperature(), row, col++);
            tableModel.setValueAt(rec.getOutdoorHumidity(), row, col++);
            tableModel.setValueAt(rec.getIndoorTemperature(), row, col++);
            tableModel.setValueAt(rec.getIndoorHumidity(), row, col++);

            loadWind(rec.getAvgWind(), row, col);
            col += 2;
            loadWind(rec.getHighWind(), row, col);
            col += 2;
            loadWind(rec.getWindGust(), row, col);
            col += 2;

            tableModel.setValueAt(rec.getBaroPressure(), row, col++);

            if (rec.getAvgUvIndex() != null)
                tableModel.setValueAt(rec.getAvgUvIndex(), row, col++);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);

            if (rec.getHighUvIndex() != null)
                tableModel.setValueAt(rec.getHighUvIndex(), row, col++);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);

            if (rec.getAvgSolarRadiation()!= null)
                tableModel.setValueAt(rec.getAvgSolarRadiation(), row, col++);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);

            if (rec.getHighSolarRadiation()!= null)
                tableModel.setValueAt(rec.getHighSolarRadiation(), row, col++);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);

            if (rec.getEvapotranspiration()!= null)
                tableModel.setValueAt(rec.getEvapotranspiration(), row, col++);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);

            tableModel.setValueAt(rec.getRainfall(), row, col++);
            tableModel.setValueAt(rec.getHighRainfallRate(), row, col++);

            row++;
        }
    }

    /**
     * Utility to load the wind data.
     * 
     * @param w The wind
     * @param row The row to which to add the wind
     * @param col The first column of the wind data
     */
    private void loadWind(Wind w, int row, int col) {
        if (w != null) {
            tableModel.setValueAt(w.getSpeed(), row, col++);
            tableModel.setValueAt(w.getDirection().getCompassLabel(), row, col++);
        }
        else {
            tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);
            tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);
        }
    }

    /**
     * Called by the date selector whenever the date is changed.
     * 
     * @param date The selected date
     */
    @Override
    public void dateChanged(LocalDate date) {
        loadData(date);
    }
}
