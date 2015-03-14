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

import java.time.format.DateTimeFormatter;

import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

public final class WeatherUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final int HOURS_PER_DAY = 24;

    private WeatherUtils() {
    }

    /**
     * Calculate the windChill using the new formula.
     * 
     * @param t The measured temperature
     * @param windSpeed The speed of the wind
     * @return The temperature after the wind chill has been applied
     */
    public static Temperature windChill(Temperature t, Speed windSpeed) {
        return WeatherUtils.windChill(t, windSpeed, true);
    }

    /**
     * Calculate the wind chill using the specified formula.
     * 
     * @param t The measured temperature
     * @param windSpeed The speed of the wind
     * @param useNewFormula Whether the new or old formula will be used
     * @return The temperature after the wind chill has been applied
     */
    public static Temperature windChill(Temperature t, Speed windSpeed, boolean useNewFormula) {
        double temp = t.get(Temperature.Unit.FAHRENHEIT);
        double speed = windSpeed.get(Speed.Unit.MILES_PER_HOUR);

        //
        // Windchill (F) = 35.74 + 0.6215T - 35.75(V^0.16) + 0.4275T(V^0.16)
        // source http://www.weather.gov/os/windchill/windchillglossary.shtml
        //
        // Old formula T(wc) = 0.0817(3.71V**0.5 + 5.81 -0.25V)(T - 91.4) + 91.4
        // source
        // http://www.usatoday.com/weather/resources/basics/windchill/wind-chill-formulas.htm
        //
        double windChill;

        if (temp > 50.0 || speed <= 3.0)
            windChill = temp;
        else {
            if (useNewFormula)
                windChill = 35.74D + (0.6215D * temp) - (35.75D * Math.pow(speed, 0.16)) + (.4275D * temp * Math.pow(speed, 0.16));
            else
                windChill = (.0817D * ((3.71D * Math.sqrt(speed)) + 5.81D - (.25D * speed))) * (temp - 91.4D) + 91.4D;
        }

        return new Temperature(windChill, Temperature.Unit.FAHRENHEIT);
    }

    private static final double CONSTANT_A = 17.27;
    private static final double CONSTANT_B = 237.7;

    private static double theta(Temperature t, Humidity h) {
        double tv = t.get(Temperature.Unit.CELSIUS);
        double hv = h.get();

        double theta = ((CONSTANT_A * tv) / (CONSTANT_B + tv)) + Math.log(hv / 100.0);

        return theta;
    }

    /**
     * Calculate dew point.
     * 
     * @param t The temperature
     * @param h The humidity
     * @return The dew point
     */
    public static Temperature dewPoint(Temperature t, Humidity h) {

        if (h.get() == 0)
            return t;

        double dp = (CONSTANT_B * theta(t, h)) / (CONSTANT_A - theta(t, h));

        return new Temperature(dp, Temperature.Unit.CELSIUS);
    }

    /**
     * Calculate the heat index.
     * 
     * @param t The temperature
     * @param h The humidity
     * @return The heat index
     */
    public static Temperature heatIndex(Temperature t, Humidity h) {
        double temp = t.get(Temperature.Unit.FAHRENHEIT);

        //
        // Heat index does not apply to temperatures below 80F
        //
        if (temp < 80.0)
            return t;

        double humid = (double)h.get();

        double hi = 16.923 + (.185212 * temp)
                           + (5.37941 * humid)
                           - (.100254 * temp * humid)
                           + (.00941695 * temp * temp)
                           + (.00728898 * humid * humid)
                           + (.000345372 * temp * temp * humid)
                           - (.000814971 * temp * humid * humid)
                           + (.0000102102 * temp * temp * humid * humid)
                           - (.000038646 * Math.pow(temp, 3.0))
                           + (.0000291583 * Math.pow(humid, 3.0))
                           + (.00000142721 * Math.pow(temp, 3.0) * humid)
                           + (.000000197483 * temp * Math.pow(humid, 3.0))
                           - (.0000000218429 * Math.pow(temp, 3.0) * humid * humid)
                           + (.000000000843296 * temp * temp * Math.pow(humid, 3.0))
                           - (.0000000000481975 * Math.pow(temp, 3.0) * Math.pow(humid, 3.0));

        return new Temperature(hi, Temperature.Unit.FAHRENHEIT);
    }
}
