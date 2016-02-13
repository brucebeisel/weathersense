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
package com.bdb.weather.display.windrose;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.common.WindSlice;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.LabeledFieldPane;

/**
 * Container that displays a Wind Rose
 * 
 * @author Bruce
 *
 */
public class WindRosePane extends ChartDataPane {
    private final WindRosePlot            windRosePlot = new WindRosePlot();
    private final JFreeChart              chart = new JFreeChart(windRosePlot);
    private final ChartViewer             chartViewer = new ChartViewer(chart);
    private final TableView<WindSlice>    dataTable;
    private final TextField               timeField = new TextField();
    private final TextField               calmField = new TextField();
    private boolean                       initialized = false;

    private static final String DEFAULT_HEADINGS[] = {
            "Heading",
            "% of Wind",
            "Avg Speed",
            "Max Speed"
    };

    /**
     * Constructor.
     */
    public WindRosePane() {
        this.setPrefSize(300, 300);
        ChartFactory.getChartTheme().apply(chart);
        chartViewer.setMinHeight(10);
        chartViewer.setMinWidth(10);
        chartViewer.setPrefSize(300, 300);

        dataTable = new TableView();

        FlowPane summaryPanel = new FlowPane();

        summaryPanel.getChildren().add(new LabeledFieldPane<>("Date:", timeField));
        timeField.setEditable(false);
        summaryPanel.getChildren().add(new LabeledFieldPane<>("% Winds are calm:", calmField));
        calmField.setEditable(false);

        summaryPanel.getChildren().add(new Label("Speeds are in " + Speed.getDefaultUnit()));

        BorderPane p = new BorderPane();

        p.setCenter(dataTable);
        p.setTop(summaryPanel);
        this.setTabContents(chartViewer, p);

        TableColumn<WindSlice,String> headingColumn = new TableColumn<>("Heading");
        headingColumn.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(Heading.headingForSlice(rec.getValue().getHeadingIndex(), 16).getCompassLabel()));
        dataTable.getColumns().add(headingColumn);

        TableColumn<WindSlice,String> percentOfWindColumn = new TableColumn<>("% of Wind");
        percentOfWindColumn.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(String.format("%.1f", rec.getValue().getPercentageOfWind())));
        dataTable.getColumns().add(percentOfWindColumn);

        TableColumn<WindSlice,Speed> avgSpeedColumn = new TableColumn<>("Avg Speed");
        avgSpeedColumn.setCellValueFactory((rec) -> new ReadOnlyObjectWrapper<>(rec.getValue().getAvgSpeed()));
        dataTable.getColumns().add(avgSpeedColumn);

        TableColumn<WindSlice,Speed> maxSpeedColumn = new TableColumn<>("Max Speed");
        maxSpeedColumn.setCellValueFactory((rec) -> new ReadOnlyObjectWrapper<>(rec.getValue().getMaxSpeed()));
        dataTable.getColumns().add(maxSpeedColumn);

    }
    
    /**
     * Initialize the component.
     * 
     * @param bins The speed bins
     */
    private void init(List<SpeedBin> bins) {
        if (initialized)
            return;
        
        initialized = true;
        
        int binNumber = 0;
        for (SpeedBin bin : bins) {
            TableColumn<WindSlice,String> column = new TableColumn<>(bin.speedString());
            column.setUserData(binNumber++);
            column.setCellValueFactory((rec) -> new ReadOnlyStringWrapper(String.format("%.1f", rec.getValue().speedBinPercentage((int)rec.getTableColumn().getUserData()))));
            dataTable.getColumns().add(column);
        }
    }

    /**
     * Load the wind rose data.
     * 
     * @param data The data
     */
    public void loadData(WindRoseData data) {
        windRosePlot.clearCornerTextItems();

        if (data == null) {
            windRosePlot.setDataset((WindRoseData)null);
            return;
        }

        init(data.getSpeedBins());
        
        DateTimeFormatter sdf = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        windRosePlot.setDataset(data);
        float calmPercent = ((float)data.getCalmDuration().getSeconds() / (float)data.getTotalDuration().getSeconds()) * 100.0f;
        windRosePlot.addCornerTextItem(String.format("Calm: %.1f%%", calmPercent));
        calmField.setText(String.format("%.1f", calmPercent));
        timeField.setText(sdf.format(data.getTime()));

        Speed maxSpeed = new Speed(0.0);
        Heading maxSpeedHeading = null;
        double speedSum = 0.0;

        //
        // Calculate annotation data
        //
        for (int i = 0; i < data.getNumSlices(); i++) {
            WindSlice slice = data.getSlice(i);

            Heading heading = Heading.headingForSlice(slice.getHeadingIndex(), data.getNumSlices());

            if (slice.getMaxSpeed().get() > maxSpeed.get()) {
                maxSpeed = slice.getMaxSpeed();
                maxSpeedHeading = heading;
            }

            speedSum += slice.getAvgSpeed().get() * slice.getSliceDuration().getSeconds();
        }

        //
        // Add annotations to the panel
        //
        if (maxSpeedHeading != null) {
            windRosePlot.addCornerTextItem(String.format("Max: %s@%s", maxSpeedHeading.getCompassLabel(), maxSpeed));
            Speed avgSpeed = new Speed(speedSum / data.getTotalDuration().getSeconds());
            windRosePlot.addCornerTextItem(String.format("Avg: %s", avgSpeed));
        }

        dataTable.setItems(FXCollections.observableList(data.getSlices()));
    }
}
