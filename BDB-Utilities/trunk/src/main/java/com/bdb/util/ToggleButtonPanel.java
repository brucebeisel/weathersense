package com.bdb.util;

import java.util.ArrayList;
import java.util.Iterator;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

public class ToggleButtonPanel extends JPanel {
    private static final long serialVersionUID = -5754315343255593368L;
    private final ArrayList<String> values = new ArrayList<>();
    private final ArrayList<JToggleButton> buttons = new ArrayList<>();

    public ToggleButtonPanel() {
        this(new GridLayout(1, 0));
    }

    public ToggleButtonPanel(String[] items) {
        this(items, null);
    }

    public ToggleButtonPanel(LayoutManager layout) {
        this(null, layout);
    }

    public ToggleButtonPanel(String[] items, LayoutManager layout) {
        setBorder(new EtchedBorder());

        if (layout != null)
            setLayout(layout);

        if (items != null)
            for (String item : items)
                addItem(item);
    }

    protected void addButton(String value, JToggleButton button) {
        values.add(value);
        buttons.add(button);
        add(button);
    }

    public void addItem(String value, JToggleButton button) {
        addButton(value, button);
    }

    public void addItem(String value, Icon icon) {
        JToggleButton btn;

        btn = new JToggleButton(icon);

        addItem(value, btn);
    }

    public void addItem(String text) {
        JToggleButton btn = new JToggleButton(text);
        addItem(text, btn);
    }

    public int getSelectedIndex() {
        Iterator<JToggleButton> it = buttons.listIterator();

        for (int i = 0; it.hasNext(); i++) {
            JToggleButton b = it.next();
            if (b.isSelected())
                return i;
        }

        return -1;
    }

    public void setSelectedIndex(int idx) {
        JToggleButton b = buttons.get(idx);

        if (idx < buttons.size())
            b.setSelected(true);
    }

    public int[] getSelectedIndicies() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i++)
            if ((buttons.get(i)).isSelected())
                list.add(i);

        int[] vals = new int[list.size()];

        for (int i = 0; i < list.size(); i++)
            vals[i] = (list.get(i));

        return vals;
    }

    public void setSelectedIndicies(int[] vals) {
        for (int i = 0; i < vals.length; i++)
            buttons.get(i).setSelected(true);
    }

    public String getValue() {
        Iterator<JToggleButton> it = buttons.listIterator();

        for (int i = 0; it.hasNext(); i++) {
            JToggleButton b = it.next();
            if (b.isSelected())
                return values.get(i);
        }

        return "";
    }

    public void setValue(String val) {
        Iterator<String> it = values.listIterator();

        for (int i = 0; it.hasNext(); i++) {
            String value = it.next();

            if (value.equals(val))
                buttons.get(i).setSelected(true);
        }
    }

    public String[] getValues() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i++)
            if (((JToggleButton)buttons.get(i)).isSelected())
                list.add(values.get(i));

        String[] vals = new String[list.size()];

        for (int i = 0; i < list.size(); i++)
            vals[i] = list.get(i);

        return vals;
    }

    public void setValues(String[] vals) {
        for (String val : vals)
            if (values.contains(val))
                setValue(val);
    }

    @Override
    public void setEnabled(boolean enabled) {
        buttons.stream().forEach((button) -> button.setEnabled(enabled));
    }
}
