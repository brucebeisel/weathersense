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

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.LogManager;

import com.bdb.util.ThreadUtils;
import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CollectorCommand;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.CollectorCommandsTable;
import com.bdb.weather.common.db.DatabaseUtilities;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.common.messages.WsParametersMessage;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Bruce
 */
public class DataMonitorTest implements WeatherDataWriter {
    static private DBConnection dbcon;
    static private DatabaseUtilities utils;
    private DataMonitor monitor;
    private CollectorCommandsTable cctbl;
    private WeatherStationTable wsTbl;
    private boolean summarizeAllReceived;
    private boolean summarizeDateReceived;
    
    /**
     *
     */
    public DataMonitorTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");
            
            if (is != null)
                LogManager.getLogManager().readConfiguration(is);
            
            dbcon = DatabaseUtilities.connectToDerby();
            utils = new DatabaseUtilities(dbcon);
        }
        catch (IOException | SecurityException ex ) {
            System.out.println("Exception caught setting up test ");
            ex.printStackTrace();
        }
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
        utils.createWeatherStationTable();
        utils.createCollectorCommandsTable();
        utils.createSensorTable();
        cctbl = new CollectorCommandsTable(dbcon);
        wsTbl = new WeatherStationTable(dbcon);
        monitor = new DataMonitor(DatabaseUtilities.DERBY_URL, "", "", null, this);
        monitor.init(100);
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
        monitor.shutdown();
        monitor = null;
        cctbl = null;
        wsTbl = null;
        utils.dropWeatherStationTable();
        utils.dropCollectorCommandsTable();
        utils.dropSensorTable();
    }

    /**
     * Test of init method, of class DataMonitor.
     */
    @Test
    public void testBadCommand() {
        System.out.println("badCommand");
        WeatherStation ws = new WeatherStation();
        wsTbl.updateRow(ws);
        cctbl.addCommand("Test Command");
        ThreadUtils.sleep(1000);
        assertEquals(0, cctbl.query().size());
    }

    /**
     *
     */
    @Test
    public void testCommand() {
        System.out.println("command");
        summarizeAllReceived = false;
        WeatherStation ws = new WeatherStation();
        wsTbl.updateRow(ws);
        cctbl.addCommand(CollectorCommand.SUMMARIZE_COMMAND + " " + CollectorCommand.SUMMARIZE_ALL);
        ThreadUtils.sleep(1000);
        assertTrue(summarizeAllReceived);
        List<CollectorCommand> list = cctbl.query(" where state='COMPLETE'");
        assertEquals(1, list.size());
    }

    /**
     *
     */
    @Test
    public void testMultipleCommands() {
        System.out.println("multipleCommands");
        summarizeAllReceived = false;
        summarizeDateReceived = false;
        WeatherStation ws = new WeatherStation();
        wsTbl.updateRow(ws);
        cctbl.addCommand(CollectorCommand.SUMMARIZE_COMMAND + " " + CollectorCommand.SUMMARIZE_ALL);
        String cmd = CollectorCommand.SUMMARIZE_COMMAND + " " + DateTimeFormatter.ofPattern(CollectorCommand.COMMAND_DATE_FORMAT).format(LocalDate.now());
        cctbl.addCommand(cmd);
        ThreadUtils.sleep(1000);
        assertTrue(summarizeAllReceived && summarizeDateReceived);
        List<CollectorCommand> list = cctbl.query(" where state='COMPLETE'");
        assertEquals(2, list.size());
    }

    @Override
    public void addHistoricalRecords(List<HistoricalRecord> records) throws CollectException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSensorList(List<Sensor> sensorList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSensorStationList(List<SensorStation> sensorStationList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSensorStationStatus(List<SensorStationStatus> sensorStationStatusList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCurrentWeather(CurrentWeather weather, String xml) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void summarizeDay(LocalDate day) {
        if (day == null)
            summarizeAllReceived = true;
        else {
            summarizeDateReceived = day.equals(LocalDate.now());
        }
    }
    
}
