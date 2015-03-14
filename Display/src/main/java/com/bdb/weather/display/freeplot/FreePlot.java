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
package com.bdb.weather.display.freeplot;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.display.DataRangeSelectionPanel;

/**
 * A JPanel that contains a Free plot. A free plot is one where any value can be displayed
 * over time.
 * 
 * @author Bruce
 *
 */
public class FreePlot implements ActionListener {
    /**
     * An interface to create the series collections, the group controls and retrieve the data from the database.
     * 
     * @author Bruce
     *
     */
    interface SeriesCollectionFactory {
        Map<String,SeriesGroupControl> createSeriesGroupControls(ActionListener listener);
        List<FreePlotSeriesCollection> createSeriesCollections(XYPlot plot, int domainAxisIndex, Stroke stroke);
        List<?> retrieveData(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    }
    
    private static final String LOAD_DATA_BUTTON = "Load Data";
    private final JComponent                      component = new JPanel(new BorderLayout());
    private final XYPlot                          plot = new XYPlot();
    private List<FreePlotSeriesCollection>        collections;
    private final Map<String, SeriesGroupControl> controls;
    private DataRangeSelectionPanel               dataRangePanel;
    private final SeriesCollectionFactory         factory;
    private boolean                               dataLoaded = false;
    private static final float STROKE_DASH[][] = {
        {5f, 5f},
        {10f, 10f},
        {15f, 5f, 5f, 5f}
    };
    private static final float STROKE_WEIGHT = 1.0f;
    private BasicStroke                      strokes[];
    
    /**
     * Constructor.
     * 
     * @param seriesFactory The factory for creating the series
     * @param controlFactory The factory for creating the controls
     */
    
    @SuppressWarnings("LeakingThisInConstructor")
    FreePlot(SeriesCollectionFactory seriesFactory) {
        controls = seriesFactory.createSeriesGroupControls(this);
        factory = seriesFactory;
        component.add(createDataRangeSelectionPanel(), BorderLayout.NORTH);
        component.add(createDataSelectionPanel(controls.values()), BorderLayout.WEST);
        
        ChartPanel chartPanel = new ChartPanel(new JFreeChart(plot));
        chartPanel.setMaximumDrawHeight(20000);
        chartPanel.setMaximumDrawWidth(20000);
        component.add(chartPanel, BorderLayout.CENTER);
        
        //
        // Create a default Y Axis
        //
        DateAxis dateAxis = new DateAxis("Date/Time");
        plot.setDomainAxis(dateAxis);
        plot.setRangeAxis(new NumberAxis("Data"));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        
        buildStrokes();
    }
    
    /**
     * Get the Swing component that is the container for a Free Plot.
     * 
     * @return The Swing container component
     */
    public JComponent getComponent() {
        return component;
    }
    
    /**
     * Build the Stroke objects that will be used for each series collection.
     */
    private void buildStrokes() {    
        strokes = new BasicStroke[STROKE_DASH.length + 1];
        //
        // The zeroith series is a solid line
        //
        strokes[0] = new BasicStroke(STROKE_WEIGHT);
        
        //
        // The rest of the strokes are build from the STROKE_DASH array
        //
        for (int i = 1; i < strokes.length; i++) {
            strokes[i] = new BasicStroke(STROKE_WEIGHT, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, STROKE_DASH[i - 1], 0.0f);
        }
    }
    
    /**
     * Create the panel that allows the user to select the date ranges
     * 
     * @return The Swing component
     */
    private JComponent createDataRangeSelectionPanel() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        
        dataRangePanel = new DataRangeSelectionPanel(start, end);
        return dataRangePanel.getComponent();
    }
    
    /**
     * Create the panel that allows the user to select what series are displayed
     * 
     * @param controls The series display controls
     * 
     * @return The Swing component
     */
    private JComponent createDataSelectionPanel(Collection<SeriesGroupControl> controls) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        
        Box box = Box.createVerticalBox();

        JButton b = new JButton(LOAD_DATA_BUTTON);
        b.addActionListener(this);
        b.setActionCommand(LOAD_DATA_BUTTON);
        b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        box.add(b);
        
        for (SeriesGroupControl control : controls) {
            JComponent collectionPanel = control.getComponent();
            box.add(collectionPanel);
        }
        
        box.add(Box.createVerticalGlue());
        
        p.add(box, BorderLayout.NORTH);
        p.add(new JPanel(), BorderLayout.CENTER);
        
        return p;
    }
    
    /**
     * Display the data based on the controls
     */
    private void displayData() {
        //
        // If we haven't loaded any data yet, then there is nothing to do
        //
        if (!dataLoaded)
            return;
        
        //
        // Build the list of series that are to be displayed
        //
        List<String> displayList = new ArrayList<>();
        
        controls.values().stream().forEach((control) -> {
            displayList.addAll(control.getVisibleSeries());
        });
        
        //
        // Clear all of the Range (Y) axes and start from scratch
        //
        int rangeAxisIndex = 0;
        plot.clearRangeAxes();
        
        //
        // Set up the Range axes. Each axis must have an index and they must be sequential (JFreeChart limitation)
        //
        for (SeriesGroupControl control : controls.values()) {
            if (control.shouldAxisBeVisible()) {
                if (control.getRangeAxis() != null) {
                    control.setRangeAxisIndex(rangeAxisIndex);
                    plot.setRangeAxis(rangeAxisIndex, control.getRangeAxis());

                    //
                    // Alternate left and right
                    //
                    if (rangeAxisIndex % 2 == 0)
                        plot.setRangeAxisLocation(rangeAxisIndex, AxisLocation.TOP_OR_LEFT);
                    else
                        plot.setRangeAxisLocation(rangeAxisIndex, AxisLocation.TOP_OR_RIGHT);

                    rangeAxisIndex++;
                }
            }   
        }
        
        //
        // For each collection, inform it what it's range axis index is then have it
        // display the series data
        //
        for (FreePlotSeriesCollection collection : collections) {
            SeriesGroupControl control = controls.get(collection.getGroupName());
            collection.setRangeAxisIndex(control.getRangeAxisIndex());
            collection.displayData(displayList);
        }
    }
    
    /**
     * Create a standard domain axis for the free plot panel
     * 
     * @param range The date range for this axis
     * @return The created axis
     */
    private DateAxis createDomainAxis(DateRange range) {
        String startDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(range.getStart());
        String endDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(range.getEnd());
        DateAxis axis = new DateAxis(startDate + " - " + endDate);
        return axis;
    }
    
    /**
     * Load the data from the database into the Series.
     * 
     * @throws SQLException A database error occurred
     */
    private void loadData() throws SQLException {
        collections = new ArrayList<>();
        List<DateRange> ranges = dataRangePanel.getRanges();
        
        //
        // Clear all of the domain (X) axes and clear out all of the datasets.
        // We will start from scratch
        //
        plot.clearDomainAxes();
        for (int i = 0; i < plot.getDatasetCount(); i++)
            plot.setDataset(i, null);
        
        int domainAxisIndex = 0;
        for (DateRange dateRange : ranges) {     
            //
            // First set up the domain axis for this date range
            //
            plot.setDomainAxis(domainAxisIndex, createDomainAxis(dateRange));
            plot.setDomainAxisLocation(domainAxisIndex, AxisLocation.BOTTOM_OR_LEFT);
            
            List<FreePlotSeriesCollection> seriesCollection = factory.createSeriesCollections(plot, domainAxisIndex, strokes[domainAxisIndex]);
            domainAxisIndex++;
            collections.addAll(seriesCollection);
            
            //
            // Now get the data for the date range
            //
            List<?> data = factory.retrieveData(dateRange.getStart(), dateRange.getEnd());
            seriesCollection.stream().forEach((collection) -> {
                collection.loadData(data);
            });
        }
        
        dataLoaded = true;
    }
    
    /**
     * Handle the pressing of the Load Data button
     * @param e The event that triggered this action
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals(LOAD_DATA_BUTTON))
                loadData();

            displayData();
        }
        catch (SQLException e1) {
            JOptionPane.showConfirmDialog(null, "A database error occurred", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
