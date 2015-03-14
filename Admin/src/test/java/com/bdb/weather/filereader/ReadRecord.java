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
package com.bdb.weather.filereader;

import java.io.File;
import java.util.Calendar;

import com.bdb.weather.common.HistoricalRecord;

public class ReadRecord
{
    public static final void main(String args[])
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(new File("history.dat"));
            reader.openHistoryFile();
            
            Calendar c = Calendar.getInstance();
            c.clear();
            c.set(2007, Calendar.MAY, 1, 6, 20, 0);
            HistoricalRecord rec = reader.readNextRecord(c);
            System.out.println(rec);
            
            for (int i = 0; i < 10; i++)
            {
                //HistoricalRecord rec = reader.readRecord(35921 + i);
                rec = reader.readNextRecord();
                System.out.println(rec);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
