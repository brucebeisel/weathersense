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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public final class RadioButtonPanel extends JPanel {
    private static final long serialVersionUID = 8497892000154190832L;
    private final List<JRadioButton> buttons = new ArrayList<>();
    private final ButtonGroup        group = new ButtonGroup();
    
    public RadioButtonPanel() {
    }
    
    public RadioButtonPanel(String[] labels) {
        for (String label : labels)
            addRadioButton(label);
    }
    
    public void addRadioButton(String text) {
        JRadioButton button = new JRadioButton(text);
        buttons.add(button);
        this.add(button);
        group.add(button);
        if (buttons.size() == 1)
            button.setSelected(true);
    }
    
    public String selectedButton() {
        for (JRadioButton button : buttons) {
            if (button.isSelected())
                return button.getText();
        }
        
        throw new IllegalStateException("No radio buttons were selected");
    }
    
    public void setSelection(String text) {
        for (JRadioButton button : buttons) {
            if (button.getText().equals(text))
                button.setSelected(true);
        }
    }
    
    public void addActionListener(ActionListener listener) {
        for (JRadioButton button : buttons) {
            button.addActionListener(listener);
        }
    }
}
