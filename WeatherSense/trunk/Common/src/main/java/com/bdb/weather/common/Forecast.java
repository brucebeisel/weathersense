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

import java.net.URL;

import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Forecast {
    SUNNY("Sunny", "clear_day.jpg", "clear_night.jpg"),
    PARTLY_CLOUDY("Partly Cloudy", "partly_cloudy_day.jpg", "partly_cloudy_night.jpg"),
    MOSTLY_CLOUDY("Mostly Cloudy", "cloudy_day.jpg", "cloudy_night.jpg"),
    MOSTLY_CLOUDY_WITH_RAIN("Mostly Cloudy with Rain", "rain_day.jpg", "rain_night.jpg"),
    MOSTLY_CLOUDY_WITH_SNOW("Mostly Cloudy with Rain", "snow_day.jpg", "snow_night.jpg"),
    MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW("Rain/Snow Mix", "snow_day.jpg", "snow_night.jpg"),
    PARTLY_CLOUDY_WITH_RAIN_LATER("Clouds with Rain Later", "rain_day.jpg", "rain_night.jpg"),
    PARTLY_CLOUDY_WITH_SNOW_LATER("Clouds with Snow Later", "snow_day.jpg", "snow_night.jpg"),
    PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER("Clouds with Rain or Snow Later", "snow_day.jpg", "snow_night.jpg");

    private final String string;
    private final ImageIcon dayImage;
    private final ImageIcon nightImage;

    Forecast(String s, String dayIconFilename, String nightIconFilename) {
	string = s;
	URL url = Forecast.class.getResource("/com/bdb/weathersense/" + dayIconFilename);

        if (url != null)
            dayImage = new ImageIcon(url);
        else
            dayImage = null;
        
        url = Forecast.class.getResource("/com/bdb/weathersense/" + nightIconFilename);

        if (url != null)
            nightImage = new ImageIcon(url);
        else
            nightImage = null;
    }
    
    public ImageIcon getDayImage() {
        return dayImage;
    }
    
    public ImageIcon getNightImage() {
        return nightImage;
    }
    
    @Override
    public String toString() {
        return string;
    }
}
