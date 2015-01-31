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
package com.bdb.weather.display.historyeditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DatabaseCellEditor extends JPanel implements ActionListener {
    private static final long serialVersionUID = -9201809790961007288L;
    private final JCheckBox isNullCB = new JCheckBox();
    private final JTextField value = new JTextField(5); // TODO Use formatted text field?
    
    @SuppressWarnings("LeakingThisInConstructor")
    public DatabaseCellEditor() {
        super(new BorderLayout());
        setNull(true);
        isNullCB.addActionListener(this);
        JPanel p = new JPanel();
        p.add(isNullCB);
        p.add(new JLabel("null  "));
        add(p, BorderLayout.WEST);
        add(value, BorderLayout.CENTER);
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
    public void actionPerformed(ActionEvent e) {
        value.setEditable(!isNullCB.isSelected());
    }
    
    public static void main(String [] args) {
        JFrame frame = new JFrame("Database Cell Editor Test");
        frame.setLayout(new GridLayout(3,3));
        for (int i = 0; i < 9; i++)
            frame.add(new DatabaseCellEditor());
        frame.pack();
        frame.setVisible(true);
    }

}
