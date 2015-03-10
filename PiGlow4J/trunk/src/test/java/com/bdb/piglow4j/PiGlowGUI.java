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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Bruce
 */
public class PiGlowGUI {

    public static void main(String args[]) {
        try {
            System.out.println(new File(".").getAbsolutePath());
            JFrame frame = new JFrame();
            InputStream is = ClassLoader.class.getResourceAsStream("./piglow.jpg");
            BufferedImage bufferedImage = ImageIO.read(new File("src/test/java/com/bdb/piglow4j/piglow.jpg"));
            frame.add(new JLabel(new ImageIcon(bufferedImage)));
            frame.pack();
            frame.setVisible(true);
        }
        catch (IOException ex) {
            Logger.getLogger(PiGlowGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
