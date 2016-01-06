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

import java.util.function.BiFunction;
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
    private final BiFunction<HistoricalRecord,Integer,Measurement> accessorWithArg;
    private final String columnName;
    private final int argument;
    
    public TableColumnValueFactory(String columnName, Function<HistoricalRecord,Measurement> accessor) {
        this.columnName = columnName;
        this.accessor = accessor;
        this.accessorWithArg = null;
        this.argument = 0;
    }
    
    public TableColumnValueFactory(String columnName, BiFunction<HistoricalRecord,Integer,Measurement> accessor, int arg) {
        this.columnName = columnName;
        this.accessorWithArg = accessor;
        this.accessor = null;
        this.argument = arg;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<HistoricalRecord,String> cdf) {
        HistoricalRecord r = cdf.getValue();
        Measurement m;
        if (accessor != null)
            m = accessor.apply(r);
        else
            m = accessorWithArg.apply(r, argument);

        String value = DisplayConstants.UNKNOWN_VALUE_STRING;
        if (m != null)
            value = m.toString();

        return new ReadOnlyStringWrapper(value);
    }
}