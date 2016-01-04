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
import java.util.Collection;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.TemperatureBin;
import com.bdb.weather.common.ThresholdType;

/**
 *
 * @author Bruce
 */
public class TemperatureBinEditor extends GridPane {
    private ObservableList<Temperature> belowTemperatureModel = new SimpleListProperty<>();
    private ObservableList<Temperature> aboveTemperatureModel = new SimpleListProperty<>();
    private final ListView<Temperature> belowTemperatureBinList = new ListView<>(belowTemperatureModel);
    private final ListView<Temperature> aboveTemperatureBinList = new ListView<>(aboveTemperatureModel);
    private final TextField temperatureTF = new TextField();
    private final Button belowBinButton = new Button("<<");
    private final Button aboveBinButton = new Button(">>");
    private final Button deleteAboveBinButton = new Button("Delete");
    private final Button deleteBelowBinButton = new Button("Delete");
    private boolean changed = false;
    
    public TemperatureBinEditor() {
        belowTemperatureBinList.setMinWidth(100);
        belowTemperatureBinList.setMinHeight(15);
        aboveTemperatureBinList.setMinWidth(100);
        aboveTemperatureBinList.setMinHeight(15);
        ScrollPane sp = new ScrollPane(belowTemperatureBinList);
        //sp.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Below Temperatures"));
        int gridx = 0;
        int gridy = 0;
        add(sp, gridx, gridy);
        gridx++;
        add(belowBinButton, gridx, gridy);
        gridx++;
        add(temperatureTF, gridx, gridy);
        gridx++;
        add(aboveBinButton, gridx, gridy);
        sp = new ScrollPane(aboveTemperatureBinList);
        //sp.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Above Temperatures"));
        gridx++;
        add(sp, gridx, gridy);

        gridx = 0;
        gridy = 1;
        add(deleteBelowBinButton, gridx, gridy);
        gridx = 4;
        add(deleteAboveBinButton, gridx, gridy);
        
        belowBinButton.setOnAction((ActionEvent e) -> {
            try {
                Temperature temperature = new Temperature(Double.parseDouble(temperatureTF.getText()));
                addBelowTemperature(temperature);
            }
            catch (NumberFormatException ex) {
                ErrorDisplayer.getInstance().displayError("Invalid temperature entered");
            }
        });
        
        aboveBinButton.setOnAction((ActionEvent e) -> {
            try {
                Temperature temperature = new Temperature(Double.parseDouble(temperatureTF.getText()));
                addAboveTemperature(temperature);
            }
            catch (NumberFormatException ex) {
                ErrorDisplayer.getInstance().displayError("Invalid temperature entered");
            }
        });

        deleteAboveBinButton.setOnAction((ActionEvent e) -> {
            List<Temperature> selectedList = aboveTemperatureBinList.getSelectionModel().getSelectedItems();
            for (Temperature t : selectedList) {
                aboveTemperatureModel.remove(t);
                changed = true;
            }
        });

        deleteBelowBinButton.setOnAction((ActionEvent e) -> {
            List<Temperature> selectedList = belowTemperatureBinList.getSelectionModel().getSelectedItems();
            for (Temperature t : selectedList) {
                belowTemperatureModel.remove(t);
                changed = true;
            }
        });
    }

    private void addAboveTemperature(Temperature t) {
        changed = true;
        int insertIndex = aboveTemperatureModel.size();
        for (int i = 0; i < aboveTemperatureModel.size(); i++) {
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
            aboveTemperatureModel.add(insertIndex, t);

    }

    private void addBelowTemperature(Temperature t) {
        changed = true;
        int insertIndex = belowTemperatureModel.size();
        for (int i = 0; i < belowTemperatureModel.size(); i++) {
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
            belowTemperatureModel.add(insertIndex, t);
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
        for (int i = 0; i < belowTemperatureBinList.getItems().size(); i++) {
            Temperature t = belowTemperatureBinList.getItems().get(i);
            TemperatureBin bin = new TemperatureBin(n++, ThresholdType.BELOW_THRESHOLD, t);
            bins.add(bin);
        }

        for (int i = 0; i < aboveTemperatureBinList.getItems().size(); i++) {
            Temperature t = aboveTemperatureBinList.getItems().get(i);
            TemperatureBin bin = new TemperatureBin(n++, ThresholdType.ABOVE_THRESHOLD, t);
            bins.add(bin);
        }
        
        return bins;
    }

    public boolean hasChangeOccurred() {
        return changed;
    }
}