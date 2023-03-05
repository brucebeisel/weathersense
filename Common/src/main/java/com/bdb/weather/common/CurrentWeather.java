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
package com.bdb.weather.common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.bdb.util.measurement.Measurement;
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
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The current weather. This is based on a combination of the Lacrosse 3610 and Davis Vantage Pro 2weather stations.
 * 
 * @author Bruce
 * @since 1.0
 * 
 */
public class CurrentWeather implements Serializable {
    private static final long serialVersionUID = -1292217095067065693L;
    static class IndexedMeasurement<T extends Measurement> {
    	public int index;
    	public T   value;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private Temperature insideTemperature;
    private Humidity insideHumidity;
    
    private Temperature outsideTemperature;
    private Temperature heatIndex;
    private Temperature windChill;
    private Temperature dewPoint;
    private Temperature thsw;
    private Humidity outsideHumidity;
    private Speed windSpeed;
    private Heading windDirection;
    private Speed gustSpeed;
    private Heading gustDirection;
    private Speed windSpeed10MinAvg;
    private Speed windSpeed2MinAvg;
    private List<String> dominantWindDirections = new ArrayList<>();
    private Pressure barometricPressure;
    private Pressure atmosphericPressure;
    private String barometerTrend;
    private String forecast;
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
    private String sunrise;
    private String sunset;

    private List<IndexedMeasurement<SoilMoisture>> soilMoistures = new ArrayList<>();
    private List<IndexedMeasurement<Temperature>> soilTemperatures = new ArrayList<>();
    private List<IndexedMeasurement<LeafWetness>> leafWetnesses = new ArrayList<>();
    private List<IndexedMeasurement<Temperature>> leafTemperatures = new ArrayList<>();
    private List<IndexedMeasurement<Temperature>> extraTemperatures = new ArrayList<>();
    private List<IndexedMeasurement<Humidity>> extraHumidities = new ArrayList<>();
    
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
     * Set the inside temperature.
     * 
     * @param temperature The inside temperature
     */
    public void setInsideTemperature(Temperature temperature) {
        this.insideTemperature = temperature;
    }

    /**
     * Get the inside temperature.
     * 
     * @return The inside temperature
     */
    public Temperature getInsideTemperature() {
        return insideTemperature;
    }

    /**
     * Set the outside temperature.
     * 
     * @param temperature The outside temperature 
     */
    public void setOutsideTemperature(Temperature temperature) {
        this.outsideTemperature = temperature;
    }

    /**
     * Get the outside temperature.
     * 
     * @return The outside temperature
     */
    public Temperature getOutsideTemperature() {
        return outsideTemperature;
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
     * Set the wind speed.
     * 
     * @param windSpeed The wind speed
     */
    public void setWindSpeed(Speed windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Set the wind direction.
     * 
     * @param direction The wind direction
     */
    public void setWindDirection(Heading windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * Get the wind speed and direction.
     * 
     * @return The wind speed
     */
    public Speed getWindSpeed() {
        return windSpeed;
    }

    /**
     * Get the wind speed and direction.
     * 
     * @return The wind direction
     */
    public Heading getWindDirection() {
        return windDirection;
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
    public void setDominantWindDirections(List<String> dominantWindDirectionList) {
    	dominantWindDirections.clear();
    	dominantWindDirections.addAll(dominantWindDirectionList);
    }

    /**
     * Get the second most wind direction tendency.
     * 
     * @return The heading
     */
    public List<String> getDominantWindDirections() {
        return Collections.unmodifiableList(dominantWindDirections);
    }

    /**
     * Set the wind gust speed.
     * 
     * @param gustSpeed The wind gust speed
     */
    public void setGustSpeed(Speed gustSpeed) {
        this.gustSpeed = gustSpeed;
    }
    

    /**
     * Get the wind gust speed.
     * 
     * @return The wind gust speed
     */
    public Speed getGustSpeed() {
        return gustSpeed;
    }
    
    /**
     * Get the wind gust direction.
     * 
     * @return The wind gust direction
     */
    public Heading getGustDirection() {
        return gustDirection;
    }

    /**
     * Set the wind gust direction.
     * 
     * @return The wind gust direction
     */
    public void setGustDirection(Heading gustDirection) {
        this.gustDirection = gustDirection;
    }
    /**
     * Set the barometric pressure.
     * 
     * @param pressure The barometric pressure
     */
    public void setBarometricPressure(Pressure pressure) {
        barometricPressure = pressure;
    }

    /**
     * Get the barometric pressure.
     * 
     * @return The barometric pressure
     */
    public Pressure getBarometricPressure() {
        return barometricPressure;
    }

    /**
     * Set the atmospheric pressure.
     * 
     * @param pressure The barometric pressure
     */
    public void setAtmosphericPressure(Pressure pressure) {
        atmosphericPressure = pressure;
    }

    /**
     * Get the atmospheric pressure.
     * 
     * @return The atmospheric pressure
     */
    public Pressure getAtmosphericPressure() {
        return atmosphericPressure;
    }

    /**
     * Set the inside humidity.
     *
     * @param humidity The inside humidity
     */
    public void setInsideHumidity(Humidity humidity) {
        insideHumidity = humidity;
    }

    /**
     * Get the inside humidity.
     *
     * @return The inside humidity
     */
    public Humidity getInsideHumidity() {
        return insideHumidity;
    }

    /**
     * Set the outside humidity.
     *
     * @param humidity The outside humidity
     */
    public void setOutsideHumidity(Humidity humidity) {
        outsideHumidity = humidity;
    }

    /**
     * Get the outside humidity. 
     *
     * @return The outside humidity
     */
    public Humidity getOutsideHumidity() {
        return outsideHumidity;
    }

    /**
     * Get the barometric trend.
     * 
     * @return The barometric trend
     */
    public String getBarometerTrend() {
        return barometerTrend;
    }

    /**
     * Set the barometric trend.
     * 
     * @param trend The barometric trend
     */
    public void setBarometerTrend(String trend) {
        barometerTrend = trend;
    }

    /**
     * Get the forecast.
     *
     * @return The forecast
     */
    public String getForecast() {
        return forecast;
    }

    /**
     * Set the forecast.
     *
     * @param forecast The forecast
     */
    public void setForecast(String forecast) {
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

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunrise() {
        return this.sunrise;
    }
    
    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunset() {
        return this.sunset;
    }
    
    public void setExtraTemperatures(List<IndexedMeasurement<Temperature>> list) {
    	extraTemperatures.clear();
    	extraTemperatures.addAll(list);
    }
    
    public void setExtraHumidities(List<IndexedMeasurement<Humidity>> list) {
    	extraHumidities.clear();
    	extraHumidities.addAll(list);
    }
    
    public void setSoilTemperatures(List<IndexedMeasurement<Temperature>> list) {
    	soilTemperatures.clear();
    	soilTemperatures.addAll(list);
    }

    public void setLeafTemperatures(List<IndexedMeasurement<Temperature>> list) {
    	leafTemperatures.clear();
    	leafTemperatures.addAll(list);
    }

    public void setLeafWetnesses(List<IndexedMeasurement<LeafWetness>> list) {
    	leafWetnesses.clear();
    	leafWetnesses.addAll(list);
    }
    
    public void setSoilMoistures(List<IndexedMeasurement<SoilMoisture>> list) {
    	soilMoistures.clear();
    	soilMoistures.addAll(list);
    }

    
    /**
     * Set the temperature for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
    public void setTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.THERMOMETER, temperature));
    }
     */
    
    /**
     * Set the temperature for a soil temperature sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
    public void setSoilTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_TEMPERATURE, temperature));
    }
     */
    
    /**
     * Set the temperature for a leaf temperature sensor.
     *
     * @param sensorId The ID of the sensor
     * @param temperature The temperature
    public void setLeafTemperatureForSensor(int sensorId, Temperature temperature) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_TEMPERATURE, temperature));
    }
     */
    
    /**
     * Get the temperature for a sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The temperature or null if the sensor does not exist
    public Temperature getTemperatureForSensor(int sensorId) {
        MeasurementEntry<Temperature> entry = temperatureSensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
     */
    
    /**
     * Set the humidity for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param humidity The humidity
    public void setHumidityForSensor(int sensorId, Humidity humidity) {
        humiditySensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.HYGROMETER, humidity));
    }
     */
    
    /**
     * Get the humidity for a sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The humidity or null if the sensor does not exist
    public Humidity getHumidityForSensor(int sensorId) {
        MeasurementEntry<Humidity> entry = humiditySensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
     */
    
    /**
     * Set the leaf wetness for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param leafWetness The leaf wetness
    public void setLeafWetnessForSensor(int sensorId, LeafWetness leafWetness) {
        leafWetnessSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_WETNESS, leafWetness));
    }
     */
    
    /**
     * Get the leaf wetness for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The leaf wetness
    public LeafWetness getLeafWetnessForSensor(int sensorId) {
        return leafWetnessSensorEntries.get(sensorId).getMeasurement();
    }
     */
    
    /**
     * Set the soil moisture for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @param soilMoisture The soil moisture
    public void setSoilMoistureForSensor(int sensorId, SoilMoisture soilMoisture) {
        soilMoistureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_MOISTURE, soilMoisture));
    }
     */
   
    /**
     * Get the soil moisture for a given sensor.
     *
     * @param sensorId The ID of the sensor
     * @return The soil moisture
    public SoilMoisture getSoilMoistureForSensor(int sensorId) {
        return soilMoistureSensorEntries.get(sensorId).getMeasurement();
    }
    
    /**
     *
     * @return
    @SuppressWarnings("rawtypes")
	public Collection<MeasurementEntry> getSensorValues() {
        List<MeasurementEntry> entries = new ArrayList<>();
        //entries.addAll(temperatureSensorEntries.values());
        //entries.addAll(humiditySensorEntries.values());
        //entries.addAll(soilMoistureSensorEntries.values());
        //entries.addAll(leafWetnessSensorEntries.values());
        return entries;
    }
     */

    
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("Time: ").append(fmt.format(time)).append("\n");
        sb.append("Baro Trend: ").append(barometerTrend).append("\n");
        sb.append("Gust: ").append(gustSpeed).append("\n");
        sb.append("Leaf Wetnesses: ").append(" Count: ").append(leafWetnesses.size()).append("\n");
        for (IndexedMeasurement<LeafWetness> m : leafWetnesses) {
        	sb.append("    Index: ").append(m.index).append("  Value: ").append(m.value).append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.time);
        hash = 97 * hash + Objects.hashCode(this.insideTemperature);
        hash = 97 * hash + Objects.hashCode(this.insideHumidity);
        hash = 97 * hash + Objects.hashCode(this.outsideTemperature);
        hash = 97 * hash + Objects.hashCode(this.heatIndex);
        hash = 97 * hash + Objects.hashCode(this.windChill);
        hash = 97 * hash + Objects.hashCode(this.dewPoint);
        hash = 97 * hash + Objects.hashCode(this.outsideHumidity);
        hash = 97 * hash + Objects.hashCode(this.windSpeed);
        hash = 97 * hash + Objects.hashCode(this.windDirection);
        hash = 97 * hash + Objects.hashCode(this.gustSpeed);
        hash = 97 * hash + Objects.hashCode(this.gustDirection);
        hash = 97 * hash + Objects.hashCode(this.windSpeed10MinAvg);
        hash = 97 * hash + Objects.hashCode(this.windSpeed2MinAvg);
        hash = 97 * hash + Objects.hashCode(this.dominantWindDirections);
        hash = 97 * hash + Objects.hashCode(this.barometricPressure);
        hash = 97 * hash + Objects.hashCode(this.barometerTrend);
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
        //hash = 97 * hash + Objects.hashCode(this.temperatureSensorEntries);
        //hash = 97 * hash + Objects.hashCode(this.humiditySensorEntries);
        //hash = 97 * hash + Objects.hashCode(this.leafWetnessSensorEntries);
        //hash = 97 * hash + Objects.hashCode(this.soilMoistureSensorEntries);
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

        if (!Objects.equals(this.insideTemperature, other.insideTemperature)) {
            return false;
        }

        if (!Objects.equals(this.insideHumidity, other.insideHumidity)) {
            return false;
        }

        if (!Objects.equals(this.outsideTemperature, other.outsideTemperature)) {
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

        if (!Objects.equals(this.outsideHumidity, other.outsideHumidity)) {
            return false;
        }

        if (!Objects.equals(this.windSpeed, other.windSpeed)) {
            return false;
        }
        if (!Objects.equals(this.windDirection, other.windDirection)) {
            return false;
        }

        if (!Objects.equals(this.gustSpeed, other.gustSpeed)) {
            return false;
        }
        if (!Objects.equals(this.gustDirection, other.gustDirection)) {
            return false;
        }

        if (!Objects.equals(this.windSpeed10MinAvg, other.windSpeed10MinAvg)) {
            return false;
        }

        if (!Objects.equals(this.windSpeed2MinAvg, other.windSpeed2MinAvg)) {
            return false;
        }

        if (!Objects.equals(this.dominantWindDirections, other.dominantWindDirections)) {
            return false;
        }

        if (!Objects.equals(this.barometricPressure, other.barometricPressure)) {
            return false;
        }

        if (this.barometerTrend != other.barometerTrend) {
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

        /*
        if (!Objects.equals(this.temperatureSensorEntries, other.temperatureSensorEntries)) {
            return false;
        }

        if (!Objects.equals(this.humiditySensorEntries, other.humiditySensorEntries)) {
            return false;
        }

        if (!Objects.equals(this.leafWetnessSensorEntries, other.leafWetnessSensorEntries)) {
            return false;
        }

        if (!Objects.equals(this.soilMoistureSensorEntries, other.soilMoistureSensorEntries)
			return false;
        */
        
        return true;
    }
}
