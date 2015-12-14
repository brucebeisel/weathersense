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
package com.bdb.weather.display.summary;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.windrose.WindRosePane;

public class SummariesGraphPanel {
    private final JComponent                     component = new JPanel(new BorderLayout());
    private final HighLowMedianTempPanel         temperaturePanel;
    private final TemperatureDeviationPlotPanel  deltaPanel;
    private final RainSummary                    rainPanel;
    private final WindSummary                    windPanel;
    private final WindRosePane                  windRosePanel;
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

        JSplitPane sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, temperaturePanel.getComponent(), deltaPanel.getComponent());
        //JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, windPanel.getComponent(), windRosePanel.getComponent());       
//        JSplitPane sp3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, rainPanel.getComponent(), sp2);
//        JSplitPane sp4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, sp1, sp3);
//        JSplitPane sp5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, highLowHumidityPanel.getComponent(), highLowPressurePanel.getComponent());
//        JSplitPane sp6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, sp4, sp5);
        
//        component.add(sp6, BorderLayout.CENTER);
        
        sp1.setResizeWeight(.5);
        //sp2.setResizeWeight(.7);
//        sp3.setResizeWeight(.5);
//        sp4.setResizeWeight(.5);
//        sp5.setResizeWeight(.5);
//        sp6.setResizeWeight(.7);
    }
    
    public JComponent getComponent() {
        return component;
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