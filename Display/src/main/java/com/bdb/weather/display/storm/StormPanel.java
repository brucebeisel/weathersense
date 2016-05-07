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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javafx.util.Duration;

import com.bdb.util.Pair;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Storm;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.StormDopplerRadarTable;
import com.bdb.weather.common.db.StormTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.RainPlot;
import com.bdb.weather.display.RainPlot.RainEntry;
import com.bdb.weather.display.StageUtilities;

/**
 *
 * @author Bruce
 */
public class StormPanel extends BorderPane {
    private static final int ANIMATION_INTERVAL = 100;
    private final StormTable stormTable;
    private final HistoryTable historyTable;
    private final StormDopplerRadarTable radarTable;
    @FXML  private TableView<Storm> table;
    @FXML private Label animationStatus = new Label();
    @FXML private Label dopplerRadarImage = new Label("No Doppler Image Yet");
    @FXML private RainPlot rainPlot;
    private List<Storm> storms;
    private TreeMap<LocalDateTime,Pair<DopplerRadarImage,Image>> images;
    private List<RainEntry> entries;
    private final Timeline animationTimer;
    private int currentFrame;
    private Depth totalRain;
    private static final String COLUMN_HEADINGS[] = {
        "Start Date", "Start Time", "Stop Date", "Stop Time", "Rainfall"
    };
    
    public StormPanel(DBConnection connection) {
        animationTimer = new Timeline(new KeyFrame(Duration.millis(ANIMATION_INTERVAL), (ActionEvent) -> animate()));
        stormTable = new StormTable(connection);
        historyTable = new HistoryTable(connection);
        radarTable = new StormDopplerRadarTable(connection);


        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StormPane.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Storm,String> column = new TableColumn<>("Start Date");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatDate(rec.getValue().getStartTime().toLocalDate())));
        table.getColumns().add(column);

        column = new TableColumn<>("Start Time");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatTime(rec.getValue().getStartTime().toLocalTime())));
        table.getColumns().add(column);

        column = new TableColumn<>("End Date");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(rec.getValue().isStormActive() ? "Active" : DisplayConstants.formatDate(rec.getValue().getEndTime().toLocalDate())));
        table.getColumns().add(column);

        column = new TableColumn<>("End Time");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(rec.getValue().isStormActive() ? "Active" : DisplayConstants.formatTime(rec.getValue().getEndTime().toLocalTime())));
        table.getColumns().add(column);

        column = new TableColumn<>("Rainfall");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(rec.getValue().getStormRainfall().toString()));
        table.getColumns().add(column);

        Platform.runLater(() -> loadStormData());
    }

    private void loadStormData() {
        animationStatus.setText("Animation not running");
        storms = stormTable.query();
	ObservableList<Storm> dataModel = FXCollections.observableList(storms);
        table.setItems(dataModel);
        int row = 0;

        if (!storms.isEmpty())
            table.getSelectionModel().clearSelection();

        currentFrame = 0;
    }

    @FXML
    private void loadStorm() {
        int row = table.getSelectionModel().getSelectedIndex();
        Storm storm = storms.get(row);
        images = new TreeMap<>();
        entries = new ArrayList<>();
        List<DopplerRadarImage> dopplerList = radarTable.getRadarImagesForStorm(storm.getStartTime());
        dopplerList.forEach((DopplerRadarImage dri) -> images.put(dri.getTime(), new Pair<>(dri, SwingFXUtils.toFXImage(dri.getImage(), null))));

        if (!images.isEmpty()) {
            LocalDateTime endTime = storm.getEndTime();

            if (endTime == null)
                endTime = images.lastKey();

            dopplerRadarImage.setGraphic(new ImageView(images.firstEntry().getValue().second));
            dopplerRadarImage.setText(null);

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

        StageUtilities.sizeStageToScene(this);
    }

    @FXML
    private void startAnimation() {
        animationTimer.setCycleCount(entries.size());
        if (!entries.isEmpty())
            totalRain = entries.get(0).rainfall;
        currentFrame = 0;
        animationTimer.playFromStart();
    }

    @FXML
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

            Map.Entry<LocalDateTime,Pair<DopplerRadarImage,Image>> entry = images.ceilingEntry(rain.time);
            if (entry != null)
                dopplerRadarImage.setGraphic(new ImageView(entry.getValue().second));

            rainPlot.addMarker(rain.time);

            animationStatus.setText("Image " + (currentFrame + 1) + " of " + entries.size() +
                                    " Rate: " + rain.rainfallRate +
                                    " Total: " + totalRain +
                                    " at " + rain.time);
        }
    }
}