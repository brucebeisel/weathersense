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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class JScalableLabel extends JLabel {

    /**
     * Constructor.
     */
    public JScalableLabel() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param icon
     * @param arg1
     */
    public JScalableLabel(Icon icon, int arg1) {
        super(icon, arg1);
    }

    /**
     * @param icon
     */
    public JScalableLabel(Icon icon) {
        super(icon);
    }

    /**
     * @param text
     * @param icon
     * @param arg2
     */
    public JScalableLabel(String text, Icon icon, int arg2) {
        super(text, icon, arg2);
    }

    /**
     * @param text
     * @param arg1
     */
    public JScalableLabel(String text, int arg1) {
        super(text, arg1);
    }

    /**
     * @param text
     */
    public JScalableLabel(String text) {
        super(text);
    }

    @Override
    public void paint(Graphics g) {
        Dimension preferredSize = getPreferredSize();
        Dimension actualSize = getSize();

        //
        // If the JComponent has been resized smaller than the preferred size, then scale
        //
        //if (actualSize.width < preferredSize.width || actualSize.height < preferredSize.height)
        //{
        double wscale = (double) actualSize.width / (double) preferredSize.width;
        double hscale = (double) actualSize.height / (double) preferredSize.height;
        double scale = Math.min(wscale, hscale);
        ((Graphics2D) g).scale(scale, scale);
        //}
        super.paint(g);
    }

    /**
     * @param args public static void main(String[] args) { JFrame f = new JFrame();
     * f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //JLabel l = new JScalableLabel("Hello World"); ImageIcon im =
     * new ImageIcon("C:/Program Files/Windows Media Player/wmpnss_color32.jpg"); JLabel l = new JScalableLabel(im);
     * l.setHorizontalAlignment(SwingConstants.LEFT); l.setVerticalAlignment(SwingConstants.TOP); l.setBorder(new
     * BevelBorder(BevelBorder.RAISED)); f.getContentPane().setLayout(new BorderLayout()); f.getContentPane().add(l,
     * BorderLayout.CENTER); //f.getContentPane().add(new JLabel("North"), BorderLayout.NORTH);
     *
     * f.pack(); f.setVisible(true);
     *
     * }
     */
}
