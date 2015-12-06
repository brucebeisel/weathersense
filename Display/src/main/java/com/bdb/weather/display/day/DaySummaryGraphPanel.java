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
package com.bdb.weather.display.day;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import javafx.scene.Node;

import com.bdb.util.DateButton;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailyAveragesTable;
import com.bdb.weather.common.db.DailyRecordsTable;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DisplayConstants;

/**
 * Panel that displays a day's worth of data and handles the panel for advancing through the days or
 * choosing a specific date.
 * 
 * @author Bruce
 * TODO This name is confusing with the other summary graphs. Should change the name.
 */
public class DaySummaryGraphPanel implements ComponentContainer, ActionListener, PropertyChangeListener {
    private static final String NEXT_CMD = "Next";
    private static final String PREV_CMD = "Prev";
    private final JComponent          component = new JPanel(new BorderLayout());
    private final DayGraphPanel       graphPanel;
    private final DailySummaryTable   dailySummaryTable;
    private final DailyAveragesTable  dailyAveragesTable;
    private final DailyRecordsTable   dailyRecordsTable;
    private final HistoryTable        historyTable;
    private final WeatherStation      ws;
    private LocalDate                 date;
    private final DateButton          dateButton = new DateButton();
    private final TemperatureBinMgr   temperatureBinMgr;

    /**
     * Constructor.
     * 
     * @param ws The weather station for which the data is being displayed
     * @param connection The database connection
     * @param day The day whose data is being displayed
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public DaySummaryGraphPanel(WeatherStation ws, DBConnection connection, LocalDate day) {
        this.ws = ws;
        this.date = day;
        dailySummaryTable = new DailySummaryTable(connection);
        historyTable = new HistoryTable(connection);
        dailyAveragesTable = new DailyAveragesTable(connection);
        dailyRecordsTable = new DailyRecordsTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);

        graphPanel = new DayGraphPanel(ws);
        
//        component.add(graphPanel.getComponent(), BorderLayout.CENTER);

        JPanel cmdPanel = new JPanel();

        JButton downButton = new JButton(PREV_CMD);
        downButton.addActionListener(this);
        downButton.setActionCommand(PREV_CMD);

        JButton upButton = new JButton(NEXT_CMD);
        upButton.addActionListener(this);
        upButton.setActionCommand(NEXT_CMD);

        cmdPanel.add(downButton);
        cmdPanel.add(dateButton);
        cmdPanel.add(upButton);

        dateButton.addPropertyChangeListener(this);

        component.add(cmdPanel, BorderLayout.NORTH);

        dateButton.setDate(day);

        loadData(day);

    }
    
    /**
     * Get the Swing c that is the container for these graphs.
     * 
     * @return The swing c
     */
    @Override
    public Node getComponent() {
        return null;
    }
    
    /**
     * Set the title of the Frame.
     * TODO This should probably be some kind of utility function
     */
    @SuppressWarnings("empty-statement")
    public void setTitle() {
        Component c;

        for (c = this.component.getParent();
             c != null && !(c instanceof JFrame) && !(c instanceof JInternalFrame);
             c = c.getParent());

        if (c != null) {
            String dateString = DisplayConstants.formatDate(date);

            if (c instanceof JFrame)
                ((JFrame)c).setTitle(dateString);
            else 
                ((JInternalFrame)c).setTitle(dateString);
        }
    }

    /**
     * Load the data into the plots.
     * 
     * @param c The day of the data to load
     */
    private void loadData(LocalDate date)
    {
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);

        temperatureBinMgr.refresh();
        SummaryRecord summaryRecord = dailySummaryTable.retrieveSummaryForDate(date, ws.getWindParameters(), temperatureBinMgr);
        DailyRecords records = dailyRecordsTable.retrieveRecordForDay(date);
        WeatherAverage averages = dailyAveragesTable.retrieveAveragesForDay(ws.getLocationCode(), date);
        graphPanel.loadData(date, list, summaryRecord, records, averages);
        setTitle();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        String cmd = evt.getActionCommand();
        switch (cmd) {
            case NEXT_CMD:
                date = date.plusDays(1);
                break;
            case PREV_CMD:
                date = date.minusDays(1);
                break;
        }

        dateButton.setDate(date);

        loadData(date);
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(DateButton.DATE_PROPERTY)) {
            date = (LocalDate)evt.getNewValue();
            loadData(date);
        }
    }
}
