/*
 * Copyright (C) 2015 bruce
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
package com.bdb.weather.display.windrose;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author bruce
 */
public class WindRoseItem {
    public static final String HEADING_KEY = "Heading";
    public static final String PERCENT_OF_WIND_KEY = "% of Wind";
    public static final String AVG_SPEED_KEY = "Avg Speed";
    public static final String MAX_SPEED_KEY = "Max Speed";
    private DoubleProperty heading;
    private DoubleProperty percentOfWind;
    private DoubleProperty avgWindSpeed;
    private DoubleProperty maxWindSpeed;

    public DoubleProperty headingProperty() {
        if (heading == null)
            heading = new SimpleDoubleProperty(this, HEADING_KEY);

        return heading;
    }

    public void setHeading(double heading) {
        headingProperty().set(heading);
    }

    public double getHeading() {
        return heading.getValue();
    }
    
    public DoubleProperty percentOfWindProperty() {
        return percentOfWind;
    }

    public void setPercentOfWind(double percentOfWind) {
        percentOfWindProperty().set(percentOfWind);
    }

    public double getPercentOfWind() {
        return percentOfWind.getValue();
    }
    
    public DoubleProperty avgWindSpeedProperty() {
        return avgWindSpeed;
    }
    
    public void setAvgWindSpeed(double avgWindSpeed) {
        avgWindSpeedProperty().set(avgWindSpeed);
    }
    
    public double getAvgWindSpeed() {
        return avgWindSpeed.getValue();
    }
    
    public DoubleProperty maxWindSpeedProperty() {
        return maxWindSpeed;
    }
    
    public void setMaxWindSpeed(double maxWindSpeed) {
        maxWindSpeedProperty().set(maxWindSpeed);
    }
    
    public double getMaxWindSpeed() {
        return maxWindSpeed.getValue();
    }
}
