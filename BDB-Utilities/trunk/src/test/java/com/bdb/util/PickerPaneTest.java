package com.bdb.util;

/**
 *
 * @author Bruce
 */
public class PickerPaneTest {
    //
    // Unit test
    //
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame();

        String[] choices = {"One", "Two", "Three", "Four"};
        String[] selections = {"Five", "Six", "Seven", "Very Long item that should cause scrolling"};

        PickerPane pp = new PickerPane("Choices", "Selections", choices, selections);

        frame.getContentPane().add(pp);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();

        frame.setVisible(true);
    }
    
}
