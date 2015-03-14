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
package com.bdb.weather.display.summary;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

@SuppressWarnings("serial")
public class HiLoItemLabelGenerator extends HighLowItemLabelGenerator {
    private final DateFormat   dateFormat;
    private final NumberFormat numberFormat;
    
    public HiLoItemLabelGenerator(DateFormat dateFormat, NumberFormat numberFormat) {
        super(dateFormat, numberFormat);
        this.dateFormat = dateFormat;
        this.numberFormat = numberFormat;
    }
    
    @Override
    public String generateToolTip(XYDataset dataset, int series, int item) {

        String result = null;

        if (dataset instanceof OHLCDataset) {
            OHLCDataset d = (OHLCDataset) dataset;
            Number high = d.getHigh(series, item);
            Number low = d.getLow(series, item);
            Number open = d.getOpen(series, item);
            Number x = d.getX(series, item);

            result = d.getSeriesKey(series).toString();

            if (x != null) {
                Date date = new Date(x.longValue());
                result = result + "--> Date=" + dateFormat.format(date);
                
                if (high != null)
                    result = result + " High=" + numberFormat.format(high.doubleValue());

                if (low != null)
                    result = result + " Low=" + numberFormat.format(low.doubleValue());
 
                if (open != null)
                    result = result + " Avg=" + numberFormat.format(open.doubleValue());

            }

        }

        return result;

    }
}
