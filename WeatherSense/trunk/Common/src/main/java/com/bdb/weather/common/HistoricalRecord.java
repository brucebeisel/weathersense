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
import com.bdb.weather.common.xml.DurationAdapter;

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
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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

@XmlRootElement
public class HistoricalRecord {
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
    
    @XmlElement(name="temperatureSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<Temperature>> temperatureSensorEntries = new TreeMap<>();
    
    @XmlElement(name="humiditySensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<Humidity>> humiditySensorEntries = new TreeMap<>();

    @XmlElement(name="leafWetnessSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<LeafWetness>> leafWetnessSensorEntries = new TreeMap<>();
    
    @XmlElement(name="soilMoistureSensorValues", type=MeasurementEntry.class)
    private final Map<Integer,MeasurementEntry<SoilMoisture>> soilMoistureSensorEntries = new TreeMap<>();

    private static final Logger logger = Logger.getLogger(HistoricalRecord.class.getName());

    public HistoricalRecord() {
    }

    public HistoricalRecord(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @XmlJavaTypeAdapter(type=java.time.LocalDateTime.class, value=LocalDateTimeAdapter.class)
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @XmlJavaTypeAdapter(type=java.time.Duration.class, value=DurationAdapter.class)
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }
    
    public Collection<MeasurementEntry<? extends Measurement>> getMeasurementEntries() {
        List<MeasurementEntry<? extends Measurement>> entries = new ArrayList<>();
        entries.addAll(temperatureSensorEntries.values());
        entries.addAll(humiditySensorEntries.values());
        entries.addAll(soilMoistureSensorEntries.values());
        entries.addAll(leafWetnessSensorEntries.values());
        return entries;
    }
    
    public Temperature getTemperatureForSensor(int sensorId) {
        MeasurementEntry<Temperature> entry = temperatureSensorEntries.get(sensorId);
		
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setTemperatureForSensor(int sensorId, Temperature t) {
        MeasurementEntry<Temperature> entry = new MeasurementEntry<>(sensorId, SensorType.THERMOMETER, t);
        temperatureSensorEntries.put(sensorId, entry);
    }
    
    public Humidity getHumidityForSensor(int sensorId) {
        MeasurementEntry<Humidity> entry = humiditySensorEntries.get(sensorId);
        
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setLeafWetnessForSensor(int sensorId, LeafWetness lw) {
        MeasurementEntry<LeafWetness> entry = new MeasurementEntry<>(sensorId, SensorType.LEAF_WETNESS, lw);
        leafWetnessSensorEntries.put(sensorId, entry);
    }
    
    public LeafWetness getLeafWetnessForSensor(int sensorId) {
        MeasurementEntry<LeafWetness> entry = leafWetnessSensorEntries.get(sensorId);
        
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setSoilMoistureForSensor(int sensorId, SoilMoisture sm) {
        MeasurementEntry<SoilMoisture> entry = new MeasurementEntry<>(sensorId, SensorType.SOIL_MOISTURE, sm);
        soilMoistureSensorEntries.put(sensorId, entry);
    }
    
    public SoilMoisture getSoilMoistureForSensor(int sensorId) {
        MeasurementEntry<SoilMoisture> entry = soilMoistureSensorEntries.get(sensorId);
        
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setHumidityForSensor(int sensorId, Humidity h) {
        MeasurementEntry<Humidity> entry = new MeasurementEntry<>(sensorId, SensorType.HYGROMETER, h);
        humiditySensorEntries.put(sensorId, entry);
    }
    
    public Temperature getAvgOutdoorTemperature() {
        return avgOutdoorTemperature;
    }
    
    public Temperature getLowOutdoorTemperature() {
        return lowOutdoorTemperature;
    }
    
    public Temperature getHighOutdoorTemperature() {
        return highOutdoorTemperature;
    }
    
    public void setAvgOutdoorTemperature(Temperature t) {
         this.avgOutdoorTemperature = t;
    }
    
    public void setHighOutdoorTemperature(Temperature t) {
        this.highOutdoorTemperature = t;
    }
    
    public void setLowOutdoorTemperature(Temperature t) {
        this.lowOutdoorTemperature = t;
    }
    
    public Humidity getOutdoorHumidity() {
        return outdoorHumidity;
    }
    
    public void setOutdoorHumidity(Humidity h) {
        this.outdoorHumidity = h;
    }

    public Temperature getIndoorTemperature() {
        return indoorTemperature;
    }
    
    public void setIndoorTemperature(Temperature t) {
        this.indoorTemperature = t;
    }
    
    public Humidity getIndoorHumidity() {
        return indoorHumidity;
    }
    
    public void setIndoorHumidity(Humidity h) {
        this.indoorHumidity = h;
    }

    public void setAvgWind(Wind wind) {
        this.avgWind = wind;
    }

    public Wind getAvgWind() {
        return avgWind;
    }

    public void setHighWind(Wind wind) {
        this.highWind = wind;
    }
    
    public Wind getHighWind() {
        return highWind;
    }
    
    public void setWindGust(Wind gust) {
        this.gust = gust;
    }
    
    public Wind getWindGust() {
        return gust;
    }

    public void setBaroPressure(Pressure pressure) {
        this.baroPressure = pressure;
    }

    public Pressure getBaroPressure() {
        return baroPressure;
    }

    public void setRainfall(Depth rain) {
        this.rainfall = rain;
    }

    public Depth getRainfall() {
        return rainfall;
    }
    
    public void setHighRainfallRate(Depth rate) {
        this.highRainfallRate = rate;
    }
    
    public Depth getHighRainfallRate() {
        return highRainfallRate;
    }

    public void setAvgUvIndex(Float uvIndex) {
        this.avgUvIndex = uvIndex;
    }
    
    public Float getAvgUvIndex() {
        return avgUvIndex;
    }

    public void setHighUvIndex(UvIndex uvIndex) {
        this.highUvIndex = uvIndex;
    }
    
    public UvIndex getHighUvIndex() {
        return highUvIndex;
    }

    public void setAvgSolarRadiation(SolarRadiation solarRadiation) {
        this.avgSolarRadiation = solarRadiation;
    }
    
    public SolarRadiation getAvgSolarRadiation() {
        return avgSolarRadiation;
    }

    public void setHighSolarRadiation(SolarRadiation solarRadiation) {
        this.highSolarRadiation = solarRadiation;
    }
    
    public SolarRadiation getHighSolarRadiation() {
        return highSolarRadiation;
    }

    public void setEvapotranspiration(Depth evapotranspiration) {
        this.evapotranspiration = evapotranspiration;
    }
    
    public Depth getEvapotranspiration() {
        return evapotranspiration;
    }

    public Temperature getWindChill() {
        if (avgOutdoorTemperature != null && avgWind != null)
            return WeatherUtils.windChill(avgOutdoorTemperature, avgWind.getSpeed(), true);
        else
            return null;
    }

    public Temperature getDewPoint() {
        if (avgOutdoorTemperature != null && outdoorHumidity != null)
            return WeatherUtils.dewPoint(avgOutdoorTemperature, outdoorHumidity);
        else
            return null;
    }

    public Temperature getHeatIndex() {
        if (avgOutdoorTemperature != null && outdoorHumidity != null)
            return WeatherUtils.heatIndex(avgOutdoorTemperature, outdoorHumidity);
        else
            return null;
    }

    public Speed getAvgWindSpeed() {
        if (avgWind != null)
            return avgWind.getSpeed();
        else
            return null;
    }
    
    public Heading getAvgWindDirection() {
        if (avgWind != null)
            return avgWind.getDirection();
        else
            return null;
    }
    
    public Speed getHighWindSpeed() {
        if (highWind != null)
            return highWind.getSpeed();
        else
            return null;
    }
    
    public Heading getHighWindDirection() {
        if (highWind != null)
            return highWind.getDirection();
        else
            return null;
    }

    public Speed getWindGustSpeed() {
        if (gust != null)
            return gust.getSpeed();
        else
            return null;
    }
    
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
