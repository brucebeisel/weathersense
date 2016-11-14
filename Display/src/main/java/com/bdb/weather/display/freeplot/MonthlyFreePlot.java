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
package com.bdb.weather.display.freeplot;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.jfree.data.time.Month;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.MonthlySummaryTable;

/**
 * A free plot that plots monthly data. This is based on the daily free plot.
 * TODO Add a summary free plot then have Daily and Monthly inherit from it.
 * 
 * @author Bruce
 *
 */
public class MonthlyFreePlot extends SummaryFreePlot {
    private final WeatherStation ws;
    private final MonthlySummaryTable table;
    
    /**
     * Constructor.
     * 
     * @param ws The weather station for which this free plot will display data
     * @param connection The database connection
     */
    public MonthlyFreePlot(WeatherStation ws, DBConnection connection) {
        super(ws, connection, Month.class);
        this.ws = ws;
        table = new MonthlySummaryTable(connection);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.DailyFreePlot#retrieveData(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    @Override
    public List<?> retrieveData(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<SummaryRecord> list = table.retrieveRange(ws, startDate.toLocalDate(), endDate.toLocalDate(), getTemperatureBinMgr());
        return list;
    }
}