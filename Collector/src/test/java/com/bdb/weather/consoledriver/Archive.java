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
package com.bdb.weather.consoledriver;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;

/**
 *
 * @author Bruce
 */
public class Archive {
    private final List<HistoricalRecord> archive = new ArrayList<>(1000);
    private int interval;
    private LocalDateTime startTime;

    /**
     *
     */
    public Archive() {
    }
    
    /**
     *
     * @param interval
     * @param start
     */
    public void setArchiveConfiguration(int interval, LocalDateTime start) {
        this.interval = interval;
        this.startTime = start;
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime newestRecordTime() {
        if (archive.isEmpty())
            return null;
        
        return archive.get(archive.size() - 1).getTime();
    }

    /**
     *
     */
    public static void testHumidity() {
        double MAX_DEGREES = 2.0 * Math.PI;
        double incrementDegrees = (double)300 / 86400.0 * MAX_DEGREES;
        double humiditySeed = 60.0;
        int humidityMaxHour = 4;
        int humidityMinHour = 16;
        double humidityMax = 90.0;
        double humidityMin = 50.0;
        double startDegrees = 90.0 - ((double)humidityMaxHour / 24.0 * MAX_DEGREES);
        double degrees = startDegrees;
        int time = 0;

        for (int i = 0; i < 288; i++) {
            double c = Math.cos(degrees);
            double h = humidityMin + ((humidityMax - humidityMin) * c);
            System.out.println(String.format("%5d %.1f", time, h));
            degrees += incrementDegrees;
            time += 300;
        }
    }
    
    private void generateDayData(LocalDateTime day, Temperature avgLow, Temperature avgHigh) {
        LocalDateTime time = day.toLocalDate().atStartOfDay();
        int date = time.getDayOfMonth();
        archive.clear();
        while (time.getDayOfMonth() == date) {
            HistoricalRecord record = new HistoricalRecord(time);
            record.setDuration(Duration.ofSeconds(interval));

            record.setLowOutdoorTemperature(new Temperature(20.0));
            record.setAvgOutdoorTemperature(new Temperature(25.0));
            record.setHighOutdoorTemperature(new Temperature(30.0));

            double incrementDegrees = (double)interval / 86400.0 * 360.0;
            double humiditySeed = 60.0;
            int humidityMaxHour = 4;
            int humidityMinHour = 16;
            double humidityMax = 90.0;
            double humidityMin = 50.0;
            double startDegrees = 90.0 - ((double)humidityMaxHour / 24.0 * 360.0);
            double c = Math.cos(startDegrees);
            record.setOutdoorHumidity(new Humidity(75.0));
            record.setIndoorHumidity(new Humidity(55.0));

            record.setIndoorTemperature(new Temperature(28.0));
            record.setBaroPressure(new Pressure(1000.0));

            record.setAvgSolarRadiation(new SolarRadiation(12.0));
            record.setHighSolarRadiation(new SolarRadiation(100.0));
            record.setAvgUvIndex(5.0F);
            record.setHighUvIndex(new UvIndex(10));
            record.setEvapotranspiration(new Depth(50.0));

            record.setAvgWind(new Wind(new Speed(4.0), new Heading(90.0F)));
            record.setHighWind(new Wind(new Speed(8.0), new Heading(180.0F)));
            record.setWindGust(new Wind(new Speed(20.0), new Heading(180.0F)));

            record.setHighRainfallRate(new Depth(2.0));
            record.setRainfall(new Depth(.5));

            record.setTemperatureForSensor(Sensor.THERMOMETER_BASE_SENSOR_ID, new Temperature(1.0));
            record.setHumidityForSensor(Sensor.HYGROMETER_BASE_SENSOR_ID, new Humidity(50.0));
            record.setLeafWetnessForSensor(Sensor.LEAF_WETNESS_BASE_SENSOR_ID, new LeafWetness(5));
            record.setSoilMoistureForSensor(Sensor.SOIL_MOISTURE_BASE_SENSOR_ID, new SoilMoisture(.5));

            archive.add(record);
            time = time.plusSeconds(interval);
        }
    }

    private LocalDateTime nextRecordTime() {
        if (archive.isEmpty())
            return null;
        
        LocalDateTime next = newestRecordTime();
        next = next.plusSeconds(interval);
        return next;
    }
    
    /**
     *
     * @param currentTime
     */
    public void initialize(LocalDateTime currentTime) {
        archive.clear();
        LocalDateTime recordTime = startTime;
        while (recordTime.isBefore(currentTime)) {
            HistoricalRecord rec = new HistoricalRecord(recordTime);
            archive.add(rec);
            recordTime = recordTime.plusSeconds(interval);
        }
    }
    
    /**
     *
     * @param cw
     */
    public void currentWeather(CurrentWeather cw) {
        LocalDateTime next = nextRecordTime();
        if (cw.getTime().isAfter(next)) {
            HistoricalRecord rec = new HistoricalRecord(next);
            // copy current values to historical record;
            archive.add(rec);
        }
    }
    
    @Override
    public String toString() {
        String s = "Archive Size = " + archive.size();
        return s;
    }
    
    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        Archive archive = new Archive();
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = startTime.withHour(23).withMinute(0).withSecond(45);
        /*
        archive.setArchiveConfiguration(60, startTime);
        archive.initialize(endTime);
        System.out.println(archive);
        for (int i = 0; i < 86400; i+= 15) {
            endTime.add(Calendar.SECOND, 15);
            CurrentWeather cw = new CurrentWeather();
            cw.setTime(endTime);
            archive.currentWeather(cw);
        }
        System.out.println(archive);
                */
        testHumidity();
    }
}
