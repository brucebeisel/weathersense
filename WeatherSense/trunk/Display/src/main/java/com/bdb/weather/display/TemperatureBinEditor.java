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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.ThresholdType;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class TemperatureBinEditor extends JPanel {
    private DefaultListModel<Temperature> belowTemperatureModel = new DefaultListModel<>();
    private DefaultListModel<Temperature> aboveTemperatureModel = new DefaultListModel<>();
    private final JList<Temperature> belowTemperatureBinList = new JList<>(belowTemperatureModel);
    private final JList<Temperature> aboveTemperatureBinList = new JList<>(aboveTemperatureModel);
    private final JTextField temperatureTF = new JTextField(6);
    private final JButton belowBinButton = new JButton("<<");
    private final JButton aboveBinButton = new JButton(">>");
    private final JButton deleteAboveBinButton = new JButton("Delete");
    private final JButton deleteBelowBinButton = new JButton("Delete");
    private boolean changed = false;
    
    public TemperatureBinEditor() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        belowTemperatureBinList.setFixedCellWidth(100);
        belowTemperatureBinList.setFixedCellHeight(15);
        aboveTemperatureBinList.setFixedCellWidth(100);
        aboveTemperatureBinList.setFixedCellHeight(15);
        JScrollPane sp = new JScrollPane(belowTemperatureBinList);
        sp.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Below Temperatures"));
        gbc.gridx = 0;
        add(sp, gbc);
        gbc.gridx++;
        add(belowBinButton, gbc);
        gbc.gridx++;
        add(temperatureTF, gbc);
        gbc.gridx++;
        add(aboveBinButton, gbc);
        sp = new JScrollPane(aboveTemperatureBinList);
        sp.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Above Temperatures"));
        gbc.gridx++;
        add(sp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(deleteBelowBinButton, gbc);
        gbc.gridx = 4;
        add(deleteAboveBinButton, gbc);
        
        belowBinButton.addActionListener((ActionEvent e) -> {
            try {
                Temperature temperature = new Temperature(Double.parseDouble(temperatureTF.getText()));
                addBelowTemperature(temperature);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid temperature entered", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        aboveBinButton.addActionListener((ActionEvent e) -> {
            try {
                Temperature temperature = new Temperature(Double.parseDouble(temperatureTF.getText()));
                addAboveTemperature(temperature);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid temperature entered", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteAboveBinButton.addActionListener((ActionEvent e) -> {
            List<Temperature> selectedList = aboveTemperatureBinList.getSelectedValuesList();
            for (Temperature t : selectedList) {
                aboveTemperatureModel.removeElement(t);
                changed = true;
            }
        });

        deleteBelowBinButton.addActionListener((ActionEvent e) -> {
            List<Temperature> selectedList = belowTemperatureBinList.getSelectedValuesList();
            for (Temperature t : selectedList) {
                belowTemperatureModel.removeElement(t);
                changed = true;
            }
        });
    }

    private void addAboveTemperature(Temperature t) {
        changed = true;
        int insertIndex = aboveTemperatureModel.getSize();
        for (int i = 0; i < aboveTemperatureModel.getSize(); i++) {
            if (t.equals(aboveTemperatureModel.get(i))) {
                insertIndex = -1;
                break;
            }
            if (t.compareTo(aboveTemperatureModel.get(i)) > 0) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex >= 0)
            aboveTemperatureModel.insertElementAt(t, insertIndex);

    }

    private void addBelowTemperature(Temperature t) {
        changed = true;
        int insertIndex = belowTemperatureModel.getSize();
        for (int i = 0; i < belowTemperatureModel.getSize(); i++) {
            if (t.equals(belowTemperatureModel.get(i))) {
                insertIndex = -1;
                break;
            }
            if (t.compareTo(belowTemperatureModel.get(i)) < 0) {
                insertIndex = i;
                break;
            }
        }
        if (insertIndex >= 0)
            belowTemperatureModel.insertElementAt(t, insertIndex);
    }
    
    public void loadValues(Collection<TemperatureBin> temperatureBins) {
        for (TemperatureBin bin : temperatureBins) {
            if (bin.getThresholdType() == ThresholdType.BELOW_THRESHOLD)
                addBelowTemperature(bin.getThreshold());
            else
                addAboveTemperature(bin.getThreshold());
        }
    }
    
    public List<TemperatureBin> saveValues() {
        List<TemperatureBin> bins = new ArrayList<>();
        int n = 0;
        for (int i = 0; i < belowTemperatureBinList.getModel().getSize(); i++) {
            Temperature t = belowTemperatureBinList.getModel().getElementAt(i);
            TemperatureBin bin = new TemperatureBin(n++, ThresholdType.BELOW_THRESHOLD, t);
            bins.add(bin);
        }

        for (int i = 0; i < aboveTemperatureBinList.getModel().getSize(); i++) {
            Temperature t = aboveTemperatureBinList.getModel().getElementAt(i);
            TemperatureBin bin = new TemperatureBin(n++, ThresholdType.ABOVE_THRESHOLD, t);
            bins.add(bin);
        }
        
        return bins;
    }

    public boolean hasChangeOccurred() {
        return changed;
    }
}