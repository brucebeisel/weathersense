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
package com.bdb.weather.display.preferences;

import com.bdb.util.RadioButtonPanel;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bdb.weather.common.measurement.Distance;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class UnitsPreferenceDialog extends JDialog {
    private RadioButtonPanel temperatureRBP;
    private RadioButtonPanel depthRBP;
    private RadioButtonPanel elevationRBP;
    private RadioButtonPanel speedRBP;
    private RadioButtonPanel pressureRBP;
    private UserPreferences  prefs = UserPreferences.getInstance();
    
    public UnitsPreferenceDialog() {
        super();
        setModal(true);
        setTitle("User Units Preferences");
        setLayout(new BorderLayout());
              
        JPanel p = new JPanel();
        JButton b = new JButton("OK");
        b.addActionListener((ActionEvent e) -> {
            prefs.putTemperatureUnitsPref(Temperature.Unit.values()[temperatureRBP.getSelectedIndex()]);
            prefs.putDepthUnitsPref(Depth.Unit.values()[depthRBP.getSelectedIndex()]);
            prefs.putDistanceUnitsPref(Distance.Unit.values()[elevationRBP.getSelectedIndex()]);
            prefs.putPressureUnitsPref(Pressure.Unit.values()[pressureRBP.getSelectedIndex()]);
            prefs.putSpeedUnitsPref(Speed.Unit.values()[speedRBP.getSelectedIndex()]);
            setVisible(false);
        });
        p.add(b);
        
        b = new JButton("Cancel");
        b.addActionListener((ActionEvent e) -> {
            setVisible(false);
            dispose();
        });
        p.add(b);
        
        add(p, BorderLayout.SOUTH);
        add(createUnitsPanel(), BorderLayout.CENTER);
        
        this.pack(); 
        this.setLocationRelativeTo(null);
    }
    
    private JPanel createUnitsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        temperatureRBP = new RadioButtonPanel();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 3;
        c.ipady = 5;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        p.add(new JLabel("Temperature"), c);
        c.gridx = 1;
        p.add(temperatureRBP, c);

        Temperature.Unit units[] = Temperature.Unit.values();

        for (int i = 0; i < units.length; i++) {
            temperatureRBP.addItem(units[i].toString());
            if (units[i] == Temperature.getDefaultUnit())
                temperatureRBP.setSelectedIndex(i);
        }
        
        depthRBP = new RadioButtonPanel();
        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Rainfall"), c);
        c.gridx = 1;
        p.add(depthRBP, c);
        
        Depth.Unit depthUnits[] = Depth.Unit.values();
        
        for (int i = 0; i < depthUnits.length; i++) {
            depthRBP.addItem(depthUnits[i].toString());
            if (depthUnits[i] == Depth.getDefaultUnit())
                depthRBP.setSelectedIndex(i);
        }
        
        elevationRBP = new RadioButtonPanel();
        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Elevation"), c);
        c.gridx = 1;
        p.add(elevationRBP, c);
        
        for (int i = 0; i < depthUnits.length; i++) {
            elevationRBP.addItem(depthUnits[i].toString());
            if (depthUnits[i] == Distance.getDefaultUnit())
                elevationRBP.setSelectedIndex(i);
        }
        
        speedRBP = new RadioButtonPanel();
        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Wind Speed"), c);
        c.gridx = 1;
        p.add(speedRBP, c);
        
        Speed.Unit speedUnits[] = Speed.Unit.values();
        
        for (int i = 0; i < speedUnits.length; i++) {
            speedRBP.addItem(speedUnits[i].toString());
            if (speedUnits[i] == Speed.getDefaultUnit())
                speedRBP.setSelectedIndex(i);
        }

        pressureRBP = new RadioButtonPanel();
        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Barometric Pressure"), c);
        c.gridx = 1;
        p.add(pressureRBP, c);

        Pressure.Unit pressureUnits[] = Pressure.Unit.values();

        for (int i = 0; i < pressureUnits.length; i++) {
            pressureRBP.addItem(pressureUnits[i].toString());
            if (pressureUnits[i] == Pressure.getDefaultUnit())
                pressureRBP.setSelectedIndex(i);
        }

        return p;
    }
}
