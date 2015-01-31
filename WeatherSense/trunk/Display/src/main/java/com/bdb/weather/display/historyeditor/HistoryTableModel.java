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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.bdb.util.measurement.Measurement;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.display.DisplayConstants;

class HistoryTableModel extends AbstractTableModel implements TableCellEditor {
    private static final long serialVersionUID = 2264972623125059516L;
    private static final int DATE_COLUMN = 0;
    private static final int DURATION_COLUMN = 1;
    private static final int INDOOR_TEMP_COLUMN = 2;
    private static final int INDOOR_HUMID_COLUMN = 3;
    private static final int OUTDOOR_TEMP_COLUMN = 4;
    private static final int OUTDOOR_HUMID_COLUMN = 5;
    private static final int WIND_SPEED_COLUMN = 6;
    private static final int WIND_DIR_COLUMN = 7;
    private static final int WIND_GUST_COLUMN = 8;
    private static final int BARO_PRESSURE_COLUMN = 9;
    private static final int UV_INDEX_COLUMN = 10;
    private static final int UV_DOSE_COLUMN = 11;
    private static final int SOLAR_RADIATION_COLUMN = 12;
    private static final String COLUMN_NAMES[] = {
        "Date", "Duration",
        "Indoor Temp.", "Indoor Humid.",
        "Outdoor Temp.", "Outdoor Humid.",
        "Wind Speed", "Wind Gust", "Wind Direction",
        "Baro Pressure", "UV Index", "UV Dose", "Solar Radiation"
    };
    public static final Class<?> COLUMN_CLASSES[] = {
        String.class, Integer.class,
        Measurement.class, Measurement.class,
        Measurement.class, Measurement.class,
        Measurement.class, Measurement.class, Measurement.class,
        Measurement.class, Measurement.class, Measurement.class, Measurement.class
    };
   
    private List<HistoricalRecord> list = new ArrayList<>();
    private final DatabaseCellEditor cellEditor = new DatabaseCellEditor();

    public HistoryTableModel() {
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
    public Class<?> getColumnClass(int column) {
        return COLUMN_CLASSES[column];
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return column > 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        
        switch (column) {
            case DATE_COLUMN:
                return DisplayConstants.formatDateTime(list.get(row).getTime());
                
            case DURATION_COLUMN:
                return list.get(row).getDuration();
                
            case INDOOR_TEMP_COLUMN:
                return list.get(row).getIndoorTemperature();
                
            case INDOOR_HUMID_COLUMN:
                return list.get(row).getIndoorHumidity();
                
            case OUTDOOR_TEMP_COLUMN:
                return list.get(row).getAvgOutdoorTemperature();
                
            case OUTDOOR_HUMID_COLUMN:
                return list.get(row).getOutdoorHumidity();
                
            case WIND_SPEED_COLUMN:
            case WIND_GUST_COLUMN:
            case WIND_DIR_COLUMN:
            case BARO_PRESSURE_COLUMN:
            case UV_INDEX_COLUMN:
            case UV_DOSE_COLUMN:
            case SOLAR_RADIATION_COLUMN:
                break;
        }
        
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancelCellEditing() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (column > 1)
            return cellEditor;
        else
            return null;
    }
}
