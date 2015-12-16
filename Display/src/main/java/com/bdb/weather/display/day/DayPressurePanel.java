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

import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Menu;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.TextAnchor;

import com.bdb.util.TimeUtils;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.axis.PressureRangeAxis;
import com.bdb.weather.display.axis.SolarRadiationAxis;

/**
 * Plot for displaying a day's worth of pressure data.
 * 
 * @author Bruce
 */
public class DayPressurePanel extends DayXYPlotPanel {
    /**
     * Constructor.
     */
    public DayPressurePanel() {
        super(new PressureRangeAxis(), new SolarRadiationAxis());
        createElements();
    }
    
    @Override
    public List<SeriesControl> configure(Menu menu) {
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.BAROMETER_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.HIGH_SOLAR_RADIATION_SERIES, true, false));

        XYToolTipGenerator ttg = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getTimeInstance(), Pressure.getDefaultFormatter());
        getPlot().getRenderer(0).setBaseToolTipGenerator(ttg);
        ttg = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getTimeInstance(), SolarRadiation.getDefaultFormatter());
        getPlot().getRenderer(1).setBaseToolTipGenerator(ttg);

        return controls;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#addAnnotations(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.SummaryRecord)
     */
    @Override
    protected void addAnnotations(XYPlot plot, SummaryRecord summaryRecord) {
        plot.getRenderer(0).removeAnnotations();
        plot.getRenderer(1).removeAnnotations();
        
        if (summaryRecord == null)
            return;

        LocalDateTime maxTime = summaryRecord.getMaxBaroPressureTime();
        Pressure maxBaroPressure = summaryRecord.getMaxBaroPressure();
        LocalDateTime minTime = summaryRecord.getMinBaroPressureTime();
        Pressure minBaroPressure = summaryRecord.getMinBaroPressure();

        //
        // Barometric pressure
        //
        String highAnnotation = maxBaroPressure.toString() + Pressure.getDefaultUnit() + " " + DisplayConstants.formatTime(maxTime.toLocalTime());
        String lowAnnotation = minBaroPressure.toString() + Pressure.getDefaultUnit() + " " + DisplayConstants.formatTime(minTime.toLocalTime());

        XYTextAnnotation a = new XYTextAnnotation(highAnnotation, (double)TimeUtils.localDateTimeToEpochMillis(maxTime), maxBaroPressure.get());
        a.setTextAnchor(TextAnchor.BASELINE_CENTER);

        plot.getRenderer(0).addAnnotation(a);

        TextAnchor anchor = TextAnchor.TOP_CENTER;

        if (minTime.getHour() <= 2)
            anchor = TextAnchor.TOP_LEFT;
        else if (minTime.getHour() >= 22)
            anchor = TextAnchor.TOP_RIGHT;

        a = new XYTextAnnotation(lowAnnotation, (double)TimeUtils.localDateTimeToEpochMillis(minTime), minBaroPressure.get());
        a.setTextAnchor(anchor);

        plot.getRenderer(0).addAnnotation(a);

        SolarRadiation maxSolarRadiation = summaryRecord.getMaxSolarRadiation();
        maxTime = summaryRecord.getMaxSolarRadiationTime();

        if (maxSolarRadiation != null) {
            highAnnotation = maxSolarRadiation.toString() + SolarRadiation.Unit.WATTS_PER_METER_SQUARED + " " + DisplayConstants.formatTime(maxTime.toLocalTime());
            a = new XYTextAnnotation(highAnnotation, (double)TimeUtils.localDateTimeToEpochMillis(maxTime), maxSolarRadiation.get());
            a.setTextAnchor(TextAnchor.BASELINE_CENTER);
            plot.getRenderer(1).addAnnotation(a);
        }
    }
}