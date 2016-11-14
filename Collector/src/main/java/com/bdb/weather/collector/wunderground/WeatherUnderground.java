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
package com.bdb.weather.collector.wunderground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.ThreadUtils;
import com.bdb.weather.collector.WeatherUploader;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.WeatherSenseConstants;
import com.bdb.weather.common.WeatherUtils;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/*
 * Weather Undergound Documentation
 * 
 * action [action=updateraw] -- always supply this parameter to indicate you are making a weather observation upload
 * ID [ID as registered by wunderground.com]
 * PASSWORD [PASSWORD registered with this ID, case sensative]
 * dateutc - [YYYY-MM-DD HH:MM:SS (mysql format)]
 * winddir - [0-360 instantaneous wind direction]
 * windspeedmph - [mph instantaneous wind speed]
 * windgustmph - [mph current wind gust, using software specific time period]
 * windgustdir - [0-360 using software specific time period]
 * windspdmph_avg2m  - [mph 2 minute average wind speed mph]
 * winddir_avg2m - [0-360 2 minute average wind direction]
 * windgustmph_10m - [mph past 10 minutes wind gust mph ]
 * windgustdir_10m - [0-360 past 10 minutes wind gust direction]
 * humidity - [% outdoor humidity 0-100%]
 * dewptf- [F outdoor dewpoint F]
 * tempf - [F outdoor temperature] 
 * rainin - [rain inches over the past hour)] -- the accumulated rainfall in the past 60 min
 * dailyrainin - [rain inches so far today in local time]
 * baromin - [barometric pressure inches]
 * indoortempf - [F indoor temperature F]
 * indoorhumidity - [% indoor humidity 0-100]
 * softwaretype - [text] ie: WeatherLink, VWS, WeatherDisplay
 * 
 * Example:
 * http://weatherstation.wunderground.com/weatherstation/updateweatherstation.php?ID=KCASANFR5&PASSWORD=XXXXXX&dateutc=2000-01-01+10%3A32%3A35&winddir=230&windspeedmph=12&windgustmph=12&tempf=70&rainin=0&baromin=29.1&dewptf=68.2&humidity=90&weather=&clouds=&softwaretype=vws%20versionxx&action=updateraw
 *
 * Response Text
 *
 * The response from an HTTP GET request contains some debugging data.
 *
 * RESPONSES and MEANINGS:
 * 
 * "success" - the observation was ingested successfully 
 * "INVALIDPASSWORDID|Password and/or id are incorrect" - invalid user data entered in the ID and PASSWORD GET parameters
 */
/**
 * Class that handles sending weather data to the Weather Underground web site.
 */
public class WeatherUnderground implements WeatherUploader {
    private static final String WEATHER_SENSE_PRODUCT_STRING = WeatherSenseConstants.PRODUCT_NAME + "-" + WeatherSenseConstants.PRODUCT_VERSION;
    private static final String WUNDERGROUND_BASE_URL = "http://weatherstation.wunderground.com/weatherstation/updateweatherstation.php?";
    private static final String WUNDERGROUND_RAPIDFIRE_BASE_URL = "http://rtupdate.wunderground.com/weatherstation/updateweatherstation.php?";
    private static final String WUNDERGROUND_RAPIDFIRE_FREQ = "&realtime=1&rtfreq=5";
    private static final String URL_SPACE = "%20";
    private static final String URL_COLON = "%3A";
    private static final String URL_FIELD_SEPARATOR = "&";
    private static final String ID_URL_TAG = "ID=";
    private static final String PASSWORD_URL_TAG = "PASSWORD=";
    private static final String UTC_URL_TAG = "dateutc=";
    private static final String ACTION_URL_TAG = "action=";
    private static final String UPDATE_RAW_VALUE = "updateraw";
    private static final String WIND_DIR_URL_TAG = "winddir=";
    private static final String WIND_GUST_DIR_URL_TAG = "windgustdir=";
    private static final String OUTDOOR_HUMIDITY_URL_TAG = "humidity=";
    private static final String INDOOR_HUMIDITY_URL_TAG = "indoorhumidity=";
    private static final String BAROMETER_URL_TAG = "baromin=";
    private static final String OUTDOOR_TEMPERATURE_URL_TAG = "tempf=";
    private static final String INDOOR_TEMPERATURE_URL_TAG = "indoortempf=";
    private static final String WIND_SPEED_URL_TAG = "windspeedmph=";
    private static final String WIND_GUST_URL_TAG = "windgustmph=";
    private static final String RAIN_RATE_URL_TAG = "rainin=";
    private static final String DAILY_RAIN_URL_TAG = "dailyrainin=";
    private static final String SOFTWARE_URL_TAG = "softwaretype=";
    private static final String DEW_POINT_URL_TAG = "dewptf=";
    private static final String WIND_SPEED_2_MIN_AVG_TAG = "windspdmph_avg2m=";
    private static final String WIND_DIR_2_MIN_AVG_TAG = "windir_avg2m=";
    private static final String WIND_GUST_10_MIN_TAG = "windgustmph_10m=";
    private static final String WIND_GUST_DIR_10_MIN_TAG = "windgustdir_10m=";
    private static final String SOLAR_RADIATION_TAG = "solarradiation=";
    private static final String SUCCESS_RESPONSE = "success";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int READ_TIMEOUT_MILLIS = 1000;
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int READ_WAIT_SECONDS = 5;

    private String wuStationId;
    private String password;
    private String urlString;
    private final ExecutorService executor;
    private final Statistics statistics;
    private static final Logger logger = Logger.getLogger(WeatherUnderground.class.getName());

    /**
     * Class to hold the statistics about the communications with Weather Underground.
     */
    public static class Statistics {
        int numAttempts;
        int numSuccesses;
        int numFailures;
    };

    /**
     * Constructor that is typically used for unit testing.
     * 
     * @param url The URL used to connect to the weather underground server
     * @param wuStationId The weather station ID from the Weather Underground web site
     * @param password The password for the user account that has the specified ID
     */
    public WeatherUnderground(String url, String wuStationId, String password) {
        this.wuStationId = wuStationId;
        this.password = password;
        this.urlString = url;
        this.statistics = new Statistics();
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Constructor.
     */
    public WeatherUnderground() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param wuStationId The weather station ID from the Weather Underground web site
     * @param password The password for the user account that has the specified ID
     */
    public WeatherUnderground(String wuStationId, String password) {
        this(WUNDERGROUND_RAPIDFIRE_BASE_URL, wuStationId, password);
    }

    /**
     * Set the weather underground parameters.
     *
     * @param wuStationId The Weather Underground station ID
     * @param password The password for the station
     */
    public void setStationParameters(String wuStationId, String password) {
        this.wuStationId = wuStationId;
        this.password = password;
    }

    /**
     * Sent the current weather to the Weather Underground.
     *
     * @param current The current weather
     */
    @Override
    public void uploadCurrentWeather(CurrentWeather current) {
        if (wuStationId == null || password == null) {
            logger.info("Skipping weather underground update. No weather undergroud information in database.");
            return;
        }

        WeatherUndergroundRecord record = new WeatherUndergroundRecord();
        record.stationId = wuStationId;
        record.password = password;
        record.rapidFire = true;
        record.time = ZonedDateTime.now(ZoneOffset.UTC);
        record.windDir = current.getWindDirection();
        record.windSpeedMph = current.getWindSpeed();
        record.windGustMph = current.getWindGustSpeed();
        record.windGustDir = current.getWindGustDirection();
        record.windSpeedMphAvg2Min = current.getWindSpeed2MinAvg();
        record.windDirAvg2Min = record.windDir;
        record.windGust10Min = current.getWindGustSpeed();
        record.winGustDir10Min = current.getWindGustDirection();
        record.outdoorHumidity = current.getOutdoorHumidity();
        record.dewPoint = current.getDewPoint();
        record.outdoorTemp = current.getOutdoorTemperature();
        record.barometer = current.getBaroPressure();
        record.indoorTemp = current.getIndoorTemperature();
        record.indoorHumidity = current.getIndoorHumidity();
        record.rainRate = current.getRainRate();
        record.rainToday = current.getRainToday();
        record.solarRadiation = current.getSolarRadiation();

        executor.execute(() -> performUrlConnection(record));
    }

    /**
     * Shutdown the weather underground thread.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Return the statistics regarding the weather underground communications.
     * 
     * @return The statistics
     */
    public Statistics getStatistics() {
        Statistics stats = new Statistics();
        stats.numAttempts = statistics.numAttempts;
        stats.numFailures = statistics.numFailures;
        stats.numSuccesses = statistics.numSuccesses;
        return stats;
    }
    /**
     * Build the URL that updates the Weather Underground database. Weather Underground uses a name/value pair URL to specify the
     * data to be loaded into their database.
     *
     * @return The URL as a StringBuffer
     */
    private StringBuffer buildURL(WeatherUndergroundRecord record) {
        //
        // In rare cases the outdoor sensor does not report any data. In that case we will
        // not send anything to Weather Underground.
        //

        // TODO Check every value and only send the values that exist in the current record

        //
        // Build the time in UTC
        //
        String time = DATE_FORMAT.format(record.time);
        time = time.replaceAll(":", URL_COLON);
        time = time.replaceAll(" ", URL_SPACE);

        StringBuffer sb = new StringBuffer(urlString);

        sb.append(ACTION_URL_TAG).append(UPDATE_RAW_VALUE).append(URL_FIELD_SEPARATOR);
        sb.append(ID_URL_TAG).append(record.stationId).append(URL_FIELD_SEPARATOR);
        sb.append(PASSWORD_URL_TAG).append(record.password).append(URL_FIELD_SEPARATOR);
        sb.append(UTC_URL_TAG).append(time).append(URL_FIELD_SEPARATOR);
        if (record.outdoorTemp != null)
            sb.append(OUTDOOR_TEMPERATURE_URL_TAG).append(Temperature.Unit.FAHRENHEIT.getFormatter().format(record.outdoorTemp.get(Temperature.Unit.FAHRENHEIT))).append(URL_FIELD_SEPARATOR);

        Temperature dewPoint = null;
        if (record.dewPoint != null)
            dewPoint = record.dewPoint;
        else if (record.outdoorTemp != null && record.outdoorHumidity != null) {
            dewPoint = WeatherUtils.dewPoint(record.outdoorTemp, record.outdoorHumidity);
        }

        if (dewPoint != null)
            sb.append(DEW_POINT_URL_TAG).append(Temperature.Unit.FAHRENHEIT.getFormatter().format(dewPoint.get(Temperature.Unit.FAHRENHEIT))).append(URL_FIELD_SEPARATOR);

        if (record.indoorTemp != null)
            sb.append(INDOOR_TEMPERATURE_URL_TAG).append(Temperature.Unit.FAHRENHEIT.getFormatter().format(record.indoorTemp.get(Temperature.Unit.FAHRENHEIT))).append(URL_FIELD_SEPARATOR);

        if (record.outdoorHumidity != null)
            sb.append(OUTDOOR_HUMIDITY_URL_TAG).append(record.outdoorHumidity.toString()).append(URL_FIELD_SEPARATOR);

        if (record.indoorHumidity != null)
            sb.append(INDOOR_HUMIDITY_URL_TAG).append(record.indoorHumidity.toString()).append(URL_FIELD_SEPARATOR);

        if (record.barometer != null)
            sb.append(BAROMETER_URL_TAG).append(Pressure.Unit.IN_HG.getFormatter().format(record.barometer.get(Pressure.Unit.IN_HG))).append(URL_FIELD_SEPARATOR);

        if (record.windSpeedMph != null)
            sb.append(WIND_SPEED_URL_TAG).append(Speed.Unit.MILES_PER_HOUR.getFormatter().format(record.windSpeedMph.get(Speed.Unit.MILES_PER_HOUR))).append(URL_FIELD_SEPARATOR);

        if (record.windGustMph != null)
            sb.append(WIND_GUST_URL_TAG).append(Speed.Unit.MILES_PER_HOUR.getFormatter().format(record.windGustMph.get(Speed.Unit.MILES_PER_HOUR))).append(URL_FIELD_SEPARATOR);

        if (record.windSpeedMphAvg2Min != null)
            sb.append(WIND_SPEED_2_MIN_AVG_TAG).append(record.windSpeedMphAvg2Min.get(Speed.Unit.MILES_PER_HOUR)).append(URL_FIELD_SEPARATOR);

        if (record.windDir != null) {
            sb.append(WIND_DIR_URL_TAG).append(record.windDir.toString()).append(URL_FIELD_SEPARATOR);
            sb.append(WIND_GUST_DIR_URL_TAG).append(record.windDir.toString()).append(URL_FIELD_SEPARATOR);
        }

        if (record.windDirAvg2Min != null)
            sb.append(WIND_DIR_2_MIN_AVG_TAG).append(record.windDirAvg2Min.toString()).append(URL_FIELD_SEPARATOR);

        if (record.windGust10Min != null)
            sb.append(WIND_GUST_10_MIN_TAG).append(record.windGust10Min.get(Speed.Unit.MILES_PER_HOUR)).append(URL_FIELD_SEPARATOR);

        if (record.winGustDir10Min != null)
            sb.append(WIND_GUST_DIR_10_MIN_TAG).append(record.windGust10Min.toString()).append(URL_FIELD_SEPARATOR);

        if (record.rainRate != null)
            sb.append(RAIN_RATE_URL_TAG).append(Depth.Unit.INCHES.getFormatter().format(record.rainRate.get(Depth.Unit.INCHES))).append(URL_FIELD_SEPARATOR);

        if (record.rainToday != null)
            sb.append(DAILY_RAIN_URL_TAG).append(Depth.Unit.INCHES.getFormatter().format(record.rainToday.get(Depth.Unit.INCHES))).append(URL_FIELD_SEPARATOR);

        if (record.solarRadiation != null)
            sb.append(SOLAR_RADIATION_TAG).append(SolarRadiation.Unit.WATTS_PER_METER_SQUARED.getFormatter().format(record.solarRadiation.get())).append(URL_FIELD_SEPARATOR);

        sb.append(SOFTWARE_URL_TAG).append(WEATHER_SENSE_PRODUCT_STRING);

        sb.append(WUNDERGROUND_RAPIDFIRE_FREQ);

        return sb;
    }

    /**
     * Actually talk to the Weather Underground web site using a RESTful interface
     *
     * @param url The URL to which to connect
     */
    @SuppressWarnings("empty-statement")
    private void performUrlConnection(WeatherUndergroundRecord record) {
        try {
            statistics.numAttempts++;
            StringBuffer sb = buildURL(record);

            URL url = new URL(sb.toString());

            //
            // Actually connect to the Weather Underground web site
            //
            logger.log(Level.FINE, "Sending to Weather Underground URL: ''{0}''", url);
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            try (InputStream is = connection.getInputStream(); BufferedReader dis = new BufferedReader(new InputStreamReader(is))) {

                boolean streamReady = false;

                //
                // Wait 5 seconds for data to be available. 
                //
                for (int i = 0; i < READ_WAIT_SECONDS && !streamReady; i++) {
                    streamReady = dis.ready();
                    ThreadUtils.sleep(READ_TIMEOUT_MILLIS);
                }

                //
                // Read the response. Not able to do anything if the connection fails, so just log.
                //
                String response = null;
                if (streamReady)
                    response = dis.readLine();

                if (response != null) {
                    logger.log(Level.FINER, "Response from Weather Underground ''{0}''", response);
                    if (response.equals(SUCCESS_RESPONSE)) {
                        statistics.numSuccesses++;
                        logger.log(Level.FINE, "Successfully uploaded weather data to Weather Underground");
                    }
                    else {
                        statistics.numFailures++;
                        logger.log(Level.WARNING, "Failed to upload data to Weather Undergound. Response = ''{0}''", response);
                    }

                    //
                    // Consume any additional response
                    //
                    while (dis.readLine() != null);
                }
                else {
                    statistics.numFailures++;
                    logger.info("No response received from Weather Underground");
                }
            }
        }
        catch (IOException e) {
            statistics.numFailures++;
            logger.log(Level.WARNING, "IOException while reading response from Weather Underground", e);
        }
        catch (Exception e2) {
            //
            // Catch generic exeception so the executor does not terminate if an unexpected exception occurs
            //
            statistics.numFailures++;
            logger.log(Level.WARNING, "Unexpected exception caught while reading response from Weather Underground", e2);
        }
    }
}
