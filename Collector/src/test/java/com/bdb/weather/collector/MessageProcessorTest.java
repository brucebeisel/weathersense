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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.bdb.weather.collector.messages.SensorStationMessage;
import com.bdb.weather.common.GsonUtils;
import com.bdb.weather.common.SensorStationType;

/**
 *
 * @author Bruce
 */
public class MessageProcessorTest implements WeatherDataWriter, WeatherUploader {
    private static DBConnection dbcon;
    private static DatabaseUtilities utils;

    private static final String INITIALIZE_MESSAGE = CollectorConstants.MESSAGE_TYPE_TOKEN + "=" + CollectorConstants.INITIALIZE_TOKEN;
    private static final String CURRENT_WEATHER_JSON = "current-weather '{\"time\":\"2016-12-04 20:51:04\",\"indoorTemp\":25.0,\"indoorHumidity\":45.0,\"outdoorTemp\":18.0,\"heatIndex\":45.0,\"windChill\":5.0,\"dewPoint\":34.0,\"thsw\":7.0,\"outdoorHumidity\":99.0,\"wind\":{\"speed\":10.0,\"direction\":180.0},\"gust\":{\"speed\":20.0,\"direction\":178.0},\"windSpeed10MinAvg\":11.0,\"windSpeed2MinAvg\":12.0,\"windDir2\":45.0,\"windDir3\":50.0,\"windDir4\":55.0,\"windDir5\":60.0,\"baroPressure\":1001.0,\"baroTrend\":\"STEADY\",\"forecast\":\"SUNNY\",\"forecastRule\":\"Forecast Rule\",\"solarRadiation\":1011.0,\"rainRate\":2.3,\"rain15Minute\":0.02,\"rainHour\":0.06,\"rain24Hour\":1.2,\"rainToday\":1.0,\"rainMonth\":4.3,\"rainWeatherYear\":12.12,\"rainCalendarYear\":10.9,\"stormStart\":\"2016-12-04\",\"stormRain\":1.2,\"dayET\":1.0,\"monthET\":20.0,\"yearET\":300.0,\"soilMoistureSensorEntries\":{\"23\":{\"sensorId\":23,\"sensorType\":\"SOIL_MOISTURE\",\"measurement\":2.0}},\"temperatureSensorEntries\":{\"10\":{\"sensorId\":10,\"sensorType\":\"THERMOMETER\",\"measurement\":28.0},\"20\":{\"sensorId\":20,\"sensorType\":\"LEAF_TEMPERATURE\",\"measurement\":45.0},\"24\":{\"sensorId\":24,\"sensorType\":\"SOIL_TEMPERATURE\",\"measurement\":2.0}},\"humiditySensorEntries\":{\"11\":{\"sensorId\":11,\"sensorType\":\"HYGROMETER\",\"measurement\":99.0}},\"leafWetnessSensorEntries\":{\"22\":{\"sensorId\":22,\"sensorType\":\"LEAF_WETNESS\",\"measurement\":15.0}}}'";
    private static final String HISTORICAL_JSON = "historical '{\"time\":\"2016-12-04 22:05:30\",\"duration\":300,\"avgOutdoorTemperature\":20.0,\"highOutdoorTemperature\":22.0,\"lowOutdoorTemperature\":18.0,\"outdoorHumidity\":18.0,\"indoorTemperature\":18.0,\"indoorHumidity\":50.0,\"avgWind\":{\"speed\":10.0,\"direction\":180.0},\"highWind\":{\"speed\":12.0,\"direction\":175.0},\"gust\":{\"speed\":14.0,\"direction\":185.0},\"baroPressure\":1001.0,\"avgUvIndex\":8.8,\"avgSolarRadiation\":400.0,\"highSolarRadiation\":500.0,\"evapotranspiration\":0.2,\"rainfall\":0.1,\"highRainfallRate\":1.1,\"temperatureSensorEntries\":{\"100\":{\"sensorId\":100,\"sensorType\":\"THERMOMETER\",\"measurement\":10.0}},\"humiditySensorEntries\":{\"101\":{\"sensorId\":101,\"sensorType\":\"HYGROMETER\",\"measurement\":48.0}},\"leafWetnessSensorEntries\":{\"102\":{\"sensorId\":102,\"sensorType\":\"LEAF_WETNESS\",\"measurement\":15.0}},\"soilMoistureSensorEntries\":{\"103\":{\"sensorId\":103,\"sensorType\":\"SOIL_MOISTURE\",\"measurement\":2.0}}}'";
    private static final String SENSOR_STATION_MESSAGE_JSON = "sensor-station '{\"sensorStationList\":[{\"name\":\"ISS\",\"type\":\"INTEGRATED_SENSOR_STATION\",\"sensorStationId\":1}]}'";
    private static final String WEATHER_STATION_PARAMETERS_JSON = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wsParametersMessage><manufacturer>Davis Instruments</manufacturer><model>Vantage Pro 2</model><firmwareDate>Sep 29 2009</firmwareDate><firmwareVersion>1.90</firmwareVersion><location><latitude><value>32.9</value></latitude><longitude><value>-117.1</value></longitude><altitude><value>147828</value></altitude></location><weatherYearStartMonth>7</weatherYearStartMonth><parameters><entry><key>Archive Period</key><value>5</value></entry><entry><key>Wind Cup Size</key><value>Large</value></entry><entry><key>Rain Collector Size</key><value>0.01 inches</value></entry></parameters></wsParametersMessage>";
    private static final String SENSOR_MESSAGE_JSON = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sensorMessage><sensor><type>THERMOMETER</type><sensorId>7</sensorId></sensor><sensor><type>THERMOMETER</type><sensorId>6</sensorId></sensor><sensor><type>HYGROMETER</type><sensorId>9</sensorId></sensor><sensor><type>HYGROMETER</type><sensorId>8</sensorId></sensor><sensor><type>BAROMETER</type><sensorId>1</sensorId></sensor><sensor><type>ANEMOMETER</type><sensorId>2</sensorId></sensor><sensor><type>RAIN_COLLECTOR</type><sensorId>5</sensorId></sensor><sensor><type>SOLAR_RADIATION</type><sensorId>4</sensorId></sensor><sensor><type>THERMOMETER</type><sensorId>100</sensorId></sensor></sensorMessage>";
    private static final String SENSOR_STATION_STATUS_JSON = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sensorStationMessage><sensorStation><name>Sensor Station - 1</name><type>INTEGRATED_SENSOR_STATION</type><sensorStationId>1</sensorStationId></sensorStation><sensorStation><name>Sensor Station - 2</name><type>TEMPERATURE_ONLY</type><sensorStationId>2</sensorStationId></sensorStation></sensorStationMessage>";
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

        SensorStation ss = new SensorStation(1, SensorStationType.INTEGRATED_SENSOR_STATION, "ISS");
        SensorStationMessage ssm = new SensorStationMessage(Arrays.asList(ss));
        Gson gson = GsonUtils.gsonBuilder();
        String s = gson.toJson(ssm);
        System.out.println(s);

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
        System.out.println("Running test consumeDataString...");
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this, this);
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
    public void testConsumeDataJSON() {
        System.out.println("Running test consumeDataJSON...");
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this, this);
        try {
            String s = "current-weather '{ xsf bad JSON :: {{{}'";
            String result = instance.consumeMessages(Arrays.asList(s));
            assertNull(result);
        }
        catch(JsonParseException e) {
            System.out.println("Caught expected exception" + e.getMessage());
        }

        System.out.println("Current Weather");
        receivedCurrentWeather = false;
        instance.consumeMessages(Arrays.asList(CURRENT_WEATHER_JSON));
        assertTrue(receivedCurrentWeather);

        System.out.println("Historical");
        receivedHistorical = false;
        instance.consumeMessages(Arrays.asList(HISTORICAL_JSON));
        assertTrue(receivedHistorical);

        System.out.println("Sensor Station Message");
        receivedSensorStationMessage = false;
        instance.consumeMessages(Arrays.asList(SENSOR_STATION_MESSAGE_JSON));
        assertTrue(receivedSensorStationMessage);

        /*
        System.out.println("Sensor Station Status");
        receivedSensorStationStatus = false;
        instance.consumeMessages(Arrays.asList(SENSOR_STATION_STATUS_JSON));
        assertTrue(receivedSensorStationStatus);

        System.out.println("Weather Station Parameters");
        receivedWeatherStationParamters = false;
        instance.consumeMessages(Arrays.asList(WEATHER_STATION_PARAMETERS_JSON));
        assertTrue(receivedWeatherStationParamters);

        System.out.println("Sensor Message");
        receivedSensorMessage = false;
        instance.consumeMessages(Arrays.asList(SENSOR_MESSAGE_JSON));
        assertTrue(receivedSensorMessage);

        */
    }

    /**
     *
     */
    @Test
    public void testFindToken() {
        System.out.println("Running test findToken...");
        String s = "Hello World\nMore Stuff\n";
        MessageProcessor instance = new MessageProcessor("jdbc:derby:memory:weathersense24;create=true", "", "", this, this);
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

    @Override
    public void uploadCurrentWeather(CurrentWeather cw) {
    }
    
}
