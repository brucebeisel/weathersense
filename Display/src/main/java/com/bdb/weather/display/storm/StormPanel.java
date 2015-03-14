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
package com.bdb.weather.display.storm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.bdb.util.BBTable;
import com.bdb.util.Pair;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Storm;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.StormDopplerRadarTable;
import com.bdb.weather.common.db.StormTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.RainPlot;
import com.bdb.weather.display.RainPlot.RainEntry;

/**
 *
 * @author Bruce
 */
public class StormPanel implements ComponentContainer {
    private static final int ANIMATION_INTERVAL = 100;
    private final JPanel panel = new JPanel();
    private final JLabel animationStatus = new JLabel();
    private final JLabel imageLabel = new JLabel("No Doppler Image Yet");
    private final RainPlot rainPlot;
    private final StormTable stormTable;
    private final HistoryTable historyTable;
    private final StormDopplerRadarTable radarTable;
    private final BBTable table;
    private List<Storm> storms;
    private TreeMap<LocalDateTime,Pair<DopplerRadarImage,ImageIcon>> images;
    private List<RainEntry> entries;
    private final Timer animationTimer;
    private int currentFrame;
    private Depth totalRain;
    private final DefaultTableModel model = new DefaultTableModel();
     private static final String COLUMN_HEADINGS[] = {
        "Start Date", "Start Time", "Stop Date", "Stop Time", "Rainfall"
    };
    
    public StormPanel(DBConnection connection) {
        animationTimer = new Timer(ANIMATION_INTERVAL, (ActionEvent) -> animate());
        panel.setLayout(new BorderLayout());
        stormTable = new StormTable(connection);
        historyTable = new HistoryTable(connection);
        radarTable = new StormDopplerRadarTable(connection);
        table = new BBTable();
        panel.add(new JScrollPane(table), BorderLayout.WEST);
        table.setModel(model);
        model.setColumnIdentifiers(COLUMN_HEADINGS);

        JPanel animationPanel = new JPanel(new BorderLayout());
        JPanel animationControlPanel = new JPanel();
        JButton button = new JButton("Load");
        animationControlPanel.add(button);
        button.addActionListener((ActionEvent) -> loadStorm());

        button = new JButton("Play");
        animationControlPanel.add(button);
        button.addActionListener((ActionEvent) -> startAnimation());

        button = new JButton("Stop");
        animationControlPanel.add(button);
        button.addActionListener((ActionEvent) -> stopAnimation());

        animationStatus.setText("Animation not running");
        animationControlPanel.add(animationStatus);
        JPanel p = new JPanel(new GridLayout(2,1));
        rainPlot = new RainPlot();
        p.add(imageLabel);
        p.add(rainPlot.getComponent());
        animationPanel.add(animationControlPanel, BorderLayout.NORTH);
        animationPanel.add(p, BorderLayout.CENTER);
        panel.add(animationPanel, BorderLayout.CENTER);

        loadStormData();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    private void loadStormData() {
        animationStatus.setText("Animation not running");
        storms = stormTable.query();
        model.setRowCount(storms.size());
        int row = 0;
        for (Storm storm : storms) {
            model.setValueAt(DisplayConstants.formatDate(storm.getStartTime().toLocalDate()), row, 0);
            model.setValueAt(DisplayConstants.formatTime(storm.getStartTime().toLocalTime()), row, 1);
            if (storm.isStormActive()) {
                model.setValueAt("Active", row, 2);
                model.setValueAt("Active", row, 3);
            }
            else {
                model.setValueAt(DisplayConstants.formatDate(storm.getEndTime().toLocalDate()), row, 2);
                model.setValueAt(DisplayConstants.formatTime(storm.getEndTime().toLocalTime()), row, 3);
            }

            model.setValueAt(storm.getStormRainfall(), row++, 4);
        }

        if (!storms.isEmpty())
            table.getSelectionModel().addSelectionInterval(0, 0);

        currentFrame = 0;
    }

    private void loadStorm() {
        int row = table.getSelectedRow();
        Storm storm = storms.get(row);
        images = new TreeMap<>();
        entries = new ArrayList<>();
        List<DopplerRadarImage> dopplerList = radarTable.getRadarImagesForStorm(storm.getStartTime());
        dopplerList.forEach((DopplerRadarImage dri) -> images.put(dri.getTime(), new Pair<>(dri, new ImageIcon(dri.getImage()))));

        if (!images.isEmpty()) {
            LocalDateTime endTime = storm.getEndTime();

            if (endTime == null)
                endTime = images.lastKey();

            imageLabel.setIcon(images.firstEntry().getValue().second);
            imageLabel.setText(null);

            List<HistoricalRecord> records = historyTable.queryRecordsForTimePeriod(storm.getStartTime(), endTime);
            boolean foundFirstRain = false;
            //
            // Trim the records after midnight that have no rain
            //
            for (HistoricalRecord rec : records) {
                if (rec.getRainfall().get() > 0.0)
                    foundFirstRain = true;

                if (foundFirstRain)
                    entries.add(new RainEntry(rec.getTime(), rec.getRainfall(), rec.getHighRainfallRate()));
            }

            rainPlot.setRainData(entries);
        }
        panel.invalidate();
    }

    private void startAnimation() {
        if (!entries.isEmpty())
            totalRain = entries.get(0).rainfall;
        animationTimer.start();
    }

    private void stopAnimation() {
        animationTimer.stop();
    }

    private void animate() {
        currentFrame++;
        if (currentFrame >= entries.size()) {
            currentFrame = 0;
            totalRain = new Depth(0.0);
        }
        else {
            RainEntry rain = entries.get(currentFrame);
            totalRain = totalRain.add(rain.rainfall);

            Map.Entry<LocalDateTime,Pair<DopplerRadarImage,ImageIcon>> entry = images.ceilingEntry(rain.time);
            if (entry != null)
                imageLabel.setIcon(entry.getValue().second);

            rainPlot.addMarker(rain.time);

            animationStatus.setText("Image " + (currentFrame + 1) + " of " + entries.size() +
                                    " Rate: " + rain.rainfallRate +
                                    " Total: " + totalRain +
                                    " at " + rain.time);
        }
    }
}