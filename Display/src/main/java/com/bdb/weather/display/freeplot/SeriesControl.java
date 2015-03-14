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
package com.bdb.weather.display.freeplot;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 * The control that determines whether a series should be visible
 * 
 * @author Bruce
 *
 */
public class SeriesControl {
    private final JCheckBox checkBox;
    private final String    name;
    
    /**
     * Constructor.
     * 
     * @param name The name of the series of this control
     * @param listener The object listening for changes to the check box state
     */
    public SeriesControl(String name, ActionListener listener) {
        this.name = name;
        checkBox = new JCheckBox(name, true);
        checkBox.addActionListener(listener);
    }
    
    /**
     * Get the swing component of this control.
     * 
     * @return The swing component
     */
    public JComponent getComponent() {
        return checkBox;
    }
    
    /**
     * Return whether the series is visible.
     * 
     * @return The state of the visibility of the series
     */
    public boolean isSeriesVisible() {
        return checkBox.isSelected();
    }
    
    /**
     * Get the name of the control for this series.
     * 
     * @return The name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Enable the swing component for this series.
     */
    public void enable() {
        checkBox.setEnabled(true);
    }
    
    /**
     * Disable the swing component for this series
     */
    public void disable() {
        checkBox.setEnabled(false);
    }
}
