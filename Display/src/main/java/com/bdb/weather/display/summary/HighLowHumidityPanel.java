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

import java.text.DecimalFormat;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.axis.HumidityRangeAxis;

public class HighLowHumidityPanel extends HighLowPanel<Humidity> {   
    private static final SeriesInfo<Humidity>[] seriesInfo;
    
    static {
        seriesInfo = new SeriesInfo[2];

        seriesInfo[0] = new SeriesInfo<>("Outdoor", SummaryRecord::getMaxOutdoorHumidity,
                                                    SummaryRecord::getMinOutdoorHumidity,
                                                    SummaryRecord::getAvgOutdoorHumidity);
        
        seriesInfo[1] = new SeriesInfo<>("Indoor", SummaryRecord::getMaxIndoorHumidity,
                                                   SummaryRecord::getMinIndoorHumidity,
                                                   SummaryRecord::getAvgIndoorHumidity);
    }
    
    public HighLowHumidityPanel(SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter) {
        super("Humidity", interval, launcher, supporter, new HumidityRangeAxis(), "Date", seriesInfo, new DecimalFormat("0.0"));
    }
}