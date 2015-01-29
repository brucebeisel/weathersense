/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdb.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Bruce
 */
public class RadioButtonPanelTest {
    
    public static void main(String[] args) {
        String[] list = {"One", "Two", "Three", "Four"};

        final RadioButtonPanel rb = new RadioButtonPanel(list);
        final RadioButtonPanel rb2 = new RadioButtonPanel(list);
        rb.setSelectedIndex(1);
        rb2.setValue("Three");

        ActionListener l = (ActionEvent e) -> {
            String action = e.getActionCommand();
            
            if (action.equals("Query")) {
                System.out.println("Index is " + rb.getSelectedIndex() + " " + rb.getValue());
                System.out.println("Index is " + rb2.getSelectedIndex() + " " + rb2.getValue());
            }
            else
                System.exit(0);
        };

        JFrame frame = new JFrame("RadioButtonPanel Test");

        frame.getContentPane().setLayout(new BorderLayout());

        JPanel p1 = new JPanel();

        p1.add(rb);
        p1.add(rb2);

        frame.getContentPane().add(p1, BorderLayout.NORTH);

        JPanel p2 = new JPanel();
        JButton b = new JButton("Query");
        b.addActionListener(l);
        p2.add(b);

        b = new JButton("Exit");
        b.addActionListener(l);
        p2.add(b);

        frame.getContentPane().add(p2, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
