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
package com.bdb.piglow4j;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlowJLabel extends JLabel {
    private static final long RED = 0x0000FF;
    private static final long ORANGE = 0xFF9900;
    private static final long GREEN = 0x00FF00;
    private static final long YELLOW = 0xFFFF00;
    private static final long BLUE = 0x0000FF;
    private static final long WHITE = 0xFFFFFF;
    private final LedInfo leds[] = new LedInfo[18];
    private Color whites[] = new Color[256];
    private Color blues[] = new Color[256];
    private Color yellows[] = new Color[256];
    private Color greens[] = new Color[256];
    private Color oranges[] = new Color[256];
    private Color reds[] = new Color[256];
    private static final Logger logger = Logger.getLogger(PiGlowJLabel.class.getName());

    private class LedInfo {
        public PiGlowArm arm;
        public PiGlowColor color;
        public int x;
        public int y;
        public int intensity;
        public Color[] colors;
        public LedInfo(PiGlowArm arm, PiGlowColor color, int x, int y, Color[] colors) {
            this.arm = arm;
            this.color = color;
            this.x = x;
            this.y = y;
            this.colors = colors;
        }
    }

    public PiGlowJLabel(ImageIcon image) {
        super(image);
        buildColors(whites, Color.WHITE);
        buildColors(blues, Color.BLUE);
        buildColors(yellows, Color.YELLOW);
        buildColors(greens, Color.GREEN);
        buildColors(oranges, Color.ORANGE);
        buildColors(reds, Color.RED);
        int x = 10;
        LedInfo info = new LedInfo(PiGlowArm.TOP, PiGlowColor.RED, x, 10, reds);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.RED).getAddress() - 1] = info;
        x += 40;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.ORANGE, x, 10, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.ORANGE).getAddress() - 1] = info;
        x += 40;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.YELLOW, x, 10, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.YELLOW).getAddress() - 1] = info;
        x += 40;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.GREEN, x, 10, greens);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.GREEN).getAddress() - 1] = info;
        x += 40;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.BLUE, x, 10, blues);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.BLUE).getAddress() - 1] = info;
        x += 40;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.WHITE, x, 10, whites);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.WHITE).getAddress() - 1] = info;
        x += 40;

        int y = 100;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.RED, 10, y, reds);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.RED).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.ORANGE, 10, y, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.YELLOW, 10, y, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.GREEN, 10, y, greens);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.GREEN).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.BLUE, 10, y, blues);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.BLUE).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.WHITE, 10, y, whites);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.WHITE).getAddress() - 1] = info;
        y += 40;

        y = 100;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.RED, 200, y, reds);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.RED).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.ORANGE, 200, y, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.YELLOW, 200, y, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.GREEN, 200, y, greens);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.GREEN).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.BLUE, 200, y, blues);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.BLUE).getAddress() - 1] = info;
        y += 40;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.WHITE, 200, y, whites);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.WHITE).getAddress() - 1] = info;
        y += 40;

    }

    private void buildColors(Color[] colors, Color baseColor) {

        for (int i = 0; i < 255; i++) {
            /*
            float ratio = (float)i / 255.0F;
            if (ratio > 1.0F)
                ratio = 1.0F;

            int red = (int)((float)baseColor.getRed() * ratio);
            int green = (int)((float)baseColor.getGreen() * ratio);
            int blue = (int)((float)baseColor.getBlue() * ratio);
                    */
            colors[i] = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), i);
        }

    }

    public void setIntensities(int[] intensities) {
        logger.info("Receiving intensities");
        for (int i = 0; i < 18; i++)
            leds[i].intensity = intensities[i];
    }

    public void commit() {
        logger.info("Committing");
        SwingUtilities.invokeLater(() -> this.repaint());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        Paint oldPaint = g2.getPaint();
        for (LedInfo info : leds) {
            if (info.intensity > 0) {
                g2.setPaint(info.colors[info.intensity]);
                g2.fillRect(info.x, info.y, 30, 15);
            }
        }
        g2.setPaint(oldPaint);
    }
}