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

import javax.swing.*;

//
// CLASS: LabeledFieldPanel
//
// DESCRIPTION:
//	A convenience panel that displays a field with a label.
//	It uses the default layout manager, but that can be changed
//	by the application if desired.
//
public class LabeledFieldPanel<T extends JComponent> extends JPanel {
    public enum LabelLocation {
        LABEL_LEFT,
        LABEL_RIGHT
    }

    private static final long serialVersionUID = 8657357228739720158L;

    private final JLabel label;
    private final T component;

    /**
     * Constructor.
     * 
     * @param text The text for the label
     * @param component The component that is being labeled
     * @param labelLocation The location of the label relative to the component
     */
    public LabeledFieldPanel(String text, T component, LabelLocation labelLocation) {
        this.component = component;
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
        label = new JLabel(text);
        label.setLabelFor(component);

        if (labelLocation == LabelLocation.LABEL_LEFT) {
            add(label);
            add(component);
        }
        else {
            add(component);
            add(label);
        }
    }

    /**
     * Constructor that places the label to the left of the component.
     * 
     * @param text The text for the label
     * @param component The component that is being labeled
     */
    public LabeledFieldPanel(String text, T component) {
        this(text, component, LabelLocation.LABEL_LEFT);
    }

    /**
     * Get the label text
     * 
     * @return The label
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Get the component.
     * 
     * @return The component
     */
    public T getComponent() {
        return component;
    }
}
