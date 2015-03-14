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

    @XmlJavaTypeAdapter(type=java.time.LocalDateTime.class, value=LocalDateTimeAdapter.class)
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setIndoorTemperature(Temperature temp) {
        this.indoorTemp = temp;
    }

    public Temperature getIndoorTemperature() {
        return indoorTemp;
    }

    public void setOutdoorTemperature(Temperature temp) {
        this.outdoorTemp = temp;
    }

    public Temperature getOutdoorTemperature() {
        return outdoorTemp;
    }

    public void setWindChill(Temperature temp) {
        this.windChill = temp;
    }

    public Temperature getWindChill() {
        return windChill;
    }

    public void setHeatIndex(Temperature temp) {
        this.heatIndex = temp;
    }

    public Temperature getHeatIndex() {
        return heatIndex;
    }

    public void setDewPoint(Temperature temp) {
        this.dewPoint = temp;
    }

    public Temperature getDewPoint() {
        return dewPoint;
    }

    public void setThsw(Temperature t) {
        this.thsw = t;
    }

    public Temperature getThsw() {
        return thsw;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Wind getWind() {
        return wind;
    }

    public Speed getWindSpeed2MinAvg() {
        return windSpeed2MinAvg;
    }

    public void setWindSpeed2MinAvg(Speed windSpeed2MinAvg) {
        this.windSpeed2MinAvg = windSpeed2MinAvg;
    }

    public Speed getWindSpeed10MinAvg() {
        return windSpeed10MinAvg;
    }

    public void setWindSpeed10MinAvg(Speed windSpeed10MinAvg) {
        this.windSpeed10MinAvg = windSpeed10MinAvg;
    }

    public void setWindDir2(Heading h) {
        windDir2 = h;
    }

    public Heading getWindDir2() {
        return windDir2;
    }

    public void setWindDir3(Heading h) {
        windDir3 = h;
    }

    public Heading getWindDir3() {
        return windDir3;
    }

    public void setWindDir4(Heading h) {
        windDir4 = h;
    }

    public Heading getWindDir4() {
        return windDir4;
    }

    public void setWindDir5(Heading h) {
        windDir5 = h;
    }

    public Heading getWindDir5() {
        return windDir5;
    }
    
    public Speed getWindSpeed() {
        if (wind != null)
            return wind.getSpeed();
        else
            return null;
    }
    
    public Heading getWindDirection() {
        if (wind != null)
            return wind.getDirection();
        else
            return null;
    }
    
    public void setWindGust(Wind gust) {
        this.gust = gust;
    }
    
    public Wind getWindGust() {
        return gust;
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

    public void setBaroPressure(Pressure pressure) {
        baroPressure = pressure;
    }

    public Pressure getBaroPressure() {
        return baroPressure;
    }

    public void setIndoorHumidity(Humidity humidity) {
        indoorHumidity = humidity;
    }

    public Humidity getIndoorHumidity() {
        return indoorHumidity;
    }

    public void setOutdoorHumidity(Humidity humidity) {
        outdoorHumidity = humidity;
    }

    public Humidity getOutdoorHumidity() {
        return outdoorHumidity;
    }

    public WeatherTrend getBaroTrend() {
        return baroTrend;
    }

    public void setBaroTrend(WeatherTrend t) {
        baroTrend = t;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast f) {
        this.forecast = f;
    }
    
    public String getForecastRule() {
        return forecastRule;
    }
    
    public void setForecastRule(String forecastRule) {
        this.forecastRule = forecastRule;
    }

    public void setUvIndex(UvIndex uvIndex) {
        this.uvIndex = uvIndex;
    }
    
    public UvIndex getUvIndex() {
        return uvIndex;
    }

    public SolarRadiation getSolarRadiation() {
        return solarRadiation;
    }
    public void setSolarRadiation(SolarRadiation solarRadiation) {
        this.solarRadiation = solarRadiation;
    }
    
    public Depth getRainRate() {
        return rainRate;
    }
    
    public void setRainRate(Depth rate) {
        this.rainRate = rate;
    }

    public Depth getRain15Minute() {
        return rain15Minute;
    }

    public void setRain15Minute(Depth rain15Minute) {
        this.rain15Minute = rain15Minute;
    }
    
    public Depth getRainHour() {
        return rainHour;
    }

    public void setRainHour(Depth rainHour) {
        this.rainHour = rainHour;
    }

    public Depth getRain24Hour() {
        return rain24Hour;
    }

    public void setRain24Hour(Depth rain24Hour) {
        this.rain24Hour = rain24Hour;
    }

    public Depth getRainToday() {
        return rainToday;
    }

    public void setRainToday(Depth rainToday) {
        this.rainToday = rainToday;
    }

    public Depth getRainMonth() {
        return rainMonth;
    }

    public void setRainMonth(Depth rainMonth) {
        this.rainMonth = rainMonth;
    }

    public Depth getRainWeatherYear() {
        return rainWeatherYear;
    }

    public void setRainWeatherYear(Depth rainWeatherYear) {
        this.rainWeatherYear = rainWeatherYear;
    }

    public Depth getRainCalendarYear() {
        return rainCalendarYear;
    }

    public void setRainCalendarYear(Depth rainCalendarYear) {
        this.rainCalendarYear = rainCalendarYear;
    }

    public LocalDate getStormStart() {
        if (stormStart != null)
            return stormStart;
        else
            return null;
    }

    @XmlJavaTypeAdapter(type=java.time.LocalDate.class, value=LocalDateAdapter.class)
    public void setStormStart(LocalDate stormStart) {
        this.stormStart = stormStart;
    }

    public Depth getStormRain() {
        return stormRain;
    }

    public void setStormRain(Depth stormRain) {
        this.stormRain = stormRain;
    }
    
    public Depth getDayET() {
        return dayET;
    }
    
    public void setDayET(Depth et) {
        this.dayET = et;
    }
    
    public Depth getMonthET() {
        return monthET;
    }
    
    public void setMonthET(Depth et) {
        this.monthET = et;
    }
    
    public Depth getYearET() {
        return yearET;
    }
    
    public void setYearET(Depth et) {
        this.yearET = et;
    }
    
    public void setTemperatureForSensor(int sensorId, Temperature t) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.THERMOMETER, t));
    }
    
    public void setSoilTemperatureForSensor(int sensorId, Temperature t) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_TEMPERATURE, t));
    }
    
    public void setLeafTemperatureForSensor(int sensorId, Temperature t) {
        temperatureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_TEMPERATURE, t));
    }
    
    public Temperature getTemperatureForSensor(int sensorId) {
        MeasurementEntry<Temperature> entry = temperatureSensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setHumidityForSensor(int sensorId, Humidity h) {
        humiditySensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.HYGROMETER, h));
    }
    
    public Humidity getHumidityForSensor(int sensorId) {
        MeasurementEntry<Humidity> entry = humiditySensorEntries.get(sensorId);
        if (entry != null)
            return entry.getMeasurement();
        else
            return null;
    }
    
    public void setLeafWetnessForSensor(int sensorId, LeafWetness lw) {
        leafWetnessSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.LEAF_WETNESS, lw));
    }
    
    public LeafWetness getLeafWetnessForSensor(int sensorId) {
        return leafWetnessSensorEntries.get(sensorId).getMeasurement();
    }
    
    public void setSoilMoistureForSensor(int sensorId, SoilMoisture sm) {
        soilMoistureSensorEntries.put(sensorId, new MeasurementEntry<>(sensorId, SensorType.SOIL_MOISTURE, sm));
    }
   
    public SoilMoisture getSoilMoistureForSensor(int sensorId) {
        return soilMoistureSensorEntries.get(sensorId).getMeasurement();
    }
    
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
