/*
 * Copyright (C) 2015 bruce
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

import java.util.function.Function;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import com.bdb.util.measurement.Measurement;
import com.bdb.weather.common.HistoricalRecord;

/**
 *
 * @author bruce
 */
public class TableColumnValueFactory implements Callback<TableColumn.CellDataFeatures<HistoricalRecord,String>,ObservableValue<String>> {
    private final Function<HistoricalRecord,Measurement> accessor;
    private final String columnName;
    
    public TableColumnValueFactory(String columnName, Function<HistoricalRecord,Measurement> accessor) {
        this.columnName = columnName;
        this.accessor = accessor;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<HistoricalRecord,String> cdf) {
        HistoricalRecord r = cdf.getValue();
        Measurement m = accessor.apply(r);

        String value = DisplayConstants.UNKNOWN_VALUE_STRING;
        if (m != null)
            value = m.toString();

        return new ReadOnlyStringWrapper(value);
    }
}
