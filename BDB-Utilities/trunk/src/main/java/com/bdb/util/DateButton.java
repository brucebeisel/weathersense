package com.bdb.util;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Custom button for entering dates. The
 * <code>DateButton</code> class is just a standard button that defines an additional bound property: "date". The button displays
 * the date property as its label. When clicked, it does not generate an
 * <code>ActionEvent</code>, but displays a {@link DateChooser} dialog instead, that allows you to change the date. When the date
 * is changed, a
 * <code>PropertyChangeEvent</code> is generated, according the contract for bound properties.
 *
 */
public class DateButton extends JButton {
    private static final long serialVersionUID = -4266169577218227266L;
    /**
     * Format to use to display the date property.
     */
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    /**
     * DateChooser instance to use to change the date.
     */
    private static final DateChooser dateChooser = new DateChooser((JFrame)null, "Select Date");
    /**
     * Date property.
     */
    private LocalDate date;
    public static final String DATE_PROPERTY = "date";

    /**
     * Called when the button is clicked, in order to fire an
     * <code>ActionEvent</code>. Displays the dialog to change the date instead of generating the event and updates the date
     * property.
     *
     * @param e <code>ActionEvent</code> to fire
     *
     */
    @Override
    protected void fireActionPerformed(ActionEvent e) {
        LocalDate newDate = dateChooser.select(date);

        if (newDate == null)
            return;

        setDate(newDate);
    }

    /**
     * Constructs a new
     * <code>DateButton</code> object with a given date.
     *
     * @param date initial date
     *
     */
    public DateButton(LocalDate date) {
        super(dateFormat.format(date));
        this.date = date;
    }

    /**
     * Constructs a new
     * <code>DateButton</code> object with the system date as the initial date.
     *
     */
    public DateButton() {
        this(LocalDate.now());
    }

    /**
     * Gets the value of the date property.
     *
     * @return the current value of the date property
     *
     */
    public LocalDate getValue() {
        return date;
    }

    /**
     * Sets the values of the date property.
     *
     * @param date new value of the date property
     */
    public void setDate(LocalDate date) {
        LocalDate old = this.date;
        this.date = date;
        setText(dateFormat.format(date));

        firePropertyChange(DATE_PROPERTY, old, date);
    }
}