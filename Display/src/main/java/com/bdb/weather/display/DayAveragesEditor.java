/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.display;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.DayWeatherAverages;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.Location;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.db.DailyAveragesTable;


/**
 * A class that manages the dialogs for editing a locations parameters.
 * 
 * @author Bruce
 *
 */
public final class DayAveragesEditor extends BorderPane implements EventHandler<ActionEvent> {
    private static final int YEAR_THAT_IS_NOT_A_LEAP_YEAR = 2001;
    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "CANCEL";
    private static final String IMPORT_COMMAND = "IMPORT";
    private static final String EXPORT_COMMAND = "EXPORT";
    private final String                    locationName;
    private final TableView<WeatherAverage> averagesTable = new TableView<>();
    private final DailyAveragesTable        dailyAveragesDbTable;
    private DayWeatherAverages              averages;
    private final ExtensionFilter           fileFilter = new ExtensionFilter("CSV", "csv");
    private static final String COLUMN_NAMES[] = {
        "Date", "Avg Low", "Avg Mean", "Avg High", "Rainfall"
    };
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DayAveragesEditor(DBConnection connection, String locationName) {
        this.locationName = locationName;
        dailyAveragesDbTable = new DailyAveragesTable(connection);
        
        FlowPane buttonPanel = new FlowPane();

        //buttonPanel.setBorder(new EtchedBorder());
        Button button = new Button("OK");
        //button.setActionCommand(OK_COMMAND);
        button.setOnAction(this);
        buttonPanel.getChildren().add(button);
        
        button = new Button("Cancel");
        //button.setActionCommand(CANCEL_COMMAND);
        button.setOnAction(this);
        buttonPanel.getChildren().add(button);
        
        setBottom(buttonPanel);
        
        BorderPane attributePanel = new BorderPane();
        //attributePanel.setBorder(new EtchedBorder());
        
        BorderPane p = new BorderPane();
        averagesTable.setEditable(true);
        p.setCenter(averagesTable);
        
        buttonPanel = new FlowPane();
        
        button = new Button("Import...");
        //button.setActionCommand(IMPORT_COMMAND);
        button.setOnAction(this);
        buttonPanel.getChildren().add(button);
        
        button = new Button("Export...");
        //button.setActionCommand(EXPORT_COMMAND);
        button.setOnAction(this);
        buttonPanel.getChildren().add(button);
        p.setBottom(buttonPanel);
        
        attributePanel.setCenter(p);
 
        setCenter(attributePanel);

        createColumns();

        loadData();
        Platform.runLater(() -> StageUtilities.setStageTitle(this, "Edit Location - " + locationName));
    }

    private void createColumns() {
        TableColumn<WeatherAverage,String> column = new TableColumn<>("Date");
        column.setCellValueFactory((rec)-> new ReadOnlyStringWrapper(String.format("%d/%d", rec.getValue().getMonth().getValue(), rec.getValue().getDay())));
        column.setEditable(false);
        averagesTable.getColumns().add(column);

        column = new TableColumn<>("Avg Low");
        column.setCellValueFactory((rec)-> new ReadOnlyStringWrapper(rec.getValue().getLowTemperature().toString()));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit((t) -> ((WeatherAverage) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLowTemperature(new Temperature(Double.parseDouble(t.getNewValue()))));
        averagesTable.getColumns().add(column);

        column = new TableColumn<>("Avg Mean");
        column.setCellValueFactory((rec)-> new ReadOnlyStringWrapper(rec.getValue().getMeanTemperature().toString()));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit((t) -> ((WeatherAverage) t.getTableView().getItems().get(t.getTablePosition().getRow())).setMeanTemperature(new Temperature(Double.parseDouble(t.getNewValue()))));
        averagesTable.getColumns().add(column);

        column = new TableColumn<>("Avg High");
        column.setCellValueFactory((rec)-> new ReadOnlyStringWrapper(rec.getValue().getHighTemperature().toString()));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit((t) -> ((WeatherAverage) t.getTableView().getItems().get(t.getTablePosition().getRow())).setHighTemperature(new Temperature(Double.parseDouble(t.getNewValue()))));
        averagesTable.getColumns().add(column);

        column = new TableColumn<>("Rainfall");
        column.setCellValueFactory((rec)-> new ReadOnlyStringWrapper(rec.getValue().getRainfall().toString()));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit((t) -> ((WeatherAverage) t.getTableView().getItems().get(t.getTablePosition().getRow())).setRainfall(new Depth(Double.parseDouble(t.getNewValue()))));
        averagesTable.getColumns().add(column);
    }
    
    private void loadData() {
        averages = dailyAveragesDbTable.retrieveDailyAverages();
        if (averages == null)
            averages = new DayWeatherAverages();

	ObservableList<WeatherAverage> dataModel = FXCollections.observableList(averages.getAllAverages());
        averagesTable.setItems(dataModel);

    }

    @Override
    public void handle(ActionEvent event) {
        String command = event.toString();
        switch (command) {
            case EXPORT_COMMAND: {
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(fileFilter);
                //chooser.setApproveButtonText("Export");
                File file = chooser.showOpenDialog(this.getScene().getWindow());
                if (file != null) {
                    try {
                        Location.exportCSVFile(file, locationName, averages);
                        ErrorDisplayer.getInstance().displayInformation("Averages successfully exported");
                    }
                    catch (FileNotFoundException e) {
                        ErrorDisplayer.getInstance().displayError("Error saving CSV file");
                    }
                }
                break;
            }
            case IMPORT_COMMAND: {
                FileChooser chooser = new FileChooser();
                //chooser.addChoosableFileFilter(fileFilter);
                //chooser.setApproveButtonText("Import");
                File file = chooser.showOpenDialog(this.getScene().getWindow());
                if (file != null) {
                    try {
                        Location.importCSVFile(file, locationName, averages);
                        //((AbstractTableModel)averagesTable.getModel()).fireTableDataChanged();
                        ErrorDisplayer.getInstance().displayInformation("Averages successfully imported");
                    }
                    catch (IOException e) {
                        ErrorDisplayer.getInstance().displayError("Error importing CSV file");
                    }
                }
                break;
            }
            default:
                if (command.equals(OK_COMMAND)) {
                    if (!dailyAveragesDbTable.updateDailyAverages(averages))
                        ErrorDisplayer.getInstance().displayError("Error storing day averages");
                }
                setVisible(false);
                break;
        }
    }
}