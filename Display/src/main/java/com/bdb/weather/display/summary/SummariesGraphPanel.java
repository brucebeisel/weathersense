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

import java.util.List;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.windrose.WindRosePane;

public class SummariesGraphPanel extends GridPane {
    private final HighLowMedianTempPanel         temperaturePanel;
    private final TemperatureDeviationPlotPanel  deltaPanel;
    private final RainSummary                    rainPanel;
    private final WindSummary                    windPanel;
    private final WindRosePane                   windRosePanel;
    private final HighLowHumidityPanel           highLowHumidityPanel;
    private final HighLowPressurePanel           highLowPressurePanel;

    public SummariesGraphPanel(SummaryInterval interval, DBConnection connection, ViewLauncher launcher, SummarySupporter supporter) {
        temperaturePanel = new HighLowMedianTempPanel(interval, launcher, supporter);
        deltaPanel = new TemperatureDeviationPlotPanel(interval, launcher, supporter);
        rainPanel = new RainSummary(interval, supporter);
        windPanel = new WindSummary(interval, launcher, supporter);
        windRosePanel = new WindRosePane();
        highLowHumidityPanel = new HighLowHumidityPanel(interval, launcher, supporter);
        highLowPressurePanel = new HighLowPressurePanel(interval, launcher, supporter);

        ColumnConstraints constraint = new ColumnConstraints();
        constraint.setPercentWidth(16.67);
        this.getColumnConstraints().add(constraint);
        this.getColumnConstraints().add(constraint);
        this.getColumnConstraints().add(constraint);
        this.getColumnConstraints().add(constraint);
        this.getColumnConstraints().add(constraint);
        this.getColumnConstraints().add(constraint);

        this.add(temperaturePanel, 0, 0, 3, 1);
        this.add(deltaPanel,       3, 0, 3, 1);

        this.add(rainPanel,     0, 1, 3, 1);
        this.add(windPanel,     3, 1, 2, 1);
        this.add(windRosePanel, 5, 1, 1, 1);

        this.add(highLowHumidityPanel, 0, 2, 3, 1);
        this.add(highLowPressurePanel, 3, 2, 3, 1);
    }
    
    public void loadData(List<SummaryRecord> list, WeatherAverages averages) {        
        temperaturePanel.loadData(list, averages);
        deltaPanel.loadData(list, averages);
        rainPanel.loadData(list);
        highLowHumidityPanel.loadData(list);
        highLowPressurePanel.loadData(list);
        windPanel.loadData(list);

        //
        // Compile wind data
        //
        if (!list.isEmpty()) {
            WindRoseData windData = list.get(0).getWindRoseData();

            for (int i = 1; i < list.size(); i++)
                windData.addWindRoseData(list.get(i).getWindRoseData());

            windRosePanel.loadData(windData);
        }
    }
}