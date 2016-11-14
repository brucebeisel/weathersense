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
package com.bdb.weather.display.day;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Menu;

import com.bdb.weather.display.axis.HumidityRangeAxis;
import com.bdb.weather.display.axis.TemperatureRangeAxis;


/**
 *
 * @author Bruce
 */
public class DewPointPlotPane extends DayXYPlotPane {
    /**
     * Constructor.
     */
    public DewPointPlotPane() {
        super(TemperatureRangeAxis.create(), HumidityRangeAxis.create());
    }

    @Override
    public List<SeriesControl> configure(Menu menu) {
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.AVG_OUTDOOR_TEMPERATURE_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.DEW_POINT_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.OUTDOOR_HUMIDITY_SERIES, true, false));
        return controls;
    }
}
