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
package com.bdb.weather.display.currenttable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import com.bdb.util.Pair;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorManager;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.CurrentWeatherProcessor;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.WeatherSense;
import java.io.IOException;

/**
 *
 * @author Bruce
 */
public class CurrentWeatherText extends BorderPane implements ComponentContainer, CurrentWeatherProcessor {
    private String frameTitle = null;
    private final JTextField indoorTemperature = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField outdoorTemperature = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField heatIndex = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField dewPoint = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windChill = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField thsw = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final Map<Integer,JTextField> sensorTemperatures = new TreeMap<>();

    private final JTextField indoorHumidity = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField outdoorHumidity = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final Map<Integer,JTextField> sensorHumidities = new TreeMap<>();

    private final JTextField windSpeed = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windDiection = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField gust = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField gustDirection = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windDirection2 = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windDirection3 = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windDirection4 = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField windDirection5 = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField wind2MinAvg = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField wind10MinAvg = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);

    private final JTextField barometer = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField barometerTrend = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField forecast = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField forecastRule = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);

    private final JTextField uvIndex = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField solarRadiation = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField dayET = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField monthET = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField yearET = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);

    private final JTextField rainRate = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rain15Minute = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rainHour = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rain24Hour = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rainToday = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rainMonth = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rainWeatherYear = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField rainCalendarYear = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField stormStart = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final JTextField stormRain = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
    private final SensorManager sensorManager;

    public CurrentWeatherText(WeatherStation ws) {
        sensorManager = ws.getSensorManager();
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("custom_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        List<Pair<String,? extends JComponent>> rainComponents = new ArrayList<>();
        rainComponents.add(new Pair<>("Rain Rate: ", rainRate));
        rainComponents.add(new Pair<>("15 Minute Rain: ", rain15Minute));
        rainComponents.add(new Pair<>("1 Hour Rain: ", rainHour));
        rainComponents.add(new Pair<>("24 Hour Rain: ", rain24Hour));
        rainComponents.add(new Pair<>("Today Rain: ", rainToday));
        rainComponents.add(new Pair<>("Month Rain: ", rainMonth));
        rainComponents.add(new Pair<>("Weather Year Rain: ", rainWeatherYear));
        rainComponents.add(new Pair<>("Calendar Year Rain: ", rainCalendarYear));
        rainComponents.add(new Pair<>("Storm Start: ", stormStart));
        rainComponents.add(new Pair<>("Storm Rain: ", stormRain));

        List<Pair<String,? extends JComponent>> temperatureComponents = new ArrayList<>();
        temperatureComponents.add(new Pair<>("Outdoor: ", outdoorTemperature));
        temperatureComponents.add(new Pair<>("Dew Point: ", dewPoint));
        temperatureComponents.add(new Pair<>("Heat Index: ", heatIndex));
        temperatureComponents.add(new Pair<>("Wind Chill: ", windChill));
        temperatureComponents.add(new Pair<>("THSW: ", thsw));
        temperatureComponents.add(new Pair<>("Indoor: ", indoorTemperature));
        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.THERMOMETER)) {
            JTextField label = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING); sensorTemperatures.put(sensor.getSensorId(), label);
            temperatureComponents.add(new Pair<>(sensor.getName() + ": ", label));
        }

        List<Pair<String,? extends JComponent>> humidityComponents = new ArrayList<>();
        humidityComponents.add(new Pair<>("Outdoor: ", outdoorHumidity));
        humidityComponents.add(new Pair<>("Indoor: ", indoorHumidity));
        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.HYGROMETER)) {
            JTextField label = new JTextField(DisplayConstants.UNKNOWN_VALUE_STRING);
            sensorHumidities.put(sensor.getSensorId(), label);
            humidityComponents.add(new Pair<>(sensor.getName() + ": ", label));
        }

        List<Pair<String,? extends JComponent>> windComponents = new ArrayList<>();
        windComponents.add(new Pair<>("Speed: ", windSpeed));
        windComponents.add(new Pair<>("Direction: ", windDiection));
        windComponents.add(new Pair<>("Gust: ", gust));
        windComponents.add(new Pair<>("Gust Dir: ", gustDirection));
        windComponents.add(new Pair<>("Dir 2: ", windDirection2));
        windComponents.add(new Pair<>("Dir 3: ", windDirection3));
        windComponents.add(new Pair<>("Dir 4: ", windDirection4));
        windComponents.add(new Pair<>("Dir 5: ", windDirection5));
        windComponents.add(new Pair<>("2 Minute Avg: ", wind2MinAvg));
        windComponents.add(new Pair<>("10 Minute Avg: ", wind10MinAvg));

        List<Pair<String,? extends JComponent>> sunComponents = new ArrayList<>();
        sunComponents.add(new Pair<>("UV Index: ", uvIndex));
        sunComponents.add(new Pair<>("Solar Radiation: ", solarRadiation));
        sunComponents.add(new Pair<>("Day ET: ", dayET));
        sunComponents.add(new Pair<>("Month ET: ", monthET));
        sunComponents.add(new Pair<>("Year ET: ", yearET));

        List<Pair<String,? extends JComponent>> conditionsComponents = new ArrayList<>();
        conditionsComponents.add(new Pair<>("Barometer: ", barometer));
        conditionsComponents.add(new Pair<>("Barometer Trend: ", barometerTrend));
        conditionsComponents.add(new Pair<>("Forecast: ", forecast));
        conditionsComponents.add(new Pair<>("Forecast Rule: ", forecastRule));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel temperaturePanel = new JPanel(new GridBagLayout());
        temperaturePanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Temperatures"));
        JPanel humidityPanel = new JPanel(new GridBagLayout());
        humidityPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Humidities"));
        JPanel windPanel = new JPanel(new GridBagLayout());
        windPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Wind"));
        JPanel rainPanel = new JPanel(new GridBagLayout());
        rainPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Rain"));
        JPanel sunPanel = new JPanel(new GridBagLayout());
        sunPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Sun"));
        JPanel conditionsPanel = new JPanel(new GridBagLayout());
        conditionsPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Conditions"));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panel.add(temperaturePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        panel.add(humidityPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(sunPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(rainPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        panel.add(windPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(conditionsPanel, gbc);

        addFields(rainPanel, rainComponents);
        addFields(temperaturePanel, temperatureComponents);
        addFields(humidityPanel, humidityComponents);
        addFields(windPanel, windComponents);
        addFields(sunPanel, sunComponents);
        addFields(conditionsPanel, conditionsComponents);

        forecastRule.setColumns(60);
        forecast.setColumns(30);
    }

    @Override
    public Node getComponent() {
        return this;
    }

    private void addFields(JPanel panel, List<Pair<String,? extends JComponent>> components) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.PAGE_START;

        for (Pair<String,? extends JComponent> component : components) {
            ((JTextField)component.second).setEditable(false);
            ((JTextField)component.second).setColumns(8);
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel label = new JLabel(component.first);
            label.setForeground(Color.BLUE);
            Font font = label.getFont();
            label.setFont(font.deriveFont(Font.BOLD));
            panel.add(label, gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(component.second, gbc);
            gbc.gridy++;
        }
    }

    @Override
    public void updateCurrentWeather(CurrentWeather cw) {
        if (frameTitle == null)
            frameTitle = WeatherSense.getFrameTitle(panel);

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

        outdoorTemperature.setText(cw.getOutdoorTemperature().toString());
        heatIndex.setText(cw.getHeatIndex().toString());
        windChill.setText(cw.getWindChill().toString());
        dewPoint.setText(cw.getDewPoint().toString());
        if (cw.getThsw() == null)
            thsw.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        else
            thsw.setText(cw.getThsw().toString());

        indoorTemperature.setText(cw.getIndoorTemperature().toString());

        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.THERMOMETER)) {
            Temperature t = cw.getTemperatureForSensor(sensor.getSensorId());
            JTextField label = sensorTemperatures.get(sensor.getSensorId());
            if (t != null)
                label.setText(t.toString());
            else
                label.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }

        outdoorHumidity.setText(cw.getOutdoorHumidity().toString());
        indoorHumidity.setText(cw.getIndoorHumidity().toString());
        for (Sensor sensor : sensorManager.getExtraSensors(SensorType.HYGROMETER)) {
            Humidity h = cw.getHumidityForSensor(sensor.getSensorId());
            JTextField label = sensorHumidities.get(sensor.getSensorId());
            if (h != null)
                label.setText(h.toString());
            else
                label.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }

        windSpeed.setText(cw.getWind().getSpeed().toString());
        windDiection.setText(cw.getWind().getDirection().toString());
        gust.setText(cw.getWindGust().getSpeed().toString());
        gustDirection.setText(cw.getWindGust().getDirection().toString());

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

        wind2MinAvg.setText(cw.getWindSpeed2MinAvg().toString());
        wind10MinAvg.setText(cw.getWindSpeed10MinAvg().toString());

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

        String date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).format(cw.getTime());
        String ammendedFrameTitle = frameTitle + " " + date;
        WeatherSense.setFrameTitle(panel, ammendedFrameTitle);
    }
}
