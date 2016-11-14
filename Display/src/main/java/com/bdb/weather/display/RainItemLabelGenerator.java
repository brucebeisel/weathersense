/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.display;

import java.text.NumberFormat;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 * Generate the labels for the rain bar chart, this is special because no label is created if the rain total is zero.
 * 
 * @author Bruce
 *
 */
public class RainItemLabelGenerator extends StandardCategoryItemLabelGenerator {
    /**
     * Constructor.
     * 
     * @param labelFormat The format of the label
     * @param formatter The formatter used to generate the value string
     */
    public RainItemLabelGenerator(String labelFormat, NumberFormat formatter) {
        super(labelFormat, formatter);
    }
    
    @Override
    public String generateLabel(CategoryDataset dataset, int row, int column) {
        Number value = dataset.getValue(row, column);
        if (value.doubleValue() == 0.0)
            return null;
        else
            return super.generateLabel(dataset, row, column);
    }

}
