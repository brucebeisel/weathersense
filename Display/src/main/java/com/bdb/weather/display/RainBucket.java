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
package com.bdb.weather.display;

import java.text.NumberFormat;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public final class RainBucket extends Canvas {
    private GraphicsContext         gc;
    private double                  value = 0.0;
    private double                  maxValue; 
    private double                  tickIncrement;
    private String                  unitLabel;
    private double                  valueTop = GUAGE_BOTTOM;
    private Font                    origFont = null;
    private Font                    defaultFont = null;
    private NumberFormat            formatter;
    private double                  average;
    private double                  toDateAverage;
    private final ObjectProperty<Color>   beakerColorProperty = new SimpleObjectProperty<>(new Color(.4, .4, 1.0, 1.0));
    private final ObjectProperty<Color>   waterColorProperty = new SimpleObjectProperty<>(Color.LIGHTSKYBLUE);
    private final ObjectProperty<Color>   surfaceColorProperty = new SimpleObjectProperty<>(Color.LIGHTSKYBLUE.darker());
    private static final Stop       WHITE_STOP = new Stop(1, Color.WHITE);
    private final Stop[]            waterStops  = {new Stop(0, waterColorProperty.getValue()), WHITE_STOP};
    private Paint                   waterPaint  = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, waterStops);
    private final Stop[]            surfaceStops = {new Stop(0, surfaceColorProperty.getValue()), WHITE_STOP};
    private Paint                   surfacePaint = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, surfaceStops);

    private static final double      INSET               = 5;
    private static final double      GUAGE_HEIGHT        = 100;
    private static final double      GUAGE_WIDTH         = 50;
    private static final double      TOP_Y               = 15;
    private static final double      BOTTOM_Y            = GUAGE_HEIGHT + TOP_Y;
    private static final double      LEFT_X              = INSET;
    private static final double      RIGHT_X             = LEFT_X + GUAGE_WIDTH;
    private static final double      GUAGE_BOTTOM        = BOTTOM_Y;
    private static final double      OVAL_HEIGHT         = 10;
    private static final double      PREFERRED_HEIGHT    = TOP_Y + GUAGE_HEIGHT + INSET + 20;
    private static final double      PREFERRED_WIDTH     = INSET + GUAGE_WIDTH + 5 + 10 + 5 + 20;
    private static final double      TICK_LINE_LENGTH    = 5;
    private static final Dimension2D PREFERRED_SIZE      = new Dimension2D(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    private static final Stop[]      BELOW_AVG_STOPS     = {new Stop(0, new Color(1.0, .6, .6, 1)), new Stop(1, Color.WHITE)};
    private static final Paint       BELOW_AVERAGE_PAINT = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, BELOW_AVG_STOPS);
    private static final Stop[]      ABOVE_AVG_STOPS     = {new Stop(0, new Color(.6, 1, .6, 1)), new Stop(1, Color.WHITE)};
    private static final Paint       ABOVE_AVERAGE_PAINT = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, ABOVE_AVG_STOPS);

    public RainBucket() {
        this(10.0, NumberFormat.getNumberInstance(), "UNK", 0.0, 0.0);
    }

    /**
     * Constructor.
     * 
     * @param maxValue The value at which the rain bucket will appear full
     * @param formatter The formatter for the value and the scale labels
     * @param unitLabel The label displayed with the current value
     * @param average The average amount of rainfall for the period depicted by this bucket or zero if no average is to be displayed
     * @param toDateAverage The average amount of rainfall that should have occurred to the current date
     * 
     * @throws IllegalArgumentException maxValue or average are less than zero
     */
    public RainBucket(double maxValue, NumberFormat formatter, String unitLabel, double average, double toDateAverage) throws IllegalArgumentException {
        super(PREFERRED_SIZE.getWidth(), PREFERRED_SIZE.getHeight());

        if (maxValue < 0.0)
            throw new IllegalArgumentException("maxValue must be >= 0.0");

        if (average < 0.0)
            throw new IllegalArgumentException("average must be >= 0.0");

        this.average = average;
        this.toDateAverage = toDateAverage;
        this.tickIncrement = maxValue / 2.0;
        this.maxValue = maxValue;
        this.unitLabel = unitLabel;
        this.formatter = formatter;
        setRainfallAmount(0.0);
        //setPreferredSize(PREFERRED_SIZE);
        //setMaximumSize(PREFERRED_SIZE);
    }

    /**
     * Constructor.
     * 
     * @param maxValue The value at which the rain bucket will appear full
     * @param formatter The formatter for the value and the scale labels
     * @param unitLabel The label displayed with the current value
     * @param average The average amount of rainfall for the period depicted by this bucket or zero if no average is to be displayed
     * 
     * @throws IllegalArgumentException maxValue or average are less than zero
     */
    public RainBucket(double maxValue, NumberFormat formatter, String unitLabel, double average) throws IllegalArgumentException {
        this(maxValue, formatter, unitLabel, average, 0.0);
    }

    /**
     * Constructor that defaults average to 0.0.
     * 
     * @param maxValue The value at which the rain bucket will appear full
     * @param formatter The formatter for the value and the scale labels
     * @param unitLabel The label displayed with the current value
     */
    public RainBucket(double maxValue, NumberFormat formatter, String unitLabel) {
        this(maxValue, formatter, unitLabel, 0.0);
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
        paint();
    }

    public double getToDateAverage() {
        return toDateAverage;
    }

    public void setToDateAverage(double toDateAverage) {
        this.toDateAverage = toDateAverage;
        paint();
    }

    public void setAverages(double average, double toDateAverage) {
        this.average = average;
        this.toDateAverage = toDateAverage;
        paint();
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        this.tickIncrement = maxValue / 2.0;
        paint();
    }

    /**
     * Set the current rainfall amount.
     * 
     * @param d The rainfall amount.
     */
    public void setRainfallAmount(double d) {
        value = d;
        valueTop = valueLocation(value);
        paint();
    }

    /**
     * Get the current rainfall amount.
     * 
     * @return The rainfall amount
     */
    public double getRainfallAmount() {
        return value;
    }

    public void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
        paint();
    }

    public Color getWaterColor() {
        return waterColorProperty.getValue();
    }

    public void setWaterColor(Color color) {
        waterColorProperty.setValue(color);
        waterStops[0] = new Stop(0, color);
        waterPaint = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, waterStops);
        paint();
    }

    public ObjectProperty<Color> waterColorProperty() {
        return waterColorProperty;
    }
    
    public Color getSurfaceColor() {
        return surfaceColorProperty.getValue();
    }

    public void setSurfaceColor(Color color) {
        surfaceColorProperty.setValue(color);
        surfaceStops[0] = new Stop(0, color);
        surfacePaint = new LinearGradient(0, 0, .5, 0, true, CycleMethod.REFLECT, surfaceStops);
        paint();
    }

    public ObjectProperty<Color> surfaceColorProperty() {
        return surfaceColorProperty;
    }

    public String getUnitLabel() {
        return unitLabel;
    }
    
    public void setUnitLabel(String label) {
        unitLabel = label;
        paint();
    }
    
    /**
     * Calculate the pixel location of the top of the water line.
     * 
     * @param value The value for which to calculate the pixel location
     * @return The pixel location
     */
    private double valueLocation(double value) {
        value = Math.min(value, maxValue);
        double offset = Math.round(GUAGE_HEIGHT * (value / maxValue));
        return GUAGE_BOTTOM - offset;
    }
    
    private void paintAverage(double average, double actual) {
        if (average > 0.0) {
            if (average >= actual)
                gc.setFill(BELOW_AVERAGE_PAINT);
            else
                gc.setFill(ABOVE_AVERAGE_PAINT);
            
            gc.fillOval(INSET, valueLocation(average) - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);
        }

    }

    private void paint() {
	gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (origFont == null) {
            origFont = gc.getFont();
            defaultFont = Font.font(origFont.getFamily(), FontWeight.BOLD, origFont.getSize());
            defaultFont = origFont;
        }

        Dimension2D actualSize = new Dimension2D(getWidth(), getHeight());

        double translateX = 0;
        double translateY = 0;

        if (actualSize.getWidth() > PREFERRED_SIZE.getWidth())
            translateX = (actualSize.getWidth() - PREFERRED_SIZE.getWidth()) / 2;

        if (actualSize.getHeight() > PREFERRED_SIZE.getHeight())
            translateY = (actualSize.getHeight() - PREFERRED_SIZE.getHeight()) / 2;

        gc.translate(translateX, translateY);

        //
        // If the JComponent has been resized smaller than the preferred size, then scale
        // TODO Determine why the if below was commented out
        //if (actualSize.width < PREFERRED_SIZE.width || actualSize.height < PREFERRED_SIZE.height)
        //{
        //double wscale = actualSize.getWidth() / PREFERRED_SIZE.getWidth();
        //double hscale = actualSize.getHeight() / PREFERRED_SIZE.getHeight();
        //double scale = Math.min(wscale, hscale);
        //gc.scale(scale, scale);
        //}

        //
        // Draw the container by drawing an entire rectangle then overwriting the
        // upper portion using the background color
        //
        gc.setStroke(beakerColorProperty.getValue());
        //
        // Line on left of bucket
        //
        gc.strokeLine(LEFT_X, TOP_Y, LEFT_X, BOTTOM_Y);
        gc.strokeLine(RIGHT_X, TOP_Y, RIGHT_X, BOTTOM_Y);
        gc.strokeLine(LEFT_X, BOTTOM_Y, RIGHT_X, BOTTOM_Y);

        //
        // Draw an oval on the top to give the appearance of a beaker
        //
        gc.setStroke(beakerColorProperty.getValue());
        gc.strokeOval(INSET, TOP_Y - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);

        //
        // Now draw tick marks
        //
        gc.setStroke(Color.BLACK);

        for (double tickValue = 0; tickValue <= maxValue; tickValue += tickIncrement) {
            Text valueString = new Text(formatter.format(tickValue));
            valueString.setFont(gc.getFont());
            double tickY = valueLocation(tickValue);
            gc.strokeLine(RIGHT_X + 5, tickY, RIGHT_X + 5 + TICK_LINE_LENGTH, tickY);
            gc.strokeText(valueString.getText(), RIGHT_X + TICK_LINE_LENGTH + 5 + 5, tickY + Math.round(valueString.getLayoutBounds().getHeight() / 4));
        }

        //
        // Draw the water that fills the beaker based on the current value
        //
        gc.setFill(waterPaint);
        gc.fillRect(INSET, valueTop, GUAGE_WIDTH, GUAGE_BOTTOM - valueTop);

        //
        // Draw the surface of the water
        //
        gc.setFill(surfacePaint);
        gc.fillOval(INSET, valueTop - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);

        paintAverage(average, value);
        paintAverage(toDateAverage, value);

        //
        // Draw the current value
        //
        gc.setStroke(Color.BLACK);
        Text valueString = new Text(formatter.format(value) + unitLabel); 
        gc.setFont(defaultFont);
        gc.strokeText(valueString.getText(), LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(valueString.getLayoutBounds().getWidth()) / 2), BOTTOM_Y - INSET);

        //
        // Draw the average values
        //
        if (average > 0.0) {
            valueString = new Text(formatter.format(average) + unitLabel);
            valueString.setFont(gc.getFont());
            gc.strokeText(valueString.getText(), LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(valueString.getLayoutBounds().getWidth()) / 2), TOP_Y + OVAL_HEIGHT + (Math.round(valueString.getLayoutBounds().getHeight()) / 2));
        }

        if (toDateAverage > 0.0) {
            valueString = new Text(formatter.format(toDateAverage) + unitLabel);
            valueString.setFont(gc.getFont());
            gc.strokeText(valueString.getText(),
                          LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(valueString.getLayoutBounds().getWidth()) / 2),
                          TOP_Y + OVAL_HEIGHT + (Math.round(valueString.getLayoutBounds().getHeight()) / 2) + (GUAGE_HEIGHT / 2) - (defaultFont.getSize() / 2));
        }
        gc.setFont(origFont);

    }

}