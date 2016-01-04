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

import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.bdb.weather.display.Changeable;
import com.bdb.weather.display.StageUtilities;
import com.bdb.weather.display.WeatherSense;

public class ColorPreferencePanel extends BorderPane implements Changeable {

    private class ColorPreferenceEntry {
        public String preferenceName;
        public ColorPicker button;
        public int row;
        public int column;

        public ColorPreferenceEntry(String name, int row, int column) {
            this.preferenceName = name;
            this.button = null;
            this.row = row;
            this.column = column;
        }
    }
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
    private final UserPreferences preferences = UserPreferences.getInstance();
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
        new ColorPreferenceEntry(UserPreferences.RAIN_COLOR_PREF, 11, 1)
    };

    public ColorPreferencePanel() {
        GridPane colorPanel = new GridPane();

        int gridx = 0;
        int gridy = 0;

        //
        // Layout the column headers
        //
        for (int i = 0; i < COLOR_COL_HEADERS.length; i++) {
            gridx = i;
            colorPanel.add(new Label(COLOR_COL_HEADERS[i]), gridx, gridy);
        }

        //
        // Layout the row leaders
        //
        //c.anchor = GridBagConstraints.EAST;
        for (String header : COLOR_ROW_HEADERS) {
            gridx = 0;
            gridy++;
            colorPanel.add(new Label(header), gridx, gridy);

            gridx = 5;
            
            Set<String> names = ColorSchemeCollection.getColorSchemeNames();
            ComboBox<String> scheme = new ComboBox<>();
            scheme.getItems().addAll(names);
            scheme.setUserData(gridy);
            colorPanel.add(scheme, gridx, gridy);
            scheme.setOnAction((ActionEvent e) -> {
                ComboBox<String> cb = (ComboBox<String>)e.getSource();
                applyColorScheme((Integer)cb.getUserData(), cb.getSelectionModel().getSelectedItem());
            });
            gridx = 6;
            CheckBox showSeries = new CheckBox();
            showSeries.setUserData(gridy);
            colorPanel.add(showSeries, gridx, gridy);
            showSeries.setOnAction((ActionEvent e) -> {
                CheckBox cb = (CheckBox)e.getSource();
                int row = (Integer)cb.getUserData();
                for (ColorPreferenceEntry entry : entries) {
                    if (entry.row == row) {
                        addRemoveSeries(entry.preferenceName, cb.isSelected());
                    }
                }
                createSeriesData();
                configureRenderer();
            });
        }

        //c.anchor = GridBagConstraints.CENTER;
        for (ColorPreferenceEntry entry : entries) {
            gridx = entry.column;
            gridy = entry.row;
            ColorPicker button = new ColorPicker();
            button.setValue(preferences.getColorPref(entry.preferenceName));
            //button.setPrefSize(10, 10);
            button.setUserData(entry);
            colorPanel.add(button, gridx, gridy);
            entry.button = button;
        }

        JFreeChart chart = ChartFactory.createXYLineChart("Example", "X Axis", "Y Axis", dataset, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(renderer);

        ChartViewer graphExamplePanel = new ChartViewer(chart);

        setTop(colorPanel);
        setCenter(graphExamplePanel);
        FlowPane buttonPanel = new FlowPane();
        Button button = new Button("OK");
        button.setOnAction((ActionEvent e) -> {
            if (dirty)
                saveData();

            //this.dispose();
        });
        buttonPanel.getChildren().add(button);
        setBottom(buttonPanel);
    }
    
    private void configureSeriesRenderer(ColorPreferenceEntry entry) {
        int index = dataset.indexOf(entry.preferenceName);
        if (index >= 0)
            renderer.setSeriesPaint(index, StageUtilities.toAwtColor(entry.button.getValue()));
    }

    private void applyColorScheme(int row, String schemeName) {
        ColorScheme scheme = ColorSchemeCollection.getScheme(schemeName);
        for (ColorPreferenceEntry entry : entries) {
            if (entry.row == row) {
                int column = entry.column - 1;
                if (column < scheme.getSchemeColorCount()) {
                    entry.button.setValue(scheme.getColor(column));
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
                series.add(j / 360.0, Math.sin(Math.toRadians(j + (i * (360.0 / dataset.getSeriesCount())))), false);
            }
            series.fireSeriesChanged();
        }
    }

    private void configureRenderer() {
        renderer.setBaseShapesVisible(false);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            String seriesName = (String)dataset.getSeriesKey(i);

            for (ColorPreferenceEntry entry : entries) {
                if (entry.preferenceName.equals(seriesName)) {
                    renderer.setSeriesPaint(i, StageUtilities.toAwtColor(entry.button.getValue()));
                    break;
                }
            }
        }
    }

    /*
    private void editColor(Button button) {
        Color color = ColorPicker.showDialog(this, "Choose Color", button.getBackground());
        if (color != null) {
            ColorPreferenceEntry entry = (ColorPreferenceEntry) button.getClientProperty("entry");
            configureSeriesRenderer(entry);
            dirty = true;
        }
    }
    */

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
            preferences.putColorPref(entry.preferenceName, entry.button.getValue());
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
