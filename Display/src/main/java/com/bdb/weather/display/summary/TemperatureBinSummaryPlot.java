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

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.ThresholdType;
import com.bdb.weather.common.measurement.Temperature;

@SuppressWarnings("serial")
public class TemperatureBinSummaryPlot extends ChartViewer {
    private final CombinedDomainCategoryPlot plot;
    private final DefaultCategoryDataset     countDataset = new DefaultCategoryDataset();
    private final CategoryPlot               countPlot = new CategoryPlot();
    private final DefaultCategoryDataset     durationDataset = new DefaultCategoryDataset();
    private final CategoryPlot               durationPlot = new CategoryPlot();
    
    public TemperatureBinSummaryPlot() {
        super(null);
        plot = new CombinedDomainCategoryPlot();
        JFreeChart chart = new JFreeChart(plot);
        setChart(chart);
        setMaxHeight(10000);
        setMaxWidth(10000);
        
        NumberAxis countAxis = new NumberAxis("Day Count");
        countAxis.setUpperMargin(.2);
        countPlot.setRangeAxis(countAxis);
        countPlot.setDataset(countDataset);
        
        BarRenderer r = new BarRenderer();
        r.setBaseItemLabelsVisible(true);     
        r.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        r.setSeriesPaint(0, Color.BLUE);
        r.setBasePaint(Color.BLUE);
        countPlot.setRenderer(r);
        
        NumberAxis durationAxis = new NumberAxis("Duration (Hours)");
        durationAxis.setUpperMargin(.2);
        durationPlot.setRangeAxis(durationAxis);
        durationPlot.setDataset(durationDataset);
        r = new BarRenderer();
        r.setBaseItemLabelsVisible(true);
        NumberFormat format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(1);
        format.setMinimumFractionDigits(1);
        r.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, format));
        r.setSeriesPaint(0, Color.RED);
        r.setBasePaint(Color.RED);
        durationPlot.setRenderer(r);
        
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        plot.add(countPlot);
        plot.add(durationPlot);
    }
    
    private String buildDatasetName(ThresholdType type, Temperature threshold) {
        String title;

        if (type == ThresholdType.ABOVE_THRESHOLD)
            title = "Above ";
        else
            title = "Below ";

        return title + threshold;
    }

    public void loadData(Map<TemperatureBin,Statistics.TemperatureBinData> binData) {
        countDataset.clear();
        durationDataset.clear();
        
        binData.entrySet().stream().forEach((entry) -> {
            String datasetName = buildDatasetName(entry.getKey().getThresholdType(), entry.getKey().getThreshold());
            countDataset.addValue(entry.getValue().dayCount, "Day Count", datasetName);
            durationDataset.addValue(entry.getValue().duration.getSeconds() / 3600.0, "Duration (Hours)", datasetName);
        });
    }
}
