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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bdb.weather.common.DateRange;

public class DataRangeSelectionPanel implements ActionListener {
    private static final int DATE_FIELD_WIDTH = 15;
    private final JComponent              component = new JPanel(new GridLayout(0,1));
    private LocalDate                     startDate;
    private LocalDate                     endDate;
    private final DateTimeFormatter       dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private final JTextField              startDateTF = new JTextField(DATE_FIELD_WIDTH);
    private final JTextField              endDateTF = new JTextField(DATE_FIELD_WIDTH);
    private final JComboBox<DateInterval> intervalsCB = new JComboBox<>(DateInterval.availableIntervals());
    private final JCheckBox               compareCB = new JCheckBox("Compare");
    private final JComboBox<DateDelta>    offsetCB = new JComboBox<>(DateDelta.values());
    private final JTextField              compareStartDateTF = new JTextField(DATE_FIELD_WIDTH);
    private DateRange                     dataRange;
    private final List<LocalDate>         comparisonStartDates = new ArrayList<>();
    private static final Logger           logger = Logger.getLogger(DataRangeSelectionPanel.class.getName());
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DataRangeSelectionPanel(LocalDate firstData, LocalDate lastData) {
        JPanel p = new JPanel();
        startDateTF.setColumns(15);
        endDateTF.setColumns(15);
        compareStartDateTF.setColumns(10);
        intervalsCB.setMaximumRowCount(DateInterval.values().length);
        p.add(intervalsCB);
        p.add(new JLabel("Start:"));
        p.add(startDateTF);
        p.add(new JLabel("End:"));
        p.add(endDateTF);
        component.add(p);
        
        p = new JPanel();
        p.add(compareCB);
        p.add(offsetCB);
        p.add(compareStartDateTF);
        
        component.add(p);
        
        
        DateRange range = DateInterval.TODAY.range();
        startDate = range.getStart().toLocalDate();
        endDate = range.getEnd().toLocalDate();
        loadDates();
        
        startDateTF.setEditable(false);
        endDateTF.setEditable(false);
        intervalsCB.setSelectedItem(DateInterval.TODAY);
        intervalsCB.addActionListener(this);
        offsetCB.setEnabled(false);
        offsetCB.addActionListener(this);
        compareCB.addActionListener(this);
        compareStartDateTF.setEditable(false);
 
        dataRange = new DateRange(firstData.atStartOfDay(), lastData.atStartOfDay());
    }
    
    public JComponent getComponent() {
        return component;
    }
    
    public void setLastDataTime(LocalDateTime lastData) {
        dataRange = new DateRange(dataRange.getStart(), lastData);
    }
    
    public List<DateRange> getRanges() {
        final int OFFSET_INCREMENT = 2000000000;
        List<DateRange> list = new ArrayList<>();
        LocalDate locaStartDate = getStartDate();
        LocalDate locaEndDate = getEndDate();
        list.add(new DateRange(locaStartDate.atStartOfDay(), locaEndDate.atStartOfDay()));
        
        if (compareCB.isSelected()) {
            LocalDate start = dateFromTextField(compareStartDateTF);
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
        startDateTF.setText(dateFormat.format(startDate));
        endDateTF.setText(dateFormat.format(endDate));
        
        DateDelta delta = (DateDelta)offsetCB.getSelectedItem();
        if (delta != DateDelta.CUSTOM) {
            LocalDate start = delta.applyDelta(getStartDate());
            compareStartDateTF.setText(dateFormat.format(start));
        }
    }
    
    private LocalDate dateFromTextField(JTextField tf) {
        try {
            String dateString = tf.getText();
            LocalDate date = LocalDate.from(dateFormat.parse(dateString));
            return date;
        }
        catch (DateTimeParseException e) {
            ErrorDisplayer.getInstance().displayError("Invalid date entered, using today as default");
            logger.log(Level.WARNING, "Invalid date entered", e);
            return LocalDate.now();
        }
    }
    
    public LocalDate getStartDate() {
        LocalDate date = dateFromTextField(startDateTF);
        return date;
    }

    public LocalDate getEndDate() {
        LocalDate date = dateFromTextField(endDateTF);
        return date;
    }

    public List<LocalDate> getComparisonStartDates() {
        return Collections.unmodifiableList(comparisonStartDates);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == intervalsCB) {
            DateInterval interval = (DateInterval)intervalsCB.getSelectedItem();
            DateRange range = interval.range();
            startDate = range.getStart().toLocalDate();
            endDate = range.getEnd().toLocalDate();

            loadDates();

            boolean editable = interval == DateInterval.CUSTOM;

            startDateTF.setEditable(editable);
            endDateTF.setEditable(editable);
        }
        else if (event.getSource() == compareCB) {
            offsetCB.setEnabled(compareCB.isSelected());
        }
        else if (event.getSource() == offsetCB) {
            DateDelta delta = (DateDelta)offsetCB.getSelectedItem();
            boolean editable = delta == DateDelta.CUSTOM;
            compareStartDateTF.setEditable(editable);
            loadDates();
        }
    }
}
