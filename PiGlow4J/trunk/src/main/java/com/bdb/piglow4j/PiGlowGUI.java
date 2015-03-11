/*
 * Copyright (C) 2015 Bruce
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Bruce
 */
public class PiGlowGUI {
    private boolean on = false;
    private boolean topArmOn = false;
    private boolean leftArmOn = false;
    private boolean rightArmOn = false;
    private int intensities[] = new int[18];

    public void createElements() {
        try {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            InputStream is = ClassLoader.class.getResourceAsStream("/piglow.jpg");
            BufferedImage bufferedImage = ImageIO.read(is);
            frame.add(new PiGlowJLabel(new ImageIcon(bufferedImage)));
            frame.pack();
            frame.setVisible(true);
        }
        catch (IOException ex) {
            Logger.getLogger(PiGlowGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processBytes(int address, byte buffer[], int length) {
        if (address == 0x0 && buffer[0] == 0x1) {
            on = true;
            System.out.println("PiGlow is ON");
            return;
        }

        if (!on) {
            System.out.println("Ignoring bytes because board is OFF");
            return;
        }

        if (address == 0x13 && ((int)buffer[0] & 0xFF) == 0xFF) {
            System.out.println("Turning on TOP arm");
            topArmOn = true;
        }
        else if (address == 0x14 && ((int)buffer[0] & 0xFF) == 0xFF) {
            System.out.println("Turning on LEFT arm");
            leftArmOn = true;
        }
        else if (address == 0x15 && ((int)buffer[0] & 0xFF) == 0xFF) {
            System.out.println("Turning on RIGHT arm");
            rightArmOn = true;
        }

        if (address >= 0x1 && address <= 0x12) {
            System.out.println("Getting intensities");
            for (int i = 0; address + i <= 0x12 && i < length; i++)
                intensities[address + i - 1] = buffer[i];


            for (int intensity : intensities)
                System.out.print("" + intensity + " ");

            System.out.println();
        }
    }
}
