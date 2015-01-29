package com.bdb.util;

import javax.swing.JComponent;

//
// INTERFACE: Verifiable
//
// DESCRIPTION:
//	Interface implemented by any Swing component who wants to be able to be
//	verified.
//
public interface Verifiable
{
    public abstract boolean verify(JComponent c);
    public abstract void setErrorListener(ErrorListener e);
}
