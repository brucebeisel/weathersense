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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JToggleButton;
import javax.swing.DefaultDesktopManager;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;

public class BBDesktopManager extends DefaultDesktopManager implements ActionListener, PropertyChangeListener {
    static class TaskbarEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private final JToggleButton button;
        private final JInternalFrame frame;

        public TaskbarEntry(JToggleButton button, JInternalFrame frame) {
            this.button = button;
            this.frame = frame;
        }

        public JToggleButton getButton() {
            return button;
        }

        public JInternalFrame getFrame() {
            return frame;
        }
    }
    private static final long serialVersionUID = 3394856933952570463L;

    private final JPanel taskbar = new JPanel();
    private final ArrayList<TaskbarEntry> entries = new ArrayList<>();
    private static final Logger s_logger = Logger.getLogger(BBDesktopManager.class.getName());

    /**
     * Constructor.
     */
    public BBDesktopManager() {
        taskbar.setLayout(new BoxLayout(taskbar, BoxLayout.X_AXIS));
        taskbar.setBorder(new LineBorder(Color.black));
        taskbar.setMinimumSize(new Dimension(0, 50));
    }

    @Override
    public void iconifyFrame(JInternalFrame f) {
        s_logger.log(Level.INFO, "Iconifying frame {0}", f.getTitle());

        super.iconifyFrame(f);
        f.getDesktopIcon().setVisible(false);
        f.setVisible(false);
        TaskbarEntry e = findTaskbarEntry(f);

        if (e != null)
            e.getButton().setSelected(false);
    }

    /**
     * Removes the frame, and if necessary the desktopIcon, from its parent.
     */
    @Override
    public void closeFrame(JInternalFrame f) {
        s_logger.log(Level.INFO, "Closing frame {0}", f.getTitle());

        super.closeFrame(f);
        TaskbarEntry e = findTaskbarEntry(f);
        taskbar.remove(e.getButton());
        taskbar.getParent().invalidate();
        taskbar.getParent().validate();
        taskbar.repaint();
    }

    // implements javax.swing.DesktopManager
    @Override
    public void deactivateFrame(JInternalFrame f) {
        s_logger.log(Level.INFO, "Deactivating frame {0}", f.getTitle());
        TaskbarEntry e = findTaskbarEntry(f);
        e.getButton().setSelected(false);

        super.deactivateFrame(f);
    }

    @Override
    public void openFrame(JInternalFrame frame) {
        s_logger.log(Level.INFO, "Opening frame {0}", frame.getTitle());

        super.openFrame(frame);

        TaskbarEntry entry = findTaskbarEntry(frame);

        if (entry == null) {
            JToggleButton button = new JToggleButton(frame.getTitle(), frame.getFrameIcon(), true);
            Dimension d = button.getPreferredSize();
            button.setMaximumSize(new Dimension(150, d.height));
            button.addActionListener(this);
            taskbar.add(button);
            entry = new TaskbarEntry(button, frame);
            entries.add(entry);
        }
        else
            entry.getButton().setSelected(true);
    }

    /**
     * This will activate <b>f</b> moving it to the front. It will set the current active frame (if any) IS_SELECTED_PROPERTY to
     * false. There can be only one active frame across all Layers.
     */
    @Override
    public void activateFrame(JInternalFrame f) {
        s_logger.log(Level.INFO, "Activating frame {0}", f.getTitle());

        super.activateFrame(f);

        TaskbarEntry e = findTaskbarEntry(f);

        if (e == null) {
            JToggleButton button = new JToggleButton(f.getTitle(), f.getFrameIcon(), true);
            Dimension d = button.getPreferredSize();
            button.setMaximumSize(new Dimension(150, d.height));
            button.addActionListener(this);
            taskbar.add(button);
            e = new TaskbarEntry(button, f);
            entries.add(e);
            f.addPropertyChangeListener(this);
        }
        else
            e.getButton().setSelected(true);
    }

    public JPanel getTaskbar() {
        return taskbar;
    }


    private TaskbarEntry findTaskbarEntry(JInternalFrame f) {
        Iterator<TaskbarEntry> it = entries.listIterator();

        while (it.hasNext()) {
            TaskbarEntry e = it.next();
            if (e.getFrame() == f)
                return e;
        }

        return null;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            JToggleButton b = (JToggleButton)evt.getSource();

            for (TaskbarEntry e : entries)
                if (e.getButton() == b) {
                    b.setSelected(true);
                    if (!e.getFrame().isVisible()) {
                        e.getFrame().setVisible(true);
                        e.getFrame().setIcon(false);
                    }
                    e.getFrame().setSelected(true);
                }
        }
        catch (PropertyVetoException e) {
            s_logger.log(Level.INFO, "Caught unexpected exception", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(JInternalFrame.TITLE_PROPERTY)) {
            JInternalFrame frame = (JInternalFrame)evt.getSource();
            String newValue = (String)evt.getNewValue();

            TaskbarEntry e = findTaskbarEntry(frame);

            if (e.getButton() != null)
                e.getButton().setText(newValue);
        }
    }
}
