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
package com.bdb.weather.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import com.bdb.weather.collector.Summarizer;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.measurement.Temperature;

public class HistoricalDataEditor implements ActionListener {
    private Summarizer     summarizer;
    private JFrame         frame = new JFrame("Historical Data Editor");
    private JTextField     timeTF = new JTextField(20);
    private JTable         table;
    private HistoryTable   historyTable;
    private HistoricalTableModel tableModel;
    private HistoricalRecord  record;
    private JComboBox      fieldCB = new JComboBox();
    private JTextField     valueTF = new JTextField(10);
 
    public HistoricalDataEditor(WeatherStation station, Summarizer summarizer, HistoryTable historyTable) {
        summarizer = summarizer;
        historyTable = historyTable;
        
        tableModel = new HistoricalTableModel();
        table = new JTable(tableModel);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.setLayout(new BorderLayout());
        JPanel p = new JPanel();
        SimpleDateFormat sdf = new SimpleDateFormat();
        p.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Date/Time(" + sdf.toLocalizedPattern() + ")"));
        p.add(timeTF);
        
        JButton button = new JButton("Get");
        button.addActionListener(this);
        button.setActionCommand("get");
        p.add(button);
        
        frame.add(p, BorderLayout.NORTH);
        
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        
        fieldCB.addItem("Indoor Temperature");
        fieldCB.addItem("Indoor Humidity");
        
        p = new JPanel();
        p.add(fieldCB);
        p.add(valueTF);
        
        button = new JButton("Save");
        button.addActionListener(this);
        button.setActionCommand("save");
        p.add(button);
        
        frame.add(p, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("get")) {
            String dateString = timeTF.getText();
            SimpleDateFormat sdf = new SimpleDateFormat();
            try {
                Date date = sdf.parse(dateString);
                Calendar recordDate = Calendar.getInstance();
                recordDate.setTime(date);
                Calendar start = (Calendar)recordDate.clone();
                Calendar end = (Calendar)recordDate.clone();
                start.add(Calendar.HOUR, -2);
                end.add(Calendar.HOUR, 2);
                List<HistoricalRecord> records = historyTable.queryRecordsForTimePeriod(start, end);
                
                record = null;
                
                for (HistoricalRecord rec : records) {
                    if (recordDate.compareTo(rec.getTime()) == 0)
                        record = rec;
                }
                
                if (record != null)
                    tableModel.setData(records);
                else
                    JOptionPane.showMessageDialog(frame, "Could not find exact match");
            }
            catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else if (e.getActionCommand().equals("save")) {
            // switch on ComboBox Value
            double value = Double.parseDouble(valueTF.getText());
            Temperature t = new Temperature(value);
            record.setAvgOutdoorTemperature(t);
            historyTable.updateRow(record);
            try {
                summarizer.updateSummary(record.getTime());
            }
            catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database update failed");
            }
        }
    }
}

class HistoricalTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 2264972623125059516L;
    private static final int DATE_COLUMN = 0;
    private static final int INDOOR_TEMP_COLUMN = 1;
    private static final int INDOOR_HUMID_COLUMN = 2;
    private static final int OUTDOOR_TEMP_COLUMN = 3;
    private static final int OUTDOOR_HUMID_COLUMN = 4;
    private static final String COLUMN_NAMES[] = {
        "Date", "Indoor Temp.", "Indoor Humid.", "Outdoor Temp.", "Outdoor Humid."
    };
    
    private List<HistoricalRecord> list = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat();

    public HistoricalTableModel() {
    }
    
    public void setData(List<HistoricalRecord> data) {
        list = Collections.unmodifiableList(data);
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return  COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
       return list.size();
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        
        switch (column) {
            case DATE_COLUMN:
                return sdf.format(list.get(row).getTime());
                
            case INDOOR_TEMP_COLUMN:
                return list.get(row).getIndoorTemperature();
                
            case INDOOR_HUMID_COLUMN:
                return list.get(row).getIndoorHumidity();
                
            case OUTDOOR_TEMP_COLUMN:
                return list.get(row).getAvgOutdoorTemperature();
                
            case OUTDOOR_HUMID_COLUMN:
                return list.get(row).getOutdoorHumidity();
        }
        
        return null;
    }
}
