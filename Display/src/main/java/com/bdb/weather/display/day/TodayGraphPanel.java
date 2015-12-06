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

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

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
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.display.Refreshable;

/**
 * Specialized version of a DayGraphPanel that refreshes automatically and always displays data for the current day.
 * 
 * @author Bruce
 *
 */
public class TodayGraphPanel extends DayGraphPanel implements Refreshable {
    private final HistoryTable historyTable;
    private final DailySummaryTable dailySummaryTable;
    private final DailyAveragesTable dailyAveragesTable;
    private final DailyRecordsTable dailyRecordsTable;
    private final TemperatureBinMgr temperatureBinMgr;
    private final WeatherStationTable wsTable;
    private static final Logger logger = Logger.getLogger(TodayGraphPanel.class.getName());

    /**
     * Constructor.
     * 
     * @param ws The weather station for which this is a display
     * @param connection The database connection
     */
    public TodayGraphPanel(WeatherStation ws, DBConnection connection) {
	super(ws);

	dailySummaryTable = new DailySummaryTable(connection);
	historyTable = new HistoryTable(connection);
	dailyRecordsTable = new DailyRecordsTable(connection);
	dailyAveragesTable = new DailyAveragesTable(connection);
	temperatureBinMgr = new TemperatureBinMgr(connection);
        wsTable = new WeatherStationTable(connection);
    }

//    /**
//     * Make this component visible or not.
//     * 
//     * @param flag The visible flag
//     */
//    public void setVisible(boolean flag) {
//        getComponent().setVisible(flag);
//    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bdb.weather.display.Refreshable#refresh()
     */
    @Override
    public void refresh() {
        logger.fine("Refreshing");
        //
        // Get the records for today and call the super classes loadData() method
        //
        LocalDate date = LocalDate.now();

        WeatherStation ws = wsTable.getWeatherStation();
        DailyRecords records = dailyRecordsTable.retrieveRecordForDay(date);
        WeatherAverage averages = dailyAveragesTable.retrieveAveragesForDay(ws.getLocationCode(), date);
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);
        temperatureBinMgr.refresh();
        SummaryRecord summaryRecord = dailySummaryTable.retrieveTodaysSummary(ws.getWindParameters(), temperatureBinMgr);

        super.loadData(date, list, summaryRecord, records, averages);
    }
}
