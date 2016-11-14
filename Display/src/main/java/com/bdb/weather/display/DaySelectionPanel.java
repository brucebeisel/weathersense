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
package com.bdb.weather.display;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;


/**
 *
 * @author Bruce
 */
public class DaySelectionPanel extends FlowPane {
    private static final String NEXT_CMD = "Next";
    private static final String PREV_CMD = "Prev";
    private final DatePicker datePicker = new DatePicker();
    private final List<DateChangedListener> listeners = new ArrayList<>();
    private LocalDate currentDate;

    public interface DateChangedListener {
        public void dateChanged(LocalDate date);
    }

    public DaySelectionPanel(LocalDate day) {
        Button downButton = new Button(PREV_CMD);
        downButton.setOnAction((event) -> {
            currentDate = currentDate.minusDays(1);
            datePicker.setValue(currentDate);
            notifyListeners();
        });

        Button upButton = new Button(NEXT_CMD);
        upButton.setOnAction((event) -> {
            currentDate = currentDate.plusDays(1);
            datePicker.setValue(currentDate);
            notifyListeners();
        });

        this.getChildren().add(downButton);
        this.getChildren().add(datePicker);
        this.getChildren().add(upButton);

        currentDate = day;

        datePicker.setValue(currentDate);
    }

    public void addDateChangedListener(DateChangedListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeDateChangedListener(DateChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        listeners.stream().forEach((listener) -> {
            listener.dateChanged(currentDate);
        });
    }
}