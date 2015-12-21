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
package com.bdb.weather.display.stripchart;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.layout.TilePane;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;

/**
 * A Swing container that manages the chart panels for this strip chart panel.
 * 
 * @author Bruce
 */
public class StripChartContainer extends TilePane implements StripChartManagerOld {
    private final Map<String,StripChartController> controllers = new TreeMap<>();
    private int nextControllerId = 0;
    
    /**
     * Constructor.
     */
    public StripChartContainer() {
    }
    
    /**
     * Add a new graph to the strip chart panel.
     * 
     * @param leftAxis The measurement type associated with the left axis
     * @param rightAxis The measurement type associated with the right axis
     * @param initialData The initial data to be loaded into the graph
     * @param hours The number of hours the domain axis should span
     * @param maxHours The maximum number of hours of data to hold in the datasets
     */
    @Override
    public void addStripChart(MeasurementType leftAxis, MeasurementType rightAxis, List<HistoricalRecord> initialData, int hours, int maxHours) {
        String id = Integer.toString(nextControllerId++);
        StripChartController controller = new StripChartController(id, leftAxis, rightAxis, initialData, hours, maxHours, this);
        controllers.put(id, controller);
        getChildren().add(controller);
    }
    
    /**
     * Update the strip charts with the latest weather data.
     * 
     * @param cw The current weather
     */
    @Override
    public void updateStripCharts(CurrentWeather cw) {
        controllers.values().stream().forEach((controller) -> {
            controller.updateStripChart(cw);
        });
    }
    
    /**
     * Change the span of the domain axis of the strip charts.
     * 
     * @param span The number of hours the axis should span
     */
    public void changeStripChartSpan(int span) {
        controllers.values().stream().forEach((controller) -> {
            controller.changeStripChartSpan(span);
        });
    }

    /**
     * Remove a strip chart from the panel.
     * 
     * @param name The name of the chart to remove
     */
    @Override
    public void removeStripChart(String name) {
        StripChartController controller = controllers.remove(name);
        controller.setVisible(false);
        this.getChildren().remove(controller);
    }
}
