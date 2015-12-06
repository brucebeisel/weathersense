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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import javafx.scene.Node;

import com.bdb.util.LabeledFieldPanel;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.CurrentWeatherProcessor;

/**
 *
 * @author Bruce
 */
public class StripChartPanel extends JPanel implements ComponentContainer, CurrentWeatherProcessor {
    private static final int MAX_HOURS = 72;
    private static final String HOUR_CHOICES[] = {"1", "3", "6", "12", "24", "48", Integer.toString(MAX_HOURS)};
    private final JPanel optionsPanel = new JPanel();
    private final StripChartContainer stripChartContainer = new StripChartContainer();
    private final JPanel newStripChartPanel = new JPanel();
    private JComboBox<String> hoursCB = new JComboBox<>(HOUR_CHOICES);
    private final JComboBox<MeasurementType> leftAxisCB = new JComboBox<>(MeasurementType.values());
    private final JComboBox<MeasurementType> rightAxisCB = new JComboBox<>(MeasurementType.values());
    private final HistoryTable historyTable;
    private int span = 1;
    private final StripChartLayoutManager layoutManager = StripChartLayoutManager.getInstance();
    private final List<HistoricalRecord> initialData = new ArrayList<>();

    public StripChartPanel(DBConnection con, String namedLayout) {
        super(new BorderLayout());
        setBackground(Color.BLUE);
        historyTable = new HistoryTable(con);
 
        createOptionPanelComponents();
        createNewStripChartPanelComponents();
        add(optionsPanel, BorderLayout.NORTH);
        add(stripChartContainer, BorderLayout.CENTER);
        add(newStripChartPanel, BorderLayout.SOUTH);
        rightAxisCB.setSelectedIndex(1);
        hoursCB.addActionListener((ActionEvent e) -> {
            int localSpan = Integer.parseInt(HOUR_CHOICES[hoursCB.getSelectedIndex()]);
            changeStripChartSpan(span);
            span = localSpan;
        });
    }

    @Override
    public Node getComponent() {
        return null;
    }

    private void createOptionPanelComponents() {
        optionsPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        optionsPanel.add(new JLabel("Hours to Display:"));
        optionsPanel.add(hoursCB);
        optionsPanel.add(new JLabel("Saved Layouts:"));
        JComboBox savedLayoutsCB = new JComboBox(layoutManager.getSavedLayoutNames());
        optionsPanel.add(savedLayoutsCB);
        JButton b = new JButton("Load Layout");
        optionsPanel.add(b);
        b = new JButton("Save Layout");
        optionsPanel.add(b);
    }

    private void createNewStripChartPanelComponents() {
        newStripChartPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        newStripChartPanel.add(new JLabel("Select Axis Types"));
        newStripChartPanel.add(new LabeledFieldPanel<>("Left Axis:", leftAxisCB));
        newStripChartPanel.add(new LabeledFieldPanel<>("Right Axis:", rightAxisCB));
        JButton addButton = new JButton("Add Chart");
        newStripChartPanel.add(addButton);
        addButton.addActionListener((ActionEvent e) -> {
            stripChartContainer.addStripChart((MeasurementType) leftAxisCB.getSelectedItem(),
                                              (MeasurementType) rightAxisCB.getSelectedItem(),
                                              retrieveInitialData(),
                                              span, MAX_HOURS);
        });

        leftAxisCB.addActionListener((ActionEvent e) -> {
            keepAxisSelectionsDifferent(leftAxisCB, rightAxisCB);
        });

        rightAxisCB.addActionListener((ActionEvent e) -> {
            keepAxisSelectionsDifferent(rightAxisCB, leftAxisCB);
        });
    }

    private void keepAxisSelectionsDifferent(JComboBox one, JComboBox two) {
        if (one.getSelectedIndex() == two.getSelectedIndex()) {
            int idx = 0;
            if (one.getSelectedIndex() == idx) {
                idx = 1;
            }

            two.setSelectedIndex(idx);
        }
    }

    @Override
    public void updateCurrentWeather(CurrentWeather cw) {
        stripChartContainer.updateStripCharts(cw);
    }

    private void changeStripChartSpan(int span) {
        stripChartContainer.changeStripChartSpan(span);
        this.span = span;
    }

    /**
     * Get the initial data to load the strip chart, it always loads the initial data with
     * the maximum amount of time the graph supports.
     * 
     * @return The list of records
     */
    private List<HistoricalRecord> retrieveInitialData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        if (initialData.isEmpty()) {
            start = now.minusHours(MAX_HOURS);
        }
        else {
            HistoricalRecord rec = initialData.get(initialData.size() - 1);
            start = rec.getTime().plusSeconds(1);
        }
        initialData.addAll(historyTable.queryRecordsForTimePeriod(start, now));
        return initialData;
    }
}