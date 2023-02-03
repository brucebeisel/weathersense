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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

/**
 *
 * @author bruce
 */
public class HistoricalRecord {
    @SuppressWarnings("unused")
	private static final long serialVersionUID = -3464537243885145413L;
    private LocalDateTime time;
    private Duration duration = Duration.ZERO;
    private Temperature avgOutdoorTemperature;
    private Temperature highOutdoorTemperature;
    private Temperature lowOutdoorTemperature;
    private Humidity outdoorHumidity;
    private Temperature indoorTemperature;
    private Humidity indoorHumidity;
    private Wind avgWind;
    private Wind highWind;
    private Wind gust;
    private Pressure baroPressure;
    private Float avgUvIndex;
    private UvIndex highUvIndex;
    private SolarRadiation avgSolarRadiation;
    private SolarRadiation highSolarRadiation;
    private Depth evapotranspiration;
    private Depth rainfall;
    private Depth highRainfallRate;
    
    private final Map<Integer,MeasurementEntry<Temperature>> temperatureSensorEntries = new TreeMap<>();
    
    private final Map<Integer,MeasurementEntry<Humidity>> humiditySensorEntries = new TreeMap<>();

    private final Map<Integer,MeasurementEntry<LeafWetness>> leafWetnessSensorEntries = new TreeMap<>();
    
    private final Map<Integer,MeasurementEntry<SoilMoisture>> soilMoistureSensorEntries = new TreeMap<>();

    /**
     * Constructor.
     */
    public HistoricalRecord() {
    }

    /**
     * Constructor.
     *
     * @param time The time of this historical record
     */
    public HistoricalRecord(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Get the time of the historical record.
     *
     * @return The time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Set the time of the historical record.
     *
     * @param time The time
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Set the duration over which this historical record was measured.
     *
     * @param duration The duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Get the duration over which this historical record was measured.
     *
     * @return The duration
     */
    public Duration getDuration() {
        return duration;
    }
    
    /**
     * Get a collection of the measurements from the non-standard sensors.
     *
     * @return The collection of measurements
     */
    public Collection<MeasurementEntry<? extends Measurement>> getMeasurementEntries() {
        List<MeasurementEntry<? extends Measurement>> entries = new ArrayList<>();
        entries.addAll(temperatureSensorEntries.values());
        entries.addAll(humiditySensorEntries.values());
        entries.addAll(soilMoistureSensorEntries.values());
        entries.addAll(leafWetnessSensorEntries.values());
        return entries;
    }
    
    /**
     * Get the temperature for a given sensor.
     *
     * @param sensorId The sensor ID
     * @return The temperature or null if the sensor ID does not exist
     */
    public Temperature getTemperatureForSensor(int sensorId) {
        MeasurementEntry<Temperature> entry = temperatureSensorEntries.get(sensorId);
		
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    /**
     * Set the temperature for a given sensor.
     *
     * @param sensorId The sensor ID
     * @param temperature
     */
    public void setTemperatureForSensor(int sensorId, Temperature temperature) {
        MeasurementEntry<Temperature> entry = new MeasurementEntry<>(sensorId, SensorType.THERMOMETER, temperature);
        temperatureSensorEntries.put(sensorId, entry);
    }
    
    /**
     * Get the humidity for a given sensor.
     *
     * @param sensorId The sensor ID
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
     * @param sensorId The sensor ID
     * @param leafWetness The leaf wetness
     */
    public void setLeafWetnessForSensor(int sensorId, LeafWetness leafWetness) {
        MeasurementEntry<LeafWetness> entry = new MeasurementEntry<>(sensorId, SensorType.LEAF_WETNESS, leafWetness);
        leafWetnessSensorEntries.put(sensorId, entry);
    }
    
    /**
     * Get the leaf wetness for a given sensor.
     *
     * @param sensorId The sensor ID
     * @return The leaf wetness or null if the sensor does not exist
     */
    public LeafWetness getLeafWetnessForSensor(int sensorId) {
        MeasurementEntry<LeafWetness> entry = leafWetnessSensorEntries.get(sensorId);
        
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    /**
     * Set the soil moisture of a given sensor.
     *
     * @param sensorId The sensor ID
     * @param soilMoisture The soil moisture
     */
    public void setSoilMoistureForSensor(int sensorId, SoilMoisture soilMoisture) {
        MeasurementEntry<SoilMoisture> entry = new MeasurementEntry<>(sensorId, SensorType.SOIL_MOISTURE, soilMoisture);
        soilMoistureSensorEntries.put(sensorId, entry);
    }
    
    /**
     * Get the soil moisture for a given sensor.
     *
     * @param sensorId The sensor ID
     * @return The soil moisture or null if the sensor does not exist
     */
    public SoilMoisture getSoilMoistureForSensor(int sensorId) {
        MeasurementEntry<SoilMoisture> entry = soilMoistureSensorEntries.get(sensorId);
        
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    /**
     * Set the humidity for a given sensor.
     *
     * @param sensorId The sensor ID
     * @param humidity The humidity
     */
    public void setHumidityForSensor(int sensorId, Humidity humidity) {
        MeasurementEntry<Humidity> entry = new MeasurementEntry<>(sensorId, SensorType.HYGROMETER, humidity);
        humiditySensorEntries.put(sensorId, entry);
    }
    
    /**
     * Get the average of the outdoor temperature over the duration.
     *
     * @return The temperature
     */
    public Temperature getAvgOutdoorTemperature() {
        return avgOutdoorTemperature;
    }
    
    /**
     *
     * Get the low outdoor temperature over the duration.
     *
     * @return The temperature
     */
    public Temperature getLowOutdoorTemperature() {
        return lowOutdoorTemperature;
    }
    
    /**
     *
     * Get the high outdoor temperature over the duration.
     *
     * @return The temperature
     */
    public Temperature getHighOutdoorTemperature() {
        return highOutdoorTemperature;
    }
    
    /**
     * Set the average of the outdoor temperature over the duration.
     *
     * @param temperature The average temperature
     */
    public void setAvgOutdoorTemperature(Temperature temperature) {
         this.avgOutdoorTemperature = temperature;
    }
    
    /**
     * Set the high outdoor temperature over the duration.
     *
     * @param temperature The high temperature
     */
    public void setHighOutdoorTemperature(Temperature temperature) {
        this.highOutdoorTemperature = temperature;
    }
    
    /**
     * Set the low outdoor temperature over the duration.
     *
     * @param temperature
     */
    public void setLowOutdoorTemperature(Temperature temperature) {
        this.lowOutdoorTemperature = temperature;
    }
    
    /**
     * Get the outdoor humidity
     *
     * @return The humidity
     */
    public Humidity getOutdoorHumidity() {
        return outdoorHumidity;
    }
    
    /**
     * Set the outdoor humidity.
     *
     * @param humidity
     */
    public void setOutdoorHumidity(Humidity humidity) {
        this.outdoorHumidity = humidity;
    }

    /**
     * Get the indoor temperature.
     *
     * @return The temperature
     */
    public Temperature getIndoorTemperature() {
        return indoorTemperature;
    }
    
    /**
     * Set the indoor temperature.
     *
     * @param temperature
     */
    public void setIndoorTemperature(Temperature temperature) {
        this.indoorTemperature = temperature;
    }
    
    /**
     * Get the indoor humidity.
     *
     * @return The humidity
     */
    public Humidity getIndoorHumidity() {
        return indoorHumidity;
    }
    
    /**
     * Set the indoor humidity.
     *
     * @param humidity The humidity
     */
    public void setIndoorHumidity(Humidity humidity) {
        this.indoorHumidity = humidity;
    }

    /**
     * Set the average of the wind over the duration.
     *
     * @param wind The wind
     */
    public void setAvgWind(Wind wind) {
        this.avgWind = wind;
    }

    /**
     * Get the average of the wind over the duration.
     *
     * @return The wind
     */
    public Wind getAvgWind() {
        return avgWind;
    }

    /**
     * Set the high wind over the duration.
     *
     * @param wind The wind
     */
    public void setHighWind(Wind wind) {
        this.highWind = wind;
    }
    
    /**
     * Get the high wind over the duration.
     *
     * @return The wind
     */
    public Wind getHighWind() {
        return highWind;
    }
    
    /**
     * Set the highest gust over the duration.
     *
     * @param gust The wind gust
     */
    public void setWindGust(Wind gust) {
        this.gust = gust;
    }
    
    /**
     * Get the highest gust over the duration.
     *
     * @return The wind gust
     */
    public Wind getWindGust() {
        return gust;
    }

    /**
     * Set the barometric pressure.
     *
     * @param pressure The barometric pressure
     */
    public void setBaroPressure(Pressure pressure) {
        this.baroPressure = pressure;
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
     * Set the rainfall over the duration.
     *
     * @param rain The rainfall amount
     */
    public void setRainfall(Depth rain) {
        this.rainfall = rain;
    }

    /**
     * Get the rainfall over the duration.
     *
     * @return The rainfall amount
     */
    public Depth getRainfall() {
        return rainfall;
    }
    
    /**
     * Set the highest rainfall rate over the duration.
     *
     * @param rate The highest rainfall rate
     */
    public void setHighRainfallRate(Depth rate) {
        this.highRainfallRate = rate;
    }
    
    /**
     * Get the highest rainfall rate over the duration.
     *
     * @return The highest rainfall rate
     */
    public Depth getHighRainfallRate() {
        return highRainfallRate;
    }

    /**
     * Set the average UV index over the duration.
     *
     * @param uvIndex The average UV index
     */
    public void setAvgUvIndex(Float uvIndex) {
        this.avgUvIndex = uvIndex;
    }
    
    /**
     * Get the average UV index over the duration.
     *
     * @return The average UV index
     */
    public Float getAvgUvIndex() {
        return avgUvIndex;
    }

    /**
     * Set the highest UV index over the duration.
     *
     * @param uvIndex The highest UV index
     */
    public void setHighUvIndex(UvIndex uvIndex) {
        this.highUvIndex = uvIndex;
    }
    
    /**
     * Get the highest UV index over the duration.
     *
     * @return The highest UV index
     */
    public UvIndex getHighUvIndex() {
        return highUvIndex;
    }

    /**
     * Set the average solar radiation over the duration.
     *
     * @param solarRadiation The average solar radiation
     */
    public void setAvgSolarRadiation(SolarRadiation solarRadiation) {
        this.avgSolarRadiation = solarRadiation;
    }
    
    /**
     * Get the average solar radiation over the duration.
     *
     * @return The average solar radiation
     */
    public SolarRadiation getAvgSolarRadiation() {
        return avgSolarRadiation;
    }

    /**
     * Set the high solar radiation over the duration.
     *
     * @param solarRadiation The high solar radiation
     */
    public void setHighSolarRadiation(SolarRadiation solarRadiation) {
        this.highSolarRadiation = solarRadiation;
    }
    
    /**
     * Get the high solar radiation over the duration.
     *
     * @return the high solar radiation
     */
    public SolarRadiation getHighSolarRadiation() {
        return highSolarRadiation;
    }

    /**
     * Set the evapotranspiration over the duration.
     *
     * @param evapotranspiration The ET
     */
    public void setEvapotranspiration(Depth evapotranspiration) {
        this.evapotranspiration = evapotranspiration;
    }
    
    /**
     * Get the evapotranspiration over the duration.
     *
     * @return The ET
     */
    public Depth getEvapotranspiration() {
        return evapotranspiration;
    }

    /**
     * Get the wind chill.
     *
     * @return The wind chill or null if the wind chill is missing
     */
    public Temperature getWindChill() {
        if (avgOutdoorTemperature != null && avgWind != null)
            return WeatherUtils.windChill(avgOutdoorTemperature, avgWind.getSpeed(), true);
        else
            return null;
    }

    /**
     * Get the dew point.
     *
     * @return The dew point or null if the dew point is missing
     */
    public Temperature getDewPoint() {
        if (avgOutdoorTemperature != null && outdoorHumidity != null)
            return WeatherUtils.dewPoint(avgOutdoorTemperature, outdoorHumidity);
        else
            return null;
    }

    /**
     * Get the heat index.
     *
     * @return The heat index or null if the heat index is missing
     */
    public Temperature getHeatIndex() {
        if (avgOutdoorTemperature != null && outdoorHumidity != null)
            return WeatherUtils.heatIndex(avgOutdoorTemperature, outdoorHumidity);
        else
            return null;
    }

    /**
     * Get the average of the wind speed over the duration.
     *
     * @return The average wind speed
     */
    public Speed getAvgWindSpeed() {
        if (avgWind != null)
            return avgWind.getSpeed();
        else
            return null;
    }
    
    /**
     * Get the average wind direction over the duration.
     *
     * @return The average wind direction
     */
    public Heading getAvgWindDirection() {
        if (avgWind != null)
            return avgWind.getDirection();
        else
            return null;
    }
    
    /**
     * Get the highest wind speed over the duration.
     *
     * @return The wind speed
     */
    public Speed getHighWindSpeed() {
        if (highWind != null)
            return highWind.getSpeed();
        else
            return null;
    }
    
    /**
     * Get the direction of the wind when the wind speed is highest over the duration.
     *
     * @return the direction of the highest wind speed or null
     */
    public Heading getHighWindDirection() {
        if (highWind != null)
            return highWind.getDirection();
        else
            return null;
    }

    /**
     * Get the speed of the highest wind gust over the duration.
     *
     * @return The highest wind gust or null if there was no gusts
     */
    public Speed getWindGustSpeed() {
        if (gust != null)
            return gust.getSpeed();
        else
            return null;
    }
    
    /**
     * Get the direction of the highest wind gust over the duration.
     *
     * @return The direction of the highest wind gust or null if there were no gusts
     */
    public Heading getWindGustDirection() {
        if (gust != null)
            return gust.getDirection();
        else
            return null;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

        String s = fmt.format(time) + " " + duration + " " + getIndoorTemperature() + " "
                + getAvgOutdoorTemperature() + " " + getIndoorHumidity() + " " + getOutdoorHumidity();

        String w;

        if (avgWind == null) {
            w = " null null null";
        }
        else {
            w = " " + avgWind.getSpeed() + " " + avgWind.getDirection();
        }

        s += "Avg Wind: " + w;

        if (gust == null) {
            w = " null null null";
        }
        else {
            w = " " + avgWind.getSpeed() + " " + avgWind.getDirection();
        }

        s += "Gust: " + w;

        String s2 = " " + rainfall + " " + avgUvIndex + " " + evapotranspiration;
        
        String ts = "\n";
        for (MeasurementEntry<Temperature> entry : temperatureSensorEntries.values()) {
            ts += "temperture sensor[" + entry.getSensorId() + "] " + entry.getMeasurement() + "\n";
        }

        return s + s2 + ts;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.time);
        hash = 67 * hash + Objects.hashCode(this.duration);
        hash = 67 * hash + Objects.hashCode(this.avgOutdoorTemperature);
        hash = 67 * hash + Objects.hashCode(this.highOutdoorTemperature);
        hash = 67 * hash + Objects.hashCode(this.lowOutdoorTemperature);
        hash = 67 * hash + Objects.hashCode(this.outdoorHumidity);
        hash = 67 * hash + Objects.hashCode(this.indoorTemperature);
        hash = 67 * hash + Objects.hashCode(this.indoorHumidity);
        hash = 67 * hash + Objects.hashCode(this.avgWind);
        hash = 67 * hash + Objects.hashCode(this.highWind);
        hash = 67 * hash + Objects.hashCode(this.gust);
        hash = 67 * hash + Objects.hashCode(this.baroPressure);
        hash = 67 * hash + Objects.hashCode(this.avgUvIndex);
        hash = 67 * hash + Objects.hashCode(this.highUvIndex);
        hash = 67 * hash + Objects.hashCode(this.avgSolarRadiation);
        hash = 67 * hash + Objects.hashCode(this.highSolarRadiation);
        hash = 67 * hash + Objects.hashCode(this.evapotranspiration);
        hash = 67 * hash + Objects.hashCode(this.rainfall);
        hash = 67 * hash + Objects.hashCode(this.highRainfallRate);
        hash = 67 * hash + Objects.hashCode(this.temperatureSensorEntries);
        hash = 67 * hash + Objects.hashCode(this.humiditySensorEntries);
        hash = 67 * hash + Objects.hashCode(this.soilMoistureSensorEntries);
        hash = 67 * hash + Objects.hashCode(this.leafWetnessSensorEntries);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final HistoricalRecord other = (HistoricalRecord)obj;
        if (!Objects.equals(this.time, other.time))
            return false;

        if (!this.duration.equals(other.duration))
            return false;

        if (!Objects.equals(this.avgOutdoorTemperature, other.avgOutdoorTemperature))
            return false;

        if (!Objects.equals(this.highOutdoorTemperature, other.highOutdoorTemperature))
            return false;

        if (!Objects.equals(this.lowOutdoorTemperature, other.lowOutdoorTemperature))
            return false;

        if (!Objects.equals(this.outdoorHumidity, other.outdoorHumidity))
            return false;

        if (!Objects.equals(this.indoorTemperature, other.indoorTemperature))
            return false;

        if (!Objects.equals(this.indoorHumidity, other.indoorHumidity))
            return false;

        if (!Objects.equals(this.avgWind, other.avgWind))
            return false;

        if (!Objects.equals(this.highWind, other.highWind))
            return false;

        if (!Objects.equals(this.gust, other.gust))
            return false;

        if (!Objects.equals(this.baroPressure, other.baroPressure))
            return false;

        if (!Objects.equals(this.avgUvIndex, other.avgUvIndex))
            return false;

        if (!Objects.equals(this.highUvIndex, other.highUvIndex))
            return false;

        if (!Objects.equals(this.avgSolarRadiation, other.avgSolarRadiation))
            return false;

        if (!Objects.equals(this.highSolarRadiation, other.highSolarRadiation))
            return false;

        if (!Objects.equals(this.evapotranspiration, other.evapotranspiration))
            return false;

        if (!Objects.equals(this.rainfall, other.rainfall))
            return false;

        if (!Objects.equals(this.highRainfallRate, other.highRainfallRate))
            return false;

        if (!Objects.equals(this.temperatureSensorEntries, other.temperatureSensorEntries))
            return false;

        if (!Objects.equals(this.humiditySensorEntries, other.humiditySensorEntries))
            return false;

        if (!Objects.equals(this.leafWetnessSensorEntries, other.leafWetnessSensorEntries))
            return false;

        return Objects.equals(this.soilMoistureSensorEntries, other.soilMoistureSensorEntries);
    }
}
