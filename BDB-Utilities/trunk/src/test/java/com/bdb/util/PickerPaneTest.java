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

/**
 *
 * @author Bruce
 */
public class PickerPaneTest {
    //
    // Unit test
    //
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame();

        String[] choices = {"One", "Two", "Three", "Four"};
        String[] selections = {"Five", "Six", "Seven", "Very Long item that should cause scrolling"};

        PickerPane pp = new PickerPane("Choices", "Selections", choices, selections);

        frame.getContentPane().add(pp);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();

        frame.setVisible(true);
    }
    
}
