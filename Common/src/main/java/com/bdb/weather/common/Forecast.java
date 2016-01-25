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

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.annotation.XmlEnum;

import javafx.scene.image.Image;

@XmlEnum
public enum Forecast {
    /**
     * Sunny forecast
     */
    SUNNY("Sunny", "clear_day.jpg", "clear_night.jpg"),
    /**
     * Partly cloudy forecast
     */
    PARTLY_CLOUDY("Partly Cloudy", "partly_cloudy_day.jpg", "partly_cloudy_night.jpg"),
    /**
     * Mostly cloudy forecast
     */
    MOSTLY_CLOUDY("Mostly Cloudy", "cloudy_day.jpg", "cloudy_night.jpg"),
    /**
     * Mostly cloudy with rain later forecast
     */
    MOSTLY_CLOUDY_WITH_RAIN("Mostly Cloudy with Rain", "rain_day.jpg", "rain_night.jpg"),
    /**
     * Mostly cloudy with snow later forecast
     */
    MOSTLY_CLOUDY_WITH_SNOW("Mostly Cloudy with Rain", "snow_day.jpg", "snow_night.jpg"),
    /**
     * Mostly cloudy with rain or snow later forecast
     */
    MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW("Rain/Snow Mix", "snow_day.jpg", "snow_night.jpg"),
    /**
     * Partly cloudy with rain later forecast
     */
    PARTLY_CLOUDY_WITH_RAIN_LATER("Clouds with Rain Later", "rain_day.jpg", "rain_night.jpg"),
    /**
     * Partly cloudy with snow later forecast
     */
    PARTLY_CLOUDY_WITH_SNOW_LATER("Clouds with Snow Later", "snow_day.jpg", "snow_night.jpg"),
    /**
     * Partly cloudy with rain or snow later forecast
     */
    PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER("Clouds with Rain or Snow Later", "snow_day.jpg", "snow_night.jpg");

    private final String string;
    private Image dayImage;
    private Image nightImage;

    /**
     * Constructor.
     * 
     * @param s The name of the forcast.
     * 
     * @param dayIconFilename The icon for the forecast during the day
     * @param nightIconFilename  The icon for the forecast during at night
     */
    Forecast(String s, String dayIconFilename, String nightIconFilename) {
        string = s;
        try {
            URL url = Forecast.class.getResource("/com/bdb/weathersense/" + dayIconFilename);
            dayImage = null;
            nightImage = null;

            if (url != null)
                dayImage = new Image(url.openStream());
            
            url = Forecast.class.getResource("/com/bdb/weathersense/" + nightIconFilename);

            if (url != null)
                nightImage = new Image(url.openStream());
        }
        catch (IOException e) {
        }
    }
    
    /**
     * Get the day image.
     * 
     * @return The image
     */
    public Image getDayImage() {
        return dayImage;
    }
    
    /**
     * Get the night image.
     * 
     * @return The night image
     */
    public Image getNightImage() {
        return nightImage;
    }
    
    @Override
    public String toString() {
        return string;
    }
}