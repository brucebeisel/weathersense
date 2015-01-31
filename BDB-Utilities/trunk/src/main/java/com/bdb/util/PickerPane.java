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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public final class PickerPane<T extends Comparable<T>> extends JPanel implements ActionListener {
    private static final long serialVersionUID = -990283686637501856L;
    DefaultListModel<T> choiceModel = new DefaultListModel<>();
    DefaultListModel<T> selectionModel = new DefaultListModel<>();
    BBList<T> choiceList = new BBList<>(choiceModel);
    BBList<T> selectionList = new BBList<>(selectionModel);

    public PickerPane(String choicesLabel, String selectionsLabel) {
        this(choicesLabel, selectionsLabel, null, null);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public PickerPane(String choicesLabel, String selectionsLabel, T[] choices, T[] selections) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBorder(new TitledBorder(new EtchedBorder(), choicesLabel));
        p1.add(new JScrollPane(choiceList), BorderLayout.CENTER);

        JPanel p2 = new JPanel(new GridLayout(0, 1));
        p2.setBorder(new EtchedBorder());
        JButton btn = new JButton("All");
        btn.addActionListener(this);
        p2.add(btn);

        btn = new JButton("->");
        btn.addActionListener(this);
        btn.setActionCommand("Add");
        p2.add(btn);

        btn = new JButton("<-");
        btn.addActionListener(this);
        btn.setActionCommand("Remove");
        p2.add(btn);

        btn = new JButton("None");
        btn.addActionListener(this);
        p2.add(btn);

        JPanel p3 = new JPanel(new BorderLayout());
        p3.setBorder(new TitledBorder(new EtchedBorder(), selectionsLabel));
        p3.add(new JScrollPane(selectionList));

        Box p4 = new Box(BoxLayout.Y_AXIS);
        p4.setBorder(new EtchedBorder());

        p4.add(Box.createVerticalGlue());

        btn = new JButton("Move Up");
        btn.addActionListener(this);
        p4.add(btn);

        btn = new JButton("Move Down");
        btn.addActionListener(this);
        p4.add(btn);

        p4.add(Box.createVerticalGlue());

        add(p1);
        add(p2);
        add(p3);
        add(p4);

        loadElements(choices, selections);

    }

    public void loadElements(T[] choices, T[] selections) {
        choiceModel.clear();
        selectionModel.clear();

        if (choices != null)
            for (T choice : choices)
                insertElement(choice);

        if (selections != null)
            for (T selection : selections)
                selectionModel.addElement(selection);
    }

    private void insertElement(T element) {
        boolean inserted = false;
        //
        // Perform insert sort
        //
        for (int j = 0; j < choiceModel.size(); j++) {
            T choice = choiceModel.get(j);

            if (element.compareTo(choice) < 0) {
                choiceModel.add(j, element);
                inserted = true;
                break;
            }
        }

        if (!inserted)
            choiceModel.addElement(element);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        switch (cmd) {
            case "All":
                for (int i = 0; i < choiceModel.size(); i++)
                    selectionModel.addElement(choiceModel.get(i));
                choiceModel.clear();
                break;

            case "None":
                for (int i = 0; i < selectionModel.size(); i++) {
                    T element = selectionModel.get(i);
                    insertElement(element);
                }
                selectionModel.clear();
                break;

            case "Add": {
                List<T> list = choiceList.getSelectedValuesList();
                for (T object : list) {
                    selectionModel.addElement(object);
                    choiceModel.removeElement(object);
                }
                break;
            }
            case "Remove": {
                List<T> list = selectionList.getSelectedValuesList();
                for (T object : list) {
                    insertElement(object);
                    selectionModel.removeElement(object);
                }
                break;
            }
            case "Move Up":
                break;
            case "Move Down":
                break;
        }
    }

    public Object[] selectedElements() {
        return selectionModel.toArray();
    }
}
