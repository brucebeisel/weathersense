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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.XYPlot;

import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.axis.HumidityRangeAxis;
import com.bdb.weather.display.axis.TemperatureRangeAxis;

/**
 *
 * @author Bruce
 */
public class DewPointPlotPanel extends DayXYPlotPanel {
    /**
     * Constructor.
     *
     * @param ws The weather station for which the temperature data is being displayed
     */
    public DewPointPlotPanel(WeatherStation ws) {
        super(ws, new TemperatureRangeAxis(), null);
        XYPlot plot = getPlot();
        plot.setRangeAxis(1, new HumidityRangeAxis());
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
    }

    @Override
    public List<SeriesControl> configure(JMenu menu) {
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.AVG_OUTDOOR_TEMPERATURE_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.DEW_POINT_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.OUTDOOR_HUMIDITY_SERIES, true, false));
        return controls;
    }
}
