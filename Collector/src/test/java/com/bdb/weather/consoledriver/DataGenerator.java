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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.bdb.weather.collector.messages.SensorMessage;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.Forecast;
import com.bdb.weather.common.GeographicLocation;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.WeatherTrend;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;
import com.bdb.weather.common.messages.WsParametersMessage;

/**
 *
 * @author Bruce
 */
public class DataGenerator {
    private int duration = 300;
    private int archiveSizeDays = 3;
    private boolean running = false;
    private ScheduledExecutorService executor;
    private LocalDateTime archiveStartDate;
    private List<HistoricalRecord> archive;
    private final CurrentWeather cw = new CurrentWeather();
    private final HistoricalRecord record;
    private final Temperature temperatureDelta = new Temperature(.2);
    private static final Sensor sensorArray[] = {
        new Sensor(Sensor.OUTDOOR_THERMOMETER_SENSOR_ID, SensorType.THERMOMETER, "Outdoor Thermometer"),
        new Sensor(Sensor.INDOOR_THERMOMETER_SENSOR_ID, SensorType.THERMOMETER, "Indoor Thermometer"),
        new Sensor(Sensor.OUTDOOR_HYGROMETER_SENSOR_ID, SensorType.HYGROMETER, "Outdoor Hygrometer"),
        new Sensor(Sensor.INDOOR_HYGROMETER_SENSOR_ID, SensorType.HYGROMETER, "Indoor Hygrometer"),
        new Sensor(Sensor.UV_SENSOR_ID, SensorType.ULTRAVIOLET, "Ultra Violet"),
        new Sensor(Sensor.BAROMETER_SENSOR_ID, SensorType.BAROMETER, "Barometer"),
        new Sensor(Sensor.SOLAR_RADIATION_SENSOR_ID, SensorType.SOLAR_RADIATION, "Solar Radiation"),
        new Sensor(Sensor.WIND_SENSOR_ID, SensorType.ANEMOMETER, "Wind Sensor"),
        new Sensor(Sensor.RAIN_COLLECTOR_SENSOR_ID, SensorType.RAIN_COLLECTOR, "Rain Collector"),
        new Sensor(Sensor.THERMOMETER_BASE_SENSOR_ID, SensorType.THERMOMETER),
        new Sensor(Sensor.THERMOMETER_BASE_SENSOR_ID + 1,SensorType.THERMOMETER),
        new Sensor(Sensor.THERMOMETER_BASE_SENSOR_ID + 2, SensorType.THERMOMETER),
        new Sensor(Sensor.HYGROMETER_BASE_SENSOR_ID, SensorType.HYGROMETER),
        new Sensor(Sensor.LEAF_WETNESS_BASE_SENSOR_ID, SensorType.LEAF_WETNESS),
        new Sensor(Sensor.SOIL_MOISTURE_BASE_SENSOR_ID, SensorType.SOIL_MOISTURE)
    };
    
    /**
     *
     */
    public DataGenerator() {
        executor = Executors.newSingleThreadScheduledExecutor();
        cw.setOutdoorTemperature(new Temperature(25.0));
        cw.setIndoorTemperature(new Temperature(20.0));
        cw.setOutdoorHumidity(new Humidity(50.0));
        cw.setIndoorHumidity(new Humidity(60.0));
        cw.setWind(new Wind(new Speed(10.0), new Heading(179.0F)));
        cw.setWindGust(new Wind(new Speed(15.0), new Heading(186.0F)));
        cw.setForecastRule("It's raining");
        cw.setBarometricPressure(new Pressure(30.0, Pressure.Unit.IN_HG));
        cw.setRain15Minute(new Depth(0.0));
        cw.setRain24Hour(new Depth(1.0));
        cw.setRainCalendarYear(new Depth(14.0));
        cw.setRainHour(new Depth(.2));
        cw.setRainMonth(new Depth(2.0));
        cw.setRainRate(new Depth(.5));
        cw.setRainToday(new Depth(.75));
        cw.setRainWeatherYear(new Depth(10.0));
        cw.setStormRain(new Depth(.8));
        cw.setStormStart(LocalDate.now());
        cw.setSolarRadiation(new SolarRadiation(4.0));
        cw.setUvIndex(new UvIndex(7));
        cw.setBarometerTrend(WeatherTrend.RISING_RAPIDLY);
        cw.setForecast(Forecast.MOSTLY_CLOUDY);
        cw.setDewPoint(new Temperature(18.0));
        cw.setWindChill(new Temperature(10.0));
        cw.setHeatIndex(new Temperature(30.0));
        /*
        cw.setWindDir2(new Heading(170.0F));
        cw.setWindDir3(new Heading(175.0F));
        cw.setWindDir4(new Heading(160.0F));
        cw.setWindDir5(new Heading(150.0F));
        */
        cw.setWindSpeed10MinAvg(new Speed(12.0));
        cw.setWindSpeed2MinAvg(new Speed(15.0));
        for (Sensor sensor : sensorArray) {
            if (sensor.getSensorId() <= Sensor.LAST_STANDARD_SENSOR_ID)
                continue;

            /*
            switch (sensor.getType()) {
                case THERMOMETER:
                case LEAF_TEMPERATURE:
                case SOIL_TEMPERATURE:
                    cw.setTemperatureForSensor(sensor.getSensorId(), new Temperature(50.0 + (100.0 - (Math.random() * 100.0))));
                    break;

                case HYGROMETER:
                    cw.setHumidityForSensor(sensor.getSensorId(), new Humidity(50.0 + (50.0 - (Math.random() * 100.0))));
                    break;

                case LEAF_WETNESS:
                    cw.setLeafWetnessForSensor(sensor.getSensorId(), new LeafWetness(Math.random() * 10.0));
                    break;

                case SOIL_MOISTURE:
                    cw.setSoilMoistureForSensor(sensor.getSensorId(), new SoilMoisture(.5));
            }
            */
        }

        LocalDateTime c = LocalDateTime.now().withSecond(0).withNano(0);
        record = new HistoricalRecord(c);
        record.setDuration(Duration.ofSeconds(duration));

        record.setLowOutdoorTemperature(new Temperature(20.0));
        record.setAvgOutdoorTemperature(new Temperature(25.0));
        record.setHighOutdoorTemperature(new Temperature(30.0));

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

        for (Sensor sensor : sensorArray) {
            if (sensor.getSensorId() <= Sensor.LAST_STANDARD_SENSOR_ID)
                continue;

            switch (sensor.getType()) {
                case THERMOMETER:
                case LEAF_TEMPERATURE:
                case SOIL_TEMPERATURE:
                    record.setTemperatureForSensor(sensor.getSensorId(), new Temperature(50.0 + (100.0 - (Math.random() * 100.0))));
                    break;

                case HYGROMETER:
                    record.setHumidityForSensor(sensor.getSensorId(), new Humidity(50.0 + (50.0 - (Math.random() * 100.0))));
                    break;

                case LEAF_WETNESS:
                    record.setLeafWetnessForSensor(sensor.getSensorId(), new LeafWetness(Math.random() * 10.0));
                    break;

                case SOIL_MOISTURE:
                    record.setSoilMoistureForSensor(sensor.getSensorId(), new SoilMoisture(.5));
            }
        }
    }
    
    /**
     *
     * @param archiveInterval
     * @param archiveStartDate
     */
    public void init(int archiveInterval, LocalDateTime archiveStartDate) {
        this.archiveStartDate = archiveStartDate;
    }

    /**
     *
     * @return
     */
    public WsParametersMessage getWeatherStationParametersMessage() {
        GeographicLocation location = new GeographicLocation(new AngularMeasurement(32.0), new AngularMeasurement(-114.0), new Distance(500.0, Distance.Unit.FEET));
        WsParametersMessage msg = new WsParametersMessage("Davis Instruments", "Vantage Pro 2", "2013-12-10", "1.90", location, Calendar.JANUARY);
        msg.addParameter("GMT Offset", "-8");
        msg.addParameter("Timezone", "PST");
        msg.addParameter("Rain Season Start Month", "July");
        msg.addParameter("Archive Period", "5");

        return msg;

    }

    /**
     *
     * @return
     */
    public SensorMessage getSensorMessage() {
        SensorMessage msg = new SensorMessage(Arrays.asList(sensorArray));
        return msg;
    }

    /**
     *
     * @param newestRecordTime
     * @return
     */
    public HistoricalRecord getNextHistoricalRecord(LocalDateTime newestRecordTime) {
        if (newestRecordTime == null)
            return null;

        Duration durationMinutes = Duration.ofSeconds(duration);

        LocalDateTime nextRecordTime;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime adjustedTime = now.minusDays(archiveSizeDays);
        int minutes = adjustedTime.getMinute();

        //
        // Ensure the start of the archive is on a boundary of the interval
        //
        if (minutes % durationMinutes.toMinutes() != 0)
            adjustedTime = adjustedTime.minusMinutes(minutes % durationMinutes.toMinutes());

        if (newestRecordTime.isBefore(adjustedTime)) {
            nextRecordTime = adjustedTime.withSecond(0).withNano(0);
        }
        else {
            nextRecordTime = newestRecordTime;
        }

        nextRecordTime = nextRecordTime.plus(durationMinutes);

        if (now.isAfter(nextRecordTime)) {
            record.setTime(nextRecordTime);
            return record;
        }

        return null;
    }
    
    private void createArchive(Calendar currentTime) {
        archive.clear();
    }
    
    /**
     *
     */
    public void start() {
        archive.clear();
        running = true;
    }
    
    /**
     *
     */
    public void stop() {
        running = false;
    }
    
    /**
     *
     * @return
     */
    public CurrentWeather currentWeather() {
        cw.setTime(LocalDateTime.now());
        if (Math.random() < .5) {
            cw.setOutdoorTemperature(cw.getOutdoorTemperature().add(temperatureDelta));
            cw.setIndoorTemperature(cw.getIndoorTemperature().add(temperatureDelta));
        }
        else {
            cw.setOutdoorTemperature(cw.getOutdoorTemperature().subtract(temperatureDelta));
            cw.setIndoorTemperature(cw.getIndoorTemperature().subtract(temperatureDelta));
        }

        return cw;
    }
    
    /**
     *
     */
    public void sendHistoricalData() {
    }
    
    /**
     *
     */
    public void sendSensorStationData() {
    }
}