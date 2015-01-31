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
package com.bdb.weather.display.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.bdb.weather.display.Changeable;

public class ColorPreferencePanel extends JDialog implements Changeable {

    private class ColorPreferenceEntry {
        public String preferenceName;
        public JButton button;
        public int row;
        public int column;

        public ColorPreferenceEntry(String name, int row, int column) {
            this.preferenceName = name;
            this.button = null;
            this.row = row;
            this.column = column;
        }
    }
    private static final long serialVersionUID = -7814294389034504261L;
    private static final String COLOR_COL_HEADERS[] = {"", "Current", "High", "Low", "Avg", "Scheme", "Show"};
    private static final String COLOR_ROW_HEADERS[] = {
        "Outdoor Temperature",
        "Indoor Temperature",
        "Outdoor Humidity",
        "Indoor Humidity",
        "Barometric Pressure",
        "Dew Point",
        "Heat Index",
        "Wind Chill",
        "Sustained Wind",
        "Wind Gust",
        "Rain"
    };
    private static final Logger logger = Logger.getLogger(ColorPreferencePanel.class.getName());
    private final UserPreferences preferences;
    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
    private boolean dirty = false;
    private ColorPreferenceEntry entries[] = {
        new ColorPreferenceEntry(UserPreferences.OUTDOOR_TEMP_COLOR_PREF, 1, 1),
        new ColorPreferenceEntry(UserPreferences.HIGH_OUTDOOR_TEMP_COLOR_PREF, 1, 2),
        new ColorPreferenceEntry(UserPreferences.LOW_OUTDOOR_TEMP_COLOR_PREF, 1, 3),
        new ColorPreferenceEntry(UserPreferences.MEAN_OUTDOOR_TEMP_COLOR_PREF, 1, 4),
        new ColorPreferenceEntry(UserPreferences.INDOOR_TEMP_COLOR_PREF, 2, 1),
        new ColorPreferenceEntry(UserPreferences.HIGH_INDOOR_TEMP_COLOR_PREF, 2, 2),
        new ColorPreferenceEntry(UserPreferences.LOW_INDOOR_TEMP_COLOR_PREF, 2, 3),
        new ColorPreferenceEntry(UserPreferences.MEAN_INDOOR_TEMP_COLOR_PREF, 2, 4),
        new ColorPreferenceEntry(UserPreferences.OUTDOOR_HUMIDITY_COLOR_PREF, 3, 1),
        new ColorPreferenceEntry(UserPreferences.HIGH_OUTDOOR_HUMIDITY_COLOR_PREF, 3, 2),
        new ColorPreferenceEntry(UserPreferences.LOW_OUTDOOR_HUMIDITY_COLOR_PREF, 3, 3),
        new ColorPreferenceEntry(UserPreferences.MEAN_OUTDOOR_HUMIDITY_COLOR_PREF, 3, 4),
        new ColorPreferenceEntry(UserPreferences.INDOOR_HUMIDITY_COLOR_PREF, 4, 1),
        new ColorPreferenceEntry(UserPreferences.HIGH_INDOOR_HUMIDITY_COLOR_PREF, 4, 2),
        new ColorPreferenceEntry(UserPreferences.LOW_INDOOR_HUMIDITY_COLOR_PREF, 4, 3),
        new ColorPreferenceEntry(UserPreferences.MEAN_INDOOR_HUMIDITY_COLOR_PREF, 4, 4),
        new ColorPreferenceEntry(UserPreferences.BARO_PRESSURE_COLOR_PREF, 5, 1),
        new ColorPreferenceEntry(UserPreferences.HIGH_BARO_PRESSURE_COLOR_PREF, 5, 2),
        new ColorPreferenceEntry(UserPreferences.LOW_BARO_PRESSURE_COLOR_PREF, 5, 3),
        new ColorPreferenceEntry(UserPreferences.MEAN_BARO_PRESSURE_COLOR_PREF, 5, 4),
        new ColorPreferenceEntry(UserPreferences.DEW_POINT_COLOR_PREF, 6, 1),
        new ColorPreferenceEntry(UserPreferences.HEAT_INDEX_COLOR_PREF, 7, 1),
        new ColorPreferenceEntry(UserPreferences.WIND_CHILL_COLOR_PREF, 8, 1),
        new ColorPreferenceEntry(UserPreferences.WIND_SPEED_COLOR_PREF, 9, 1),
        new ColorPreferenceEntry(UserPreferences.MAX_WIND_SPEED_COLOR_PREF, 9, 2),
        new ColorPreferenceEntry(UserPreferences.AVG_WIND_SPEED_COLOR_PREF, 9, 4),
        new ColorPreferenceEntry(UserPreferences.WIND_GUST_COLOR_PREF, 10, 1),
        new ColorPreferenceEntry(UserPreferences.MAX_WIND_GUST_COLOR_PREF, 10, 2),
        new ColorPreferenceEntry(UserPreferences.RAIN_COLOR_PREF, 11, 1),};

    public ColorPreferencePanel(JFrame parent, UserPreferences preferences) {
        super(parent, "Color Preferences", true);
        this.preferences = preferences;
        setLayout(new BorderLayout());
        JPanel colorPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.ipadx = 3;
        c.ipady = 5;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;

        //
        // Layout the column headers
        //
        for (int i = 0; i < COLOR_COL_HEADERS.length; i++) {
            c.gridx = i;
            colorPanel.add(new JLabel(COLOR_COL_HEADERS[i]), c);
        }

        //
        // Layout the row leaders
        //
        c.anchor = GridBagConstraints.EAST;
        for (String header : COLOR_ROW_HEADERS) {
            c.gridx = 0;
            c.gridy++;
            colorPanel.add(new JLabel(header), c);
            c.gridx = 5;
            
            Set<String> names = ColorSchemeCollection.getColorSchemeNames();
            String[] list = new String[names.size()];
            names.toArray(list);
            JComboBox<String> scheme = new JComboBox<>(list);
            scheme.putClientProperty("row", c.gridy);
            colorPanel.add(scheme, c);
            scheme.addActionListener((ActionEvent e) -> {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                applyColorScheme((Integer)cb.getClientProperty("row"), cb.getItemAt(cb.getSelectedIndex()));
            });
            c.gridx = 6;
            JCheckBox showSeries = new JCheckBox();
            showSeries.putClientProperty("row", c.gridy);
            colorPanel.add(showSeries, c);
            showSeries.addActionListener((ActionEvent e) -> {
                JCheckBox cb = (JCheckBox)e.getSource();
                int row = (Integer)cb.getClientProperty("row");
                for (ColorPreferenceEntry entry : entries) {
                    if (entry.row == row) {
                        addRemoveSeries(entry.preferenceName, cb.isSelected());
                    }
                }
                createSeriesData();
                configureRenderer();
            });
        }

        c.anchor = GridBagConstraints.CENTER;
        for (ColorPreferenceEntry entry : entries) {
            c.gridx = entry.column;
            c.gridy = entry.row;
            JButton button = new JButton("");
            button.setBackground(preferences.getColorPref(entry.preferenceName));
            button.setSize(20, 20);
            button.setPreferredSize(new Dimension(10, 10));
            button.putClientProperty("entry", entry);
            button.addActionListener((ActionEvent e) -> {
                editColor((JButton)e.getSource());
            });
            colorPanel.add(button, c);
            entry.button = button;
        }

        JFreeChart chart = ChartFactory.createXYLineChart("Example", "X Axis", "Y Axis", dataset, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(renderer);

        JPanel graphExamplePanel = new ChartPanel(chart);

        add(colorPanel, BorderLayout.NORTH);
        add(graphExamplePanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton("OK");
        button.addActionListener((ActionEvent e) -> {
            if (dirty)
                saveData();

            this.dispose();
        });
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void configureSeriesRenderer(ColorPreferenceEntry entry) {
        int index = dataset.indexOf(entry.preferenceName);
        if (index >= 0)
            renderer.setSeriesPaint(index, entry.button.getBackground());
    }

    private void applyColorScheme(int row, String schemeName) {
        ColorScheme scheme = ColorSchemeCollection.getScheme(schemeName);
        for (ColorPreferenceEntry entry : entries) {
            if (entry.row == row) {
                int column = entry.column - 1;
                if (column < scheme.getSchemeColorCount()) {
                    entry.button.setBackground(scheme.getColor(column));
                    configureSeriesRenderer(entry);
                    dirty = true;
                }
            }
        }
    }

    private void addRemoveSeries(String seriesName, boolean add) {
        if (add) {
            XYSeries series = new XYSeries(seriesName);
            dataset.addSeries(series);
        }
        else {
            dataset.removeSeries(dataset.getSeries(seriesName));
        }
    }

    private void createSeriesData() {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            XYSeries series = dataset.getSeries(i);
            series.clear();
            for (int j = 0; j < 360; j++) {
                series.add(j / 360.0, Math.sin(Math.toRadians(j + (i * (360.0 / dataset.getSeriesCount())))));
            }
        }
    }

    private void configureRenderer() {
        renderer.setBaseShapesVisible(false);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            String seriesName = (String)dataset.getSeriesKey(i);

            for (ColorPreferenceEntry entry : entries) {
                if (entry.preferenceName.equals(seriesName)) {
                    renderer.setSeriesPaint(i, entry.button.getBackground());
                    break;
                }
            }
        }
    }

    private void editColor(JButton button) {
        Color color = JColorChooser.showDialog(this, "Choose Color", button.getBackground());
        if (color != null) {
            button.setBackground(color);
            ColorPreferenceEntry entry = (ColorPreferenceEntry) button.getClientProperty("entry");
            configureSeriesRenderer(entry);
            dirty = true;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.Changeable#hasChanged()
     */
    @Override
    public boolean hasChanged() {
        return dirty;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.Changeable#resetValues()
     */
    @Override
    public void resetData() {
        configureRenderer();
        dirty = false;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.Changeable#saveData()
     */
    @Override
    public boolean saveData() {
        for (ColorPreferenceEntry entry : entries) {
            preferences.putColorPref(entry.preferenceName, entry.button.getBackground());
        }
        return true;
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        frame.add(new ColorPreferencePanel());
//
//        frame.pack();
//
//        frame.setVisible(true);
//    }
}
