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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.scene.Node;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.SummaryRecord;

import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.MonthlyAveragesTable;
import com.bdb.weather.common.db.MonthlySummaryTable;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DateInterval;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.WeatherSense;

public class MonthlySummariesPanel implements ComponentContainer, ActionListener, SummarySupporter {
    private final JComponent             component = new JPanel(new BorderLayout());
    private final SummariesGraphPanel    graphPanel;
    private final MonthlySummaryTable    monthlySummaryTable;
    private final MonthlyAveragesTable   monthlyAveragesTable;
    private final TemperatureBinMgr      temperatureBinMgr;
    private final WeatherStation         ws;
    private LocalDate                    startDate;
    private LocalDate                    endDate;
    private final DateTimeFormatter      dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private final JFormattedTextField    startDateTF = new JFormattedTextField(dateFormat);
    private final JFormattedTextField    endDateTF = new JFormattedTextField(dateFormat);
    private final JComboBox<DateInterval> intervalsCB = new JComboBox<>(DateInterval.availableIntervals());
    private final JButton                goButton = new JButton("Go");

    @SuppressWarnings("LeakingThisInConstructor")
    public MonthlySummariesPanel(WeatherStation ws, DBConnection connection, ViewLauncher launcher, LocalDate start, LocalDate end, DateInterval interval) {
        this.ws = ws;
        monthlySummaryTable = new MonthlySummaryTable(connection);
        monthlyAveragesTable = new MonthlyAveragesTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);

        graphPanel = new SummariesGraphPanel(SummaryInterval.MONTH_INTERVAL, connection, launcher, this);

        component.add(graphPanel.getComponent(), BorderLayout.CENTER);

        JPanel cmdPanel = new JPanel();

        cmdPanel.add(intervalsCB);
        cmdPanel.add(new JLabel("Start:"));
        cmdPanel.add(startDateTF);
        cmdPanel.add(new JLabel("End:"));
        cmdPanel.add(endDateTF);
        cmdPanel.add(goButton);

        component.add(cmdPanel, BorderLayout.NORTH);

        startDate = start;
        endDate = end;

        startDateTF.setText(dateFormat.format(startDate));
        endDateTF.setText(dateFormat.format(endDate));
        
        if (interval != DateInterval.CUSTOM) {
            startDateTF.setEditable(false);
            endDateTF.setEditable(false);
        }
        
        intervalsCB.setSelectedItem(interval);
        intervalsCB.setMaximumRowCount(intervalsCB.getItemCount());
        intervalsCB.addActionListener(this);
        
        goButton.addActionListener(this);
        goButton.setEnabled(false);

        loadData(startDate, endDate);
    }
    
    @Override
    public Node getComponent() {
        return null;
    }

    public void setWindowTitle() {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        String dateString = df.format(startDate) + " - " + df.format(endDate);
        //WeatherSense.setFrameTitle(component, dateString);
    }

    private void loadData(LocalDate startDate, LocalDate endDate) {
        temperatureBinMgr.refresh();
        List<SpeedBin> speedBins = ws.getWindParameters().getSpeedBinList();
        List<SummaryRecord> summaryRecords = monthlySummaryTable.retrieveRange(ws, startDate, endDate, temperatureBinMgr);
        
        if (summaryRecords.isEmpty())
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "No data available for date range", "No Data", JOptionPane.INFORMATION_MESSAGE);
        });
            // TODO put combobox back to original value, probably the same for the text fields when in custom mode.
        else {
            WeatherAverages dailyAverages = monthlyAveragesTable.retrieveMonthlyAveragesForLocation(ws.getLocationCode());

            if (dailyAverages != null) {
                Statistics totalSummary = new Statistics(ws, dailyAverages, speedBins, temperatureBinMgr);

                summaryRecords.stream().forEach((rec) -> {
                    totalSummary.applySummaryRecord(rec);
                });

                graphPanel.loadData(summaryRecords, dailyAverages);

                setWindowTitle();
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == intervalsCB) {
            DateInterval interval = (DateInterval)intervalsCB.getSelectedItem();

            DateRange range = interval.range();

            boolean editable = interval == DateInterval.CUSTOM;

            startDateTF.setEditable(editable);
            endDateTF.setEditable(editable);
            goButton.setEnabled(editable);
            startDateTF.setValue(dateFormat.format(range.getStart()));
            endDateTF.setText(dateFormat.format(range.getEnd()));
            
            if (!editable)
                loadData(range.getStart().toLocalDate(), range.getEnd().toLocalDate());
        }
        else if (source == goButton) {
            try {
                startDate = LocalDate.from(dateFormat.parse(startDateTF.getText()));
                endDate = LocalDate.from(dateFormat.parse(endDateTF.getText()));
                loadData(startDate, endDate);
            }
            catch (DateTimeParseException e) {
                // TODO Add dialog that indicates a bad date was entered.
            }
        }
    }

    @Override
    public Temperature retrieveAvgOutdoorTemperature(SummaryRecord rec) {
        return rec.getAvgMeanOutdoorTemp();
    }

    @Override
    public Temperature retrieveHighOutdoorTemperature(SummaryRecord rec) {
        return rec.getAvgHighOutdoorTemp();
    }

    @Override
    public Temperature retrieveLowOutdoorTemperature(SummaryRecord rec) {
        return rec.getAvgLowOutdoorTemp();
    }

    @Override
    public void launchView(ViewLauncher launcher, LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        launcher.launchDailySummariesView(start, end);
    }
}
