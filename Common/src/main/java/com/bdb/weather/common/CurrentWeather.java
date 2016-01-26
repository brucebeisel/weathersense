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
package com.bdb.weather.common;

import com.bdb.weather.common.xml.LocalDateTimeAdapter;
import com.bdb.weather.common.xml.LocalDateAdapter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
 * The current weather. This is based on a combination of the Lacrosse 3610 and Davis Vantage Pro 2weather stations.
 * 
 * @author Bruce
 * @since 1.0
 * 
 */
@XmlRootElement
public class CurrentWeather implements Serializable {
    private static final long serialVersionUID = -1292217095067065693L;

    private LocalDateTime time;
    private Temperature indoorTemp;
    private Humidity indoorHumidity;
    
    private Temperature outdoorTemp;
    private Temperature heatIndex;
    private Temperature windChill;
    private Temperature dewPoint;
    private Temperature thsw;
    private Humidity outdoorHumidity;
    private Wind wind;
    private Wind gust;
    private Speed windSpeed10MinAvg;
    private Speed windSpeed2MinAvg;
    private Heading windDir2;
    private Heading windDir3;
    private Heading windDir4;
    private Heading windDir5;
    private Pressure baroPressure;
    private WeatherTrend baroTrend;
    private Forecast forecast;
    private String forecastRule;
    private UvIndex uvIndex;
    private SolarRadiation solarRadiation;
    private Depth rainRate;
    private Depth rain15Minute;
    private Depth rainHour;
    private Depth rain24Hour;
    private Depth rainToday;
    private Depth rainMonth;
    private Depth rainWeatherYear;
    private Depth rainCalendarYear;
    private LocalDate stormStart;
    private Depth stormRain;
    private Depth dayET;
    private Depth monthET;
    private Depth yearET;

    @XmlElement(name="soilMoistureSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<SoilMoisture>> soilMoistureSensorEntries = new TreeMap<>();

    @XmlElement(name="temperatureSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<Temperature>> temperatureSensorEntries = new TreeMap<>();
    
    @XmlElement(name="humiditySensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<Humidity>> humiditySensorEntries = new TreeMap<>();

    @XmlElement(name="leafWetnessSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<LeafWetness>> leafWetnessSensorEntries = new TreeMap<>();
    
    /**
     * Constructor.
     */
    public CurrentWeather() {
    }

    /**
     * Set the time of this current weather measurement.
     * 
     * @param time The local time
     */
    @XmlJavaTypeAdapter(type=java.time.LocalDateTime.class, value=LocalDateTimeAdapter.class)
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Get the time of the current weather observation.
     * 
     * @return The observation time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Set the indoor temperature.
     * 
     * @param temperature The indoor temperature
     */
    public void setIndoorTemperature(Temperature temperature) {
        this.indoorTemp = temperature;
    }

    /**
     * Get the indoor temperature.
     * 
     * @return The indoor temperature
     */
    public Temperature getIndoorTemperature() {
        return indoorTemp;
    }

    /**
     * Set the outdoor temperature.
     * 
     * @param temperature The outdoor temperature 
     */
    public void setOutdoorTemperature(Temperature temperature) {
        this.outdoorTemp = temperature;
    }

    /**
     * Get the outdoor temperature.
     * 
     * @return The outdoor temperature
     */
    public Temperature getOutdoorTemperature() {
        return outdoorTemp;
    }

    /**
     * Set the wind chill.
     * 
     * @param temperature  The wind chill
     */
    public void setWindChill(Temperature temperature) {
        this.windChill = temperature;
    }

    /**
     * Get the wind chill.
     * 
     * @return The wind chill
     */
    public Temperature getWindChill() {
        return windChill;
    }

    /**
     * Set heat index.
     * 
     * @param temperature The heat index
     */
    public void setHeatIndex(Temperature temperature) {
        this.heatIndex = temperature;
    }

    /**
     * Get the heat index.
     * 
     * @return The heat index
     */
    public Temperature getHeatIndex() {
        return heatIndex;
    }

    /**
     * Set the dew point.
     * 
     * @param temperature The dew point
     */
    public void setDewPoint(Temperature temperature) {
        this.dewPoint = temperature;
    }

    /**
     * Get the dew point.
     * 
     * @return The dew point
     */
    public Temperature getDewPoint() {
        return dewPoint;
    }

    /**
     * Set the THSW.
     * 
     * @param temperature The THSW
     */
    public void setThsw(Temperature temperature) {
        this.thsw = temperature;
    }

    /**
     * Get the THSW.
     * 
     * @return The THSW
     */
    public Temperature getThsw() {
        return thsw;
    }

    /**
     * Set the wind speed and direction.
     * 
     * @param wind The wind
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    /**
     * Get the wind speed and direction.
     * 
     * @return The wind
     */
    public Wind getWind() {
        return wind;
    }

    /**
     * Get the average wind speed over the last 2 minutes.
     * 
     * @return The average wind speed over the last 2 minutes
     */
    public Speed getWindSpeed2MinAvg() {
        return windSpeed2MinAvg;
    }

    /**
     * Set the average wind speed over the last 2 minutes.
     * 
     * @param windSpeed2MinAvg The average wind speed over the last 2 minutes
     */
    public void setWindSpeed2MinAvg(Speed windSpeed2MinAvg) {
        this.windSpeed2MinAvg = windSpeed2MinAvg;
    }

    /**
     * Get the average wind speed over the last 10 minutes.
     * 
     * @return The average wind speed over the last 10 minutes
     */
    public Speed getWindSpeed10MinAvg() {
        return windSpeed10MinAvg;
    }

    /**
     * Set the average wind speed over the last 10 minutes.
     * 
     * @param windSpeed10MinAvg The average wind speed over the last 10 minutes
     */
    public void setWindSpeed10MinAvg(Speed windSpeed10MinAvg) {
        this.windSpeed10MinAvg = windSpeed10MinAvg;
    }

    /**
     * Set the second most wind direction tendency.
     * 
     * @param heading The heading
     */
    public void setWindDir2(Heading heading) {
        windDir2 = heading;
    }

    /**
     * Get the second most wind direction tendency.
     * 
     * @return The heading
     */
    public Heading getWindDir2() {
        return windDir2;
    }

    /**
     * Set the third most wind direction tendency.
     * 
     * @param heading The heading
     */
    public void setWindDir3(Heading heading) {
        windDir3 = heading;
    }

    /**
     * Get the third most wind direction tendency.
     * 
     * @return The heading
     */
    public Heading getWindDir3() {
        return windDir3;
    }

    /**
     * Set the fourth most wind direction tendency.
     * 
     * @param heading The heading
     */
    public void setWindDir4(Heading heading) {
        windDir4 = heading;
    }

    /**
     * Get the fourth most wind direction tendency.
     * 
     * @return The heading
     */
    public Heading getWindDir4() {
        return windDir4;
    }

    /**
     * Set the fifth most wind direction tendency.
     * 
     * @param heading The heading
     */
    public void setWindDir5(Heading heading) {
        windDir5 = heading;
    }

    /**
     * Get the fifth most wind direction tendency.
     * 
     * @return The heading
     */
    public Heading getWindDir5() {
        return windDir5;
    }
    
    /**
     * Get the wind speed.
     * 
     * @return The wind speed or null if no reading
     */
    public Speed getWindSpeed() {
        if (wind != null)
            return wind.getSpeed();
        else
            return null;
    }
    
    /**
     * Get the wind direction.
     * 
     * @return The wind direction of null if no reading
     */
    public Heading getWindDirection() {
        if (wind != null)
            return wind.getDirection();
        else
            return null;
    }
    
    /**
     * Set the wind gust
     * 
     * @param gust The wind gust
     */
    public void setWindGust(Wind gust) {
        this.gust = gust;
    }
    
    /**
     * Get the wind gust.
     * 
     * @return The wind gust speed and direction
     */
    public Wind getWindGust() {
        return gust;
    }

    /**
     * Get the wind gust speed.
     * 
     * @return The wind gust speed or null
     */
    public Speed getWindGustSpeed() {
        if (gust != null)
            return gust.getSpeed();
        else
            return null;
    }
    
    /**
     * Get the wind gust direction.
     * 
     * @return The wind gust direction of null
     */
    public Heading getWindGustDirection() {
        if (gust != null)
            return gust.getDirection();
        else
            return null;
    }

    /**
     * Set the barometric pressure.
     * 
     * @param pressure The barometric pressure
     */
    public void setBaroPressure(Pressure pressure) {
        baroPressure = pressure;
    }

    /**
     * Get the barometric pressure.
     * 
     * @return The barometric pressure
     */
    public Pressure getBaroPressure() {
        return baroPressure;
    }

    /**
     * Set the indoor humidity.
     *
     * @param humidity The indoor humidity
     */
    public void setIndoorHumidity(Humidity humidity) {
        indoorHumidity = humidity;
    }

    /**
     * Get the indoor humidity.
     *
     * @return The indoor humidity
     */
    public Humidity getIndoorHumidity() {
        return indoorHumidity;
    }

    /**
     * Set the outdoor humidity.
     *
     * @param humidity The outdoor humidity
     */
    public void setOutdoorHumidity(Humidity humidity) {
        outdoorHumidity = humidity;
    }

    /**
     * Get the outdoor humidity. 
     *
     * @return The outdoor humidity
     */
    public Humidity getOutdoorHumidity() {
        return outdoorHumidity;
    }

    /**
     * Get the barometric trend.
     * 
     * @return The barometric trend
     */
    public WeatherTrend getBaroTrend() {
        return baroTrend;
    }

    /**
     * Set the barometric trend.
     * 
     * @param trend The barometric trend
     */
    public void setBaroTrend(WeatherTrend trend) {
        baroTrend = trend;
    }

    /**
     * Get the forecast.
     *
     * @return The forecast
     */
    public Forecast getForecast() {
        return forecast;
    }

    /**
     * Set the forecast.
     *
     * @param forecast The forecast
     */
    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }
    
    /**
     * Get the forecast rule.
     *
     * @return The forecast rule
     */
    public String getForecastRule() {
        return forecastRule;
    }
    
    /**
     * Set the forecast rule.
     *
     * @param forecastRule The forecast rule
     */
    public void setForecastRule(String forecastRule) {
        this.forecastRule = forecastRule;
    }

    /**
     * Set the UV index.
     *
     * @param uvIndex The UV index
     */
    public void setUvIndex(UvIndex uvIndex) {
        this.uvIndex = uvIndex;
    }
    
    /**
     * Get the UV index.
     *
     * @return The UV index
     */
    public UvIndex getUvIndex() {
        return uvIndex;
    }

    /**
     * Get the solar radiation.
     *
     * @return The solar radiation
     */
    public SolarRadiation getSolarRadiation() {
        return solarRadiation;
    }

    /**
     * Set the solar radiation.
     *
     * @param solarRadiation The solar radiation
     */
    public void setSolarRadiation(SolarRadiation solarRadiation) {
        this.solarRadiation = solarRadiation;
    }
    
    /**
     * Get the rain rate.
     * 
     * @return The rain rate
     */
    public Depth getRainRate() {
        return rainRate;
    }
    
    /**
     * Set the rain rate.
     *
     * @param rate The rain rate
     */
    public void setRainRate(Depth rate) {
        this.rainRate = rate;
    }

    /**
     * Get the amount of rain in the last 15 minutes.
     *
     * @return The rain amount
     */
    public Depth getRain15Minute() {
        return rain15Minute;
    }

    /**
     * Set the amount of rain in the last 15 minutes.
     *
     * @param rain15Minute The rain amount
     */
    public void setRain15Minute(Depth rain15Minute) {
        this.rain15Minute = rain15Minute;
    }
    
    /**
     * Get the amount of rain in the last hour.
     *
     * @return The rain amount
     */
    public Depth getRainHour() {
        return rainHour;
    }

    /**
     * Set the amount of rain in the last hour.
     *
     * @param rainHour The rain amount
     */
    public void setRainHour(Depth rainHour) {
        this.rainHour = rainHour;
    }

    /**
     * Get the amount of rain in the last 24 hours.
     *
     * @return The rain amount
     */
    public Depth getRain24Hour() {
        return rain24Hour;
    }

    /**
     * Set the amount of rain in the last 24 hours.
     *
     * @param rain24Hour The rain amount
     */
    public void setRain24Hour(Depth rain24Hour) {
        this.rain24Hour = rain24Hour;
    }

    /**
     * Get the amount of rain since midnight.
     *
     * @return The rain amount
     */
    public Depth getRainToday() {
        return rainToday;
    }

    /**
     * Set the amount of rain since midnight.
     *
     * @param rainToday The rain amount
     */
    public void setRainToday(Depth rainToday) {
        this.rainToday = rainToday;
    }

    /**
     * Get the amount of rain since the beginning of the month.
     *
     * @return The rain amount
     */
    public Depth getRainMonth() {
        return rainMonth;
    }

    /**
     * Set the amount of rain since the beginning of the month.
     *
     * @param rainMonth The rain amount
     */
    public void setRainMonth(Depth rainMonth) {
        this.rainMonth = rainMonth;
    }

    /**
     * Set the amount of rain since the beginning of the weather year.
     *
     * @return The rain amount
     */
    public Depth getRainWeatherYear() {
        return rainWeatherYear;
    }

    /**
     * Get the amount of rain since the beginning of the weather year.
     *
     * @param rainWeatherYear The rain amount
     */
    public void setRainWeatherYear(Depth rainWeatherYear) {
        this.rainWeatherYear = rainWeatherYear;
    }

    /**
     * Get the amount of rain since the beginning of the calendar year.
     *
     * @return The rain amount
     */
    public Depth getRainCalendarYear() {
        return rainCalendarYear;
    }

    /**
     * Set the amount of rain since the beginning of the calendar year.
     *
     * @param rainCalendarYear The rain amount
     */
    public void setRainCalendarYear(Depth rainCalendarYear) {
        this.rainCalendarYear = rainCalendarYear;
    }

    /**
     * Get the date that the current storm started.
     *
     * @return The storm start date or null if there is no active storm
     */
    public LocalDate getStormStart() {
        if (stormStart != null)
            return stormStart;
        else
            return null;
    }

    /**
     * Set the start date of the current storm.
     *
     * @param stormStart The start date of the current storm or null if there is no storm
     */
    @XmlJavaTypeAdapter(type=java.time.LocalDate.class, value=LocalDateAdapter.class)
    public void setStormStart(LocalDate stormStart) {
        this.stormStart = stormStart;
    }

    /**
     * Get the amount of rain in the current storm.
     *
     * @return The amount of rain in the current storm or 0 if there is not an active storm
     */
    public Depth getStormRain() {
        return stormRain;
    }

    /**
     * Set the amount of rain in the current storm.
     *
     * @param stormRain The amount of rain in the current storm or 0 if there is not an active storm
     */
    public void setStormRain(Depth stormRain) {
        this.stormRain = stormRain;
    }
    
    /**
     * Get the Evapotranspiration for today.
     *
     * @return The ET
     */
    public Depth getDayET() {
        return dayET;
    }
    
    /**
     * Set the Evapotranspiration for today.
     *
     * @param et The ET
     */
    public void setDayET(Depth et) {
        this.dayET = et;
    }
    
    /**
     * Get the Evapotranspiration for the month.
     *
     * @return The ET
     */
    public Depth getMonthET() {
        return monthET;
    }
    
    /**
     * Set the Evapotranspiration for the month.
     *
     * @param et The ET
     */
    public void setMonthET(Depth et) {
        this.monthET = et;
    }
    
    /**
     * Get the Evapotranspiration for the month.
     *
     * @return The ET
     */
    public Depth getYearET() {
        return yearET;
    }
    
    /**
     * Set the Evapotranspiration for the month.
     *
     * @param et The ET
     */
    public void setYearET(Depth et) {
        this.yearET = et;
    }
    
    /**
     * Set the temperature for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
     */
    public void setTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.THERMOMETER, temperature));
    }
    
    /**
     * Set the temperature for a soil temperature sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
     */
    public void setSoilTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_TEMPERATURE, temperature));
    }
    
    /**
     * Set the temperature for a leaf temperature sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
     */
    public void setLeafTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_TEMPERATURE, temperature));
    }
    
    /**
     * Get the temperature for a sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The temperature or null if the sensor does not exist
     */
    public Temperature getTemperatureForSensor(int sensorId) {
        MeasurementEntry<Temperature> entry = temperatureSensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    /**
     * Set the humidity for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param humidity The humidity
     */
    public void setHumidityForSensor(int sensorId, Humidity humidity) {
        humiditySensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.HYGROMETER, humidity));
    }
    
    /**
     * Get the humidity for a sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The humidity or null if the sensor does not exist
     */
    public Humidity getHumidityForSensor(int sensorId) {
        MeasurementEntry<Humidity> entry = humiditySensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    /**
     * Set the leaf wetness for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param leafWetness The leaf wetness
     */
    public void setLeafWetnessForSensor(int sensorId, LeafWetness leafWetness) {
        leafWetnessSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_WETNESS, leafWetness));
    }
    
    /**
     * Get the leaf wetness for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The leaf wetness
     */
    public LeafWetness getLeafWetnessForSensor(int sensorId) {
        return leafWetnessSensorEntries.get(sensorId).getMeasurement();
    }
    
    /**
     * Set the soil moisture for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param soilMoisture The soil moisture
     */
    public void setSoilMoistureForSensor(int sensorId, SoilMoisture soilMoisture) {
        soilMoistureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_MOISTURE, soilMoisture));
    }
   
    /**
     * Get the soil moisture for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The soil moisture
     */
    public SoilMoisture getSoilMoistureForSensor(int sensorId) {
        return soilMoistureSensorEntries.get(sensorId).getMeasurement();
    }
    
    /**
     *
     * @return
     */
    public Collection<MeasurementEntry> getSensorValues() {
        List<MeasurementEntry> entries = new ArrayList<>();
        entries.addAll(temperatureSensorEntries.values());
        entries.addAll(humiditySensorEntries.values());
        entries.addAll(soilMoistureSensorEntries.values());
        entries.addAll(leafWetnessSensorEntries.values());
        return entries;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        StringBuilder sb = new StringBuilder();

        sb.append("Time: ").append(fmt.format(time)).append("\n");
        sb.append("Baro Trend: ").append(baroTrend);
        sb.append("Gust: ").append(gust);
        
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.time);
        hash = 97 * hash + Objects.hashCode(this.indoorTemp);
        hash = 97 * hash + Objects.hashCode(this.indoorHumidity);
        hash = 97 * hash + Objects.hashCode(this.outdoorTemp);
        hash = 97 * hash + Objects.hashCode(this.heatIndex);
        hash = 97 * hash + Objects.hashCode(this.windChill);
        hash = 97 * hash + Objects.hashCode(this.dewPoint);
        hash = 97 * hash + Objects.hashCode(this.outdoorHumidity);
        hash = 97 * hash + Objects.hashCode(this.wind);
        hash = 97 * hash + Objects.hashCode(this.gust);
        hash = 97 * hash + Objects.hashCode(this.windSpeed10MinAvg);
        hash = 97 * hash + Objects.hashCode(this.windSpeed2MinAvg);
        hash = 97 * hash + Objects.hashCode(this.windDir2);
        hash = 97 * hash + Objects.hashCode(this.windDir3);
        hash = 97 * hash + Objects.hashCode(this.windDir4);
        hash = 97 * hash + Objects.hashCode(this.windDir5);
        hash = 97 * hash + Objects.hashCode(this.baroPressure);
        hash = 97 * hash + Objects.hashCode(this.baroTrend);
        hash = 97 * hash + Objects.hashCode(this.forecast);
        hash = 97 * hash + Objects.hashCode(this.forecastRule);
        hash = 97 * hash + Objects.hashCode(this.uvIndex);
        hash = 97 * hash + Objects.hashCode(this.solarRadiation);
        hash = 97 * hash + Objects.hashCode(this.rainRate);
        hash = 97 * hash + Objects.hashCode(this.rain15Minute);
        hash = 97 * hash + Objects.hashCode(this.rainHour);
        hash = 97 * hash + Objects.hashCode(this.rain24Hour);
        hash = 97 * hash + Objects.hashCode(this.rainToday);
        hash = 97 * hash + Objects.hashCode(this.rainMonth);
        hash = 97 * hash + Objects.hashCode(this.rainWeatherYear);
        hash = 97 * hash + Objects.hashCode(this.rainCalendarYear);
        hash = 97 * hash + Objects.hashCode(this.stormStart);
        hash = 97 * hash + Objects.hashCode(this.stormRain);
        hash = 97 * hash + Objects.hashCode(this.dayET);
        hash = 97 * hash + Objects.hashCode(this.monthET);
        hash = 97 * hash + Objects.hashCode(this.yearET);
        hash = 97 * hash + Objects.hashCode(this.temperatureSensorEntries);
        hash = 97 * hash + Objects.hashCode(this.humiditySensorEntries);
        hash = 97 * hash + Objects.hashCode(this.leafWetnessSensorEntries);
        hash = 97 * hash + Objects.hashCode(this.soilMoistureSensorEntries);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CurrentWeather other = (CurrentWeather) obj;

        if (!Objects.equals(this.time, other.time)) {
            return false;
        }

        if (!Objects.equals(this.indoorTemp, other.indoorTemp)) {
            return false;
        }

        if (!Objects.equals(this.indoorHumidity, other.indoorHumidity)) {
            return false;
        }

        if (!Objects.equals(this.outdoorTemp, other.outdoorTemp)) {
            return false;
        }

        if (!Objects.equals(this.heatIndex, other.heatIndex)) {
            return false;
        }

        if (!Objects.equals(this.windChill, other.windChill)) {
            return false;
        }

        if (!Objects.equals(this.dewPoint, other.dewPoint)) {
            return false;
        }

        if (!Objects.equals(this.outdoorHumidity, other.outdoorHumidity)) {
            return false;
        }

        if (!Objects.equals(this.wind, other.wind)) {
            return false;
        }

        if (!Objects.equals(this.gust, other.gust)) {
            return false;
        }

        if (!Objects.equals(this.windSpeed10MinAvg, other.windSpeed10MinAvg)) {
            return false;
        }

        if (!Objects.equals(this.windSpeed2MinAvg, other.windSpeed2MinAvg)) {
            return false;
        }

        if (!Objects.equals(this.windDir2, other.windDir2)) {
            return false;
        }

        if (!Objects.equals(this.windDir3, other.windDir3)) {
            return false;
        }

        if (!Objects.equals(this.windDir4, other.windDir4)) {
            return false;
        }

        if (!Objects.equals(this.windDir5, other.windDir5)) {
            return false;
        }

        if (!Objects.equals(this.baroPressure, other.baroPressure)) {
            return false;
        }

        if (this.baroTrend != other.baroTrend) {
            return false;
        }

        if (this.forecast != other.forecast) {
            return false;
        }

        if (!Objects.equals(this.forecastRule, other.forecastRule)) {
            return false;
        }

        if (!Objects.equals(this.uvIndex, other.uvIndex)) {
            return false;
        }

        if (!Objects.equals(this.solarRadiation, other.solarRadiation)) {
            return false;
        }

        if (!Objects.equals(this.rainRate, other.rainRate)) {
            return false;
        }

        if (!Objects.equals(this.rain15Minute, other.rain15Minute)) {
            return false;
        }

        if (!Objects.equals(this.rainHour, other.rainHour)) {
            return false;
        }

        if (!Objects.equals(this.rain24Hour, other.rain24Hour)) {
            return false;
        }

        if (!Objects.equals(this.rainToday, other.rainToday)) {
            return false;
        }

        if (!Objects.equals(this.rainMonth, other.rainMonth)) {
            return false;
        }

        if (!Objects.equals(this.rainWeatherYear, other.rainWeatherYear)) {
            return false;
        }

        if (!Objects.equals(this.rainCalendarYear, other.rainCalendarYear)) {
            return false;
        }

        if (!Objects.equals(this.stormStart, other.stormStart)) {
            return false;
        }

        if (!Objects.equals(this.stormRain, other.stormRain)) {
            return false;
        }

        if (!Objects.equals(this.dayET, other.dayET)) {
            return false;
        }

        if (!Objects.equals(this.monthET, other.monthET)) {
            return false;
        }

        if (!Objects.equals(this.yearET, other.yearET)) {
            return false;
        }

        if (!Objects.equals(this.temperatureSensorEntries, other.temperatureSensorEntries)) {
            return false;
        }

        if (!Objects.equals(this.humiditySensorEntries, other.humiditySensorEntries)) {
            return false;
        }

        if (!Objects.equals(this.leafWetnessSensorEntries, other.leafWetnessSensorEntries)) {
            return false;
        }

        return Objects.equals(this.soilMoistureSensorEntries, other.soilMoistureSensorEntries);
    }
}