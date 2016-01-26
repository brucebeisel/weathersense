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
package com.bdb.weather.collector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import com.bdb.util.ThreadUtils;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.collector.socket.SocketReaderThread;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.DatabaseUtilities;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;
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
public class CollectorDbWriterTest {
    private static DBConnection dbcon;
    private static List<HistoricalRecord> historyList = new ArrayList<>();
    private static DatabaseUtilities utils;
    private static HistoryTable historyTable;
    private static String dbUrl;
    
    /**
     *
     */
    public CollectorDbWriterTest() {
    }
    
    /**
     *
     * @throws IOException
     */
    @BeforeClass
    public static void setUpClass() throws IOException {
        dbUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER,
                              DatabaseConstants.DATABASE_HOST,
                              DatabaseConstants.DATABASE_PORT,
                              DatabaseConstants.DATABASE_NAME);

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");

        if (is != null)
            LogManager.getLogManager().readConfiguration(is);

        LocalDateTime c = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        double highTemp = 22.0;
        double lowTemp = 19.0;
        double avgTemp = 20;
        double pressure = 1010.0;
        float inHumidity = 65.0F;
        float outHumidity = 50.0F;
        double inTemp = 16;
        double sensorTemp = 38;
        double rain = 3.0;
        double rainRate = 5.0;
        float heading = 180.0F;
        double speed = 1.0;
        LocalDateTime ht = c.toLocalDate().atStartOfDay();
        for (int i = 0; i < 20; i++) {
            HistoricalRecord rec = new HistoricalRecord(ht);
            rec.setDuration(Duration.ofSeconds(300));
            rec.setAvgOutdoorTemperature(new Temperature(avgTemp));
            rec.setHighOutdoorTemperature(new Temperature(highTemp));
            rec.setLowOutdoorTemperature(new Temperature(lowTemp));
            rec.setBaroPressure(new Pressure(pressure));
            rec.setOutdoorHumidity(new Humidity(outHumidity));
            rec.setIndoorHumidity(new Humidity(inHumidity));
            rec.setIndoorTemperature(new Temperature(inTemp));
            rec.setRainfall(new Depth(rain));
            rec.setAvgUvIndex(5.5f);
            rec.setHighUvIndex(UvIndex.getUvIndex(6));
            rec.setAvgSolarRadiation(new SolarRadiation(1.0));
            rec.setHighSolarRadiation(new SolarRadiation(9.0));
            rec.setEvapotranspiration(new Depth(20.0));
            if (rain > 0.0)
                rain -= 1.0;
            rec.setHighRainfallRate(new Depth(rainRate));
            if (rainRate > 0.0)
                rainRate -= 1.0;
            rec.setAvgWind(new Wind(new Speed(speed), new Heading(heading)));
            rec.setHighWind(new Wind(new Speed(speed * 2), new Heading(heading - 10.0F)));
            speed += 1.0;
            heading += 4.0;
            rec.setTemperatureForSensor(100, new Temperature(sensorTemp));
//            rec.setHumidityForSensor(2, new Humidity(95.0));
//            rec.setLeafWetnessForSensor(3, new LeafWetness(3));
//            rec.setSoilMoistureForSensor(4, new SoilMoisture(5.0));
            highTemp += 1.0;
            lowTemp += 1.0;
            avgTemp += 1.0;
            pressure += .5;
            inHumidity += 1.0;
            outHumidity += 2.0;
            inTemp += 1.5;
            sensorTemp -= 1.0;
            historyList.add(rec);
            ht = ht.plusSeconds(300);
	}
        dbcon = DatabaseUtilities.connectToDerby();
        dbcon.connect();
        utils = new DatabaseUtilities(dbcon);
        utils.createDatabaseSchema();
        historyTable = new HistoryTable(dbcon);
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
        utils.dropSchema();
        dbcon.close();
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class CollectorDbWriter.
     * @throws java.lang.Exception
     */
    //@Test
    public void testInit() throws Exception {
        System.out.println("init");
        SocketReaderThread socketReader = null;
        boolean runMissingDataFinder = false;
        CollectorDbWriter instance = null;
        instance.init(socketReader, runMissingDataFinder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    @Test
    public void testAddHistoricalRecords() throws IOException, SQLException {
        System.out.println("addHistoricalRecords");
        CollectorDbWriter instance = new CollectorDbWriter(DatabaseUtilities.DERBY_URL, "", "");
        instance.init(null, false);
        List<HistoricalRecord> input = new ArrayList<>(historyList);
        instance.addHistoricalRecords(input);
        input.clear();
        ThreadUtils.sleep(2000);
        List<HistoricalRecord> list = historyTable.query();
        assertEquals(historyList.size(), list.size());
        historyTable.deleteAllRows();
    }

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    @Test
    public void testAddHistoricalRecordsTiming() throws IOException, SQLException {
        System.out.println("addHistoricalRecordsTiming");
        CollectorDbWriter instance = new CollectorDbWriter(DatabaseUtilities.DERBY_URL, "", "");
        instance.init(null, false);
        List<HistoricalRecord> input = new ArrayList<>();
        instance.addHistoricalRecords(input);
        ThreadUtils.sleep(2000);
        historyTable.deleteAllRows();
        input.clear();
        for (HistoricalRecord rec : historyList) {
            input.add(rec);
            instance.addHistoricalRecords(input);
            input.clear();
        }
        ThreadUtils.sleep(1000);
    }

    //@Test

    /**
     *
     */
    public void testHandleAddHistoricalRecords() {
        System.out.println("handleAddHistoricalRecords");
        List<HistoricalRecord> records = null;
        CollectorDbWriter instance = null;
        instance.handleAddHistoricalRecords(records);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSensorList method, of class CollectorDbWriter.
     */
    //@Test
    public void testUpdateSensorList() {
        System.out.println("updateSensorList");
        List<Sensor> sensorList = null;
        CollectorDbWriter instance = null;
        instance.updateSensorList(sensorList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleUpdateSensorList method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleUpdateSensorList() {
        System.out.println("handleUpdateSensorList");
        List<Sensor> sensorList = null;
        CollectorDbWriter instance = null;
        instance.handleUpdateSensorList(sensorList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSensorStationList method, of class CollectorDbWriter.
     */
    //@Test
    public void testUpdateSensorStationList() {
        System.out.println("updateSensorStationList");
        List<SensorStation> sensorStationList = null;
        CollectorDbWriter instance = null;
        instance.updateSensorStationList(sensorStationList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleUpdateSensorStationList method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleUpdateSensorStationList() {
        System.out.println("handleUpdateSensorStationList");
        List<SensorStation> sensorStationList = null;
        CollectorDbWriter instance = null;
        instance.handleUpdateSensorStationList(sensorStationList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSensorStationStatus method, of class CollectorDbWriter.
     */
    //@Test
    public void testUpdateSensorStationStatus() {
        System.out.println("updateSensorStationStatus");
        List<SensorStationStatus> list = null;
        CollectorDbWriter instance = null;
        instance.updateSensorStationStatus(list);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleUpdateSensorStationStatus method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleUpdateSensorStationStatus() {
        System.out.println("handleUpdateSensorStationStatus");
        List<SensorStationStatus> list = null;
        CollectorDbWriter instance = null;
        instance.handleUpdateSensorStationStatus(list);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWeatherStationParameters method, of class CollectorDbWriter.
     */
    //@Test
    public void testSetWeatherStationParameters() {
        System.out.println("setWeatherStationParameters");
        WsParametersMessage parameters = null;
        CollectorDbWriter instance = null;
        instance.setWeatherStationParameters(parameters);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleWeatherStationParametersUpdate method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleWeatherStationParametersUpdate() {
        System.out.println("handleWeatherStationParametersUpdate");
        WsParametersMessage parameters = null;
        CollectorDbWriter instance = null;
        instance.handleUpdateWeatherStationParameters(parameters);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCurrentWeather method, of class CollectorDbWriter.
     */
    //@Test
    public void testSetCurrentWeather() {
        System.out.println("setCurrentWeather");
        CurrentWeather weather = null;
        String xml = "";
        CollectorDbWriter instance = null;
        instance.setCurrentWeather(weather, xml);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleUpdateCurrentWeather method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleUpdateCurrentWeather() {
        System.out.println("handleUpdateCurrentWeather");
        CurrentWeather weather = null;
        String xml = "";
        CollectorDbWriter instance = null;
        instance.handleUpdateCurrentWeather(weather, xml);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of requestDbConnection method, of class CollectorDbWriter.
     */
    //@Test
    public void testRequestDbConnection() {
        System.out.println("requestDbConnection");
        CollectorDbWriter instance = null;
        instance.requestDbConnection();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addDopplerRadarImage method, of class CollectorDbWriter.
     */
    //@Test
    public void testAddDopplerRadarImage() {
        System.out.println("addDopplerRadarImage");
        DopplerRadarImage dri = null;
        CollectorDbWriter instance = null;
        instance.addDopplerRadarImage(dri);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleDopplerRadarUpdate method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleDopplerRadarUpdate() {
        System.out.println("handleDopplerRadarUpdate");
        DopplerRadarImage dri = null;
        CollectorDbWriter instance = null;
        instance.handleDopplerRadarUpdate(dri);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleSummarizeCommand method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleSummarizeCommand() {
        System.out.println("handleSummarizeCommand");
        LocalDate day = null;
        CollectorDbWriter instance = null;
        instance.handleSummarizeCommand(day);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleConnectCommand method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleConnectCommand() {
        System.out.println("handleConnectCommand");
        CollectorDbWriter instance = null;
        instance.handleConnectCommand();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of summarizeDay method, of class CollectorDbWriter.
     */
    //@Test
    public void testSummarizeDay() {
        System.out.println("summarizeDay");
        LocalDate day = null;
        CollectorDbWriter instance = null;
        instance.summarizeDay(day);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of handleSummarizeDay method, of class CollectorDbWriter.
     */
    //@Test
    public void testHandleSummarizeDay() {
        System.out.println("handleSummarizeDay");
        LocalDate day = null;
        CollectorDbWriter instance = null;
        instance.handleSummarizeDay(day);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class CollectorDbWriter.
     */
    //@Test
    public void testRun() {
        System.out.println("run");
        CollectorDbWriter instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatistics method, of class CollectorDbWriter.
     */
    //@Test
    public void testGetStatistics() {
        System.out.println("getStatistics");
        CollectorDbWriter instance = null;
        CollectorDbWriter.Statistics expResult = null;
        CollectorDbWriter.Statistics result = instance.getStatistics();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
