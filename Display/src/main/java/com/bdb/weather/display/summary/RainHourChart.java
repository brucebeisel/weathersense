/*
 * Copyright (C) 2015 bruce
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

import javafx.scene.layout.HBox;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 *
 * @author bruce
 */
public class RainHourChart extends HBox {
    private static final String CATEGORY_NAME = "Rain";
    private final CategoryPlot plot;
    private final DefaultCategoryDataset hourRainDataset = new DefaultCategoryDataset();

    public RainHourChart() {
        for (int i = 0; i < 24; i++)
            hourRainDataset.addValue(0.0, CATEGORY_NAME, Integer.toString(i));

        JFreeChart rainChart = ChartFactory.createBarChart(null, "Hour", "", hourRainDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartViewer rainChartViewer = new ChartViewer(rainChart);
        rainChartViewer.setMaxHeight(10000);
        rainChartViewer.setMaxWidth(10000);
        rainChartViewer.setPrefSize(800, 200);
        plot = (CategoryPlot)rainChart.getPlot();
        plot.setRangeAxis(new RainRangeAxis());
        this.getChildren().add(rainChartViewer);
    }

    public void loadData(Statistics rec) {
        DayHourRain hourlyRain = rec.getHourlyRainfall();
        hourlyRain.getHourValues().forEach((hour) -> {
            hourRainDataset.setValue(hourlyRain.getRain(hour).get(), CATEGORY_NAME, hour.toString());
        });
    }
}
