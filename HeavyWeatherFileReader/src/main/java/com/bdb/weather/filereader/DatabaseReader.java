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
package com.bdb.weather.filereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.HistoryTable;

public class DatabaseReader extends HistoryFileReader {
    private DBConnection m_connection;
    private HistoryTable m_historyTable;
    private Calendar     m_lastReadDataTime;

    public DatabaseReader(File file) throws FileNotFoundException {
        super(null);
    }

    @Override
    void closeHistoryFile() {
        m_connection.close();
    }

    @Override
    void openHistoryFile() throws FileNotFoundException, IOException {
        

        m_connection = new DBConnection(String.format(DatabaseConstants.DATABASE_URL_FORMATTER, 
                                                      DatabaseConstants.DATABASE_HOST,
                                                      DatabaseConstants.DATABASE_PORT,
                                                      DatabaseConstants.DATABASE_NAME),
                                        DatabaseConstants.DATABASE_DRIVER,
                                        DatabaseConstants.DATABASE_USER,
                                        DatabaseConstants.DATABASE_PASSWORD);
        
        m_connection.connect();
        m_historyTable = new HistoryTable(m_connection, null);
    }

    @Override
    HistoricalRecord readNextRecord(Calendar cal) throws IOException {
        m_lastReadDataTime = (Calendar)cal.clone();
        return readNextRecord();
    }

    @Override
    HistoricalRecord readNextRecord() throws IOException {
        Calendar end = Calendar.getInstance();
        end.setTime(m_lastReadDataTime.getTime());
        end.add(Calendar.DATE, 1);
        List<HistoricalRecord> list = m_historyTable.queryRecordsForTimePeriod(m_lastReadDataTime, end);

        for (HistoricalRecord rec : list) {
            if (rec.getTime().after(m_lastReadDataTime)) {
                m_lastReadDataTime = rec.getTime();
                return rec;
            }
        }

        return null;
    }
}
