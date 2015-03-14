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
package com.bdb.weather.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class HistoricalInfo
{
    private final LocalDateTime firstRecordTime;
    private final LocalDateTime lastRecordTime;
    private final int minutesToNextReading;
    private int	numRecords = 0;
    private int recordIntervalSetting = 0;	// Current interval
    private final int lastRecordWritten;

    public HistoricalInfo(LocalDateTime firstRecordTime,
			  LocalDateTime lastRecordTime,
			  int minutesToNextReading,
			  int numRecords,
			  int recordIntervalSetting,
			  int lastRecordWritten)
    {

	if (firstRecordTime != null)
	    this.firstRecordTime = firstRecordTime;
        else
            this.firstRecordTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

	this.lastRecordTime = lastRecordTime;

	this.minutesToNextReading = minutesToNextReading;
	this.numRecords = numRecords;
	this.recordIntervalSetting = recordIntervalSetting;
	this.lastRecordWritten = lastRecordWritten;
    }

    @Override
    public String toString()
    {
	StringBuilder sb = new StringBuilder();

	String nl = System.getProperty("line.separator");

	sb = sb.append("First Record Time: ").append(firstRecordTime).append(nl);

	sb.append("Last Record Time: ");
	sb.append(lastRecordTime);
	sb.append(nl);

	sb.append("Minutes To Next Reading: ");
	sb.append(minutesToNextReading);
	sb.append(nl);

	sb.append("Number of Records: ");
	sb.append(numRecords);
	sb.append(nl);

	sb.append("Record Interval Setting: ");
	sb.append(recordIntervalSetting);
	sb.append(nl);

	sb.append("Last Record Index: ");
	sb.append(lastRecordWritten);
	sb.append(nl);

	return sb.toString();
    }


}
