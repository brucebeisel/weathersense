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
import java.awt.Component;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.YearWeatherAverages;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DateInterval;
import com.bdb.weather.display.ViewLauncher;

/**
 *
 * @author Bruce
 */
public class YearlySummariesPanel implements ComponentContainer, SummarySupporter {
    private final JComponent             component = new JPanel(new BorderLayout());
    private final SummariesGraphPanel    graphPanel;
    private final DailySummaryTable      dailySummaryTable;
    private final TemperatureBinMgr      temperatureBinMgr;
    private final WeatherStation         ws;
    private final DateRange              dataRange;
    private final DateTimeFormatter      dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    @SuppressWarnings("LeakingThisInConstructor")
    public YearlySummariesPanel(WeatherStation ws, DBConnection connection, ViewLauncher launcher) {
        this.ws = ws;
        dailySummaryTable = new DailySummaryTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);
        HistoryTable historyTable = new HistoryTable(connection); 
        dataRange = historyTable.dataRange();

        graphPanel = new SummariesGraphPanel(SummaryInterval.YEAR_INTERVAL, connection, launcher, this);

        component.add(graphPanel.getComponent(), BorderLayout.CENTER);

        /*
        JPanel cmdPanel = new JPanel();

        cmdPanel.add(intervalsCB);
        cmdPanel.add(new JLabel("Start:"));
        cmdPanel.add(startDateTF);
        cmdPanel.add(new JLabel("End:"));
        cmdPanel.add(endDateTF);
        cmdPanel.add(goButton);

        component.add(cmdPanel, BorderLayout.NORTH);

        startDate = (Calendar)start.clone();
        endDate = (Calendar)end.clone();
        TimeUtils.setToMidnight(startDate);
        TimeUtils.setToMidnight(endDate);

        startDateTF.setText(dateFormat.format(startDate.getTime()));
        endDateTF.setText(dateFormat.format(endDate.getTime()));
        
        if (interval != DateInterval.CUSTOM) {
            startDateTF.setEditable(false);
            endDateTF.setEditable(false);
        }
        
        intervalsCB.setSelectedItem(interval);
        intervalsCB.setMaximumRowCount(intervalsCB.getItemCount());
        intervalsCB.addActionListener(this);
        
        goButton.addActionListener(this);
        goButton.setEnabled(false);
        */

        loadData(dataRange.getStart().toLocalDate(), dataRange.getEnd().toLocalDate());
    }
    
    @Override
    public JComponent getComponent() {
        return component;
    }

    @SuppressWarnings("empty-statement")
    private Component getParentFrame() {
        Component c;

        for (c = component.getParent();
             c != null && !(c instanceof JFrame) && !(c instanceof JInternalFrame);
             c = c.getParent());
        
        return c;
    }
    
    public void setWindowTitle() {
        Component c = getParentFrame();

        if (c != null) {
            String dateString = dateFormat.format(dataRange.getStart()) + " - " + dateFormat.format(dataRange.getEnd());

            if (c instanceof JFrame)
                ((JFrame)c).setTitle(dateString);
            else 
                ((JInternalFrame)c).setTitle(dateString);
        }
    }

    private void loadData(LocalDate startDate, LocalDate endDate) {
        List<SpeedBin> speedBins = ws.getWindParameters().getSpeedBinList();
        temperatureBinMgr.refresh();
        List<SummaryRecord> summaryRecords = retrieveRange(ws, startDate, endDate, temperatureBinMgr);
        
        if (summaryRecords.isEmpty())
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(getParentFrame(), "No data available for date range", "No Data", JOptionPane.INFORMATION_MESSAGE);
            });
            // TODO put combobox back to original value, probably the same for the text fields when in custom mode.
        else {
            WeatherAverage avg = new WeatherAverage(new Temperature(77.5, Temperature.Unit.FAHRENHEIT),
                                                    new Temperature(50.7, Temperature.Unit.FAHRENHEIT),
                                                    new Temperature(64, Temperature.Unit.FAHRENHEIT),
                                                    new Depth(13.5, Depth.Unit.INCHES));
            WeatherAverages yearlyAverages = new YearWeatherAverages(avg);

            Statistics totalSummary = new Statistics(ws, yearlyAverages, speedBins, temperatureBinMgr);

            summaryRecords.stream().forEach((rec) -> {
                totalSummary.applySummaryRecord(rec);
            });

            graphPanel.loadData(summaryRecords, yearlyAverages);

            setWindowTitle();
        }
    }
    
    /*
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == intervalsCB) {
            DateInterval interval = (DateInterval)intervalsCB.getSelectedItem();

            interval.range(startDate, endDate);

            boolean editable = interval == DateInterval.CUSTOM;

            startDateTF.setEditable(editable);
            endDateTF.setEditable(editable);
            goButton.setEnabled(editable);
            startDateTF.setValue(startDate.getTime());
            endDateTF.setText(dateFormat.format(endDate.getTime()));
            
            if (!editable)
                loadData(startDate, endDate);
        }
        else if (source == goButton) {
            try {
                startDate.setTime(dateFormat.parse(startDateTF.getText()));
                endDate.setTime(dateFormat.parse(endDateTF.getText()));
                loadData(startDate, endDate);
            }
            catch (ParseException e) {
                // TODO Add dialog that indicates a bad date was entered.
            }
        }
    }
    */

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
        DateInterval interval = DateInterval.CUSTOM;
        launcher.launchMonthlySummariesView(interval);
    }

    private List<SummaryRecord> retrieveRange(WeatherStation ws, LocalDate start, LocalDate end, TemperatureBinMgr mgr) {
        List<SummaryRecord> yearlyList = new ArrayList<>();
        
        start = LocalDate.of(start.getYear(), Month.JANUARY, 1);

        end = LocalDate.of(end.getYear(), Month.DECEMBER, Month.DECEMBER.maxLength());
        
        while (start.isBefore(end)) {
            LocalDate yearEnd = LocalDate.of(start.getYear(), Month.DECEMBER, Month.DECEMBER.maxLength());
            List<SummaryRecord> dailyList = dailySummaryTable.retrieveRange(start, yearEnd, ws.getWindParameters(), mgr);
            if (dailyList.size() > 0) {
                SummaryRecord yearlySummary = new SummaryRecord(ws.getWindParameters(), mgr, start);
                
                dailyList.stream().forEach((record) -> {
                    yearlySummary.applySummaryRecord(record);
                });
                
                yearlyList.add(yearlySummary);
            }
            start = start.plusYears(1);
        }   
        
        return yearlyList;
    }
}
