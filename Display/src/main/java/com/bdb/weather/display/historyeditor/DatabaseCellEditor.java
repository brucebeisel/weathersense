/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.display.historyeditor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class DatabaseCellEditor extends BorderPane implements EventHandler<ActionEvent> {
    private final CheckBox isNullCB = new CheckBox();
    private final TextField value = new TextField(); // TODO Use formatted text field?
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DatabaseCellEditor() {
        setNull(true);
        isNullCB.setOnAction(this);
        FlowPane p = new FlowPane();
        p.getChildren().add(isNullCB);
        p.getChildren().add(new Label("null  "));
        setLeft(p);
        setCenter(value);
    }
    
    public DatabaseCellEditor(String value) {
        this();
        setValue(value);
    }

    private void setNull(boolean isNull) {
        isNullCB.setSelected(isNull);
        value.setEditable(isNull);
    }
    
    private void setValue(String value) {
        isNullCB.setSelected(false);
        this.value.setEditable(true);
        this.value.setText(value);
    }
    
    public String getValue() {
        if (isNullCB.isSelected())
            return null;
        else
            return value.getText();
    }

    @Override
    public void handle(ActionEvent e) {
        value.setEditable(!isNullCB.isSelected());
    }
}
