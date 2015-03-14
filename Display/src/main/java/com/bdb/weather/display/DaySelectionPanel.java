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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.bdb.util.DateButton;

/**
 *
 * @author Bruce
 */
public class DaySelectionPanel extends JPanel implements ActionListener, PropertyChangeListener {
    private static final String NEXT_CMD = "Next";
    private static final String PREV_CMD = "Prev";
    private final DateButton dateButton = new DateButton();
    private final List<DateChangedListener> listeners = new ArrayList<>();
    private LocalDate currentDate;

    public interface DateChangedListener {
        public void dateChanged(LocalDate date);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public DaySelectionPanel(LocalDate day) {
        JButton downButton = new JButton(PREV_CMD);
        downButton.addActionListener(this);
        downButton.setActionCommand(PREV_CMD);

        JButton upButton = new JButton(NEXT_CMD);
        upButton.addActionListener(this);
        upButton.setActionCommand(NEXT_CMD);

        this.add(downButton);
        this.add(dateButton);
        this.add(upButton);

        dateButton.addPropertyChangeListener(this);

        currentDate = day;

        dateButton.setDate(currentDate);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(DateButton.DATE_PROPERTY)) {
            currentDate = (LocalDate)evt.getNewValue();
            notifyListeners();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();

        switch (cmd) {
            case NEXT_CMD:
                currentDate = currentDate.plusDays(1);
                break;
            case PREV_CMD:
                currentDate = currentDate.minusDays(1);
                break;
        }

        dateButton.setDate(currentDate);
        notifyListeners();
    }
}
