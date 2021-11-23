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
package com.bdb.weather.collector;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.db.DatabaseUtilities;
import com.bdb.weather.common.messages.WsParametersMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bruce
 */
public class MessageProcessorTest implements WeatherDataWriter {
    private static DBConnection dbcon;
    private static DatabaseUtilities utils;

    private static final String INITIALIZE_MESSAGE = CollectorConstants.MESSAGE_TYPE_TOKEN + "=" + CollectorConstants.INITIALIZE_TOKEN;
    private static final String CURRENT_WEATHER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><currentWeather><time>2014-05-17 23:10:25</time><indoorTemperature><value>24.88889</value></indoorTemperature><indoorHumidity><value>51</value></indoorHumidity><outdoorTemperature><value>17.27778</value></outdoorTemperature><outdoorHumidity><value>83</value></outdoorHumidity><dewPoint><value>14.44444</value></dewPoint><windChill><value>17.22222</value></windChill><heatIndex><value>17.22222</value></heatIndex><thsw><value>17.22222</value></thsw><wind><speed><value>0</value></speed><direction><value>180</value></direction></wind><windGust><speed><value>1.341124</value></speed><direction><value>180</value></direction></windGust><windSpeed10MinAvg><value>0.4470413</value></windSpeed10MinAvg><windSpeed2MinAvg><value>0.4470413</value></windSpeed2MinAvg><windDir2><value>180</value></windDir2><windDir3><value>202</value></windDir3><windDir4><value>225</value></windDir4><baroPressure><value>1011.412</value></baroPressure><baroTrend>RISING_SLOWLY</baroTrend><rainRate><value>0</value></rainRate><rainToday><value>0</value></rainToday><rain15Minute><value>0</value></rain15Minute><rainHour><value>0</value></rainHour><rain24Hour><value>0</value></rain24Hour><rainMonth><value>0</value></rainMonth><rainWeatherYear><value>147.32</value></rainWeatherYear><solarRadiation><value>0</value></solarRadiation><dayET><value>6.223</value></dayET><monthET><value>100.33</value></monthET><yearET><value>446.532</value></yearET><forecastRule>Increasing clouds with little temperature change.</forecastRule><forecast>PARTLY_CLOUDY</forecast><temperatureSensorEntries><entry><key>100</key><value><sensorId>100</sensorId><sensorType>THERMOMETER</sensorType><measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\"><value>25.55556</value></measurement></value></entry></temperatureSensorEntries><humiditySensorEntries></humiditySensorEntries><soilMoistureSensorEntries></soilMoistureSensorEntries><leafWetnessSensorEntries></leafWetnessSensorEntries></currentWeather>";
    private static final String HISTORICAL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><historicalRecord><time>2014-05-17 23:15:00</time><duration>300</duration><avgOutdoorTemperature><value>17.22222</value></avgOutdoorTemperature><highOutdoorTemperature><value>17.27778</value></highOutdoorTemperature><lowOutdoorTemperature><value>17.16667</value></lowOutdoorTemperature><rainfall><value>0</value></rainfall><highRainfallRate><value>0</value></highRainfallRate><baroPressure><value>1011.378</value></baroPressure><avgSolarRadiation><value>0</value></avgSolarRadiation><indoorTemperature><value>24.88889</value></indoorTemperature><indoorHumidity><value>51</value></indoorHumidity><outdoorHumidity><value>83</value></outdoorHumidity><avgWind><speed><value>0</value></speed><direction><value>180</value></direction></avgWind><highWind><speed><value>0.8940825</value></speed><direction><value>180</value></direction></highWind><windGust><speed><value>1.341124</value></speed><direction><value>180</value></direction></windGust><highSolarRadiation><value>0</value></highSolarRadiation><leafWetnessSensorEntries></leafWetnessSensorEntries><humiditySensorEntries></humiditySensorEntries><temperatureSensorEntries><entry><key>100</key><value><sensorId>100</sensorId><sensorType>THERMOMETER</sensorType><measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\"><value>25.55556</value></measurement></value></entry></temperatureSensorEntries><soilMoistureSensorEntries></soilMoistureSensorEntries></historicalRecord>";
    private static final String SENSOR_STATION_STATUS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sensorStationStatusMessage><sensorStationStatus><time>2014-05-31 20:25:00</time><sensorStationId>1</sensorStationId><batteryOk>true</batteryOk><linkQuality>98</linkQuality></sensorStationStatus><sensorStationStatus><time>2014-05-31T20:25:00.0000000-07:00</time><sensorStationId>2</sensorStationId><batteryOk>true</batteryOk></sensorStationStatus></sensorStationStatusMessage>";
    private static final String WEATHER_STATION_PARAMETERS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wsParametersMessage><manufacturer>Davis Instruments</manufacturer><model>Vantage Pro 2</model><firmwareDate>Sep 29 2009</firmwareDate><firmwareVersion>1.90</firmwareVersion><location><latitude><value>32.9</value></latitude><longitude><value>-117.1</value></longitude><altitude><value>147828</value></altitude></location><weatherYearStartMonth>7</weatherYearStartMonth><parameters><entry><key>Archive Period</key><value>5</value></entry><entry><key>Wind Cup Size</key><value>Large</value></entry><entry><key>Rain Collector Size</key><value>0.01 inches</value></entry></parameters></wsParametersMessage>";
    private static final String SENSOR_MESSAGE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sensorMessage><sensor><type>THERMOMETER</type><sensorId>7</sensorId></sensor><sensor><type>THERMOMETER</type><sensorId>6</sensorId></sensor><sensor><type>HYGROMETER</type><sensorId>9</sensorId></sensor><sensor><type>HYGROMETER</type><sensorId>8</sensorId></sensor><sensor><type>BAROMETER</type><sensorId>1</sensorId></sensor><sensor><type>ANEMOMETER</type><sensorId>2</sensorId></sensor><sensor><type>RAIN_COLLECTOR</type><sensorId>5</sensorId></sensor><sensor><type>SOLAR_RADIATION</type><sensorId>4</sensorId></sensor><sensor><type>THERMOMETER</type><sensorId>100</sensorId></sensor></sensorMessage>";
    private static final String SENSOR_STATION_MESSAGE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sensorStationMessage><sensorStation><name>Sensor Station - 1</name><type>INTEGRATED_SENSOR_STATION</type><sensorStationId>1</sensorStationId></sensorStation><sensorStation><name>Sensor Station - 2</name><type>TEMPERATURE_ONLY</type><sensorStationId>2</sensorStationId></sensorStation></sensorStationMessage>";
    private boolean receivedCurrentWeather;
    private boolean receivedHistorical;
    private boolean receivedSensorStationStatus;
    private boolean receivedWeatherStationParamters;
    private boolean receivedSensorMessage;
    private boolean receivedSensorStationMessage;

    /**
     *
     */
    public MessageProcessorTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        dbcon = DatabaseUtilities.connectToDerby();
        utils = new DatabaseUtilities(dbcon);
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
        dbcon.close();
    }
    
    /**
     *
     * @throws SQLException
     */
    @Before
    public void setUp() throws SQLException {
        utils.createHistoryTables();
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
        utils.dropHistoryTables();
    }

    /**
     *
     * @throws JAXBException
     */
    @Test
    public void testConsumeDataString() throws JAXBException {
        System.out.println("consumeDataString");
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this);
        LocalDateTime expectedTime = LocalDate.now().minusYears(1).atStartOfDay();
        String expResult = DateTimeFormatter.ofPattern(CollectorConstants.NEWEST_RECORD_DATE_FORMAT).format(expectedTime);
        String result = instance.consumeMessages(Arrays.asList(INITIALIZE_MESSAGE));
        assertEquals(expResult, result);

        result = instance.consumeMessages(Arrays.asList("foo bar"));
        assertNull(result);
    }

    /**
     *
     * @throws JAXBException
     */
    @Test
    public void testConsumeDataXML() throws JAXBException {
        System.out.println("consumeDataXML");
        String s = "<?xml bad XML";
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this);
        String result = instance.consumeMessages(Arrays.asList(s));
        assertNull(result);

        System.out.println("Current Weather");
        receivedCurrentWeather = false;
        instance.consumeMessages(Arrays.asList(CURRENT_WEATHER_XML));
        assertTrue(receivedCurrentWeather);

        System.out.println("Historical");
        receivedHistorical = false;
        instance.consumeMessages(Arrays.asList(HISTORICAL_XML));
        assertTrue(receivedHistorical);

        System.out.println("Sensor Station Status");
        receivedSensorStationStatus = false;
        instance.consumeMessages(Arrays.asList(SENSOR_STATION_STATUS_XML));
        assertTrue(receivedSensorStationStatus);

        System.out.println("Weather Station Parameters");
        receivedWeatherStationParamters = false;
        instance.consumeMessages(Arrays.asList(WEATHER_STATION_PARAMETERS_XML));
        assertTrue(receivedWeatherStationParamters);

        System.out.println("Sensor Message");
        receivedSensorMessage = false;
        instance.consumeMessages(Arrays.asList(SENSOR_MESSAGE_XML));
        assertTrue(receivedSensorMessage);

        System.out.println("Sensor Station Message");
        receivedSensorStationMessage = false;
        instance.consumeMessages(Arrays.asList(SENSOR_STATION_MESSAGE_XML));
        assertTrue(receivedSensorStationMessage);
    }

    /**
     *
     * @throws JAXBException
     */
    @Test
    public void testFindToken() throws JAXBException {
        System.out.println("findToken");
        String s = "Hello World\nMore Stuff\n";
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this);
        int expResult = 11;
        int result = instance.findToken(s);
        assertEquals(expResult, result);

        result = instance.findToken("No terminator in this string");
        assertEquals(MessageProcessor.NO_TOKEN_FOUND, result);
    }

    @Override
    public void addHistoricalRecords(List<HistoricalRecord> records) throws CollectException {
        receivedHistorical = true;
    }

    @Override
    public void updateSensorList(List<Sensor> sensorList) {
        receivedSensorMessage = true;
    }

    @Override
    public void updateSensorStationList(List<SensorStation> sensorStationList) {
        receivedSensorStationMessage = true;
    }

    @Override
    public void updateSensorStationStatus(List<SensorStationStatus> sensorStationStatusList) {
        receivedSensorStationStatus = true;
    }

    @Override
    public void setCurrentWeather(CurrentWeather weather, String xml) {
        receivedCurrentWeather = true;
    }

    @Override
    public void requestDbConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addDopplerRadarImage(DopplerRadarImage dri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWeatherStationParameters(WsParametersMessage parameters) {
        receivedWeatherStationParamters = true;
    }

    @Override
    public void summarizeDay(LocalDate day) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
