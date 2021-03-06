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
package com.bdb.weather.display.freeplot;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import org.jfree.chart.axis.NumberAxis;

import com.bdb.weather.display.RadioButtonPanel;

/**
 * The control that determines the visibility of the series in a Series Collection.
 * 
 * @author Bruce
 *
 */
public class SeriesGroupControl extends VBox {
    private static final String CUSTOM_LABEL = "Custom";
    private static final String NONE_LABEL = "None";
    private static final String ALL_LABEL = "All";
    private static final String BUTTONS[] = {ALL_LABEL, NONE_LABEL, CUSTOM_LABEL};
    private final String              groupName;
    private final RadioButtonPanel    radioButtonPanel = new RadioButtonPanel(BUTTONS);
    private final TilePane            innerPanel = new TilePane();
    private final NumberAxis          rangeAxis;
    private int                       rangeAxisIndex;
    private final List<SeriesControl> controls = new ArrayList<>();
    
    /**
     * Constructor.
     * 
     * @param name The name of the series group control
     * @param rangeAxis The axis for the X axis
     * @param listener A listener that is watching for changes to the radio buttons
     */
    public SeriesGroupControl(String name, NumberAxis rangeAxis, EventHandler<ActionEvent> listener) {
        groupName = name;
        radioButtonPanel.addEventHandler((ActionEvent e) -> {
            controls.stream().forEach((control) -> {
                if (!radioButtonPanel.selectedButton().equals(CUSTOM_LABEL))
                    control.disable();
                else
                    control.enable();
            });
        });
        radioButtonPanel.addEventHandler(listener); 
        this.getChildren().add(radioButtonPanel);
        //panel.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), name));
        innerPanel.setPrefRows(0);
        innerPanel.setPrefColumns(1);
        this.getChildren().add(innerPanel);
        this.rangeAxis = rangeAxis;
    }
    
    /**
     * Add a series control for this group.
     * 
     * @param control The control to add
     */
    public void addSeriesControl(SeriesControl control) {
        innerPanel.getChildren().add(control.getNode());
        controls.add(control);
        if (!radioButtonPanel.selectedButton().equals(CUSTOM_LABEL))
            control.disable();
        else
            control.enable();
    }
    
    /**
     * Get the list of the series names that should be displayed
     * 
     * @return The list
     */
    public List<String> getVisibleSeries() {
        List<String> list = new ArrayList<>();
        
        for (SeriesControl control : controls) {
            String selectedButton = radioButtonPanel.selectedButton();
            if (selectedButton.equals(ALL_LABEL) || (selectedButton.equals(CUSTOM_LABEL) && control.isSeriesVisible()))
                list.add(control.getName());
        }
        
        return list;
    }
    
    /**
     * Set the index for the range axis of this series. This is needed in order to map
     * the data set to the axis.
     * 
     * @param index The index of the range axis
     */
    public void setRangeAxisIndex(int index) {
        rangeAxisIndex = index;
    }
    
    /**
     * Get the index of the range axis.
     * 
     * @return The index of the range axis
     */
    public int getRangeAxisIndex() {
        return rangeAxisIndex;
    }
    
    /**
     * Get the name of this series group.
     * 
     * @return The series group name
     */
    public String getGroupName() {
        return groupName;
    }
    
    /**
     * Get the range axis for this series group.
     * 
     * @return The range axis
     */
    public NumberAxis getRangeAxis() {
        return rangeAxis;
    }
    
    /**
     * Determines whether the axis that this series group uses should be visible
     * 
     * @return The axis visibility state
     */
    public boolean shouldAxisBeVisible() {
        //
        // If the radio button is set to All then all series are visible and the axis
        // should be visible.
        //
        switch (radioButtonPanel.selectedButton()) {
            case ALL_LABEL:
                return true;
            case NONE_LABEL:
                return false;
        }
        
        return controls.stream().anyMatch((control) -> (control.isSeriesVisible()));
    }
}