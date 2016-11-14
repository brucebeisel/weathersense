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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.MeasurementStatistics;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author bruce
 */
public class SummaryRecord {
    private final LocalDate date;
    private Duration        duration = Duration.ZERO;
    
    private static final String NO_VALUE = "N/A";

    private final MeasurementStatistics<Temperature> indoorTemp = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Temperature> outdoorTemp = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Humidity>    indoorHumidity = new MeasurementStatistics<>(new Humidity(0.0));
    private final MeasurementStatistics<Humidity>    outdoorHumidity = new MeasurementStatistics<>(new Humidity(0.0));
    private final MeasurementStatistics<Pressure>    baroPressure = new MeasurementStatistics<>(new Pressure(0.0));
    private final MeasurementStatistics<Speed>       windSpeed = new MeasurementStatistics<>(new Speed(0.0));
    private final MeasurementStatistics<Speed>       windGust = new MeasurementStatistics<>(new Speed(0.0));
    
    private final MeasurementStatistics<Temperature> outdoorHighTemp = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Temperature> outdoorMeanTemp = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Temperature> outdoorLowTemp = new MeasurementStatistics<>(new Temperature(0.0));

    private final MeasurementStatistics<Temperature> heatIndex = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Temperature> windChill = new MeasurementStatistics<>(new Temperature(0.0));
    private final MeasurementStatistics<Temperature> dewpoint = new MeasurementStatistics<>(new Temperature(0.0));

    private final MeasurementStatistics<SolarRadiation> solarRadiation = new MeasurementStatistics<>(new SolarRadiation(0.0));

    private final WindRoseData windRoseData;

    private Depth totalRainfall = new Depth(0.0);
    private Depth maxRainfallRate;  // In units for mm/hour
    private LocalDateTime maxRainfallRateTime;
    private DayHourRain hourlyRainfall;
    private Depth totalET = new Depth(0.0);

    private final List<TemperatureBinDuration> temperatureBinDurations = new ArrayList<>();
    private final Map<Integer,MeasurementStatisticsEntry<? extends Measurement>> sensors = new TreeMap<>();

    private static final DateTimeFormatter sdf = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final Logger logger = Logger.getLogger(SummaryRecord.class.getName());

    /**
     *
     * @param windParameters
     * @param temperatureBinMgr
     * @param date
     */
    public SummaryRecord(WindParameters windParameters, TemperatureBinMgr temperatureBinMgr, LocalDate date) {
        this.date = date;
        windRoseData = new WindRoseData(date, windParameters);
        hourlyRainfall = new DayHourRain(date);

        temperatureBinMgr.getAllBins().stream().forEach((bin) -> {
            temperatureBinDurations.add(new TemperatureBinDuration(bin, date));
        });
    }

    /**
     *
     * @return
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public Duration getOutdoorTempDuration() {
        return outdoorTemp.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Temperature getMinOutdoorTemp() {
        return outdoorTemp.getMinimumValue();
    }

    /**
     *
     * @param t
     * @param time
     */
    public void setMinOutdoorTemp(Temperature t, LocalDateTime time) {
        outdoorTemp.setMinimum(t, time);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinOutdoorTempTime() {
        return outdoorTemp.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getMaxOutdoorTemp() {
        return outdoorTemp.getMaximumValue();
    }

    /**
     *
     * @param t
     * @param time
     */
    public void setMaxOutdoorTemp(Temperature t, LocalDateTime time) {
        outdoorTemp.setMaximum(t, time);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxOutdoorTempTime() {
        return outdoorTemp.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getAvgOutdoorTemp() {
        return outdoorTemp.getAverage();
    }

    /**
     *
     * @param t
     * @param duration
     */
    public void setAvgOutdoorTemp(Temperature t, Duration duration) {
        outdoorTemp.setAverage(t, duration);
    }
    
    /**
     *
     * @return
     */
    public Temperature getMaxHighOutdoorTemp() {
        Temperature max = outdoorHighTemp.getMaximumValue();
        if (max != null)
            return max;
        else
            return outdoorTemp.getMaximumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMaxHighOutdoorTempTime() {
        LocalDateTime t = outdoorHighTemp.getMaximumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMaximumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getMinHighOutdoorTemp() {
        Temperature min = outdoorHighTemp.getMinimumValue();
        if (min != null)
            return min;
        else
            return outdoorTemp.getMaximumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMinHighOutdoorTempTime() {
        LocalDateTime t = outdoorHighTemp.getMinimumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMaximumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getAvgHighOutdoorTemp() {
        Temperature avg = outdoorHighTemp.getAverage();
        if (avg != null)
            return avg;
        else
            return outdoorTemp.getAverage();
    }
    
    /**
     *
     * @return
     */
    public Temperature getMaxLowOutdoorTemp() {
        Temperature max = outdoorLowTemp.getMaximumValue();
        if (max != null)
            return max;
        else
            return outdoorTemp.getMinimumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMaxLowOutdoorTempTime() {
        LocalDateTime t = outdoorLowTemp.getMaximumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMinimumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getMinLowOutdoorTemp() {
        Temperature min = outdoorLowTemp.getMinimumValue();
        if (min != null)
            return min;
        else
            return outdoorTemp.getMinimumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMinLowOutdoorTempTime() {
        LocalDateTime t = outdoorLowTemp.getMinimumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMinimumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getAvgLowOutdoorTemp() {
        Temperature avg = outdoorLowTemp.getAverage();
        if (avg != null)
            return avg;
        else
            return outdoorTemp.getAverage();
    }
    
    //
    //

    /**
     *
     * @return
     */
    
    public Temperature getMaxMeanOutdoorTemp() {
        Temperature max = outdoorMeanTemp.getMaximumValue();
        if (max != null)
            return max;
        else
            return outdoorTemp.getAverage();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMaxMeanOutdoorTempTime() {
        LocalDateTime t = outdoorMeanTemp.getMaximumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMinimumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getMinMeanOutdoorTemp() {
        Temperature min = outdoorMeanTemp.getMinimumValue();
        if (min != null)
            return min;
        else
            return outdoorTemp.getAverage();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMinMeanOutdoorTempTime() {
        LocalDateTime t = outdoorMeanTemp.getMinimumTime();
        if (t != null)
            return t;
        else
            return outdoorTemp.getMinimumTime();
    }
    
    /**
     *
     * @return
     */
    public Temperature getAvgMeanOutdoorTemp() {
        Temperature avg = outdoorMeanTemp.getAverage();
        if (avg != null)
            return avg;
        else
            return outdoorTemp.getAverage();
    }
    
    /**
     *
     * @return
     */
    public Duration getIndoorTempDuration() {
        return indoorTemp.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Temperature getMinIndoorTemp() {
        return indoorTemp.getMinimumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMinIndoorTemp(Temperature t, LocalDateTime c) {
        indoorTemp.setMinimum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinIndoorTempTime() {
        return indoorTemp.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getMaxIndoorTemp() {
        return indoorTemp.getMaximumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMaxIndoorTemp(Temperature t, LocalDateTime c) {
        indoorTemp.setMaximum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxIndoorTempTime() {
        return indoorTemp.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getAvgIndoorTemp() {
        return indoorTemp.getAverage();
    }

    /**
     *
     * @param t
     * @param duration
     */
    public void setAvgIndoorTemp(Temperature t, Duration duration)
    {
        indoorTemp.setAverage(t, duration);
    }

    /**
     *
     * @return
     */
    public Duration getDewPointDuration() {
        return dewpoint.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Temperature getMinDewPoint() {
        return dewpoint.getMinimumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMinDewPoint(Temperature t, LocalDateTime c) {
        dewpoint.setMinimum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinDewPointTime() {
        return dewpoint.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getMaxDewPoint() {
        return dewpoint.getMaximumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMaxDewPoint(Temperature t, LocalDateTime c) {
        dewpoint.setMaximum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxDewPointTime() {
        return dewpoint.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getAvgDewPoint() {
        return dewpoint.getAverage();
    }

    /**
     *
     * @param t
     * @param duration
     */
    public void setAvgDewPoint(Temperature t, Duration duration) {
        dewpoint.setAverage(t, duration);
    }

    /**
     *
     * @return
     */
    public Duration getHeatIndexDuration() {
        return heatIndex.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Temperature getMinHeatIndex() {
        return heatIndex.getMinimumValue();
    }

    /**
     *
     * @return
     */
    public Temperature getMaxHeatIndex() {
        return heatIndex.getMaximumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMaxHeatIndex(Temperature t, LocalDateTime c) {
        heatIndex.setMaximum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxHeatIndexTime() {
        return heatIndex.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getAvgHeatIndex() {
        return heatIndex.getAverage();
    }

    /**
     *
     * @param t
     * @param duration
     */
    public void setAvgHeatIndex(Temperature t, Duration duration) {
        heatIndex.setAverage(t, duration);
    }

    /**
     *
     * @return
     */
    public Duration getWindChillDuration() {
        return windChill.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Temperature getMinWindChill() {
        return windChill.getMinimumValue();
    }

    /**
     *
     * @param t
     * @param c
     */
    public void setMinWindChill(Temperature t, LocalDateTime c) {
        windChill.setMinimum(t, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinWindChillTime() {
        return windChill.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Temperature getAvgWindChill() {
        return windChill.getAverage();
    }

    /**
     *
     * @param t
     * @param duration
     */
    public void setAvgWindChill(Temperature t, Duration duration) {
        windChill.setAverage(t, duration);
    }

    /**
     *
     * @return
     */
    public Duration getOutdoorHumidityDuration() {
        return outdoorHumidity.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Humidity getMinOutdoorHumidity() {
        return outdoorHumidity.getMinimumValue();
    }

    /**
     *
     * @param h
     * @param c
     */
    public void setMinOutdoorHumidity(Humidity h, LocalDateTime c) {
        outdoorHumidity.setMinimum(h, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinOutdoorHumidityTime() {
        return outdoorHumidity.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Humidity getMaxOutdoorHumidity() {
        return outdoorHumidity.getMaximumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMaxOutdoorHumidityTime() {
        return outdoorHumidity.getMaximumTime();
    }

    /**
     *
     * @param h
     * @param c
     */
    public void setMaxOutdoorHumidity(Humidity h, LocalDateTime c) {
        outdoorHumidity.setMaximum(h, c);
    }

    /**
     *
     * @return
     */
    public Humidity getAvgOutdoorHumidity() {
        return outdoorHumidity.getAverage();
    }

    /**
     *
     * @param h
     * @param duration
     */
    public void setAvgOutdoorHumidity(Humidity h, Duration duration) {
        outdoorHumidity.setAverage(h, duration);
    }

    /**
     *
     * @return
     */
    public Duration getIndoorHumidityDuration() {
        return indoorHumidity.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Humidity getMinIndoorHumidity() {
        return indoorHumidity.getMinimumValue();
    }

    /**
     *
     * @param h
     * @param c
     */
    public void setMinIndoorHumidity(Humidity h, LocalDateTime c) {
        indoorHumidity.setMinimum(h, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMinIndoorHumidityTime() {
        return indoorHumidity.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public Humidity getMaxIndoorHumidity() {
        return indoorHumidity.getMaximumValue();
    }

    /**
     *
     * @param h
     * @param c
     */
    public void setMaxIndoorHumidity(Humidity h, LocalDateTime c) {
        indoorHumidity.setMaximum(h, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxIndoorHumidityTime() {
        return indoorHumidity.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Humidity getAvgIndoorHumidity() {
        return indoorHumidity.getAverage();
    }

    /**
     *
     * @param h
     * @param duration
     */
    public void setAvgIndoorHumidity(Humidity h, Duration duration) {
        indoorHumidity.setAverage(h, duration);
    }

    /**
     *
     * @return
     */
    public SolarRadiation getMaxSolarRadiation() {
        return solarRadiation.getMaximumValue();
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxSolarRadiationTime() {
        return solarRadiation.getMaximumTime();
    }

    /**
     *
     * @param sr
     * @param c
     */
    public void setMaxSolarRadiation(SolarRadiation sr, LocalDateTime c) {
        solarRadiation.setMaximum(sr, c);
    }

    /**
     *
     * @return
     */
    public SolarRadiation getAvgSolarRadiation() {
        return solarRadiation.getAverage();
    }

    /**
     *
     * @return
     */
    public Duration getSolarRadiationDuration() {
        return solarRadiation.getAverageDuration();
    }

    /**
     *
     * @param avg
     * @param duration
     */
    public void setAvgSolarRadiation(SolarRadiation avg, Duration duration) {
        solarRadiation.setAverage(avg, duration);
    }

    /**
     *
     * @return
     */
    public Duration getBaroPressureDuration() {
        return baroPressure.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Pressure getMinBaroPressure() {
        return baroPressure.getMinimumValue();
    }

    /**
     *
     * @param baro
     * @param c
     */
    public void setMinBaroPressure(Pressure baro, LocalDateTime c) {
        baroPressure.setMinimum(baro, c);
    }

    /**
     *
     * @return
     */
    public Pressure getMaxBaroPressure() {
        return baroPressure.getMaximumValue();
    }
    
    /**
     *
     * @return
     */
    public LocalDateTime getMinBaroPressureTime() {
        return baroPressure.getMinimumTime();
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxBaroPressureTime() {
        return baroPressure.getMaximumTime();
    }

    /**
     *
     * @param baro
     * @param c
     */
    public void setMaxBaroPressure(Pressure baro, LocalDateTime c) {
        baroPressure.setMaximum(baro, c);
    }

    /**
     *
     * @return
     */
    public Pressure getAvgBaroPressure() {
        return baroPressure.getAverage();
    }

    /**
     *
     * @param baro
     * @param duration
     */
    public void setAvgBaroPressure(Pressure baro, Duration duration) {
        baroPressure.setAverage(baro, duration);
    }

    /**
     *
     * @return
     */
    public Duration getWindDuration() {
        return windSpeed.getAverageDuration();
    }

    /**
     *
     * @return
     */
    public Speed getMaxWindSpeed() {
        return windSpeed.getMaximumValue();
    }

    /**
     *
     * @param s
     * @param c
     */
    public void setMaxWindSpeed(Speed s, LocalDateTime c) {
        windSpeed.setMaximum(s, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxWindSpeedTime() {
        return windSpeed.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Speed getMaxWindGust() {
        return windGust.getMaximumValue();
    }

    /**
     *
     * @param s
     * @param c
     */
    public void setMaxWindGust(Speed s, LocalDateTime c) {
        windGust.setMaximum(s, c);
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxWindGustTime() {
        return windGust.getMaximumTime();
    }

    /**
     *
     * @return
     */
    public Speed getAvgWindSpeed() {
        return windSpeed.getAverage();
    }

    /**
     *
     * @param s
     * @param duration
     */
    public void setAvgWindSpeed(Speed s, Duration duration) {
        windSpeed.setAverage(s, duration);
    }

    /**
     *
     * @return
     */
    public WindRoseData getWindRoseData() {
        return windRoseData;
    }

    /**
     *
     * @return
     */
    public Depth getTotalRainfall() {
        return totalRainfall;
    }

    /**
     *
     * @param d
     */
    public void setTotalRainfall(Depth d) {
        totalRainfall = d;
    }

    /**
     *
     * @return
     */
    public Depth getMaxRainfallRate() {
        return maxRainfallRate;
    }

    /**
     *
     * @param d
     * @param c
     */
    public void setMaxRainfallRate(Depth d, LocalDateTime c) {
        maxRainfallRate = d;
        maxRainfallRateTime = c;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getMaxRainfallRateTime() {
        return maxRainfallRateTime;
    }

    /**
     *
     * @return
     */
    public DayHourRain getHourlyRainfall() {
        return hourlyRainfall;
    }

    /**
     *
     * @param d
     * @param hour
     */
    public void setRainfallForHour(Depth d, int hour) {
        if (ChronoField.HOUR_OF_DAY.range().isValidValue(hour))
            throw new IllegalArgumentException("Illegal hour specified (" + hour + "). Must be 0 through 23");

        hourlyRainfall.putRain(hour, d);
    }
    
    /**
     *
     * @param rain
     */
    public void setRainfall(DayHourRain rain) {
        hourlyRainfall = rain;
    }

    /**
     *
     * @return
     */
    public Depth getTotalET() {
        return totalET;
    }

    /**
     *
     * @param et
     */
    public void setTotalET(Depth et) {
        totalET = et;
    }

    /**
     *
     * @return
     */
    public List<TemperatureBinDuration> getTemperatureBinDurations() {
        return Collections.unmodifiableList(temperatureBinDurations);
    }
    
    /**
     *
     * @param durations
     */
    public void setTemperatureBinDurations(List<TemperatureBinDuration> durations) {
        temperatureBinDurations.clear();
        temperatureBinDurations.addAll(durations);
    }

    private void applySensorValue(LocalDateTime time, Duration duration, MeasurementEntry<?> entry) {
        int sensorId = entry.getSensorId();
        SensorType sensorType = entry.getSensorType();
        switch (sensorType) {
            case THERMOMETER:
            case LEAF_TEMPERATURE:
            case SOIL_TEMPERATURE:
                MeasurementStatisticsEntry<Temperature> tstats = (MeasurementStatisticsEntry<Temperature>)sensors.get(sensorId);
                if (tstats == null) {
                    tstats = new MeasurementStatisticsEntry<>(time.toLocalDate(), sensorId, sensorType, new MeasurementStatistics<>(new Temperature(0.0)));
                    sensors.put(sensorId, tstats);
                }
                tstats.getStats().applyValue((Temperature)entry.getMeasurement(), time, duration);
                break;

            case HYGROMETER:
                MeasurementStatisticsEntry<Humidity> hstats = (MeasurementStatisticsEntry<Humidity>)sensors.get(sensorId);
                if (hstats == null) {
                    hstats = new MeasurementStatisticsEntry<>(time.toLocalDate(), sensorId, sensorType, new MeasurementStatistics<>(new Humidity(0.0)));
                    sensors.put(sensorId, hstats);
                }
                hstats.getStats().applyValue((Humidity)entry.getMeasurement(), time, duration);
                break;
                
            case SOIL_MOISTURE:
                MeasurementStatisticsEntry<SoilMoisture> mstats = (MeasurementStatisticsEntry<SoilMoisture>)sensors.get(sensorId);
                if (mstats == null) {
                    mstats = new MeasurementStatisticsEntry<>(time.toLocalDate(), sensorId, sensorType, new MeasurementStatistics<>(new SoilMoisture(0.0)));
                    sensors.put(sensorId, mstats);
                }
                mstats.getStats().applyValue((SoilMoisture)entry.getMeasurement(), time, duration);
                break;

            case LEAF_WETNESS:
                MeasurementStatisticsEntry<LeafWetness> wstats = (MeasurementStatisticsEntry<LeafWetness>)sensors.get(sensorId);
                if (wstats == null) {
                    wstats = new MeasurementStatisticsEntry<>(time.toLocalDate(), sensorId, sensorType, new MeasurementStatistics<>(new LeafWetness(0)));
                    sensors.put(sensorId, wstats);
                }
                wstats.getStats().applyValue((LeafWetness)entry.getMeasurement(), time, duration);
                break;
                
            default:
                logger.log(Level.WARNING, "Trying to summarize unsupported measurement type: {0}", sensorType);
                break;
        }
    }

    /**
     *
     * @param summary
     */
    public void addSensorStatistics(MeasurementStatisticsEntry<? extends Measurement> summary) {
	sensors.put(summary.getSensorId(), summary);
    }

    private void applySensorStatistics(MeasurementStatisticsEntry<? extends Measurement> summary) {
        SensorType sensorType = summary.getSensorType();
        int sensorId = summary.getSensorId();

        switch (sensorType) {
            case THERMOMETER:
            case SOIL_TEMPERATURE:
            case LEAF_TEMPERATURE:
                MeasurementStatisticsEntry<Temperature> tentry = (MeasurementStatisticsEntry<Temperature>)sensors.get(sensorId);
                tentry.getStats().applyStatistics((MeasurementStatistics<Temperature>)summary.getStats());
                break;

            case HYGROMETER:
                MeasurementStatisticsEntry<Humidity> hentry = (MeasurementStatisticsEntry<Humidity>)sensors.get(sensorId);
                hentry.getStats().applyStatistics((MeasurementStatistics<Humidity>)summary.getStats());
                break;

            case LEAF_WETNESS:
                MeasurementStatisticsEntry<LeafWetness> wentry = (MeasurementStatisticsEntry<LeafWetness>)sensors.get(sensorId);
                wentry.getStats().applyStatistics((MeasurementStatistics<LeafWetness>)summary.getStats());
                break;

            case SOIL_MOISTURE:
                MeasurementStatisticsEntry<SoilMoisture> mentry = (MeasurementStatisticsEntry<SoilMoisture>)sensors.get(sensorId);
                mentry.getStats().applyStatistics((MeasurementStatistics<SoilMoisture>)summary.getStats());
                break;

            default:
                logger.log(Level.WARNING, "Trying to summarize unsupported measurement type: {0}", sensorType);
                break;
        }
    }

    /**
     *
     * @return
     */
    public Collection<MeasurementStatisticsEntry<? extends Measurement>> getSensorValues() {
	return Collections.unmodifiableCollection(sensors.values());
    }

    private void applyWindToRoseData(Wind wind, Duration duration) {
        windRoseData.measurement(duration, wind);
    }

    private void applyRainfall(Depth d) {
        if (d != null)
            totalRainfall = totalRainfall.add(d);
    }

//    private void resizeHourlyRainfallList(int hour) {
//        if (hourlyRainfall.size() <= hour) {
//            for (int i = hourlyRainfall.size(); i <= hour; i++)
//                hourlyRainfall.add(new Depth(0.0));
//        }
//    }

    private void applyHourlyRainfall(int hour, Depth d) {
        if (d == null)
            return;

        //resizeHourlyRainfallList(hour);

        Depth hourly = hourlyRainfall.getRain(hour);

        if (hourly == null)
            hourlyRainfall.putRain(hour, d);
        else
            hourlyRainfall.putRain(hour, hourly.add(d));
    }

    private void applyRainfallRate(Depth rainfallRate, LocalDateTime rainfallRateTime) {
        if (rainfallRate == null)
            return;

        if (maxRainfallRate == null || maxRainfallRate.compareTo(rainfallRate) < 0) {
            maxRainfallRate = rainfallRate;
            maxRainfallRateTime = rainfallRateTime;
        }
    }

    private void applyET(Depth et) {
        if (et != null)
            totalET = totalET.add(et);
    }

    /**
     *
     * @param rec
     */
    public void applyHistoricalRecord(HistoricalRecord rec) {
        duration = duration.plus(rec.getDuration());
        indoorTemp.applyValue(rec.getIndoorTemperature(), rec.getTime(), rec.getDuration());
        outdoorTemp.applyValues(rec.getLowOutdoorTemperature(), rec.getHighOutdoorTemperature(), rec.getAvgOutdoorTemperature(), rec.getTime(), rec.getDuration());
        outdoorHumidity.applyValue(rec.getOutdoorHumidity(), rec.getTime(), rec.getDuration());
        indoorHumidity.applyValue(rec.getIndoorHumidity(), rec.getTime(), rec.getDuration());
        baroPressure.applyValue(rec.getBaroPressure(), rec.getTime(), rec.getDuration());
        dewpoint.applyValue(rec.getDewPoint(), rec.getTime(), rec.getDuration());
        heatIndex.applyValue(rec.getHeatIndex(), rec.getTime(), rec.getDuration());
        windChill.applyValue(rec.getWindChill(), rec.getTime(), rec.getDuration());
        solarRadiation.applyValues(new SolarRadiation(0.0), rec.getHighSolarRadiation(), rec.getAvgSolarRadiation(), rec.getTime(), rec.getDuration());
        
        Optional<Wind> avgWind = Optional.ofNullable(rec.getAvgWind());
        if (avgWind.isPresent()) {
            windSpeed.applyValues(null, rec.getHighWindSpeed(), avgWind.get().getSpeed(), rec.getTime(), rec.getDuration());
            applyWindToRoseData(avgWind.get(), rec.getDuration());
        }

	if (rec.getWindGust() != null)
            windGust.applyValue(rec.getWindGust().getSpeed(), rec.getTime(), rec.getDuration());

        applyRainfall(rec.getRainfall());
        applyRainfallRate(rec.getHighRainfallRate(),  rec.getTime());
        int hour = rec.getTime().getHour();
        applyHourlyRainfall(hour, rec.getRainfall());
        applyET(rec.getEvapotranspiration());

        for (TemperatureBinDuration bin : temperatureBinDurations) {
            if (rec.getAvgOutdoorTemperature() != null)
                bin.addDuration(rec.getAvgOutdoorTemperature(), rec.getDuration());
        }

        rec.getMeasurementEntries().stream().forEach((e) -> {
            applySensorValue(rec.getTime(), rec.getDuration(), e);
        });
    }

    /**
     *
     * @param rec
     */
    public void applySummaryRecord(SummaryRecord rec) {
        duration = duration.plus(rec.duration);
        indoorTemp.applyStatistics(rec.indoorTemp);
        outdoorTemp.applyStatistics(rec.outdoorTemp);
        indoorHumidity.applyStatistics(rec.indoorHumidity);
        outdoorHumidity.applyStatistics(rec.outdoorHumidity);
        baroPressure.applyStatistics(rec.baroPressure);
        windSpeed.applyStatistics(rec.windSpeed);
        windGust.applyStatistics(rec.windGust);
        outdoorHighTemp.applyValue(rec.outdoorTemp.getMaximumValue(), rec.outdoorTemp.getMaximumTime(), rec.duration);
        outdoorMeanTemp.applyValue(rec.outdoorTemp.getAverage(), rec.outdoorTemp.getMaximumTime(), rec.outdoorTemp.getAverageDuration());
        outdoorLowTemp.applyValue(rec.outdoorTemp.getMinimumValue(), rec.outdoorTemp.getMinimumTime(), rec.duration);
        dewpoint.applyStatistics(rec.dewpoint);
        windChill.applyStatistics(rec.windChill);
        heatIndex.applyStatistics(rec.heatIndex);
        solarRadiation.applyStatistics(rec.solarRadiation);
        applyET(rec.totalET);

        applyRainfall(rec.totalRainfall);
        applyRainfallRate(rec.maxRainfallRate, rec.maxRainfallRateTime);

        rec.hourlyRainfall.getHourValues().stream().forEach((hour) -> {
            applyHourlyRainfall(hour, rec.hourlyRainfall.getRain(hour));
        });

        for (TemperatureBinDuration bin : rec.temperatureBinDurations) {
            for (TemperatureBinDuration thisbin : this.temperatureBinDurations)
                if (thisbin.isSameBin(bin))
                    thisbin.addDuration(bin.getDuration());
        }
        
        windRoseData.addWindRoseData(rec.getWindRoseData());

        sensors.values().stream().forEach((m) -> {
            applySensorStatistics(m);
        });
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;

	if (getClass() != obj.getClass())
	    return false;

	final SummaryRecord other = (SummaryRecord)obj;

	if (!Objects.equals(this.date, other.date))
	    return false;

	if (!Objects.equals(this.duration, other.duration))
	    return false;

	if (!Objects.equals(this.indoorTemp, other.indoorTemp))
	    return false;

	if (!Objects.equals(this.outdoorTemp, other.outdoorTemp))
	    return false;

	if (!Objects.equals(this.indoorHumidity, other.indoorHumidity))
	    return false;

	if (!Objects.equals(this.outdoorHumidity, other.outdoorHumidity))
	    return false;

	if (!Objects.equals(this.baroPressure, other.baroPressure))
	    return false;

	if (!Objects.equals(this.windSpeed, other.windSpeed))
	    return false;

	if (!Objects.equals(this.windGust, other.windGust))
	    return false;

	if (!Objects.equals(this.outdoorHighTemp, other.outdoorHighTemp))
	    return false;

	if (!Objects.equals(this.outdoorMeanTemp, other.outdoorMeanTemp))
	    return false;

	if (!Objects.equals(this.outdoorLowTemp, other.outdoorLowTemp))
	    return false;

	if (!Objects.equals(this.windRoseData, other.windRoseData))
	    return false;

	if (!Objects.equals(this.totalRainfall, other.totalRainfall))
	    return false;

	if (!Objects.equals(this.maxRainfallRate, other.maxRainfallRate))
	    return false;

	if (!Objects.equals(this.maxRainfallRateTime, other.maxRainfallRateTime))
	    return false;

	if (!Objects.equals(this.hourlyRainfall, other.hourlyRainfall))
	    return false;

	if (!Objects.equals(this.temperatureBinDurations, other.temperatureBinDurations))
	    return false;

	return Objects.equals(this.sensors, other.sensors);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 97 * hash + Objects.hashCode(this.date);
	hash = 97 * hash + Objects.hashCode(this.duration);
	hash = 97 * hash + Objects.hashCode(this.indoorTemp);
	hash = 97 * hash + Objects.hashCode(this.outdoorTemp);
	hash = 97 * hash + Objects.hashCode(this.indoorHumidity);
	hash = 97 * hash + Objects.hashCode(this.outdoorHumidity);
	hash = 97 * hash + Objects.hashCode(this.baroPressure);
	hash = 97 * hash + Objects.hashCode(this.windSpeed);
	hash = 97 * hash + Objects.hashCode(this.windGust);
//	hash = 97 * hash + Objects.hashCode(this.outdoorHighTemp);
//	hash = 97 * hash + Objects.hashCode(this.outdoorMeanTemp);
//	hash = 97 * hash + Objects.hashCode(this.outdoorLowTemp);
	hash = 97 * hash + Objects.hashCode(this.windRoseData);
	hash = 97 * hash + Objects.hashCode(this.totalRainfall);
	hash = 97 * hash + Objects.hashCode(this.maxRainfallRate);
	hash = 97 * hash + Objects.hashCode(this.maxRainfallRateTime);
	hash = 97 * hash + Objects.hashCode(this.hourlyRainfall);
	hash = 97 * hash + Objects.hashCode(this.temperatureBinDurations);
	hash = 97 * hash + Objects.hashCode(this.sensors);
	return hash;
    }

    @Override
    public String toString() {
        String t;
        StringBuilder sb = new StringBuilder("Date: " + sdf.format(date) + "\n");
        
        if (outdoorTemp.getMinimumTime() != null)
            t = sdf.format(outdoorTemp.getMinimumTime());
        else
            t = NO_VALUE;
        
        sb.append("Outdoor Temp: Min: ").append(outdoorTemp.getMinimumValue()).append(" (").append(t).append(") ");
        
        if (outdoorTemp.getMaximumTime() != null)
            t = sdf.format(outdoorTemp.getMaximumTime());
        else
            t = NO_VALUE;
            
        sb.append("  Max: ").append( outdoorTemp.getMaximumValue()).append(" (").append(t).append(")");
        
        sb.append("  Avg: ").append(outdoorTemp.getAverage()).append("\n");
        
        if (indoorTemp.getMinimumTime() != null)
            t = sdf.format(indoorTemp.getMinimumTime());
        else
            t = NO_VALUE;
        
        sb.append("Indoor Temp:  Min: ").append(indoorTemp.getMinimumValue()).append(" (").append(t).append(")");
        
        if (indoorTemp.getMaximumTime() != null)
            t = sdf.format(indoorTemp.getMaximumTime());
        else
            t = NO_VALUE;
        
        sb.append("  Max: ").append(indoorTemp.getMaximumValue()).append(" (").append(t).append(")");
        sb.append("  Avg: ").append(indoorTemp.getAverage()).append("\n");
        
        if (outdoorHumidity.getMinimumTime() != null)
            t = sdf.format(outdoorHumidity.getMinimumTime());
        else
            t = NO_VALUE;

        sb.append("Outdoor Humidity: Min: ").append(outdoorHumidity.getMinimumValue()).append(" (").append(t).append(")");
        
        if (outdoorHumidity.getMaximumTime() != null)
            t = sdf.format(outdoorHumidity.getMaximumTime());
        else
            t = NO_VALUE;
        
        sb.append("  Max: ").append(outdoorHumidity.getMaximumValue()).append("(").append(t).append(")");
        sb.append("  Avg: ").append(outdoorHumidity.getAverage()).append("\n");
        
        if (indoorHumidity.getMinimumTime() != null)
            t = sdf.format(indoorHumidity.getMinimumTime());
        else
            t = NO_VALUE;
        
        sb.append("Indoor Humidity:  Min: ").append(indoorHumidity.getMinimumValue()).append(" (").append(t).append(")");
        
        if (indoorHumidity.getMaximumTime() != null)
            t = sdf.format(indoorHumidity.getMaximumTime());
        else
            t = NO_VALUE;
        
        sb.append("  Max: ").append(indoorHumidity.getMaximumValue()).append("(").append(t).append(")");
        sb.append("  Avg: ").append(indoorHumidity.getAverage()).append("\n");

        sb.append(windRoseData);
        
        if (windSpeed.getMaximumTime() != null)
            t = sdf.format(windSpeed.getMaximumTime());
        else
            t = NO_VALUE;

        sb.append("Wind:  Max: ").append(windSpeed.getMaximumValue()).append(" (").append(t).append(")");

        if (windGust.getMaximumValue() == null)
            sb.append("      Gust: <NONE> (N/A)");
        else {
            if (windGust.getMaximumTime() != null)
                t = sdf.format(windGust.getMaximumTime());
            else
                t = NO_VALUE;
            
            sb.append("      Gust: ").append(windGust.getMaximumValue()).append(" (").append(t).append(")");
        }

        sb = sb.append("       Avg: ").append(windSpeed.getAverage()).append("\n");
        
        if (maxRainfallRateTime != null)
            t = sdf.format(maxRainfallRateTime);
        else
            t = NO_VALUE;
        
        sb.append("Total Rain: ").append(totalRainfall).append(" Max Rate: ").append(maxRainfallRate).append(" (").append(t).append(")\n");
        sb.append("Hourly: ");

        for (int hour : hourlyRainfall.getHourValues())
            sb = sb.append("").append(hour).append("-").append(hourlyRainfall.getRain(hour)).append(" ");

	sb.append("\n");

	for (MeasurementStatisticsEntry<? extends Measurement> entry : sensors.values()) {
	    sb.append(entry).append("\n");
	}

        return sb.toString();
    }
}
