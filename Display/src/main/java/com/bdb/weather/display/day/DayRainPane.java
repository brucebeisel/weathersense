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
package com.bdb.weather.display.day;

import java.awt.Color;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.RainItemLabelGenerator;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 * Class to display a day's worth of rain data.
 * 
 * @author Bruce
 *
 */
public class DayRainPane extends ChartDataPane {
    private static final String HOUR_ROW_KEY = "Hour";
    private static final String RAIN_ROW_KEY = "Rain";
    private static final String ET_ROW_KEY = "ET";

    private final CategoryPlot        rainPlot;
    private final JFreeChart          chart;
    private final ChartViewer         chartViewer;
    private final TableView           dataTable;
    private final NumberAxis          valueAxis = RainRangeAxis.create();
    private final DateTimeFormatter   hourFormatter = DateTimeFormatter.ofPattern("h a");
    private LocalDateTime             timeCache = LocalDate.now().atStartOfDay();

    public final class RainItem {
        private final IntegerProperty hourProperty;
        private final ObjectProperty<Depth> rainProperty;
        private final ObjectProperty<Depth> etProperty;
        
        public RainItem(int hour, Depth rainfall, Depth ET) {
            hourProperty = new SimpleIntegerProperty(this, HOUR_ROW_KEY);
            rainProperty = new SimpleObjectProperty<>(this, RAIN_ROW_KEY);
            etProperty = new SimpleObjectProperty<>(this, ET_ROW_KEY);

            setHour(hour);
            setRain(rainfall);
            setET(ET);
        }
        
        public IntegerProperty hourProperty() {
            return hourProperty;
        }
        
        public void setHour(int hour) {
            hourProperty().set(hour);
        }
        
        public int getHour() {
            return hourProperty.getValue();
        }
        
        public ObjectProperty<Depth> rainProperty() {
            return rainProperty;
        }
        
        public void setRain(Depth rain) {
            rainProperty().set(rain);
        }
        
        public Depth getRain() {
            return rainProperty.getValue();
        }
        
        public ObjectProperty<Depth> etProperty() {
            return etProperty;
        }
        
        public void setET(Depth et) {
            etProperty().set(et);
        }
        
        public Depth getET() {
            return etProperty.getValue();
        }
    }
    /**
     * Constructor.
     */
    public DayRainPane() {
        setPrefSize(400, 300);
        chart = ChartFactory.createBarChart("Water Cycle", "Hour", "", null, PlotOrientation.VERTICAL, true, true, false);

        chartViewer = new ChartViewer(chart);

        rainPlot = (CategoryPlot)chart.getPlot();
        rainPlot.setNoDataMessage("There is no data for the specified day");
        
        BarRenderer renderer = (BarRenderer)rainPlot.getRenderer();
        renderer.setBasePaint(Color.BLUE);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesItemLabelGenerator(0, new RainItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, Depth.getDefaultFormatter()));
        StandardCategoryToolTipGenerator ttgen = new StandardCategoryToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, Depth.getDefaultFormatter());
        rainPlot.getRenderer().setSeriesToolTipGenerator(0, ttgen);

        NumberFormat etFormatter = (NumberFormat)Depth.getDefaultFormatter().clone();
        etFormatter.setMaximumFractionDigits(etFormatter.getMaximumFractionDigits() + 1);
        renderer.setSeriesItemLabelGenerator(1, new RainItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, etFormatter));
        ttgen = new StandardCategoryToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, etFormatter);
        rainPlot.getRenderer().setSeriesToolTipGenerator(1, ttgen);

        rainPlot.setRangeAxis(valueAxis);
        rainPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        dataTable = new TableView();
       
        TableColumn<RainItem,Integer> hourColumn = new TableColumn<>(HOUR_ROW_KEY);
        hourColumn.setCellValueFactory(new PropertyValueFactory(HOUR_ROW_KEY));
        dataTable.getColumns().add(hourColumn);
        
        TableColumn<RainItem,Depth> rainfallColumn = new TableColumn<>("Rainfall");
        rainfallColumn.setCellValueFactory(new PropertyValueFactory(RAIN_ROW_KEY));
        dataTable.getColumns().add(rainfallColumn);
        
        TableColumn<RainItem,Depth> etColumn = new TableColumn<>(ET_ROW_KEY);
        etColumn.setCellValueFactory(new PropertyValueFactory(ET_ROW_KEY));
        dataTable.getColumns().add(etColumn);
        
        this.setTabContents(chartViewer, dataTable);
    }
    
    /**
     * Build the label for the domain axis
     * 
     * @param hour The hour for which the label will be created
     * @return The axis label
     */
    private String buildHourLabel(int hour) {
        timeCache = timeCache.withHour(hour);
        return hourFormatter.format(timeCache);
    }

    /**
     * Load the data into the plot.
     * 
     * @param hourlyRain The data to load
     * @param records The list of historical records
     */
    public void loadData(DayHourRain hourlyRain, List<HistoricalRecord> records) {
        if (hourlyRain == null) {
            dataTable.setItems(null);
            rainPlot.setDataset(null);
            return;
        }
        
        List<RainItem> rows = new ArrayList<>();
        DefaultCategoryDataset rainDataset = new DefaultCategoryDataset();

        int n = 0;

        Set<Integer> hours = hourlyRain.getHourValues();
        for (int hour : hours) {
            String label = buildHourLabel(hour);
            rainDataset.addValue(hourlyRain.getRain(hour).get(), RAIN_ROW_KEY, label);
            Depth et = new Depth(0.0);
            for (HistoricalRecord record : records) {
                if (record.getTime().getHour() == n && record.getEvapotranspiration() != null)
                    et = et.add(record.getEvapotranspiration());
            }

            rainDataset.addValue(et.get(), ET_ROW_KEY, label);

            n++;
            RainItem row = new RainItem(hour, hourlyRain.getRain(hour), et);
            rows.add(row);
        }
        dataTable.setItems(FXCollections.observableList(rows));

        rainPlot.setDataset(rainDataset);
    }
}
