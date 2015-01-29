package com.bdb.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Custom dialog box to enter dates. The
 * <code>DateChooser</code> class presents a calendar and allows the user to visually select a day, month and year so that it is
 * impossible to enter an invalid date.
 *
 */
public class DateChooser extends JDialog implements ItemListener, MouseListener, FocusListener, KeyListener, ActionListener {
    private static final long serialVersionUID = 6071061409549816070L;
    /**
     * Text color of the days of the weeks, used as column headers in the calendar.
     */
    private static final Color WEEK_DAYS_FOREGROUND = Color.black;
    /**
     * Text color of the day's numbers in the calendar.
     */
    private static final Color DAYS_FOREGROUND = Color.blue;
    /**
     * Background color of the selected day in the calendar.
     */
    private static final Color SELECTED_DAY_FOREGROUND = Color.white;
    /**
     * Text color of the selected day in the calendar.
     */
    private static final Color SELECTED_DAY_BACKGROUND = Color.blue;
    /**
     * Empty border, used when the calendar does not have the focus.
     */
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    /**
     * Border used to highlight the selected day when the calendar has the focus.
     */
    private static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(Color.yellow, 1);
    /**
     * First year that can be selected.
     */
    private static final int FIRST_YEAR = 1900;
    /**
     * Last year that can be selected.
     */
    private static final int LAST_YEAR = 2100;
    /**
     * Auxiliary variable to compute dates.
     */
    private LocalDate date;
    /**
     * Calendar, as a matrix of labels. The first row represents the first week of the month, the second row, the second week, and
     * so on. Each column represents a day of the week, the first is Sunday, and the last is Saturday. The label's text is the
     * number of the corresponding day.
     */
    private JLabel[][] days;
    /**
     * Day selection control. It is just a panel that can receive the focus. The actual user interaction is driven by the
     * <code>DateChooser</code> class.
     */
    private FocusablePanel daysGrid;
    /**
     * Month selection control.
     */
    private JComboBox<Month> monthComboBox;
    /**
     * Year selection control.
     */
    private JComboBox<Integer> yearComboBox;
    /**
     * "Ok" button.
     */
    private JButton okButton;
    /**
     * "Cancel" button.
     */
    private JButton cancelButton;
    /**
     * Day of the week (0=Sunday) corresponding to the first day of the selected month. Used to calculate the position, in the
     * calendar ({@link #days}), corresponding to a given day.
     */
    private int offset;
    /**
     * Last day of the selected month.
     */
    private int lastDay;
    /**
     * Selected day.
     */
    private JLabel dayLabel;
    /**
     * <
     * code>true</code> if the "Ok" button was clicked to close the dialog box,
     * <code>false</code> otherwise.
     */
    private boolean okClicked;

    private static final DayOfWeek daysOfWeek[] = {
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    };

    /**
     * Custom panel that can receive the focus. Used to implement the calendar control.
     *
     */
    @SuppressWarnings("serial")
    private static class FocusablePanel extends JPanel {
        /**
         * Constructs a new
         * <code>FocusablePanel</code> with the given layout manager.
         *
         * @param layout layout manager
	 *
         */
        public FocusablePanel(LayoutManager layout) {
            super(layout);
        }

        /**
         * Always returns
         * <code>true</code>, since
         * <code>FocusablePanel</code> can receive the focus.
         *
         * @return <code>true</code>
	 *
         */
        @Override
        public boolean isFocusable() {
            return true;
        }
    }

    /**
     * Initializes this
     * <code>DateChooser</code> object. Creates the controls, registers listeners and initializes the dialog box.
     *
     */
    private void construct() {
        date = LocalDate.now();

        monthComboBox = new JComboBox<>(Month.values());
        monthComboBox.addItemListener(this);

        yearComboBox = new JComboBox<>();

        for (int i = FIRST_YEAR; i <= LAST_YEAR; i++)
            yearComboBox.addItem(i);

        yearComboBox.addItemListener(this);

        days = new JLabel[7][7];

        for (int i = 0; i < 7; i++) {
            days[0][i] = new JLabel(daysOfWeek[i].getDisplayName(TextStyle.SHORT, Locale.getDefault()), JLabel.RIGHT);
            days[0][i].setForeground(WEEK_DAYS_FOREGROUND);
        }

        for (int i = 1; i < 7; i++)
            for (int j = 0; j < 7; j++) {
                days[i][j] = new JLabel(" ", JLabel.RIGHT);
                days[i][j].setForeground(DAYS_FOREGROUND);
                days[i][j].setBackground(SELECTED_DAY_BACKGROUND);
                days[i][j].setBorder(EMPTY_BORDER);
                days[i][j].addMouseListener(this);
            }

        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        JPanel monthYear = new JPanel();
        monthYear.add(monthComboBox);
        monthYear.add(yearComboBox);

        daysGrid = new FocusablePanel(new GridLayout(7, 7, 5, 0));
        daysGrid.addFocusListener(this);
        daysGrid.addKeyListener(this);

        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++)
                daysGrid.add(days[i][j]);

        daysGrid.setBackground(Color.white);
        daysGrid.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel daysPanel = new JPanel();
        daysPanel.add(daysGrid);

        JPanel buttons = new JPanel();
        buttons.add(okButton);
        buttons.add(cancelButton);

        Container dialog = getContentPane();
        dialog.add("North", monthYear);
        dialog.add("Center", daysPanel);
        dialog.add("South", buttons);

        pack();
        setResizable(false);
    }

    /**
     * Gets the selected day, as an
     * <code>int</code>. Parses the text of the selected label in the calendar to get the day.
     *
     * @return the selected day or -1 if there is no day selected
     */
    private int getSelectedDay() {
        if (dayLabel == null)
            return -1;

        try {
            return Integer.parseInt(dayLabel.getText());
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Sets the selected day. The day is specified as the label control, in the calendar, corresponding to the day to select.
     *
     * @param newDay day to select
     */
    private void setSelected(JLabel newDay) {
        if (dayLabel != null) {
            dayLabel.setForeground(DAYS_FOREGROUND);
            dayLabel.setOpaque(false);
            dayLabel.setBorder(EMPTY_BORDER);
        }

        dayLabel = newDay;
        dayLabel.setForeground(SELECTED_DAY_FOREGROUND);
        dayLabel.setOpaque(true);

        if (daysGrid.hasFocus())
            dayLabel.setBorder(FOCUSED_BORDER);
    }

    /**
     * Sets the selected day. The day is specified as the number of the day, in the month, to selected. The function compute the
     * corresponding control to select.
     *
     * @param newDay day to select
     */
    private void setSelected(int newDay) {
        setSelected(days[(newDay + offset - 1) / 7 + 1][(newDay + offset - 1) % 7]);
    }

    /**
     * Updates the calendar. This function updates the calendar panel to reflect the month and year selected. It keeps the same
     * day of the month that was selected, except if it is beyond the last day of the month. In this case, the last day of the
     * month is selected.
     */
    private void update() {
        int iday = getSelectedDay();

        for (int i = 0; i < 7; i++) {
            days[1][i].setText(" ");
            days[5][i].setText(" ");
            days[6][i].setText(" ");
        }

        date = LocalDate.of(yearComboBox.getSelectedIndex() + FIRST_YEAR, (Month)monthComboBox.getSelectedItem(), 1);

        DayOfWeek offsetDay = date.getDayOfWeek();
        if (offsetDay == DayOfWeek.SUNDAY)
            offset = 0;
        else
            offset = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue() + 1;

        lastDay = date.getMonth().maxLength();

        for (int i = 0; i < lastDay; i++)
            days[(i + offset) / 7 + 1][(i + offset) % 7].setText(String.valueOf(i + 1));

        if (iday != -1) {
            if (iday > lastDay)
                iday = lastDay;

            setSelected(iday);
        }
    }

    /**
     * Called when the "Ok" button is pressed. Just sets a flag and hides the dialog box.
     *
     * @param e The Action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton)
            okClicked = true;

        setVisible(false);
    }

    /**
     * Called when the calendar gains the focus. Just re-sets the selected day so that it is redrawn with the border that indicate
     * focus.
     *
     * @param e The Focus event
     */
    @Override
    public void focusGained(FocusEvent e) {
        setSelected(dayLabel);
    }

    /**
     * Called when the calendar loses the focus. Just re-sets the selected day so that it is redrawn without the border that
     * indicate focus.
     *
     * @param e The Focus event
     */
    @Override
    public void focusLost(FocusEvent e) {
        setSelected(dayLabel);
    }

    /**
     * Called when a new month or year is selected. Updates the calendar to reflect the selection.
     *
     * @param e The item event
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        update();
    }

    /**
     * Called when a key is pressed and the calendar has the focus. Handles the arrow keys so that the user can select a day using
     * the keyboard.
     *
     * @param e The Key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int iday = getSelectedDay();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (iday > 1)
                    setSelected(iday - 1);
                break;

            case KeyEvent.VK_RIGHT:
                if (iday < lastDay)
                    setSelected(iday + 1);
                break;

            case KeyEvent.VK_UP:
                if (iday > 7)
                    setSelected(iday - 7);
                break;

            case KeyEvent.VK_DOWN:
                if (iday <= lastDay - 7)
                    setSelected(iday + 7);
                break;

            default:
                // Will never get here due to switching on an enum
                break;
        }
    }

    /**
     * Called when the mouse is clicked on a day in the calendar. Selects the clicked day.
     *
     * @param e The mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel day = (JLabel)e.getSource();

        if (!day.getText().equals(" "))
            setSelected(day);

        daysGrid.requestFocus();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
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

    /**
     * Constructs a new
     * <code>DateChooser</code> with the given title.
     *
     * @param owner owner dialog
     *
     * @param title dialog title
     *
     */
    public DateChooser(Dialog owner, String title) {
        super(owner, title, true);
        construct();
    }

    /**
     * Constructs a new
     * <code>DateChooser</code>.
     *
     * @param owner owner dialog
     *
     */
    public DateChooser(Dialog owner) {
        super(owner, true);
        construct();
    }

    /**
     * Constructs a new
     * <code>DateChooser</code> with the given title.
     *
     * @param owner owner frame
     *
     * @param title dialog title
     *
     */
    public DateChooser(Frame owner, String title) {
        super(owner, title, true);
        construct();
    }

    /**
     * Constructs a new
     * <code>DateChooser</code>.
     *
     * @param owner owner frame
     *
     */
    public DateChooser(Frame owner) {
        super(owner, true);
        construct();
    }

    /**
     * Selects a date. Displays the dialog box, with a given date as the selected date, and allows the user select a new date.
     *
     * @param date initial date
     *
     * @return the new date selected or <code>null</code> if the user press "Cancel" or closes the dialog box
     *
     */
    public LocalDate select(LocalDate date) {
        this.date = date;

        int day = this.date.getDayOfMonth();
        Month month = this.date.getMonth();
        int year = this.date.getYear();

        yearComboBox.setSelectedIndex(year - FIRST_YEAR);
        monthComboBox.setSelectedIndex(month.getValue() - Month.JANUARY.getValue());
        setSelected(day);
        okClicked = false;
        setVisible(true);

        if (!okClicked)
            return null;

        this.date = LocalDate.of(yearComboBox.getSelectedIndex() + FIRST_YEAR, (Month)monthComboBox.getSelectedItem(), getSelectedDay());

        return this.date;
    }

    /**
     * Selects new date. Just calls {@link #select(Date)} with the system date as the parameter.
     *
     * @return the same as the function {@link #select(Date)}
     *
     */
    public LocalDate select() {
        return select(LocalDate.now());
    }
}
