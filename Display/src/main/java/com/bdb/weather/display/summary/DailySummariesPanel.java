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
package com.bdb.weather.display.summary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailyAveragesTable;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.TemperatureRecordTable;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.DateInterval;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.WeatherSense;

public final class DailySummariesPanel extends BorderPane implements EventHandler, SummarySupporter
{
    private final SummariesGraphPanel         graphPanel;
    private final DailySummariesTextPanel     textPanel;
    private final DailySummaryTable           dailySummaryTable;
    private final DailyAveragesTable          dailyAveragesTable;
    private final TemperatureRecordTable      temperatureRecordTable;
    private final TemperatureBinMgr           temperatureBinMgr;
    private final WeatherStation              ws;
    private LocalDate                         startDate;
    private LocalDate                         endDate;
    private final DateTimeFormatter           dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private final TextField                   startDateTF = new TextField();
    private final TextField                   endDateTF = new TextField();
    private final ComboBox                    intervalsCB = new ComboBox();
    private final Button                      goButton = new Button("Go");

    @SuppressWarnings("LeakingThisInConstructor")
    public DailySummariesPanel(WeatherStation ws, DBConnection connection, ViewLauncher launcher, LocalDate start, LocalDate end, DateInterval interval) {
        this.ws = ws;
        dailySummaryTable = new DailySummaryTable(connection);
        dailyAveragesTable = new DailyAveragesTable(connection);
        temperatureRecordTable = new TemperatureRecordTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);

        graphPanel = new SummariesGraphPanel(SummaryInterval.DAY_INTERVAL, connection, launcher, this);
        textPanel = new DailySummariesTextPanel();
        TabPane tabPane = new TabPane();
        Tab tab = new Tab("Charts");
        tab.setContent(graphPanel);
        tabPane.getTabs().add(tab);

        tab = new Tab("Statistics");
        tab.setContent(new ScrollPane(textPanel));
        tabPane.getTabs().add(tab);

        setCenter(tabPane);

        HBox cmdPanel = new HBox();

        cmdPanel.getChildren().add(intervalsCB);
        cmdPanel.getChildren().add(new Label("Start:"));
        cmdPanel.getChildren().add(startDateTF);
        cmdPanel.getChildren().add(new Label("End:"));
        cmdPanel.getChildren().add(endDateTF);
        cmdPanel.getChildren().add(goButton);

        setTop(cmdPanel);

        startDate = start;
        endDate = end;

        startDateTF.setText(dateFormat.format(startDate));
        endDateTF.setText(dateFormat.format(endDate));
        
        if (interval != DateInterval.CUSTOM) {
            startDateTF.setEditable(false);
            endDateTF.setEditable(false);
        }
        
        intervalsCB.getSelectionModel().select(interval);
       // intervalsCB.setMaximumRowCount(intervalsCB.getItemCount());
        intervalsCB.setOnAction(this);
        
        goButton.setOnAction((ae)-> {
            try {
                startDate = LocalDate.from(dateFormat.parse(startDateTF.getText()));
                endDate = LocalDate.from(dateFormat.parse(endDateTF.getText()));
                loadData(startDate, endDate);
            }
            catch (DateTimeParseException e) {
                // TODO Add dialog that indicates a bad date was entered.
            }
        });

        goButton.setDisable(true);

        loadData(startDate, endDate);
    }
    
    private void loadData(LocalDate startDate, LocalDate endDate) {
        temperatureBinMgr.refresh();
        List<SummaryRecord> summaryRecords = dailySummaryTable.retrieveRange(startDate, endDate, ws.getWindParameters(), temperatureBinMgr);
        
        WeatherAverage seasonalAverages = dailyAveragesTable.retrieveCustomAverages(startDate.getMonth(), startDate.getDayOfMonth(),
                                                                                    endDate.getMonth(), endDate.getDayOfMonth());
                                                
        if (summaryRecords.isEmpty())
            //SwingUtilities.invokeLater(() -> {
            //    JOptionPane.showMessageDialog(getParentFrame(), "No data available for date range", "No Data", JOptionPane.INFORMATION_MESSAGE);
            ;
            // TODO put combobox back to original value, probably the same for the text fields when in custom mode.
        else {
            WeatherAverages dailyAverages = dailyAveragesTable.retrieveDailyAverages();

            if (dailyAverages == null) {
                //SwingUtilities.invokeLater(() -> {
                //    JOptionPane.showMessageDialog(getParentFrame(), "No weather average data", "No Averages", JOptionPane.INFORMATION_MESSAGE);
                //});
                return;
            }

            Statistics totalSummary = new Statistics(ws, dailyAverages, ws.getWindParameters().getSpeedBinList(), temperatureBinMgr);

            summaryRecords.stream().forEach((rec) -> {
                totalSummary.applySummaryRecord(rec);
            });

            graphPanel.loadData(summaryRecords, dailyAverages);
            List<Extreme<Temperature,TemperatureRecordType>> records = temperatureRecordTable.retrieveRecordsForDateRange(startDate, endDate);
            textPanel.loadData(totalSummary, seasonalAverages, records);

            DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
            String dateString = df.format(startDate) + " - " + df.format(endDate);
            //WeatherSense.setStageTitle(this, dateString);
        }
    }

    @Override
    public Temperature retrieveAvgOutdoorTemperature(SummaryRecord rec) {
        return rec.getAvgOutdoorTemp();
    }

    @Override
    public Temperature retrieveHighOutdoorTemperature(SummaryRecord rec) {
        return rec.getMaxOutdoorTemp();
    }

    @Override
    public Temperature retrieveLowOutdoorTemperature(SummaryRecord rec) {
        return rec.getMinOutdoorTemp();
    }

    @Override
    public void launchView(ViewLauncher launcher, LocalDate date) {
        launcher.launchDaySummaryView(date);
        
    }

    @Override
    public void handle(Event event) {
        Object source = event.getSource();

        if (source == intervalsCB) {
            DateInterval interval = (DateInterval)intervalsCB.getSelectionModel().getSelectedItem();

            DateRange range = interval.range();

            boolean editable = interval == DateInterval.CUSTOM;

            startDateTF.setEditable(editable);
            endDateTF.setEditable(editable);
            goButton.setDisable(!editable);
            startDateTF.setText(dateFormat.format(range.getStart()));
            endDateTF.setText(dateFormat.format(range.getEnd()));
            
            if (!editable) {
                startDate = range.getStart().toLocalDate();
                endDate = range.getEnd().toLocalDate();
                loadData(startDate, endDate);
            }
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
