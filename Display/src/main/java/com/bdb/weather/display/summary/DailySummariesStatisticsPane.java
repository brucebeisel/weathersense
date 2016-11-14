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
package com.bdb.weather.display.summary;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
/**
 * Need fields for:
 * Temperatures:
 *      Min Low, Max Low, Min High, Max High with times - DONE
 *      Avg (s), avg high, avg low - DONE
 *      Largest temperature range with day - DONE
 *      Smallest temperature range with day - DONE
 *      List of records set with values and dates
 *      number of days high is above and below normal - DONE
 *      number of days low is above and below normal - DONE
 *      Over/Under tables with HH:MM above/below, Days above/below, %days above/below
 *      Time/days below freezing, could be part of above with 32F being a special value
 * Rain - DONE
 *      Max rain day - amount and day (s)
 *      Max rain rate - rate and time (s)
 *      Number of rain days
 *      Total rain
 *      Avg per/day
 *      Rain in each hour
 * Wind
 *      Max wind with time (s) - DONE
 *      Max gust with time (s) - DONE
 *      Avg wind (s) - DONE
 *      Day with highest average (s) - DONE
 *      Direction ?
 * Pressure - DONE
 *      Max with time (s)
 *      Min with time (s)
 *      Max range with date
 *      Min range with date
 *      Avg. (s)
 * Humidity - DONE
 *      Max with time (s)
 *      Min with time (s)
 *      Max range with date
 *      Min range with date
 *      Avg. (s)
 *      
 * @author Bruce
 *
 */

public class DailySummariesStatisticsPane extends VBox {
    private static final int DATE_FIELD_LENGTH = 10;
    private static final int DATETIME_FIELD_LENGTH = 15;
    private static final int PRESSURE_FIELD_LENGTH = 5;
    private static final int HUMIDITY_FIELD_LENGTH = 4;
    private static final int RAIN_FIELD_LENGTH = 6;
    private final TableView                 recordTable = new TableView();
    @FXML private TitledPane                temperatureTitledPane;
    @FXML private TemperatureStatisticsPane temperatureStatisticsPane;
    @FXML private TitledPane                rainTitledPane;
    @FXML private RainStatisticsPane        rainStatisticsPane;
    @FXML private TitledPane                windTitledPane;
    @FXML private WindStatisticsPane        windStatisticsPane;
    @FXML private TitledPane                pressureTitledPane;
    @FXML private PressureStatisticsPane    pressureStatisticsPane;
    @FXML private HumidityStatisticsPane    humidityStatisticsPane;

    private static final String[] COLUMN_NAMES = {
        "Date", "Record", "Type", "Previous Record Date", "Previous Record"
    };
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DailySummariesStatisticsPane() {
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DailySummariesStatistics.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        temperatureTitledPane.setText(temperatureTitledPane.getText() + " (" + Temperature.getDefaultUnit() + ")");
        rainTitledPane.setText(rainTitledPane.getText() + " (" + Depth.getDefaultUnit() + ")");
        windTitledPane.setText(windTitledPane.getText() + " (" + Speed.getDefaultUnit() + ")");
        pressureTitledPane.setText(pressureTitledPane.getText() + " (" + Pressure.getDefaultUnit() + ")");

        /*
        DefaultTableColumnModel colModel = new DefaultTableColumnModel();
        recordTable.setColumnModel(colModel);

        recordTable.setAutoCreateColumnsFromModel(false);

        
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(COLUMN_NAMES[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(COLUMN_NAMES.length);
        
        JPanel recordsPanel = new JPanel(new BorderLayout());
        recordsPanel.setBorder(new TitledBorder(innerBorder, "Records"));
        recordsPanel.setCenter(recordTable);
        
        add(recordsPanel);
*/
    }
    
    // TODO Get the simple date formatter from a global class, like one that contains some sort of preferences
    public void loadData(Statistics rec, WeatherAverage seasonalAverages, List<Extreme<Temperature,TemperatureRecordType>> records) {
        temperatureStatisticsPane.loadData(rec, seasonalAverages);
        rainStatisticsPane.loadData(rec);
        windStatisticsPane.loadData(rec);
        pressureStatisticsPane.loadData(rec);
        humidityStatisticsPane.loadData(rec);

        /*
        tableModel.setNumRows(records.size());
        
        int n = 0;
        for (Extreme<Temperature,TemperatureRecordType> record : records) {
            tableModel.setValueAt(dateOnly.format(record.getDate()), n, 0);
            Temperature t = record.getValue();
            String val = t.toString();
            tableModel.setValueAt(val, n, 1);
            tableModel.setValueAt(record.getType(), n, 2);
            tableModel.setValueAt(dateOnly.format(record.getPreviousDate()), n, 3);
            tableModel.setValueAt(record.getPreviousValue(), n++, 4);
        }
*/
    }
}
