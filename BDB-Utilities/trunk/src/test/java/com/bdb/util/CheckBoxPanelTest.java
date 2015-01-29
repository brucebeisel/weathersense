package com.bdb.util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Bruce
 */
public class CheckBoxPanelTest {
    public static void main(String[] args) {
        String[] list = {"One", "Two", "Three", "Four"};

        final CheckBoxPanel rb = new CheckBoxPanel(list);
        rb.setLayout(new GridLayout(0, 1));
        rb.addItem("five", new JCheckBox("Hello"));

        final ToggleButtonPanel rb2 = new ToggleButtonPanel(list);
        rb.setSelectedIndex(1);
        rb2.setValue("Three");

        ActionListener l = (ActionEvent e) -> {
            String action = e.getActionCommand();

            if (action.equals("Query")) {
                System.out.println("Index is " + rb.getSelectedIndex());
                System.out.print("Values are: ");
                String[] values = rb.getValues();

                for (String value : values)
                    System.out.print(value + ", ");

                System.out.println("");

                System.out.println("Index is " + rb2.getSelectedIndex() + " " + Arrays.toString(rb2.getValues()));
            }
            else
                System.exit(0);
        };

        JFrame frame = new JFrame("CheckBox Test");

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
