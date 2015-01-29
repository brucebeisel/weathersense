/**
 * 
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
