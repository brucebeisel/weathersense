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
package com.bdb.util;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * JLabel that always uses a bold font (unless the caller overrides the font).
 * 
 * @author Bruce
 *
 */
public class JBoldLabel extends JLabel {
    private static final long serialVersionUID = -5912452442312292762L;

    /**
     * Constructor.
     */
    public JBoldLabel() {
        makeFontBold();
    }

    /**
     * Constructor that takes a string argument.
     * 
     * @param text The label's text
     */
    public JBoldLabel(String text) {
        super(text);
        makeFontBold();
    }

    /**
     * Constructor that takes an image argument.
     * 
     * @param image The label's image
     */
    public JBoldLabel(Icon image) {
        super(image);
        makeFontBold();
    }

    /**
     * @param text
     * @param horizontalAlignment
     */
    public JBoldLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        makeFontBold();
    }

    /**
     * @param image
     * @param horizontalAlignment
     */
    public JBoldLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        makeFontBold();
    }

    /**
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public JBoldLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        makeFontBold();
    }
    
    private void makeFontBold() {
        Font font = getFont();
        setFont(font.deriveFont(Font.BOLD));
    }
}
