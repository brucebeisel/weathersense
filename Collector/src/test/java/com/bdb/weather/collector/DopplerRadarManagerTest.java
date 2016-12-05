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
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.LogManager;

import com.bdb.util.ThreadUtils;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
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
public class DopplerRadarManagerTest implements WeatherDataWriter {
    private int radarImageCount;
    
    /**
     *
     * @throws IOException
     */
    public DopplerRadarManagerTest() throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("collector_logging.properties");
        
        if (is != null)
            LogManager.getLogManager().readConfiguration(is);
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
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
     * Test of init method, of class DopplerRadarManager.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        radarImageCount = 0;
        URL url = DopplerRadarManagerTest.class.getResource("/foobar");
        DopplerRadarManager instance = new DopplerRadarManager(url, this, 500);
        ThreadUtils.sleep(2000);
        assertEquals(0, radarImageCount);

        radarImageCount = 0;
        url = DopplerRadarManagerTest.class.getResource("/collector_logging.properties");
        instance.setDopplerUrl(url);
        ThreadUtils.sleep(2000);
        assertEquals(0, radarImageCount);

        radarImageCount = 0;
        url = DopplerRadarManagerTest.class.getResource("/radar_image_1.jpg");
        instance.setDopplerUrl(url);
        ThreadUtils.sleep(2000);
        assertEquals(1, radarImageCount);

        url = DopplerRadarManagerTest.class.getResource("/radar_image_2.jpg");
        instance.setDopplerUrl(url);
        ThreadUtils.sleep(2000);
        assertEquals(2, radarImageCount);

        url = DopplerRadarManagerTest.class.getResource("/radar_image_1.jpg");
        instance.setDopplerUrl(url);
        ThreadUtils.sleep(2000);
        assertEquals(2, radarImageCount);

        instance.terminate();
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
    public void setCurrentWeather(CurrentWeather weather, String json) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void requestDbConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addDopplerRadarImage(DopplerRadarImage dri) {
        radarImageCount++;
    }

    @Override
    public void setWeatherStationParameters(WsParametersMessage parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void summarizeDay(LocalDate day) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
