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

import com.bdb.util.LabeledFieldPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.bdb.util.RadioButtonPanel;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.JColorChooser;

@SuppressWarnings("serial")
public class UserPreferenceDialog extends JDialog implements ActionListener
{
    private static final String UNIT_CARD_NAME = "Units";
    private static final String DB_CARD_NAME = "Database";
    private static final String COLOR_CARD_NAME = "Colors";
    private static final String LOCALE_CARD_NAME = "Locale";
    private static final String COLOR_BUTTON_TEXT = "    ";
    
    private final UserPreferences  prefs = UserPreferences.getInstance();
    private final JTabbedPane      tabbedPane = new JTabbedPane();
    private RadioButtonPanel       temperatureRBP;
    private RadioButtonPanel       depthRBP;
    private RadioButtonPanel       speedRBP;
    private RadioButtonPanel       pressureRBP;
    private final JTextField       hostTF = new JTextField(10);
    private final JTextField       portTF = new JTextField(10);
    private final JButton          highTempColorButton = new JButton(COLOR_BUTTON_TEXT);
    private final JButton          lowTempColorButton = new JButton(COLOR_BUTTON_TEXT);
    private final JButton          outdoorTempColorButton = new JButton(COLOR_BUTTON_TEXT);
    private final JButton          indoorTempColorButton = new JButton(COLOR_BUTTON_TEXT);
    private final JComboBox        localeCB = new JComboBox(Locale.getAvailableLocales());
    
    private static final Logger s_log = Logger.getLogger(UserPreferenceDialog.class.getName());
//    private static final String COLOR_COL_HEADERS[] = {"", "Current", "High", "Low", "Avg"};
//    private static final String COLOR_ROW_HEADERS[] = {"Outdoor Temperature", "Indoor Temperature"};
    //protected static ResourceBundle s_localizationResources = ResourceBundle.getBundle("com.bdb.weathersense.Localization");
    
    @SuppressWarnings("LeakingThisInConstructor")
    public UserPreferenceDialog() {
        super();
        setModal(true);
        setTitle("User Preferences");
        setLayout(new BorderLayout());

        tabbedPane.add(unitsCard(), UNIT_CARD_NAME);
        tabbedPane.add(dbCard(), DB_CARD_NAME);
        tabbedPane.add(colorsCard(), COLOR_CARD_NAME);
        tabbedPane.add(localeCard(), LOCALE_CARD_NAME);
              
        add(tabbedPane, BorderLayout.CENTER);
        
        JPanel p = new JPanel();
        JButton b = new JButton("OK");
        b.setActionCommand("OK");
        b.addActionListener(this);
        p.add(b);
        
        b = new JButton("Cancel");
        b.setActionCommand("Cancel");
        b.addActionListener(this);
        p.add(b);
        
        add(p, BorderLayout.SOUTH);
        
        this.pack(); 
        this.setLocationRelativeTo(null);
    }
    
    private JPanel unitsCard() {
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
        p.add(new JLabel("Depth"), c);
        c.gridx = 1;
        p.add(depthRBP, c);
        
        Depth.Unit depthUnits[] = Depth.Unit.values();
        
        for (int i = 0; i < depthUnits.length; i++) {
            depthRBP.addItem(depthUnits[i].toString());
            if (depthUnits[i] == Depth.getDefaultUnit())
                depthRBP.setSelectedIndex(i);
        }
        
        speedRBP = new RadioButtonPanel();
        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Speed"), c);
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
        p.add(new JLabel("Pressure"), c);
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

    private JPanel dbCard() { 
        hostTF.setText(prefs.getDbHostPref());
        portTF.setText(prefs.getDbPortPref());
        JPanel p = new JPanel();
        
        JPanel p1 = new JPanel();
        p1.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Database Host"));
        p1.add(hostTF);
        p.add(p1);
        
        JPanel p2 = new JPanel();
        p2.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Database Port"));
        p2.add(portTF);
        p.add(p2);
        
        return p;
    }
    
    private JPanel colorsCard() {
        JPanel p = new JPanel();
//        JPanel p = new JPanel(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.anchor = GridBagConstraints.LINE_START;
//        c.ipadx = 3;
//        c.ipady = 5;
//        c.gridx = 0;
//        c.gridy = 0;
//        
//        for (int i = 0; i < COLOR_COL_HEADERS.length; i++) {
//            c.gridx = i;
//            p.add(new JLabel(COLOR_COL_HEADERS[i]), c);
//        }
//        
//        c.gridy++;
//        for (int i = 0; i < COLOR_ROW_HEADERS.length; i++) {
//            c.gridx = 0;
//            p.add(new JLabel(COLOR_ROW_HEADERS[i]), c);
//            for (int j = 1; j < COLOR_COL_HEADERS.length; j++) {
//                c.gridx++;
//                JButton button = new JButton(" ");
//                button.setBackground(Color.BLUE);
//                p.add(button, c);
//            }
//            c.gridy++;
//        }
        
        outdoorTempColorButton.setBackground(prefs.getOutdoorTempColorPref());
        outdoorTempColorButton.addActionListener(this);
        outdoorTempColorButton.setActionCommand("color");
        JPanel p1 = new LabeledFieldPanel("Outdoor Temperature:", outdoorTempColorButton, LabeledFieldPanel.LabelLocation.LABEL_LEFT);        
        p.add(p1); 
        
        highTempColorButton.setBackground(prefs.getHighOutdoorTempColorPref());
        highTempColorButton.addActionListener(this);
        highTempColorButton.setActionCommand("color");
        p1 = new LabeledFieldPanel("Outdoor High Temperature:", highTempColorButton, LabeledFieldPanel.LabelLocation.LABEL_LEFT);        
        p.add(p1);
        
        lowTempColorButton.setBackground(prefs.getLowOutdoorTempColorPref());
        lowTempColorButton.addActionListener(this);
        lowTempColorButton.setActionCommand("color");
        p1 = new LabeledFieldPanel("Outdoor Low Temperature:", lowTempColorButton, LabeledFieldPanel.LabelLocation.LABEL_LEFT);        
        p.add(p1);
        
        indoorTempColorButton.setBackground(prefs.getIndoorTempColorPref());
        indoorTempColorButton.addActionListener(this);
        indoorTempColorButton.setActionCommand("color");
        p1 = new LabeledFieldPanel("Indoor Temperature:", indoorTempColorButton, LabeledFieldPanel.LabelLocation.LABEL_LEFT);        
        p.add(p1);
        
        return p;
    }
    
    private JPanel localeCard() {
        JPanel p = new JPanel();

        p.add(localeCB);

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        s_log.log(Level.FINE, "Action command = {0}", event.getActionCommand());
        
        switch (event.getActionCommand()) {
            case "OK":
                prefs.putTemperatureUnitsPref(Temperature.Unit.values()[temperatureRBP.getSelectedIndex()]);
                prefs.putDepthUnitsPref(Depth.Unit.values()[depthRBP.getSelectedIndex()]);
                prefs.putPressureUnitsPref(Pressure.Unit.values()[pressureRBP.getSelectedIndex()]);
                prefs.putSpeedUnitsPref(Speed.Unit.values()[speedRBP.getSelectedIndex()]);
                prefs.putDbHostPref(hostTF.getText());
                prefs.putDbPortPref(portTF.getText());
                prefs.putHighOutdoorTempColorPref(highTempColorButton.getBackground());
                prefs.putOutdoorTempColorPref(outdoorTempColorButton.getBackground());
                prefs.sync();
                setVisible(false);
                break;
                
            case "Cancel":
                setVisible(false);
                highTempColorButton.setBackground(prefs.getHighOutdoorTempColorPref());
                break;
                
            default:
                if (event.getSource() == highTempColorButton) {
                    Color color = JColorChooser.showDialog(this, "Choose Color", prefs.getHighOutdoorTempColorPref());
                    if (color != null)
                        highTempColorButton.setBackground(color);
                }
                else if (event.getSource() == outdoorTempColorButton) {
                    Color color = JColorChooser.showDialog(this, "Choose Color", prefs.getOutdoorTempColorPref());
                    if (color != null)
                        outdoorTempColorButton.setBackground(color);
                }
                break;
        }
    }
}
