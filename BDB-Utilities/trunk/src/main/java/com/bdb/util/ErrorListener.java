package com.bdb.util;
import java.io.Serializable;

public interface ErrorListener extends Serializable {
    void reportError(String shortErrorText, String longErrorText);
}
