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

import java.util.Map;
import java.util.TreeMap;

import javafx.fxml.FXMLLoader;

import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorManager;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.display.CurrentWeatherProcessor;
import com.bdb.weather.display.DisplayConstants;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.WeatherSense;

/**
 *
 * @author Bruce
 */
public class CurrentWeatherForm extends GridPane implements CurrentWeatherProcessor {
    private String frameTitle = null;
    @FXML private GridPane temperaturePane;
    @FXML private GridPane humidityPane;
    @FXML private TextField indoorTemperature;
    @FXML private TextField outdoorTemperature;
    @FXML private TextField heatIndex;
    @FXML private TextField dewPoint;
    @FXML private TextField windChill;
    @FXML private TextField thsw;
    @FXML private TextField indoorHumidity;
    @FXML private TextField outdoorHumidity;
    @FXML private TextField windSpeed;
    @FXML private TextField windDirection;
    @FXML private TextField windGust;
    @FXML private TextField windGustDirection;
    @FXML private TextField windDirection2;
    @FXML private TextField windDirection3;
    @FXML private TextField windDirection4;
    @FXML private TextField windDirection5;
    @FXML private TextField windAvg2Minute;
    @FXML private TextField windAvg10Minute;
    @FXML private TextField rainRate;
    @FXML private TextField rain15Minute;
    @FXML private TextField rainHour;
    @FXML private TextField rain24Hour;
    @FXML private TextField rainToday;
    @FXML private TextField rainMonth;
    @FXML private TextField rainWeatherYear;
    @FXML private TextField rainCalendarYear;
    @FXML private TextField stormStart;
    @FXML private TextField stormRain;
    @FXML private TextField barometer;
    @FXML private TextField barometerTrend;
    @FXML private TextField forecast;
    @FXML private TextField forecastRule;
    @FXML private TextField uvIndex;
    @FXML private TextField solarRadiation;
    @FXML private TextField dayET;
    @FXML private TextField monthET;
    @FXML private TextField yearET;

    private final Map<Integer,TextField> sensorTemperatures = new TreeMap<>();
    private final Map<Integer,TextField> sensorHumidities = new TreeMap<>();

    private SensorManager sensorManager;

    @SuppressWarnings("LeakingThisInConstructor")
    public CurrentWeatherForm(WeatherStation ws) {
        try {
            sensorManager = ws.getSensorManager();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CurrentWeatherForm.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            createExtraSensorFields();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void createExtraSensorFields() {
        int lastRow = GridPane.getRowIndex(indoorTemperature);
        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.THERMOMETER)) {
            Label label = new Label(sensor.getName() + ":");
            TextField value = new TextField(DisplayConstants.UNKNOWN_VALUE_STRING);
            value.setEditable(false);
            sensorTemperatures.put(sensor.getSensorId(), value);
            GridPane.setHalignment(label, HPos.RIGHT);
            temperaturePane.add(label, 0, lastRow + 1);
            temperaturePane.add(value, 1, lastRow + 1);
        }

        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.HYGROMETER)) {
            Label label = new Label(sensor.getName() + ":");
            TextField value = new TextField(DisplayConstants.UNKNOWN_VALUE_STRING);
            value.setEditable(false);
            sensorHumidities.put(sensor.getSensorId(), value);
            GridPane.setHalignment(label, HPos.RIGHT);
            humidityPane.add(label, 0, lastRow + 1);
            humidityPane.add(value, 1, lastRow + 1);
        }
    }
        
    @Override
    public void updateCurrentWeather(CurrentWeather cw) {
        if (frameTitle == null)
            frameTitle = WeatherSense.getStageTitle(this);

        String date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).format(cw.getTime());
        String ammendedFrameTitle = frameTitle + " " + date;
        WeatherSense.setStageTitle(this, ammendedFrameTitle);

        indoorTemperature.setText(cw.getIndoorTemperature().toString());
        outdoorTemperature.setText(cw.getOutdoorTemperature().toString());
        heatIndex.setText(cw.getHeatIndex().toString());
        windChill.setText(cw.getWindChill().toString());
        dewPoint.setText(cw.getDewPoint().toString());
        if (cw.getThsw() == null)
            thsw.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        else
            thsw.setText(cw.getThsw().toString());

        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.THERMOMETER)) {
            Temperature t = cw.getTemperatureForSensor(sensor.getSensorId());
            TextField value = sensorTemperatures.get(sensor.getSensorId());
            if (t != null)
                value.setText(t.toString());
            else
                value.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }

        rainRate.setText(cw.getRainRate().toString());
        rain15Minute.setText(cw.getRain15Minute().toString());
        rainHour.setText(cw.getRainHour().toString());
        rainToday.setText(cw.getRainToday().toString());
        rain24Hour.setText(cw.getRain24Hour().toString());
        rainMonth.setText(cw.getRainMonth().toString());

        if (cw.getRainCalendarYear() != null)
            rainCalendarYear.setText(cw.getRainCalendarYear().toString());

        if (cw.getRainWeatherYear() != null)
            rainWeatherYear.setText(cw.getRainWeatherYear().toString());

        if (cw.getStormStart() != null)
            stormStart.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(cw.getStormStart()));
        else
            stormStart.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getStormRain() != null && cw.getStormRain().get() > 0.0)
            stormRain.setText(cw.getStormRain().toString());
        else
            stormRain.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        outdoorHumidity.setText(cw.getOutdoorHumidity().toString());
        indoorHumidity.setText(cw.getIndoorHumidity().toString());

        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.HYGROMETER)) {
            Humidity h = cw.getHumidityForSensor(sensor.getSensorId());
            TextField label = sensorHumidities.get(sensor.getSensorId());
            if (h != null)
                label.setText(h.toString());
            else
                label.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }

        windSpeed.setText(cw.getWind().getSpeed().toString());
        windDirection.setText(cw.getWind().getDirection().toString());
        windGust.setText(cw.getWindGust().getSpeed().toString());
        windGustDirection.setText(cw.getWindGust().getDirection().toString());

        if (cw.getWindDir2() != null)
            windDirection2.setText(cw.getWindDir2().toString());
        else
            windDirection2.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getWindDir3() != null)
            windDirection3.setText(cw.getWindDir3().toString());
        else
            windDirection3.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getWindDir4() != null)
            windDirection4.setText(cw.getWindDir4().toString());
        else
            windDirection4.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getWindDir5() != null)
            windDirection5.setText(cw.getWindDir5().toString());
        else
            windDirection5.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        windAvg2Minute.setText(cw.getWindSpeed2MinAvg().toString());
        windAvg10Minute.setText(cw.getWindSpeed10MinAvg().toString());

        if (cw.getUvIndex() != null)
            uvIndex.setText(cw.getUvIndex().toString());
        else
            uvIndex.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getSolarRadiation() != null)
            solarRadiation.setText(cw.getSolarRadiation().toString());
        else
            solarRadiation.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getDayET() != null)
            dayET.setText(cw.getDayET().toString());
        else
            dayET.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getMonthET() != null)
            monthET.setText(cw.getMonthET().toString());
        else
            monthET.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        if (cw.getYearET() != null)
            yearET.setText(cw.getYearET().toString());
        else
            yearET.setText(DisplayConstants.UNKNOWN_VALUE_STRING);

        barometer.setText(cw.getBaroPressure().toString());
        barometerTrend.setText(cw.getBaroTrend().toString());
        forecast.setText(cw.getForecast().toString());
        forecastRule.setText(cw.getForecastRule());
    }
}