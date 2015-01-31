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

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;

import com.bdb.util.LabeledFieldPanel;

import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.common.WindSlice;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.display.DisplayConstants;

/**
 * Container that displays a Wind Rose
 * 
 * @author Bruce
 *
 */
public class WindRosePanel {
    private final JTabbedPane             component = new JTabbedPane();
    private final WindRosePlot 	          windRosePlot = new WindRosePlot();
    private final JFreeChart	 	  chart = new JFreeChart(windRosePlot);
    private final PolarChartPanel	  chartPanel = new PolarChartPanel(chart);
    private final JTable		  dataTable;
    private final DefaultTableModel	  tableModel = new DefaultTableModel();
    private final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
    private final JTextField		  timeField = new JTextField(10);
    private final JTextField		  calmField = new JTextField(5);
    private boolean                       initialized = false;
    private static final int              HEADING_COLUMN = 0;
    private static final int              PERCENT_OF_WIND_COLUMN = 1;
    private static final int              AVG_SPEED_COLUMN = 2;
    private static final int              MAX_SPEED_COLUMN = 3;

    private static final String	DEFAULT_HEADINGS[] = {
            "Heading",
            "% of Wind",
            "Avg Speed",
            "Max Speed"
    };

    /**
     * Constructor.
     */
    public WindRosePanel() {
        ChartFactory.getChartTheme().apply(chart);
        chartPanel.setMinimumDrawHeight(10);
        chartPanel.setMinimumDrawWidth(10);

        component.addTab(DisplayConstants.GRAPH_TAB_NAME, chartPanel);

        dataTable = new JTable();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(columnModel);

        dataTable.setAutoCreateColumnsFromModel(false);
        
        JScrollPane sp = new JScrollPane(dataTable);

        JPanel summaryPanel = new JPanel();

        summaryPanel.add(new LabeledFieldPanel<>("Date:", timeField));
        timeField.setEditable(false);
        summaryPanel.add(new LabeledFieldPanel<>("% Winds are calm:", calmField));
        calmField.setEditable(false);

        summaryPanel.add(new JLabel("Speeds are in " + Speed.getDefaultUnit()));

        JPanel p = new JPanel(new BorderLayout());

        p.add(sp, BorderLayout.CENTER);
        p.add(summaryPanel, BorderLayout.NORTH);

        component.addTab(DisplayConstants.DATA_TAB_NAME, p);
    }
    
    /**
     * Get the swing component that contains the wind rose.
     * 
     * @return The swing container
     */
    public JComponent getComponent() {
        return component;
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
        
        //
        // Setup the table heading and columns
        //
        String tableHeadings[] = new String[DEFAULT_HEADINGS.length + bins.size()];
        int n = 0;
        for (String heading : DEFAULT_HEADINGS)
            tableHeadings[n++] = heading;
        
        for (SpeedBin bin : bins)
            tableHeadings[n++] = bin.toString();

        for (int i = 0; i < tableHeadings.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(tableHeadings[i]);
            col.setModelIndex(i);
            columnModel.addColumn(col);
        }

        tableModel.setColumnCount(tableHeadings.length);
    }

    /**
     * Load the wind rose data.
     * 
     * @param data The data
     */
    public void loadData(WindRoseData data) {
        windRosePlot.clearCornerTextItems();

        if (data == null) {
            tableModel.setRowCount(0);
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
        // Load the table
        //
        tableModel.setRowCount(data.getNumSlices());

        for (int i = 0; i < data.getNumSlices(); i++) {
            WindSlice slice = data.getSlice(i);

            Heading heading = Heading.headingForSlice(slice.getHeadingIndex(), data.getNumSlices());
            tableModel.setValueAt(heading.getCompassLabel(), i, HEADING_COLUMN);
            tableModel.setValueAt(String.format("%.1f", slice.getPercentageOfWind()), i, PERCENT_OF_WIND_COLUMN);
            tableModel.setValueAt(slice.getAvgSpeed(), i, AVG_SPEED_COLUMN);
            tableModel.setValueAt(slice.getMaxSpeed(), i, MAX_SPEED_COLUMN);

            if (slice.getMaxSpeed().get() > maxSpeed.get()) {
                maxSpeed = slice.getMaxSpeed();
                maxSpeedHeading = heading;
            }

            speedSum += slice.getAvgSpeed().get() * slice.getSliceDuration().getSeconds();

            for (int j = 0; j < slice.getNumSpeedBins(); j++)
                tableModel.setValueAt(String.format("%.1f", slice.speedBinPercentage(j)), i, DEFAULT_HEADINGS.length + j);
        }

        //
        // Add annotations to the panel
        //
        if (maxSpeedHeading != null) {
            windRosePlot.addCornerTextItem(String.format("Max: %s@%s", maxSpeedHeading.getCompassLabel(), maxSpeed));
            Speed avgSpeed = new Speed(speedSum / data.getTotalDuration().getSeconds());
            windRosePlot.addCornerTextItem(String.format("Avg: %s", avgSpeed));
        }
    }
}
