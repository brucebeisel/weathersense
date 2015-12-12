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
package com.bdb.weather.display.current;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import com.bdb.util.Pair;
import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.measurement.Measurement;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.MonthWeatherAverages;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.WeatherTrend;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.db.MonthlyAveragesTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.display.CurrentWeatherProcessor;
import com.bdb.weather.display.RainPlot;
import com.bdb.weather.display.WeatherSense;

/**
 * The top-level component for displaying the current weather.
 * 
 * @author Bruce
 *
 */
public class CurrentWeatherCharts extends VBox implements CurrentWeatherProcessor {
    private final WeatherStation          ws;
    @FXML private Barometer               barometer;
    @FXML private WindGauge               windGauge;
    @FXML private Hygrometer              indoorHumidity;
    @FXML private Hygrometer              outdoorHumidity;
    @FXML private Thermometer             outdoorTemperature;
    @FXML private Thermometer             indoorTemperature;
    @FXML private TextField               forecastRule;
    @FXML private DopplerRadar            dopplerRadar;
    @FXML private RainPlot                rainPlot;
    private final HistoryTable            historyTable;
    private final DailySummaryTable       summaryTable;
    private final Month                   weatherYearStartMonth;
    private final TemperatureBinMgr       temperatureBinMgr;
    private final MonthlyAveragesTable    monthlyAverageTable;
    private String                        frameTitle = null;
    private static final Logger           logger = Logger.getLogger(CurrentWeatherCharts.class.getName());
    
    /**
     * Constructor.
     * 
     * @param ws The weather station
     * @param connection The database connection
     */
    public CurrentWeatherCharts(WeatherStation ws, DBConnection connection) {
        this.ws = ws;
        weatherYearStartMonth = ws.getWeatherYearStartMonth();
        temperatureBinMgr = new TemperatureBinMgr(connection);

        historyTable = new HistoryTable(connection);
        summaryTable = new DailySummaryTable(connection);
	monthlyAverageTable = new MonthlyAveragesTable(connection);

	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CurrentWeatherCharts.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
	barometer.setMinValue(ws.getBarometerMin());
	barometer.setMaxValue(ws.getBarometerMax());

        outdoorTemperature.setMinValue(ws.getThermometerMin());
	outdoorTemperature.setMaxValue(ws.getThermometerMax());

        indoorTemperature.setMinValue(ws.getThermometerMin());
        indoorTemperature.setMaxValue(ws.getThermometerMax());

	dopplerRadar.setConnection(connection);

        MonthWeatherAverages averages = (MonthWeatherAverages)monthlyAverageTable.retrieveMonthlyAveragesForLocation(ws.getLocationCode());

	Depth yearlyAverage;
	Depth thisMonthAverage;
	Depth lastMonthAverage;
	LocalDate thisMonth = LocalDate.now();
        LocalDate lastMonth = thisMonth.minusMonths(1);
        if (averages != null) {
            yearlyAverage = averages.calculateYearlyAverageRainfall();
            //calendarYearAverageToDate = averages.calculateCalendarYearAverageRainfallToDate(thisMonth);
            //weatherYearAverageToDate = averages.calculateWeatherYearAverageRainfallToDate(thisMonth, ws.getWeatherYearStartMonth());
            thisMonthAverage = averages.getAverage(thisMonth).getRainfall();
            lastMonthAverage = averages.getAverage(lastMonth).getRainfall();
            
        }
        else {
            yearlyAverage = new Depth(0.0);
            //calendarYearAverageToDate = yearlyAverage;
            //weatherYearAverageToDate = yearlyAverage;
            thisMonthAverage = yearlyAverage;
            lastMonthAverage = yearlyAverage;
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Measurement> Pair<WeatherTrend,T> processTrend(T current, T historical, T delta) {
        WeatherTrend trend = WeatherTrend.STEADY;
        
        if (historical != null && current != null && delta == null) {
            int compare = historical.compareTo(current);
            if (compare < 0) {
                delta = current.subtract(historical);
                trend = WeatherTrend.RISING_SLOWLY;
            }
            else if (compare > 0) {
                delta = historical.subtract(current);
                trend = WeatherTrend.FALLING_SLOWLY;
            }
            else
                delta = (T)current.newMeasurement(0.0);
            
            return new Pair<>(trend, delta);
        }
        else
            return null;
    }
    
    /**
     * Load the data into the current weather components.
     * 
     * @param cw The current weather
     * @throws SQLException See JDBC documentation
     */
    public void loadData(CurrentWeather cw) throws SQLException {
	if (frameTitle == null)
	    frameTitle = WeatherSense.getStageTitle(this);

        // TODO check the age of the current weather. if it is more that about 20 minutes old, ignore it
        LocalDateTime now = LocalDateTime.now();
        
        forecastRule.setText("Forecast: " + cw.getForecastRule());
        
        //
        // TODO: All this logic should be in the collector so that there are no null values
        // when we get here.
        //
        LocalDateTime hourBegin = now.minusHours(1);
        Depth hourRain;
        if ((hourRain = cw.getRainHour()) == null) {
            hourRain = historyTable.rainTotal(hourBegin, now);
        }
        
        Depth last24HourRain;
        if ((last24HourRain = cw.getRain24Hour()) == null) {
            LocalDateTime start = now.minusHours(24);
            last24HourRain = historyTable.rainTotal(start, now);
        }
        
        Depth todayRain;
        if ((todayRain = cw.getRainToday()) == null) {
            LocalDateTime start = now.toLocalDate().atStartOfDay();
            todayRain = historyTable.rainTotal(start, now);
        }
        
        LocalDateTime yearBegin = LocalDate.now().withDayOfYear(1).atStartOfDay();
        Depth ytdRain;
        if ((ytdRain = cw.getRainCalendarYear()) == null) {
            ytdRain = historyTable.rainTotal(yearBegin, now);
        }

        Depth weatherYearRain;
        if ((weatherYearRain = cw.getRainWeatherYear()) == null) {
            yearBegin = yearBegin.withMonth(weatherYearStartMonth.getValue());

            if (now.getMonth().getValue() < weatherYearStartMonth.getValue())
                yearBegin = yearBegin.minusYears(1);

            weatherYearRain = historyTable.rainTotal(yearBegin, now);
        }
        
        Depth thisMonthRain;
        LocalDate nowDate = LocalDate.now();
        LocalDate monthBegin = nowDate.withDayOfMonth(1);
        if ((thisMonthRain = cw.getRainMonth()) == null) {
            thisMonthRain = historyTable.rainTotal(monthBegin.atStartOfDay(), now);
        }
        
        monthBegin = monthBegin.minusMonths(1);
        LocalDate monthEnd = monthBegin.plusDays(monthBegin.lengthOfMonth());
        Depth lastMonthRain = historyTable.rainTotal(monthBegin.atStartOfDay(), monthEnd.atTime(23, 59, 59));
        
        LocalDateTime start = now.minusHours(25);
        
        //
        // The pair is the current rain amount, the hourly rain rate
        //
        List<RainPlot.RainEntry> rainList = new ArrayList<>();
        List<HistoricalRecord> list = historyTable.queryRecordsForTimePeriod(start, now);
        
        list.stream().forEach((rec) -> {
            rainList.add(new RainPlot.RainEntry(rec.getTime(), rec.getRainfall(), rec.getHighRainfallRate()));
        });
        
        //rainPanel.setRainData(ytdRain, weatherYearRain, thisMonthRain, lastMonthRain, last24HourRain, todayRain, hourRain, rainList);
        
        temperatureBinMgr.refresh();
        final SummaryRecord summary = summaryTable.retrieveTodaysSummary(ws.getWindParameters(), temperatureBinMgr);
          
        //
        // Find the indoor and outdoor humidity for up to an hour ago
        //
        Humidity ihd = null;
        WeatherTrend iht = WeatherTrend.STEADY;
        Humidity ohd = null;
        WeatherTrend oht = WeatherTrend.STEADY;
        Pressure pd = null;
        final WeatherTrend pressureTrend = cw.getBaroTrend();

        for (HistoricalRecord record : list) {
            if (record.getTime().isAfter(hourBegin)) {
                Pair<WeatherTrend, Humidity> hpair = processTrend(cw.getOutdoorHumidity(), record.getOutdoorHumidity(), ohd);
                if (hpair != null) {
                    oht = hpair.getFirst();
                    ohd = hpair.getSecond();
                }

                hpair = processTrend(cw.getIndoorHumidity(), record.getIndoorHumidity(), ihd);
                if (hpair != null) {
                    iht = hpair.getFirst();
                    ihd = hpair.getSecond();
                }

                Pair<WeatherTrend, Pressure> ppair = processTrend(cw.getBaroPressure(), record.getBaroPressure(), pd);
                if (hpair != null) {
                    pd = ppair.getSecond();
                }
            }
        }

        final Humidity indoorHumidityDelta = ihd;
        final WeatherTrend indoorHumidityTrend = iht; 
        final Humidity outdoorHumidityDelta = ohd;
        final WeatherTrend outdoorHumidityTrend = oht;
        final Pressure pressureDelta = pd;

        List<Heading> headings = new ArrayList<>();
        if (cw.getWindDir2() != null) {
            headings.add(cw.getWindDir2());
        }
        if (cw.getWindDir3() != null) {
            headings.add(cw.getWindDir3());
        }
        if (cw.getWindDir4() != null) {
            headings.add(cw.getWindDir4());
        }
        if (cw.getWindDir5() != null) {
            headings.add(cw.getWindDir5());
        }

        //
        // If there is no summary record then just use the current temperature for both high and low
        //
	Platform.runLater(() -> {
	    if (summary != null) {
		outdoorTemperature.loadData(cw.getOutdoorTemperature(), summary.getMinOutdoorTemp(), summary.getMaxOutdoorTemp());
		indoorTemperature.loadData(cw.getIndoorTemperature(), summary.getMinIndoorTemp(), summary.getMaxIndoorTemp());
		windGauge.loadData(cw.getWind(), cw.getWindGust(), summary.getMaxWindSpeed(), summary.getMaxWindGust(), summary.getAvgWindSpeed(), headings);
		barometer.loadData(cw.getBaroPressure(), summary.getMinBaroPressure(), summary.getMaxBaroPressure(), pressureDelta, pressureTrend);
		outdoorHumidity.loadData(cw.getOutdoorHumidity(), summary.getMinOutdoorHumidity(), summary.getMaxOutdoorHumidity(), outdoorHumidityDelta, outdoorHumidityTrend);
		indoorHumidity.loadData(cw.getIndoorHumidity(), summary.getMinIndoorHumidity(), summary.getMaxIndoorHumidity(), indoorHumidityDelta, indoorHumidityTrend);
	    }
	    else {
		outdoorTemperature.loadData(cw.getOutdoorTemperature(), cw.getOutdoorTemperature(), cw.getOutdoorTemperature());
		indoorTemperature.loadData(cw.getIndoorTemperature(), cw.getIndoorTemperature(), cw.getIndoorTemperature());
		windGauge.loadData(cw.getWind(), cw.getWindGust(), null, null, null, headings);
		barometer.loadData(cw.getBaroPressure(), cw.getBaroPressure(), cw.getBaroPressure(), new Pressure(0.0), WeatherTrend.STEADY);
		outdoorHumidity.loadData(cw.getOutdoorHumidity(), cw.getOutdoorHumidity(), cw.getOutdoorHumidity(), new Humidity(0.0), WeatherTrend.STEADY);
		indoorHumidity.loadData(cw.getIndoorHumidity(), cw.getIndoorHumidity(), cw.getIndoorHumidity(), new Humidity(0.), WeatherTrend.STEADY);
	    }

	    rainPlot.setRainData(rainList);
	    String date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).format(cw.getTime());
	    String ammendedFrameTitle = frameTitle + " " + date;
	    WeatherSense.setStageTitle(this, ammendedFrameTitle);
	});
    }

    @Override
    public void updateCurrentWeather(CurrentWeather cw) {
        try {
            loadData(cw);
        }
        catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}