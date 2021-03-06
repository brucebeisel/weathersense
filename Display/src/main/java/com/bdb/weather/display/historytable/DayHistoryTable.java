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
package com.bdb.weather.display.historytable;

import java.time.LocalDate;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorManager;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.DaySelectionPanel;
import com.bdb.weather.display.DaySelectionPanel.DateChangedListener;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.TableColumnValueFactory;

/**
 * TODO Provide mechanism to export to a CSV file
 * @author Bruce
 */
public class DayHistoryTable extends BorderPane implements DateChangedListener {
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
    private final TableView<HistoricalRecord> dataTable;
    private final HistoryTable                historyTable;

    /**
     * Constructor.
     * 
     * @param connection Connection to the database
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public DayHistoryTable(DBConnection connection) {
        historyTable = new HistoryTable(connection);
        LocalDate date = LocalDate.now();
        DaySelectionPanel selectionPanel = new DaySelectionPanel(date);
        selectionPanel.addDateChangedListener(this);
        this.setTop(selectionPanel);

        
        dataTable = new TableView();

        TableColumn<HistoricalRecord,String> col = new TableColumn("Time");
        col.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatTime(rec.getValue().getTime().toLocalTime())));
        dataTable.getColumns().add(col);

        col = new TableColumn("Low Outdoor Temperature");
        col.setCellValueFactory(new TableColumnValueFactory("Low Outdoor Temperature", HistoricalRecord::getLowOutdoorTemperature));
        dataTable.getColumns().add(col);

        col = new TableColumn("High Solar Radiation");
        col.setCellValueFactory(new TableColumnValueFactory("High Solar Radiation", HistoricalRecord::getHighSolarRadiation));
        dataTable.getColumns().add(col);

        //col = new TableColumn("UV Index");
        //col.setCellValueFactory(new TableColumnValueFactory("UV Index", HistoricalRecord::getHighUvIndex));
        //dataTable.getColumns().add(col);

        List<Sensor> sensors = SensorManager.getInstance().getExtraSensors(SensorType.THERMOMETER);
        for (Sensor sensor : sensors) {
            col = new TableColumn(sensor.getName());
            col.setCellValueFactory(new TableColumnValueFactory("High Solar Radiation", HistoricalRecord::getTemperatureForSensor, sensor.getSensorId()));
            dataTable.getColumns().add(col);
        }
        this.setCenter(dataTable);

        Platform.runLater(() -> loadData(date));
    }

    /**
     * Load the data for the specified day.
     * 
     * @param c The date for the date of data to be loaded
     */
    private void loadData(LocalDate date) {
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);
        dataTable.setItems(FXCollections.observableList(list));

        /*
        int row = 0;
        for (HistoricalRecord rec : list) {
            int col = 0;
            //tableModel.setValueAt(DisplayConstants.formatTime(rec.getTime().toLocalTime()), row, col++);
            //tableModel.setValueAt(rec.getLowOutdoorTemperature(), row, col++);
            //tableModel.setValueAt(rec.getAvgOutdoorTemperature(), row, col++);
            //tableModel.setValueAt(rec.getHighOutdoorTemperature(), row, col++);
            //tableModel.setValueAt(rec.getOutdoorHumidity(), row, col++);
            //tableModel.setValueAt(rec.getIndoorTemperature(), row, col++);
            //tableModel.setValueAt(rec.getIndoorHumidity(), row, col++);

            loadWind(rec.getAvgWind(), row, col);
            col += 2;
            loadWind(rec.getHighWind(), row, col);
            col += 2;
            loadWind(rec.getWindGust(), row, col);
            col += 2;

            //tableModel.setValueAt(rec.getBaroPressure(), row, col++);

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
    */
    }

    /**
     * Utility to load the wind data.
     * 
     * @param w The wind
     * @param row The row to which to add the wind
     * @param col The first column of the wind data
     */
    private void loadWind(Wind w, int row, int col) {
        /*
        if (w != null) {
            tableModel.setValueAt(w.getSpeed(), row, col++);
            tableModel.setValueAt(w.getDirection().getCompassLabel(), row, col++);
        }
        else {
            tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);
            tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, row, col++);
        }
        */
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
