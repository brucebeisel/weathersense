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

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.ui.TextAnchor;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.SensorManager;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.StageUtilities;
import com.bdb.weather.display.axis.TemperatureRangeAxis;
import com.bdb.weather.display.preferences.UserPreferences;

/**
 * Class to display a day's worth of temperature data.
 * 
 * @author Bruce
 *
 */
public class DayTemperaturePlot extends DayXYPlotPanel implements EventHandler<ActionEvent> {
    private Temperature             recordLow;
    private Temperature             recordHigh;
    private final CheckMenuItem     minMaxLabelsItem = new CheckMenuItem("Min/Max Labels");
    private SummaryRecord           summary;
    
    /**
     * Constructor.
     */
    public DayTemperaturePlot() {
	super(new TemperatureRangeAxis(), null);
        createElements();
        minMaxLabelsItem.setSelected(true);
    }
    
    @Override
    public List<SeriesControl> configure(Menu menu) {
	menu.getItems().add(minMaxLabelsItem);
	minMaxLabelsItem.setOnAction(this);
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.INDOOR_TEMPERATURE_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.LOW_OUTDOOR_TEMPERATURE_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.HIGH_OUTDOOR_TEMPERATURE_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.AVG_OUTDOOR_TEMPERATURE_SERIES, true));
        controls.add(new SeriesControl(HistoricalSeriesInfo.DEW_POINT_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.HEAT_INDEX_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.WIND_CHILL_SERIES, false));
        
        SensorManager.getInstance().getExtraSensors(SensorType.THERMOMETER).stream().forEach((sensor) -> {
            controls.add(new SeriesControl(sensor.getName(), false));
        });

        XYToolTipGenerator ttg = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getTimeInstance(), Temperature.getDefaultFormatter());
        getPlot().getRenderer().setBaseToolTipGenerator(ttg);

        return controls;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#addExtremeMarkers(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.DailyRecords, com.bdb.weather.common.DailyAverages)
     */
    @Override
    public void addExtremeMarkers(XYPlot plot, DailyRecords records, WeatherAverage averages) {
        if (minMaxLabelsItem.isSelected()) {
            String unit = Temperature.getDefaultUnit().toString();

            if (records != null) {
                recordHigh = records.getMaxHighTemperature();
                ValueMarker marker = new ValueMarker(recordHigh.get());
                marker.setLabel("Record High " + recordHigh.toString() + " " + unit + " (" + records.getMaxHighTemperatureYear() + ")");
                marker.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);
                marker.setPaint(StageUtilities.toAwtColor(UserPreferences.getInstance().getHighOutdoorTempColorPref()));
                plot.addRangeMarker(marker);

                recordLow = records.getMinLowTemperature();
                marker = new ValueMarker(recordLow.get());
                marker.setLabel("Record Low " + recordLow.toString() + " " + unit + " (" + records.getMinLowTemperatureYear() + ")");
                marker.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);
                marker.setPaint(StageUtilities.toAwtColor(UserPreferences.getInstance().getLowOutdoorTempColorPref()));
                plot.addRangeMarker(marker);
            }
    
            if (averages != null) {
                Temperature value = averages.getHighTemperature();
                ValueMarker marker = new ValueMarker(value.get());
                marker.setLabel("Average High " + value.toString() + " " + unit);
                marker.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);
                marker.setPaint(StageUtilities.toAwtColor(UserPreferences.getInstance().getHighOutdoorTempColorPref()));
                plot.addRangeMarker(marker);

                value = averages.getLowTemperature();
                marker = new ValueMarker(value.get());
                marker.setLabel("Average Low " + value.toString() + " " + unit);
                marker.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);
                marker.setPaint(StageUtilities.toAwtColor(UserPreferences.getInstance().getLowOutdoorTempColorPref()));
                plot.addRangeMarker(marker);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#addAnnotations(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.SummaryRecord)
     */
    @Override
    public void addAnnotations(XYPlot plot, SummaryRecord summaryRecord) {
	plot.clearAnnotations();
        summary = summaryRecord;
	if (summaryRecord == null)
	    return;
	
	LocalDateTime highTime = summaryRecord.getMaxOutdoorTempTime();
	Temperature outdoorHighTemp = summaryRecord.getMaxOutdoorTemp();
	LocalDateTime lowTime = summaryRecord.getMinOutdoorTempTime();
	Temperature outdoorLowTemp = summaryRecord.getMinOutdoorTemp();

	if (highTime == null || outdoorHighTemp == null || lowTime == null || outdoorLowTemp == null)
	    return;
	
	String highAnnotation = outdoorHighTemp.toString() + " " + Temperature.getDefaultUnit() + " " + DisplayConstants.formatTime(highTime.toLocalTime());
	String lowAnnotation = outdoorLowTemp.toString() + " " + Temperature.getDefaultUnit() + " " + DisplayConstants.formatTime(lowTime.toLocalTime());

	XYTextAnnotation a = new XYTextAnnotation(highAnnotation, (double)TimeUtils.localDateTimeToEpochMillis(highTime), outdoorHighTemp.get());
	a.setTextAnchor(TextAnchor.BASELINE_CENTER);
	plot.addAnnotation(a);

	a = new XYTextAnnotation(lowAnnotation, TimeUtils.localDateTimeToEpochMillis(lowTime), outdoorLowTemp.get());
	a.setTextAnchor(TextAnchor.TOP_CENTER);
	plot.addAnnotation(a);

	highTime = summaryRecord.getMaxIndoorTempTime();
	Temperature indoorHighTemp = summaryRecord.getMaxIndoorTemp();
	lowTime = summaryRecord.getMinIndoorTempTime();
	Temperature indoorLowTemp = summaryRecord.getMinIndoorTemp();

	highAnnotation = indoorHighTemp.toString() + " " + Temperature.getDefaultUnit() + " " + DisplayConstants.formatTime(highTime.toLocalTime());
	lowAnnotation = indoorLowTemp + " " + Temperature.getDefaultUnit() + " " + DisplayConstants.formatTime(lowTime.toLocalTime());

	a = new XYTextAnnotation(highAnnotation, TimeUtils.localDateTimeToEpochMillis(highTime), indoorHighTemp.get());
	a.setTextAnchor(TextAnchor.BASELINE_CENTER);
	plot.addAnnotation(a);

	a = new XYTextAnnotation(lowAnnotation, TimeUtils.localDateTimeToEpochMillis(lowTime), indoorLowTemp.get());
	a.setTextAnchor(TextAnchor.TOP_CENTER);
	plot.addAnnotation(a);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#finishLoadData()
     */
    @Override
    public void finishLoadData() {
        if (recordHigh == null || recordLow == null)
            return;
        
        ValueAxis axis = getPlot().getRangeAxis();
        
        Range range = axis.getRange();
        
        double lowRange = recordLow.get() < range.getLowerBound() ? recordLow.get() : range.getLowerBound();
        double highRange = recordHigh.get() > range.getUpperBound() ? recordHigh.get() : range.getUpperBound();

        axis.setAutoRange(false);
        axis.setRange(lowRange - 10.0, highRange + 10.0);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void handle(ActionEvent event) {
	super.handle(event);
        Object source = event.getSource();
        if (source == minMaxLabelsItem) {
            addAnnotations(getPlot(), summary);
        }
    }
}