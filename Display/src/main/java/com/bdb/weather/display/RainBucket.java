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
package com.bdb.weather.display;

import javafx.scene.canvas.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import java.awt.Graphics2D;


public final class RainBucket extends Canvas {
    private static final long serialVersionUID = -4981948661829987749L;

    private final GraphicsContext gc;
    private double       value = 0.0;
    private final double       maxValue; 
    private final double       tickIncrement;
    private String       unitLabel;
    private int          valueTop = GUAGE_BOTTOM;
    private Font         origFont = null;
    private Font         boldFont = null;
    private final NumberFormat formatter;
    private double       average;
    private double       toDateAverage;

    private static final int       INSET               = 5;
    private static final int       GUAGE_HEIGHT        = 100;
    private static final int       GUAGE_WIDTH         = 50;
    private static final int       TOP_Y               = 15;
    private static final int       BOTTOM_Y            = GUAGE_HEIGHT + TOP_Y;
    private static final int       LEFT_X              = INSET;
    private static final int       RIGHT_X             = LEFT_X + GUAGE_WIDTH;
    private static final int       GUAGE_BOTTOM        = BOTTOM_Y;
    private static final int       OVAL_HEIGHT         = 10;
    private static final int       PREFERRED_HEIGHT    = TOP_Y + GUAGE_HEIGHT + INSET + 20;
    private static final int       PREFERRED_WIDTH     = INSET + GUAGE_WIDTH + 5 + 10 + 5 + 20;
    private static final int       TICK_LINE_LENGTH    = 5;
    private static final Dimension PREFERRED_SIZE      = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    private static final Color     BEAKER_COLOR        = new Color(100, 100, 255);
    private static final Paint     WATER_PAINT         = new GradientPaint(0.0f, 0.0f, new Color(0, 0, 255, 50), 35.0f, 0.0f, Color.WHITE, true);
    private static final Paint     SURFACE_PAINT       = new GradientPaint(0.0f, 0.0f, new Color(150, 150, 255), 35.0f, 0.0f, Color.WHITE, true);
    private static final Paint     BELOW_AVERAGE_PAINT = new GradientPaint(0.0f, 0.0f, new Color(255, 150, 150), 35.0f, 0.0f, Color.WHITE, true);
    private static final Paint     ABOVE_AVERAGE_PAINT = new GradientPaint(0.0f, 0.0f, new Color(150, 255, 150), 35.0f, 0.0f, Color.WHITE, true);

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
	gc = this.getGraphicsContext2D();
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

    /**
     * Calculate the pixel location of the top of the water line.
     * 
     * @param value The value for which to calculate the pixel location
     * @return The pixel location
     */
    private int valueLocation(double value) {
        value = Math.min(value, maxValue);
        int offset = (int)Math.round((double)GUAGE_HEIGHT * (value / maxValue));
        return GUAGE_BOTTOM - offset;
    }
    
    private void paintAverage(Graphics2D g2, double average, double actual) {
        if (average > 0.0) {
            if (average > actual)
                g2.setPaint(BELOW_AVERAGE_PAINT);
            else
                g2.setPaint(ABOVE_AVERAGE_PAINT);
            
            g2.fillOval(INSET, valueLocation(average) - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);
        }

    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    public void paint() {

        if (origFont == null) {
            origFont = gc.getFont();
            boldFont = origFont.deriveFont(Font.BOLD);
        }

        Dimension actualSize = getSize();

        int translateX = 0;
        int translateY = 0;

        if (actualSize.width > PREFERRED_SIZE.width)
            translateX = (actualSize.width - PREFERRED_SIZE.width) / 2;

        if (actualSize.height > PREFERRED_SIZE.height)
            translateY = (actualSize.height - PREFERRED_SIZE.height) / 2;

        gc.translate(translateX, translateY);

        //
        // If the JComponent has been resized smaller than the preferred size, then scale
        // TODO Determine why the if below was commented out
        //if (actualSize.width < PREFERRED_SIZE.width || actualSize.height < PREFERRED_SIZE.height)
        //{
        double wscale = (double)actualSize.width / (double)PREFERRED_SIZE.width;
        double hscale = (double)actualSize.height / (double)PREFERRED_SIZE.height;
        double scale = Math.min(wscale, hscale);
        gc.scale(scale, scale);
        //}

        //
        // Draw the container by drawing an entire rectangle then overwriting the
        // upper portion using the background color
        //
        gc.setStroke(BEAKER_COLOR);
        //
        // Line on left of bucket
        //
        gc.strokeLine(LEFT_X, TOP_Y, LEFT_X, BOTTOM_Y);
        gc.strokeLine(RIGHT_X, TOP_Y, RIGHT_X, BOTTOM_Y);
        gc.strokeLine(LEFT_X, BOTTOM_Y, RIGHT_X, BOTTOM_Y);

        //
        // Draw an oval on the top to give the appearance of a beaker
        //
        gc.setPaint(BEAKER_COLOR);
        gc.drawOval(INSET, TOP_Y - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);

        //
        // Now draw tick marks
        //
        gc.setPaint(Color.black);

        for (double tickValue = 0; tickValue <= maxValue; tickValue += tickIncrement) {
            String valueString = formatter.format(tickValue);
            Rectangle2D rect = gc.getFont().getStringBounds(valueString, gc.getFontRenderContext());
            int tickY = valueLocation(tickValue);
            gc.drawLine(RIGHT_X + 5, tickY, RIGHT_X + 5 + TICK_LINE_LENGTH, tickY);
            gc.drawString(valueString, RIGHT_X + TICK_LINE_LENGTH + 5 + 5, tickY - Math.round(rect.getCenterY()));
        }

        //
        // Draw the water that fills the beaker based on the current value
        //
        gc.setPaint(WATER_PAINT);
        gc.fillRect(INSET, valueTop, GUAGE_WIDTH, GUAGE_BOTTOM - valueTop);

        //
        // Draw the surface of the water
        //
        gc.setPaint(SURFACE_PAINT);
        gc.fillOval(INSET, valueTop - (OVAL_HEIGHT / 2), GUAGE_WIDTH, OVAL_HEIGHT);

        paintAverage(gc, average, value);
        paintAverage(gc, toDateAverage, value);

        //
        // Draw the current value
        //
        gc.setPaint(Color.BLACK);
        String valueString = formatter.format(value) + unitLabel; 
        gc.setFont(boldFont);
        Rectangle2D rect = gc.getFont().getStringBounds(valueString, gc.getFontRenderContext());
        gc.drawString(valueString, LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(rect.getWidth()) / 2), BOTTOM_Y - INSET);

        //
        // Draw the average values
        //
        if (average > 0.0) {
            valueString = formatter.format(average) + unitLabel;
            rect = gc.getFont().getStringBounds(valueString, gc.getFontRenderContext());
            gc.drawString(valueString, LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(rect.getWidth()) / 2), TOP_Y + OVAL_HEIGHT + (Math.round(rect.getHeight()) / 2));
        }

        if (toDateAverage > 0.0) {
            valueString = formatter.format(toDateAverage) + unitLabel;
            rect = gc.getFont().getStringBounds(valueString, g2.getFontRenderContext());
            gc.drawString(valueString,
                          LEFT_X + (GUAGE_WIDTH / 2) - (Math.round(rect.getWidth()) / 2),
                          TOP_Y + OVAL_HEIGHT + (Math.round(rect.getHeight()) / 2) + (GUAGE_HEIGHT / 2) - (boldFont.getSize() / 2));
        }
        gc.setFont(origFont);

    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean v) {
        setSize(PREFERRED_SIZE);
        super.setVisible(v);
    }

    /*
    public static final void main(String args[]) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new GridLayout(1, 0));

        RainBucket btmp = null;

        for (int i = 0; i < 5; i++) {
            btmp = new RainBucket(10.0, new DecimalFormat("0.0"), "\"", 8.0, 5.0);
            final Double amount = 6.0;
            btmp.setRainfallAmount(amount);
            frame.getContentPane().add(btmp);
        }

        final RainBucket b = btmp;

        frame.setSize(200, 200);

        frame.setVisible(true);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                b.setRainfallAmount(b.getRainfallAmount() + .1);
                                if (b.getRainfallAmount() >= 12.0)
                                    b.setRainfallAmount(0.0);
                            }
                        });

                        Thread.sleep(1000);
                    }
                    catch (Exception e) {
                    }
                }
            }
        });
        t.start();
    }
    */
}