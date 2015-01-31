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
package com.bdb.weather.display.historyeditor;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.bdb.util.DateButton;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.DisplayConstants;

public class HistoryEditorPanel implements ComponentContainer {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JPanel datePanel = new JPanel();
    private final HistoryTable historyTable;
    private final JTable table = new JTable();
    
    public HistoryEditorPanel(WeatherStation ws, DBConnection connection) {
        historyTable = new HistoryTable(connection);
        panel.add(datePanel, BorderLayout.NORTH);
        DateButton dateButton = new DateButton();
        datePanel.add(dateButton);
        DateRange range = historyTable.dataRange();
        datePanel.add(new JLabel(DisplayConstants.formatDateTime(range.getStart())));
        datePanel.add(new JLabel(DisplayConstants.formatDateTime(range.getEnd())));
        
        panel.add(new JScrollPane(table));
    }
    
    @Override
    public JComponent getComponent() {
        return panel;
    }

}
