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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.net.MalformedURLException;
import java.net.URL;
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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

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
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.CurrentWeatherProcessor;
import com.bdb.weather.display.ErrorDisplayer;
import com.bdb.weather.display.RainPlot;
import com.bdb.weather.display.WeatherSense;

/**
 * The top-level component for displaying the current weather.
 * 
 * @author Bruce
 *
 */
public class CurrentWeatherPanel implements ComponentContainer, CurrentWeatherProcessor {
    private static final String           USER_DIVIDER_LOCATION_PROPERTY = "cw_divider_loc";
    private final JSplitPane              component = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final HistoryTable            historyTable;
    private final DailySummaryTable       summaryTable;
    private final TemperatureBinMgr       temperatureBinMgr;
    private final MonthlyAveragesTable    monthlyAverageTable;
    private final WeatherStation          ws;
    private final CurrentWeatherRainPanel rainPanel;
    private final Thermometer             outdoorThermometer;
    private final JTextField              forecastRuleTF = new JTextField();
    private final Thermometer             indoorThermometer;
    private final Hygrometer              outdoorHumidity = new Hygrometer("Outdoor");
    private final Hygrometer              indoorHumidity = new Hygrometer("Indoor");
    private final WindGauge               windPanel = new WindGauge();
    private final Barometer               barometerPanel;
    private final DopplerRadar            radar;
    private final Month                   weatherYearStartMonth;
    private String                        frameTitle = null;
    private static final Logger           logger = Logger.getLogger(CurrentWeatherPanel.class.getName());
    private final Preferences             rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences             prefs = rootPref.node("window-geometry");
    
    /**
     * Constructor.
     * 
     * @param ws The weather station
     * @param connection The database connection
     */
    public CurrentWeatherPanel(WeatherStation ws, DBConnection connection) {
        this.ws = ws;
        barometerPanel = new Barometer("Barometer", ws.getBarometerMin(), ws.getBarometerMax());
        historyTable = new HistoryTable(connection);
        summaryTable = new DailySummaryTable(connection);
        monthlyAverageTable = new MonthlyAveragesTable(connection);
        weatherYearStartMonth = ws.getWeatherYearStartMonth();
        temperatureBinMgr = new TemperatureBinMgr(connection);
        forecastRuleTF.setEditable(false);
        
        URL dopplerRadarUrl = null;
        try {
            dopplerRadarUrl = new URL(ws.getDopplerRadarUrl());
        }
        catch (MalformedURLException ex) {
            logger.log(Level.INFO, "Invalid URL for doppler radar images: '" + ws.getDopplerRadarUrl() + "'", ex);
        }

        radar = new DopplerRadar(connection, dopplerRadarUrl);
        
        outdoorThermometer = new Thermometer("Outdoor", ws.getThermometerMin(), ws.getThermometerMax());
        indoorThermometer = new Thermometer("Indoor", ws.getThermometerMin(), ws.getThermometerMax());

        JPanel b0 = new JPanel(new BorderLayout());
        b0.add(radar.getComponent(), BorderLayout.NORTH);
        
        Box b1 = new Box(BoxLayout.X_AXIS);
        b1.add(outdoorThermometer.getComponent());
        b1.add(indoorThermometer.getComponent());
        b0.add(b1, BorderLayout.CENTER);
        
        JPanel p1 = new JPanel(new GridLayout(2,0));
        p1.add(outdoorHumidity.getComponent());
        p1.add(barometerPanel.getComponent());
        p1.add(indoorHumidity.getComponent());
        p1.add(windPanel.getComponent());
        
        Box b2 = new Box(BoxLayout.X_AXIS);
        b2.add(b0);
        b2.add(p1);
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.add(b2, BorderLayout.CENTER);
        upperPanel.add(forecastRuleTF, BorderLayout.SOUTH);
        
        component.add(upperPanel);
        
        LocalDate thisMonth = LocalDate.now();
        LocalDate lastMonth = thisMonth.minusMonths(1);
        
        Depth thisMonthAverage;
        Depth lastMonthAverage;
        Depth yearlyAverage;
        Depth calendarYearAverageToDate;
        Depth weatherYearAverageToDate;
        
        MonthWeatherAverages averages = (MonthWeatherAverages)monthlyAverageTable.retrieveMonthlyAveragesForLocation(ws.getLocationCode());
        if (averages != null) {
            yearlyAverage = averages.calculateYearlyAverageRainfall();
            calendarYearAverageToDate = averages.calculateCalendarYearAverageRainfallToDate(thisMonth);
            weatherYearAverageToDate = averages.calculateWeatherYearAverageRainfallToDate(thisMonth, ws.getWeatherYearStartMonth());
            thisMonthAverage = averages.getAverage(thisMonth).getRainfall();
            lastMonthAverage = averages.getAverage(lastMonth).getRainfall();
            
        }
        else {
            yearlyAverage = new Depth(0.0);
            calendarYearAverageToDate = yearlyAverage;
            weatherYearAverageToDate = yearlyAverage;
            thisMonthAverage = yearlyAverage;
            lastMonthAverage = yearlyAverage;
        }
        
        rainPanel = new CurrentWeatherRainPanel(ws, thisMonthAverage, lastMonthAverage, yearlyAverage, calendarYearAverageToDate, weatherYearAverageToDate);
        
        component.add(rainPanel.getComponent());

        int dividerLocation = prefs.getInt(USER_DIVIDER_LOCATION_PROPERTY, 50);
        component.setDividerLocation(dividerLocation);
        
        component.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, (PropertyChangeEvent evt) -> {
            //
            // This is a temporary attempt to save the divider locations so they can be reset when the screen is started again
            // TODO Decide if this needs to be in all top-level containers that have splitters.
            //
            if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
                int divLocation = component.getDividerLocation();
                prefs.putInt(USER_DIVIDER_LOCATION_PROPERTY, divLocation);
                try {
                    prefs.flush();
                    logger.log(Level.FINE,"Setting property " + USER_DIVIDER_LOCATION_PROPERTY + " to {0}", divLocation);
                }
                catch (BackingStoreException e) {
                    ErrorDisplayer.getInstance().displayInformation("Failed to store size of inner panes.");
                    logger.log(Level.INFO, "Backing store of divider locations failed", e);
                }       
            }
        });
    }
    
    /**
     * Get the swing component that contains the current weather components.
     * 
     * @return The swing container
     */
    @Override
    public JComponent getComponent() {
        return component;
    }
    
    /**
     * Make this component visible or invisible.
     * 
     * @param b Whether to show or hide the component
     */
    public void setVisible(boolean b) {
        component.setVisible(b);
        component.setDividerLocation(.50);

        logger.log(Level.FINE, "Setting current weather panel visiblity to {0}", b);
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
            frameTitle = WeatherSense.getFrameTitle(component);

        // TODO check the age of the current weather. if it is more that about 20 minutes old, ignore it
        LocalDateTime now = LocalDateTime.now();
        
        forecastRuleTF.setText("Forecast: " + cw.getForecastRule());
        
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
        
        rainPanel.setRainData(ytdRain, weatherYearRain, thisMonthRain, lastMonthRain, last24HourRain, todayRain, hourRain, rainList);
        
        temperatureBinMgr.refresh();
        SummaryRecord summary = summaryTable.retrieveTodaysSummary(ws.getWindParameters(), temperatureBinMgr);
          
        //
        // Find the indoor and outdoor humidity for up to an hour ago
        //
        Humidity indoorHumidityDelta = null;
        WeatherTrend indoorHumidityTrend = WeatherTrend.STEADY;
        Humidity outdoorHumidityDelta = null;
        WeatherTrend outdoorHumidityTrend = WeatherTrend.STEADY;
        Pressure pressureDelta = null;
        WeatherTrend pressureTrend = cw.getBaroTrend();

        for (HistoricalRecord record : list) {
            if (record.getTime().isAfter(hourBegin)) {
                Pair<WeatherTrend, Humidity> hpair = processTrend(cw.getOutdoorHumidity(), record.getOutdoorHumidity(), outdoorHumidityDelta);
                if (hpair != null) {
                    outdoorHumidityTrend = hpair.getFirst();
                    outdoorHumidityDelta = hpair.getSecond();
                }

                hpair = processTrend(cw.getIndoorHumidity(), record.getIndoorHumidity(), indoorHumidityDelta);
                if (hpair != null) {
                    indoorHumidityTrend = hpair.getFirst();
                    indoorHumidityDelta = hpair.getSecond();
                }

                Pair<WeatherTrend, Pressure> ppair = processTrend(cw.getBaroPressure(), record.getBaroPressure(), pressureDelta);
                if (hpair != null) {
                    pressureDelta = ppair.getSecond();
                }
            }
        }

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
        if (summary != null) {
            outdoorThermometer.loadData(cw.getOutdoorTemperature(), summary.getMinOutdoorTemp(), summary.getMaxOutdoorTemp());
            indoorThermometer.loadData(cw.getIndoorTemperature(), summary.getMinIndoorTemp(), summary.getMaxIndoorTemp());
            windPanel.loadData(cw.getWind(), cw.getWindGust(), summary.getMaxWindSpeed(), summary.getMaxWindGust(), summary.getAvgWindSpeed(), headings);
            barometerPanel.loadData(cw.getBaroPressure(), summary.getMinBaroPressure(), summary.getMaxBaroPressure(), pressureDelta, pressureTrend);
            outdoorHumidity.loadData(cw.getOutdoorHumidity(), summary.getMinOutdoorHumidity(), summary.getMaxOutdoorHumidity(), outdoorHumidityDelta, outdoorHumidityTrend);
            indoorHumidity.loadData(cw.getIndoorHumidity(), summary.getMinIndoorHumidity(), summary.getMaxIndoorHumidity(), indoorHumidityDelta, indoorHumidityTrend);
        }
        else {
            outdoorThermometer.loadData(cw.getOutdoorTemperature(), cw.getOutdoorTemperature(), cw.getOutdoorTemperature());
            indoorThermometer.loadData(cw.getIndoorTemperature(), cw.getIndoorTemperature(), cw.getIndoorTemperature());
            windPanel.loadData(cw.getWind(), cw.getWindGust(), null, null, null, headings);
            barometerPanel.loadData(cw.getBaroPressure(), cw.getBaroPressure(), cw.getBaroPressure(), new Pressure(0.0), WeatherTrend.STEADY);
            outdoorHumidity.loadData(cw.getOutdoorHumidity(), cw.getOutdoorHumidity(), cw.getOutdoorHumidity(), new Humidity(0.0), WeatherTrend.STEADY);
            indoorHumidity.loadData(cw.getIndoorHumidity(), cw.getIndoorHumidity(), cw.getIndoorHumidity(), new Humidity(0.), WeatherTrend.STEADY);
        }

        String date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).format(cw.getTime());
        String ammendedFrameTitle = frameTitle + " " + date;
        WeatherSense.setFrameTitle(component, ammendedFrameTitle);
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