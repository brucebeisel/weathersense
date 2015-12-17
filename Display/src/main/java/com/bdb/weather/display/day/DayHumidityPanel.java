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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Menu;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.TextAnchor;

import com.bdb.util.TimeUtils;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.axis.HumidityRangeAxis;

/**
 * Class to display a day's worth of humidity data.
 * 
 * @author Bruce
 *
 */
public class DayHumidityPanel extends DayXYPlotPanel {
    /**
     * Constructor.
     */
    public DayHumidityPanel() {
        super(new HumidityRangeAxis(), null);
        createElements();
    }
    
    /**
     *
     * @param menu
     * @return
     */
    @Override
    public List<SeriesControl> configure(Menu menu) {
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.OUTDOOR_HUMIDITY_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.INDOOR_HUMIDITY_SERIES, true));
	return controls;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#addAnnotations(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.SummaryRecord)
     */
    @Override
    protected void addAnnotations(XYPlot plot, SummaryRecord summaryRecord) {
        plot.clearAnnotations();
        
        if (summaryRecord == null)
            return;
        
        LocalDateTime highTime = summaryRecord.getMaxOutdoorHumidityTime();
        Humidity highHumidity = summaryRecord.getMaxOutdoorHumidity();
        LocalDateTime lowTime = summaryRecord.getMinOutdoorHumidityTime();
        Humidity lowHumidity = summaryRecord.getMinOutdoorHumidity();
        
        if (highTime == null || highHumidity == null || lowTime == null || lowHumidity == null)
            return;

        String highAnnotation = highHumidity.toString() + Humidity.Unit.RELATIVE_HUMIDITY + " " +  DisplayConstants.formatTime(highTime.toLocalTime());
        String lowAnnotation = lowHumidity.toString() +  Humidity.Unit.RELATIVE_HUMIDITY + " " + DisplayConstants.formatTime(lowTime.toLocalTime());

        XYTextAnnotation a = new XYTextAnnotation(highAnnotation, TimeUtils.localDateTimeToEpochMillis(highTime), highHumidity.get());
        a.setTextAnchor(TextAnchor.BASELINE_CENTER);
        plot.addAnnotation(a);

        a = new XYTextAnnotation(lowAnnotation, TimeUtils.localDateTimeToEpochMillis(lowTime), lowHumidity.get());
        a.setTextAnchor(TextAnchor.TOP_CENTER);
        plot.addAnnotation(a);
    }
}