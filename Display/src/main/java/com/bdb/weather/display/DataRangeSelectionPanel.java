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
package com.bdb.weather.display;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;

import com.bdb.weather.common.DateRange;

public class DataRangeSelectionPanel extends TilePane implements EventHandler<ActionEvent> {
    private LocalDate                     startDate;
    private LocalDate                     endDate;
    private final DatePicker              startDateTF = new DatePicker();
    private final DatePicker              endDateTF = new DatePicker();
    private final ComboBox<DateInterval>  intervalsCB = new ComboBox<>();
    private final CheckBox                compareCB = new CheckBox("Compare");
    private final ComboBox<DateDelta>     offsetCB = new ComboBox<>();
    private final DatePicker              compareStartDateTF = new DatePicker();
    private DateRange                     dataRange;
    private final List<LocalDate>         comparisonStartDates = new ArrayList<>();
    private static final Logger           logger = Logger.getLogger(DataRangeSelectionPanel.class.getName());
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DataRangeSelectionPanel(LocalDate firstData, LocalDate lastData) {
        this.setPrefRows(0);
        this.setPrefColumns(1);

        intervalsCB.getItems().addAll(Arrays.asList(DateInterval.values()));
        offsetCB.getItems().addAll(Arrays.asList(DateDelta.values()));
        offsetCB.getSelectionModel().selectFirst();

        FlowPane p = new FlowPane();
        //compareStartDateTF.setColumns(10);
        //intervalsCB.setMaximumRowCount(DateInterval.values().length);
        p.getChildren().add(intervalsCB);
        p.getChildren().add(new Label("Start:"));
        p.getChildren().add(startDateTF);
        p.getChildren().add(new Label("End:"));
        p.getChildren().add(endDateTF);
        this.getChildren().add(p);
        
        p = new FlowPane();
        p.getChildren().add(compareCB);
        p.getChildren().add(offsetCB);
        p.getChildren().add(compareStartDateTF);
        
        this.getChildren().add(p);
        
        DateRange range = DateInterval.TODAY.range();
        startDate = range.getStart().toLocalDate();
        endDate = range.getEnd().toLocalDate();
        loadDates();
        
        startDateTF.setEditable(false);
        endDateTF.setEditable(false);
        intervalsCB.getSelectionModel().select(DateInterval.TODAY);
        intervalsCB.setOnAction(this);
        offsetCB.setDisable(true);
        offsetCB.setOnAction(this);
        compareCB.setOnAction(this);
        compareStartDateTF.setEditable(false);
 
        dataRange = new DateRange(firstData.atStartOfDay(), lastData.atStartOfDay());
    }
    
    public void setLastDataTime(LocalDateTime lastData) {
        dataRange = new DateRange(dataRange.getStart(), lastData);
    }
    
    public List<DateRange> getRanges() {
        final int OFFSET_INCREMENT = 2000000000;
        List<DateRange> list = new ArrayList<>();
        LocalDate locaStartDate = startDateTF.getValue();
        LocalDate locaEndDate = endDateTF.getValue();
        list.add(new DateRange(locaStartDate.atStartOfDay(), locaEndDate.atStartOfDay()));
        
        if (compareCB.isSelected()) {
            LocalDate start = compareStartDateTF.getValue();
            LocalDate end = start;
            Duration delta = Duration.between(start, end);

            for (int i = 0; i < delta.getSeconds() / OFFSET_INCREMENT; i++)
                end.plus(OFFSET_INCREMENT, ChronoUnit.MILLIS);
            
            end.plus(delta.getSeconds() % OFFSET_INCREMENT, ChronoUnit.MILLIS);
            list.add(new DateRange(start.atStartOfDay(), end.atStartOfDay()));
        }

        return list;
    }
    
    private void loadDates() {
        startDateTF.setValue(startDate);
        endDateTF.setValue(endDate);
        
        DateDelta delta = offsetCB.getSelectionModel().getSelectedItem();
        if (delta != DateDelta.CUSTOM) {
            LocalDate start = delta.applyDelta(startDateTF.getValue());
            compareStartDateTF.setValue(start);
        }
    }
    
    public List<LocalDate> getComparisonStartDates() {
        return Collections.unmodifiableList(comparisonStartDates);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == intervalsCB) {
            DateInterval interval = (DateInterval)intervalsCB.getSelectionModel().getSelectedItem();
            DateRange range = interval.range();
            startDate = range.getStart().toLocalDate();
            endDate = range.getEnd().toLocalDate();

            loadDates();

            boolean editable = interval == DateInterval.CUSTOM;

            startDateTF.setEditable(editable);
            endDateTF.setEditable(editable);
        }
        else if (event.getSource() == compareCB) {
            offsetCB.setDisable(!compareCB.isSelected());
        }
        else if (event.getSource() == offsetCB) {
            DateDelta delta = (DateDelta)offsetCB.getSelectionModel().getSelectedItem();
            boolean editable = delta == DateDelta.CUSTOM;
            compareStartDateTF.setEditable(editable);
            loadDates();
        }
    }
}