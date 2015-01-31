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

import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;

public class CheckBoxPanel extends ToggleButtonPanel {
    private static final long serialVersionUID = 8719824705346029049L;

    public CheckBoxPanel() {
        super();
    }

    public CheckBoxPanel(String[] items) {
        super(items);
    }

    public CheckBoxPanel(LayoutManager layout) {
        super(layout);
    }

    public CheckBoxPanel(String[] items, LayoutManager layout) {
        super(items, layout);
    }

    public void addItem(String value, JCheckBox button) {
        addButton(value, button);
    }

    @Override
    public void addItem(String value, Icon icon) {
        JCheckBox btn = new JCheckBox(icon);

        addItem(value, btn);
    }

    @Override
    public void addItem(String text) {
        JToggleButton btn = new JCheckBox(text);

        addItem(text, btn);
    }
}