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
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

//
// CLASS: BBTextField
//
// DESCRIPTION:
//	Bruce Beisel's text field. This is a text field that adds more functionality
//	than the vanilla text field provided by Swing. This field allows you to
//	specify the maximum length of the value typed in. In addition you can tell
//	the field to move to the next focus field once the field has been filled in
//	(auto-tab). With auto-tab you can set up multiple fields to represent a single
//	value (e.g. Time with a separate field for hours, minutes and seconds).
//	This has the advantage of being able to verify the contents of each field on the
//	fly as opposed to parsing some complex string after all the data has been
//	entered.
//
//	This field also allows you to specify the set of characters that are valid for
//	this field. You could restrict it to numbers for an integer field, or numbers
//	and "." for floating point numbers.
//
//	Finally a single class may register as the error listener. The error listeners
//	method is called whenever one of the policies setup are violated (Such as an
//	invalid character being entered). The error listener can display the error
//	message to the user in the manner that make the most sense for the application.
//	
public class BBTextField extends JTextField implements DocumentListener,
                                                       FocusListener,
                                                       Verifiable {
    private static final long serialVersionUID = 4769646263973628514L;
    private boolean autoTabOn = false;
    private int maxLength = -1;
    private String validChars = null;
    private ErrorListener errorListener;

    @SuppressWarnings("LeakingThisInConstructor")
    public BBTextField() {
        super();
        setDocument(new BBDocument(this));

        //
        // Listen for document changes so we can perform auto-tab
        //
        getDocument().addDocumentListener(this);

        //
        // Listen for focus changes so we can select the field contents when
        // we acquire the focus
        //
        addFocusListener(this);

        //
        // Register the sister class BBVerifier with the Swing text field parent class
        // BBVerifier uses the Verifiable class to notify other class when an error
        // occurs.
        //
        setInputVerifier(new BBVerifier());
    }

    public BBTextField(int maxLength) {
        this(maxLength, maxLength);
    }

    public BBTextField(String text) {
        this();
        setText(text);
    }

    public BBTextField(String text, int maxLength) {
        this(maxLength);
        setText(text);
    }

    public BBTextField(int maxLength, int displayLength) {
        this();
        this.maxLength = maxLength;
        setColumns(displayLength);
    }

    public void enableAutoTab() {
        autoTabOn = true;
    }

    public void disableAutoTab() {
        autoTabOn = false;
    }

    public void setAutoTab(boolean autoTabOn) {
        this.autoTabOn = autoTabOn;
    }

    public boolean isAutoTabOn() {
        return autoTabOn;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setValidChars(String s) {
        validChars = s;
    }

    public String getValidChars() {
        return validChars;
    }

    public void insertError(String shortError, String longError) {
        if (errorListener != null)
            errorListener.reportError(shortError, longError);
    }

    //
    // DocumentListener interface
    //
    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {

        //
        // If auto tab is on and the inserted character is
        // at the end of the field then move the focus
        //
        if (autoTabOn && maxLength > 0 && e.getOffset() + e.getLength() == maxLength)
            FocusManager.getCurrentManager().focusNextComponent(this);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        //
        // Removes are not important to us
        //
    }

    //
    // FocusListener interface
    //
    @Override
    public void focusGained(FocusEvent e) {
        //
        // Select all text when we get the focus so the user can just type over the
        // old value. This is typcal PC and MAC behavior that was not implemented in
        // Swing.
        //
        selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    //
    // Verifiable interface
    //
    @Override
    public boolean verify(JComponent c) {
        return true;
    }

    @Override
    public void setErrorListener(ErrorListener l) {
        errorListener = l;
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }
}

class BBVerifier extends InputVerifier implements Serializable {
    private static final long serialVersionUID = 7922302381581263247L;

    @Override
    public boolean verify(JComponent c) {
        //
        // A disabled field is always valid
        //
        if (!c.isEnabled())
            return true;

        if (c instanceof Verifiable) {
            Verifiable v = (Verifiable)c;
            return v.verify(c);
        }
        else
            return true;

    }
}

//
// CLASS: BBDocument
//
// DESCRIPTION:
//	Private class used by the BBTextField to implement all the wonderful extra
//	capabilities.
//
class BBDocument extends PlainDocument {
    private static final long serialVersionUID = -4402868250028063175L;
    private final BBTextField textField;

    public BBDocument(BBTextField textField) {
        this.textField = textField;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        //
        // Break this insert up into multiple single character inserts
        //
        byte[] bytes = str.getBytes(Charset.defaultCharset());
        String validChars = textField.getValidChars();
        int maxLength = textField.getMaxLength();

        for (int i = 0; i < bytes.length; i++) {
            //
            // Make sure the character is valid for this field
            //
            if (validChars != null && validChars.indexOf(bytes[i]) == -1) {
                textField.insertError("Invalid character entered",
                                        "The character '" + (char)bytes[i] + "' is not valid for this field");
                continue;
            }

            //
            // See if this character will fill up the field
            //
            if (maxLength > 0 && getLength() + 1 > maxLength) {
                textField.insertError("The current field is full",
                                        "The current field is full. Only " + maxLength + " characters are allowed");
                continue;
            }

            //
            // Actually insert the character
            //
            super.insertString(offset + i, new String(bytes, i, 1, Charset.defaultCharset()), attr);
        }
    }
}
