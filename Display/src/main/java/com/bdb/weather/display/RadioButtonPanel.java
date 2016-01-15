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

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

public final class RadioButtonPanel extends FlowPane {
    private final List<RadioButton> buttons = new ArrayList<>();
    private final ToggleGroup       group = new ToggleGroup();
    
    public RadioButtonPanel() {
    }
    
    public RadioButtonPanel(String[] labels) {
        for (String label : labels)
            addRadioButton(label);
    }
    
    public void addRadioButton(String text) {
        RadioButton button = new RadioButton(text);
        buttons.add(button);
        this.getChildren().add(button);
        group.getToggles().add(button);
        if (buttons.size() == 1)
            button.setSelected(true);
    }
    
    public String selectedButton() {
        for (RadioButton button : buttons) {
            if (button.isSelected())
                return button.getText();
        }
        
        throw new IllegalStateException("No radio buttons were selected");
    }
    
    public void setSelection(String text) {
        for (RadioButton button : buttons) {
            if (button.getText().equals(text))
                button.setSelected(true);
        }
    }
    
    public void addEventHandler(EventHandler<ActionEvent> listener) {
        buttons.forEach((button) -> button.setOnAction(listener));
    }
}
