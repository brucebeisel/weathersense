package com.bdb.util;

import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;

public class CheckBoxPanel extends ToggleButtonPanel {
    private static final long serialVersionUID = 8719824705346029049L;

    public CheckBoxPanel() {
        super();
    }

    public CheckBoxPanel(String[] items) {
        super(items);
    }

    public CheckBoxPanel(LayoutManager layout) {
        super(layout);
    }

    public CheckBoxPanel(String[] items, LayoutManager layout) {
        super(items, layout);
    }

    public void addItem(String value, JCheckBox button) {
        addButton(value, button);
    }

    @Override
    public void addItem(String value, Icon icon) {
        JCheckBox btn = new JCheckBox(icon);

        addItem(value, btn);
    }

    @Override
    public void addItem(String text) {
        JToggleButton btn = new JCheckBox(text);

        addItem(text, btn);
    }
}