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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.MeasurementAverage;
import com.bdb.util.measurement.MeasurementValueTime;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

public class Statistics {

    public class TemperatureBinData {
        public int dayCount;
        public Duration duration;

        public TemperatureBinData() {
            dayCount = 0;
            duration = Duration.ZERO;
        }
    };

    public class RangeTime {
        Measurement min;
        Measurement max;
        Measurement range;
        LocalDate date;

        public RangeTime(Measurement r, Measurement minarg, Measurement maxarg, LocalDate date) {
            range = r;
            min = minarg;
            max = maxarg;
            this.date = date;
        }

        public Measurement getRange() {
            return range;
        }

        /**
         * @return the min
         */
        public Measurement getMin() {
            return min;
        }

        /**
         * @return the max
         */
        public Measurement getMax() {
            return max;
        }

        /**
         * @return the time
         */
        public LocalDate getDate() {
            return date;
        }
    }
    private final SummaryRecord summary;
    private final WeatherAverages averagesCollection;
    private RangeTime largestTemperatureRange;
    private RangeTime smallestTemperatureRange;
    private MeasurementValueTime<Temperature> maxLowOutdoorTemperature;
    private MeasurementValueTime<Temperature> maxMeanOutdoorTemperature;
    private MeasurementValueTime<Temperature> minMeanOutdoorTemperature;
    private final MeasurementAverage<Temperature> avgLowTemperature = new MeasurementAverage<>(new Temperature(0.0), Duration.ZERO);
    private final MeasurementAverage<Temperature> avgMeanTemperature = new MeasurementAverage<>(new Temperature(0.0), Duration.ZERO);
    private int daysHighAboveAverage;
    private int daysHighBelowAverage;
    private int daysLowAboveAverage;
    private int daysLowBelowAverage;
    private int daysMeanBelowAverage;
    private int daysMeanAboveAverage;
    private int rainDays;
    private Depth maxDayRainDepth;
    private LocalDate maxDayRainDate;
    private MeasurementValueTime<Speed> maxAvgWind;
    private int numberOfDays;
    private RangeTime smallestPressureRange;
    private RangeTime largestPressureRange;
    private RangeTime smallestHumidityRange;
    private RangeTime largestHumidityRange;
    private final Map<TemperatureBin, TemperatureBinData> temperatureBinData = new TreeMap<>();

    public Statistics(WeatherStation ws, WeatherAverages averagesCollection, List<SpeedBin> speedBins, TemperatureBinMgr temperatureBinMgr) {
        this.averagesCollection = averagesCollection;
        summary = new SummaryRecord(ws.getWindParameters(), temperatureBinMgr, LocalDate.now());

        temperatureBinMgr.getAllBins().stream().forEach((bin) -> {
            temperatureBinData.put(bin, new TemperatureBinData());
        });
    }

    /**
     * @param rec
     * @see com.bdb.weather.common.SummaryRecord#applyHistoricalRecord(com.bdb.weather.common.HistoricalRecord)
     */
    public void applySummaryRecord(SummaryRecord rec) {
        numberOfDays++;

        summary.applySummaryRecord(rec);

        if (rec.getTotalRainfall().compareTo(new Depth(0.0)) != 0) {
            rainDays++;
        }

        Temperature t;

        if (rec.getMaxOutdoorTemp() != null && rec.getMinOutdoorTemp() != null) {
            t = new Temperature(rec.getMaxOutdoorTemp().get() - rec.getMinOutdoorTemp().get());
            if (largestTemperatureRange == null || largestTemperatureRange.range.compareTo(t) < 0) {
                largestTemperatureRange = new RangeTime(t, rec.getMinOutdoorTemp(), rec.getMaxOutdoorTemp(), rec.getDate());
            }

            if (smallestTemperatureRange == null || smallestTemperatureRange.range.compareTo(t) > 0) {
                smallestTemperatureRange = new RangeTime(t, rec.getMinOutdoorTemp(), rec.getMaxOutdoorTemp(), rec.getDate());
            }
        }

        t = rec.getMinOutdoorTemp();
        if (t != null && (maxLowOutdoorTemperature == null || maxLowOutdoorTemperature.getValue().compareTo(t) < 0)) {
            maxLowOutdoorTemperature = new MeasurementValueTime<>(t, rec.getMinOutdoorTempTime());
        }

        t = rec.getAvgOutdoorTemp();

        if (t != null) {
            if (minMeanOutdoorTemperature == null || minMeanOutdoorTemperature.getValue().compareTo(t) > 0) {
                minMeanOutdoorTemperature = new MeasurementValueTime<>(t, rec.getDate().atStartOfDay());
            }

            if (maxMeanOutdoorTemperature == null || maxMeanOutdoorTemperature.getValue().compareTo(t) < 0) {
                maxMeanOutdoorTemperature = new MeasurementValueTime<>(t, rec.getDate().atStartOfDay());
            }
        }

        WeatherAverage dailyAverages = averagesCollection.getAverage(rec.getDate());

        Temperature highTemperature = dailyAverages.getHighTemperature();
        int compare;

        if (rec.getMaxOutdoorTemp() != null) {
            compare = highTemperature.compareTo(rec.getMaxOutdoorTemp());

            if (compare < 0) {
                daysHighAboveAverage++;
            }
            else if (compare > 0) {
                daysHighBelowAverage++;
            }
        }

        if (rec.getMinOutdoorTemp() != null) {
            compare = averagesCollection.getAverage(rec.getDate()).getLowTemperature().compareTo(rec.getMinOutdoorTemp());

            if (compare > 0) {
                daysLowBelowAverage++;
            }
            else if (compare < 0) {
                daysLowAboveAverage++;
            }

            avgLowTemperature.applyValue(rec.getMinOutdoorTemp(), Duration.ofSeconds(1));
        }

        if (rec.getAvgOutdoorTemp() != null) {
            compare = averagesCollection.getAverage(rec.getDate()).getMeanTemperature().compareTo(rec.getAvgOutdoorTemp());

            if (compare > 0) {
                daysMeanBelowAverage++;
            }
            else {
                daysMeanAboveAverage++;
            }

            avgMeanTemperature.applyValue(rec.getAvgOutdoorTemp(), Duration.ofSeconds(1));
        }

        if (maxDayRainDepth == null || maxDayRainDepth.compareTo(rec.getTotalRainfall()) < 0) {
            maxDayRainDepth = rec.getTotalRainfall();
            maxDayRainDate = rec.getDate();
        }

        if (maxAvgWind == null || maxAvgWind.getValue().compareTo(rec.getAvgWindSpeed()) < 0) {
            maxAvgWind = new MeasurementValueTime<>(rec.getAvgWindSpeed(), rec.getDate().atStartOfDay());
        }

        if (rec.getMaxBaroPressure() != null && rec.getMinBaroPressure() != null) {
            Pressure p = new Pressure(rec.getMaxBaroPressure().get() - rec.getMinBaroPressure().get());
            if (largestPressureRange == null || largestPressureRange.range.compareTo(p) < 0) {
                largestPressureRange = new RangeTime(p, rec.getMinBaroPressure(), rec.getMaxBaroPressure(), rec.getDate());
            }

            if (smallestPressureRange == null || smallestPressureRange.range.compareTo(p) > 0) {
                smallestPressureRange = new RangeTime(p, rec.getMinBaroPressure(), rec.getMaxBaroPressure(), rec.getDate());
            }
        }

        if (rec.getMaxOutdoorHumidity() != null && rec.getMinOutdoorHumidity() != null) {
            Humidity h = new Humidity(rec.getMaxOutdoorHumidity().get() - rec.getMinOutdoorHumidity().get());
            if (largestHumidityRange == null || largestHumidityRange.range.compareTo(h) < 0) {
                largestHumidityRange = new RangeTime(h, rec.getMinOutdoorHumidity(), rec.getMaxOutdoorHumidity(), rec.getDate());
            }

            if (smallestHumidityRange == null || smallestHumidityRange.range.compareTo(h) > 0) {
                smallestHumidityRange = new RangeTime(h, rec.getMinOutdoorHumidity(), rec.getMaxOutdoorHumidity(), rec.getDate());
            }
        }

        if (rec.getMaxOutdoorTemp() != null && rec.getMinOutdoorTemp() != null) {
            for (TemperatureBin bin : temperatureBinData.keySet()) {
                if (bin.isInBin(rec.getMaxOutdoorTemp()) || bin.isInBin(rec.getMinOutdoorTemp())) {
                    TemperatureBinData d = temperatureBinData.get(bin);
                    d.dayCount++;
                }
            }

            for (TemperatureBinDuration binDuration : summary.getTemperatureBinDurations()) {
                TemperatureBinData data = temperatureBinData.get(binDuration.getTemperatureBin());
                data.duration = binDuration.getDuration();
            }
        }
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * @return the largestTemperatureRange
     */
    public RangeTime getLargestTemperatureRange() {
        return largestTemperatureRange;
    }

    /**
     * @return the smallestTemperatureRange
     */
    public RangeTime getSmallestTemperatureRange() {
        return smallestTemperatureRange;
    }

    /**
     * @return the maxLowOutdoorTemperature
     */
    public MeasurementValueTime<Temperature> getMaxLowOutdoorTemperature() {
        return maxLowOutdoorTemperature;
    }

    /**
     * @return the minHighOutdoorTemperature
     */
    public MeasurementValueTime<Temperature> getMinHighOutdoorTemperature() {
        return new MeasurementValueTime<>(summary.getMinHighOutdoorTemp(), summary.getMinHighOutdoorTempTime());
    }

    /**
     * @return the minMeanOutdoorTemperature
     */
    public MeasurementValueTime<Temperature> getMinMeanOutdoorTemperature() {
        return minMeanOutdoorTemperature;
    }

    /**
     * @return the maxMeanOutdoorTemperature
     */
    public MeasurementValueTime<Temperature> getMaxMeanOutdoorTemperature() {
        return maxMeanOutdoorTemperature;
    }

    /**
     * @return the avgHighTemperature
     */
    public Temperature getAvgHighTemperature() {
        return summary.getAvgHighOutdoorTemp();
    }

    /**
     * @return the avgMeanTemperature
     */
    public Temperature getAvgMeanTemperature() {
        return avgMeanTemperature.getAverage();
    }

    /**
     * @return the avgLowTemperature
     */
    public Temperature getAvgLowTemperature() {
        return avgLowTemperature.getAverage();
    }

    /**
     * @return the daysHighAboveNormal
     */
    public int getDaysHighAboveNormal() {
        return daysHighAboveAverage;
    }

    /**
     * @return the daysHighBelowNormal
     */
    public int getDaysHighBelowNormal() {
        return daysHighBelowAverage;
    }

    /**
     * @return the daysLowAboveNormal
     */
    public int getDaysLowAboveNormal() {
        return daysLowAboveAverage;
    }

    /**
     * @return the daysLowBelowNormal
     */
    public int getDaysLowBelowNormal() {
        return daysLowBelowAverage;
    }

    /**
     * @return the daysMeanAboveNormal
     */
    public int getDaysMeanAboveNormal() {
        return daysMeanAboveAverage;
    }

    /**
     * @return the daysMeanBelowNormal
     */
    public int getDaysMeanBelowNormal() {
        return daysMeanBelowAverage;
    }

    /**
     * @return the rainDays
     */
    public int getRainDays() {
        return rainDays;
    }

    /**
     * @return the largestPressureRange
     */
    public RangeTime getLargestPressureRange() {
        return largestPressureRange;
    }

    /**
     * @return the smallestTemperatureRange
     */
    public RangeTime getSmallestPressureRange() {
        return smallestPressureRange;
    }

    /**
     * @return the largestHumidityRange
     */
    public RangeTime getLargestHumidityRange() {
        return largestHumidityRange;
    }

    /**
     * @return the smallestHumidityRange
     */
    public RangeTime getSmallestHumidityRange() {
        return smallestHumidityRange;
    }

    public Depth getMaxDayRainDepth() {
        return maxDayRainDepth;
    }

    public LocalDate getMaxDayRainDate() {
        return maxDayRainDate;
    }

    public Depth getAvgRainPerDay() {
        return new Depth(getTotalRainfall().get() / (double)numberOfDays);
    }

    public MeasurementValueTime<Speed> getMaxAvgWindSpeed() {
        return maxAvgWind;
    }

    public Map<TemperatureBin, TemperatureBinData> getTemperatureBinData() {
        return Collections.unmodifiableMap(temperatureBinData);
    }

    public List<TemperatureBinDuration> getTemperatureBinDurations() {
        return summary.getTemperatureBinDurations();
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        if (this.getClass() != obj.getClass())
            return false;
        
        return summary.equals(obj);
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgBaroPressure()
     */
    public Pressure getAvgBaroPressure() {
        return summary.getAvgBaroPressure();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgIndoorHumidity()
     */
    public Humidity getAvgIndoorHumidity() {
        return summary.getAvgIndoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgIndoorTemp()
     */
    public Temperature getAvgIndoorTemp() {
        return summary.getAvgIndoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgOutdoorHumidity()
     */
    public Humidity getAvgOutdoorHumidity() {
        return summary.getAvgOutdoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgOutdoorTemp()
     */
    public Temperature getAvgOutdoorTemp() {
        return summary.getAvgOutdoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getAvgWindSpeed()
     */
    public Speed getAvgWindSpeed() {
        return summary.getAvgWindSpeed();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getDate()
     */
    public LocalDate getDate() {
        return summary.getDate();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getDuration()
     */
    public Duration getDuration() {
        return summary.getDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getHourlyRainfall()
     */
    public DayHourRain getHourlyRainfall() {
        return summary.getHourlyRainfall();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getIndoorHumidityDuration()
     */
    public Duration getIndoorHumidityDuration() {
        return summary.getIndoorHumidityDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getIndoorTempDuration()
     */
    public Duration getIndoorTempDuration() {
        return summary.getIndoorTempDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxBaroPressure()
     */
    public Pressure getMaxBaroPressure() {
        return summary.getMaxBaroPressure();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxIndoorHumidity()
     */
    public Humidity getMaxIndoorHumidity() {
        return summary.getMaxIndoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxIndoorHumidityTime()
     */
    public LocalDateTime getMaxIndoorHumidityTime() {
        return summary.getMaxIndoorHumidityTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxIndoorTemp()
     */
    public Temperature getMaxIndoorTemp() {
        return summary.getMaxIndoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxIndoorTempTime()
     */
    public LocalDateTime getMaxIndoorTempTime() {
        return summary.getMaxIndoorTempTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxOutdoorHumidity()
     */
    public Humidity getMaxOutdoorHumidity() {
        return summary.getMaxOutdoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxOutdoorHumidityTime()
     */
    public LocalDateTime getMaxOutdoorHumidityTime() {
        return summary.getMaxOutdoorHumidityTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxOutdoorTemp()
     */
    public Temperature getMaxOutdoorTemp() {
        return summary.getMaxOutdoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxOutdoorTempTime()
     */
    public LocalDateTime getMaxOutdoorTempTime() {
        return summary.getMaxOutdoorTempTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxPressureTime()
     */
    public LocalDateTime getMaxPressureTime() {
        return summary.getMaxBaroPressureTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxRainfallRate()
     */
    public Depth getMaxRainfallRate() {
        return summary.getMaxRainfallRate();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxRainfallRateTime()
     */
    public LocalDateTime getMaxRainfallRateTime() {
        return summary.getMaxRainfallRateTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxWindGust()
     */
    public Speed getMaxWindGust() {
        return summary.getMaxWindGust();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxWindGustTime()
     */
    public LocalDateTime getMaxWindGustTime() {
        return summary.getMaxWindGustTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxWindSpeed()
     */
    public Speed getMaxWindSpeed() {
        return summary.getMaxWindSpeed();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMaxWindSpeedTime()
     */
    public LocalDateTime getMaxWindSpeedTime() {
        return summary.getMaxWindSpeedTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinBaroPressure()
     */
    public Pressure getMinBaroPressure() {
        return summary.getMinBaroPressure();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinIndoorHumidity()
     */
    public Humidity getMinIndoorHumidity() {
        return summary.getMinIndoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinIndoorHumidityTime()
     */
    public LocalDateTime getMinIndoorHumidityTime() {
        return summary.getMinIndoorHumidityTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinIndoorTemp()
     */
    public Temperature getMinIndoorTemp() {
        return summary.getMinIndoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinIndoorTempTime()
     */
    public LocalDateTime getMinIndoorTempTime() {
        return summary.getMinIndoorTempTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinOutdoorHumidity()
     */
    public Humidity getMinOutdoorHumidity() {
        return summary.getMinOutdoorHumidity();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinOutdoorHumidityTime()
     */
    public LocalDateTime getMinOutdoorHumidityTime() {
        return summary.getMinOutdoorHumidityTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinOutdoorTemp()
     */
    public Temperature getMinOutdoorTemp() {
        return summary.getMinOutdoorTemp();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinOutdoorTempTime()
     */
    public LocalDateTime getMinOutdoorTempTime() {
        return summary.getMinOutdoorTempTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getMinPressureTime()
     */
    public LocalDateTime getMinBaroPressureTime() {
        return summary.getMinBaroPressureTime();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getOutdoorHumidityDuration()
     */
    public Duration getOutdoorHumidityDuration() {
        return summary.getOutdoorHumidityDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getOutdoorTempDuration()
     */
    public Duration getOutdoorTempDuration() {
        return summary.getOutdoorTempDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getPressureDuration()
     */
    public Duration getBaroPressureDuration() {
        return summary.getBaroPressureDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getTotalRainfall()
     */
    public Depth getTotalRainfall() {
        return summary.getTotalRainfall();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getWindDuration()
     */
    public Duration getWindDuration() {
        return summary.getWindDuration();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#getWindRoseData()
     */
    public WindRoseData getWindRoseData() {
        return summary.getWindRoseData();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return summary.hashCode();
    }

    /**
     * @return
     * @see com.bdb.weather.common.SummaryRecord#toString()
     */
    @Override
    public String toString() {
        return summary.toString();
    }
}