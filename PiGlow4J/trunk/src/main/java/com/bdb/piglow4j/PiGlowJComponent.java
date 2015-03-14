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
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Bruce Beisel
 */
@SuppressWarnings("serial")
public class PiGlowJComponent extends JPanel {
    private final LedInfo leds[] = new LedInfo[18];
    private final Color whites[] = new Color[256];
    private final Color blues[] = new Color[256];
    private final Color yellows[] = new Color[256];
    private final Color greens[] = new Color[256];
    private final Color oranges[] = new Color[256];
    private final Color reds[] = new Color[256];
    private final ImageIcon background;
    private static final Logger logger = Logger.getLogger(PiGlowJComponent.class.getName());

    private class LedInfo {
        public PiGlowArm arm;
        public PiGlowColor color;
        public int x;
        public int y;
        public int intensity;
        public Color[] colors;
        public JLabel label;
        public LedInfo(PiGlowArm arm, PiGlowColor color, int x, int y, Color[] colors) {
            this.arm = arm;
            this.color = color;
            this.x = x;
            this.y = y;
            this.colors = colors;
            label = new JLabel();
            label.setOpaque(true);
            label.setSize(15, 15);
            label.setPreferredSize(new Dimension(15, 15));
            label.setLocation(x, y);
            label.setBackground(colors[0]);
        }
    }

    public PiGlowJComponent(ImageIcon image) {
        setLayout(null);
        setBackground(Color.BLACK);
        background = image;
        buildColors(whites, Color.WHITE);
        buildColors(blues, Color.BLUE);
        buildColors(yellows, Color.YELLOW);
        buildColors(greens, Color.GREEN);
        buildColors(oranges, Color.ORANGE);
        buildColors(reds, Color.RED);
        LedInfo info = new LedInfo(PiGlowArm.TOP, PiGlowColor.WHITE, 210, 432, whites);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.WHITE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.BLUE, 155, 415, blues);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.GREEN, 135, 360, greens);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.YELLOW, 135, 305, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.ORANGE, 170, 255, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.RED, 225, 220, reds);
        leds[PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.RED).getAddress() - 1] = info;

        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.RED, 50, 500, reds);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.RED).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.ORANGE, 115, 520, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.YELLOW, 175, 525, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.GREEN, 230, 505, greens);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.BLUE, 265, 460, blues);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.WHITE, 260, 405, whites);
        leds[PiGlowLED.findLed(PiGlowArm.LEFT, PiGlowColor.WHITE).getAddress() - 1] = info;

        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.RED, 385, 504, reds);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.RED).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.ORANGE, 380, 445, oranges);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.YELLOW, 350, 395, yellows);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.GREEN, 310, 355, greens);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.BLUE, 260, 340, blues);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.WHITE, 205, 375, whites);
        leds[PiGlowLED.findLed(PiGlowArm.RIGHT, PiGlowColor.WHITE).getAddress() - 1] = info;

        for (LedInfo led : leds)
            add(led.label);

        setPreferredSize(new Dimension(background.getIconWidth(), background.getIconHeight()));
    }

    private void buildColors(Color[] colors, Color baseColor) {

        for (int i = 0; i <= 255; i++) {
            float ratio = (float)i / 255.0F;
            if (ratio > 1.0F)
                ratio = 1.0F;

            int red = (int)((float)baseColor.getRed() * ratio);
            int green = (int)((float)baseColor.getGreen() * ratio);
            int blue = (int)((float)baseColor.getBlue() * ratio);
            colors[i] = new Color(red, green, blue);
        }
    }

    public void setIntensities(int[] intensities) {
        logger.info("Receiving intensities");
        for (int i = 0; i < leds.length; i++) {
            leds[i].intensity = intensities[i];
            System.out.print("" + intensities[i] + " ");
        }
        System.out.println();
    }

    public void commit() {
        logger.info("Committing");
        for (LedInfo led : leds)
            led.label.setBackground(led.colors[led.intensity]);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background.getImage(), 0, 0, null);
    }
    
}