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

import java.awt.event.*;
import java.util.EventListener;
import javax.swing.*;
import javax.swing.table.*;

public class BBTable extends JTable implements MouseListener {
    private static final long serialVersionUID = 7466128915321133517L;
    private String actionCommand;

    @SuppressWarnings("LeakingThisInConstructor")
    public BBTable() {
        super();
        addMouseListener(this);
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    public void setActionCommand(String cmd) {
        actionCommand = cmd;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    protected void fireActionCommand(ActionEvent e) {
        //
        // Guaranteed to return a non-null array
        //
        EventListener[] listeners = listenerList.getListeners(ActionListener.class);

        //
        // Process the listeners last to first, notifying
        // those that are interested in this event
        //
        for (EventListener listener : listeners) {
            if (e == null)
                e = new ActionEvent(this, 0, actionCommand);
            ((ActionListener)listener).actionPerformed(e);
        }
    }

    //
    // MouseListener Interface
    //
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            ActionEvent evt = new ActionEvent(this, 0, actionCommand);
            fireActionCommand(evt);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
    //
    // End MouseListener Interface
    //

    /**
     * Test driver.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            
            DefaultTableModel model = new DefaultTableModel();
            DefaultTableColumnModel colModel = new DefaultTableColumnModel();
            
            JTable table = new JTable();
            table.setAutoCreateColumnsFromModel(false);
            
            TableColumn col = new TableColumn();
            col.setHeaderValue("Column 1");
            colModel.addColumn(col);
            
            col = new TableColumn();
            col.setHeaderValue("Column 2");
            col.setModelIndex(1);
            colModel.addColumn(col);
            
            col = new TableColumn();
            col.setHeaderValue("Column 3");
            col.setModelIndex(1);
            colModel.addColumn(col);
            
            model.setColumnCount(3);
            
            frame.getContentPane().add(new JScrollPane(table));
            
            frame.pack();
            frame.setVisible(true);
            
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            
            String[] data = {"Col 1 data", "Col 2 data", "Col 3 data"};
            
            model.addRow(data);
            model.addRow(data);
            model.addRow(data);
            
            table.setModel(model);
            table.setColumnModel(colModel);
            
            System.out.println("Value 1,2 = " + model.getValueAt(1, 2));
        });
    }
}
