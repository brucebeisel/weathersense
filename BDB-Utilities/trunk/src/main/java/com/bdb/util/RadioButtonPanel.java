package com.bdb.util;

import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public final class RadioButtonPanel extends ToggleButtonPanel {
    private static final long serialVersionUID = 5609017334490164684L;

    private final ButtonGroup buttonGroup = new ButtonGroup();

    public RadioButtonPanel() {
        this(null, null);
    }

    public RadioButtonPanel(String[] items) {
        this(items, null);
    }

    public RadioButtonPanel(LayoutManager layout) {
        this(null, layout);
    }

    public RadioButtonPanel(String[] items, LayoutManager layout) {
        super(layout);

        if (items != null)
            for (String item : items)
                addItem(item);
    }

    public void addItem(String value, JRadioButton button) {
        addButton(value, button);
        buttonGroup.add(button);
    }

    @Override
    public void addItem(String value, Icon icon) {
        JRadioButton btn = new JRadioButton(icon);
        addItem(value, btn);
    }

    @Override
    public void addItem(String text) {
        JRadioButton btn = new JRadioButton(text);
        addItem(text, btn);
    }
}
