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
package com.bdb.weather.display.summary;


import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.axis.PressureRangeAxis;

public class HighLowPressurePanel extends HighLowPanel<Pressure> {
    private static final SeriesInfo<Pressure>[] seriesInfo;
    
    static {
        seriesInfo = new SeriesInfo[1];
        seriesInfo[0] = new SeriesInfo<>("Barometric Pressure", SummaryRecord::getMaxBaroPressure,
                                                                SummaryRecord::getMinBaroPressure,
                                                                SummaryRecord::getAvgBaroPressure);
    }
    
    public HighLowPressurePanel(SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter) {
        super("Pressure", interval, launcher, supporter, PressureRangeAxis.create(), "Date", seriesInfo, Pressure.getDefaultFormatter());
    }
}
