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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.text.ParsePosition;
import java.time.LocalDate;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

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
public final class DayAveragesEditor extends JDialog implements ActionListener {
    private static final long serialVersionUID = -1789278579624833961L;
    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "CANCEL";
    private static final String IMPORT_COMMAND = "IMPORT";
    private static final String EXPORT_COMMAND = "EXPORT";
    private final String                  locationName;
    private final JFrame                  frame;
    private final JTable                  averagesTable;
    private final DailyAveragesTable      dailyAveragesDbTable;
    private DayWeatherAverages      averages;
    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("CSV", "csv");
    
    @SuppressWarnings("LeakingThisInConstructor")
    private DayAveragesEditor(JFrame owner, DBConnection connection, String locationName) {
        super(owner, true);
        frame = owner;
        this.locationName = locationName;
        dailyAveragesDbTable = new DailyAveragesTable(connection);
        averages = dailyAveragesDbTable.retrieveDailyAverages();
        if (averages == null)
            averages = new DayWeatherAverages();
        
        setTitle("Edit Location - " + locationName);

        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EtchedBorder());
        JButton button = new JButton("OK");
        button.setActionCommand(OK_COMMAND);
        button.addActionListener(this);
        buttonPanel.add(button);
        
        button = new JButton("Cancel");
        button.setActionCommand(CANCEL_COMMAND);
        button.addActionListener(this);
        buttonPanel.add(button);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new BorderLayout());
        attributePanel.setBorder(new EtchedBorder());
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        averagesTable = new AveragesTable(averages);
        JScrollPane sp = new JScrollPane(averagesTable);
        p.add(sp, BorderLayout.CENTER);
        
        buttonPanel = new JPanel();
        
        button = new JButton("Import...");
        button.setActionCommand(IMPORT_COMMAND);
        button.addActionListener(this);
        buttonPanel.add(button);
        
        button = new JButton("Export...");
        button.setActionCommand(EXPORT_COMMAND);
        button.addActionListener(this);
        buttonPanel.add(button);
        p.add(buttonPanel, BorderLayout.SOUTH);
        
        attributePanel.add(p, BorderLayout.CENTER);
 
        add(attributePanel, BorderLayout.CENTER);
        
        pack();
        
        setLocationRelativeTo(owner);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public static void editAverages(JFrame frame, DBConnection connection, String locationName) {
        JDialog dialog = new DayAveragesEditor(frame, connection, locationName);
        dialog.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        switch (command) {
            case EXPORT_COMMAND: {
                JFileChooser chooser = new JFileChooser();
                chooser.addChoosableFileFilter(fileFilter);
                chooser.setApproveButtonText("Export");
                int returnVal = chooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        Location.exportCSVFile(file, locationName, averages);
                        JOptionPane.showMessageDialog(frame, "Averages successfully exported", "Export", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(frame, "Error saving CSV file", "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            }
            case IMPORT_COMMAND: {
                JFileChooser chooser = new JFileChooser();
                chooser.addChoosableFileFilter(fileFilter);
                chooser.setApproveButtonText("Import");
                int returnVal = chooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        Location.importCSVFile(file, locationName, averages);
                        ((AbstractTableModel)averagesTable.getModel()).fireTableDataChanged();
                        JOptionPane.showMessageDialog(frame, "Averages successfully imported", "Import", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException e) {
                        JOptionPane.showMessageDialog(frame, "Error importing CSV file", "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            }
            default:
                if (command.equals(OK_COMMAND)) {
                    if (!dailyAveragesDbTable.updateDailyAverages(averages))
                        JOptionPane.showConfirmDialog(this, "Error storing day averages", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                setVisible(false);
                break;
        }
    }
}
/**
 * The specialized JTable for the averages data.
 * 
 * @author Bruce
 *
 */
class AveragesTable extends JTable {
    private static final long serialVersionUID = -3780972995860735557L;
    private final AveragesTableModel model;

    public AveragesTable(DayWeatherAverages avgs) {
        super(new AveragesTableModel(avgs));
        model = (AveragesTableModel)getModel();
        JTextField tmp = new JTextField();
        setRowHeight(tmp.getPreferredSize().height);
    }
    
    public void setValues(DayWeatherAverages avgs) {
        model.setValues(avgs);
    }
    
    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return model.getColumnEditor(column);
    }
}

//
// TODO Add the CellEditor as a JFormattedTextField with a decimal for date formatter.
//
/**
 * The table model for the averages data. It uses a Location object for the data storage
 */
class AveragesTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 7605015851451686390L;
    private LocalDate lookupDate = LocalDate.now();
    private DayWeatherAverages dayWeatherAverages;
    private static final int YEAR_THAT_IS_NOT_A_LEAP_YEAR = 2001;
    private static final int DAYS_IN_A_NON_LEAP_YEAR = 365;
    private static final int DATE_COLUMN = 0;
    private static final int LOW_COLUMN = 1;
    private static final int MEAN_COLUMN = 2;
    private static final int HIGH_COLUMN = 3;
    private static final int RAIN_COLUMN = 4;
    private static final String COLUMN_NAMES[] = {
        "Date", "Avg Low", "Avg Mean", "Avg High", "Rainfall"
    };
    private static final Class<?> COLUMN_CLASSES[] = {
        String.class, Temperature.class, Temperature.class, Temperature.class, Depth.class, String.class, String.class
    };
    
    private static final Format COLUMN_FORMAT[] = {
        null,
        Temperature.getDefaultFormatter(),
        Temperature.getDefaultFormatter(),
        Temperature.getDefaultFormatter(),
        Depth.getDefaultFormatter(),
    };
    
    private static final TableCellEditor CELL_EDITORS[] = {
        null,
        new DefaultCellEditor(new JFormattedTextField(COLUMN_FORMAT[1])),
        new DefaultCellEditor(new JFormattedTextField(COLUMN_FORMAT[2])),
        new DefaultCellEditor(new JFormattedTextField(COLUMN_FORMAT[3])),
        new DefaultCellEditor(new JFormattedTextField(COLUMN_FORMAT[4]))
    };
    
    public AveragesTableModel(DayWeatherAverages avgs) {
        lookupDate = lookupDate.withYear(YEAR_THAT_IS_NOT_A_LEAP_YEAR);
        dayWeatherAverages = avgs;
    }
    
    public void setValues(DayWeatherAverages avgs) {
        dayWeatherAverages = avgs;
    }

    @Override
    public int getColumnCount() {
        return RAIN_COLUMN + 1;
    }

    @Override
    public int getRowCount() {
        return DAYS_IN_A_NON_LEAP_YEAR;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_CLASSES[column];
    }
    
    public TableCellEditor getColumnEditor(int column) {
        return CELL_EDITORS[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        lookupDate = lookupDate.withDayOfYear(row + 1);
        WeatherAverage avgs = dayWeatherAverages.getAverage(lookupDate);
        
        switch (column) {
            case DATE_COLUMN:
                return String.format("%d/%d", lookupDate.getMonth().getValue(), lookupDate.getDayOfMonth());
                
            case LOW_COLUMN:
                return avgs.getLowTemperature();
                
            case MEAN_COLUMN:
                return avgs.getMeanTemperature();
                
            case HIGH_COLUMN:
                return avgs.getHighTemperature();
                
            case RAIN_COLUMN:
                return avgs.getRainfall();
        }
        
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return column >= 1;    
    }
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        lookupDate = lookupDate.withDayOfYear(row + 1);
        WeatherAverage avgs = dayWeatherAverages.getAverage(lookupDate);
        
        Temperature low = avgs.getLowTemperature();
        Temperature mean = avgs.getMeanTemperature();
        Temperature high = avgs.getHighTemperature();
        Depth rainfall = avgs.getRainfall();
        
        switch (column) {
            case DATE_COLUMN:
                return; // Error
                
            case LOW_COLUMN:
                String stringValue = (String)value;
                low = new Temperature(Double.parseDouble(stringValue));
                break;
                
            case MEAN_COLUMN:
                stringValue = (String)value;
                mean = new Temperature(Double.parseDouble(stringValue));
                break;
                
            case HIGH_COLUMN:
                stringValue = (String)value;
                high = new Temperature(Double.parseDouble(stringValue));
                break;
                
            case RAIN_COLUMN:
                stringValue = (String)value;
                Double temp = (Double)COLUMN_FORMAT[RAIN_COLUMN].parseObject(stringValue, new ParsePosition(0));
                if (temp != null)
                    rainfall = new Depth(temp);
                break;
        }
        
        WeatherAverage updatedAvgs = new WeatherAverage(avgs.getMonth(), avgs.getDay(), high, low, mean, rainfall);
        dayWeatherAverages.putAverage(updatedAvgs, lookupDate);
    }
}