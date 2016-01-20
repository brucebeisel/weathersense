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
package com.bdb.weather.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord rec) {
        String className = rec.getSourceClassName();

        String s = String.format("%-7s", rec.getLevel().toString()) + " -- "
                + sdf.format(rec.getMillis()) + " -- "
                + String.format("%-20s", className.substring(className.lastIndexOf('.') + 1)) + " -- "
                + String.format("%-20s", rec.getSourceMethodName()) + " -- "
                + formatMessage(rec);

        Throwable t = rec.getThrown();

        if (t != null) {
            s += " --\r\nStack Trace:\r\n";

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);

            s += sw;
        }
        else {
            s += "\r\n";
        }

        return s;
    }

    @Override
    public String getHead(Handler h) {
        return "";
    }

    @Override
    public String getTail(Handler h) {
        return "";
    }
}
