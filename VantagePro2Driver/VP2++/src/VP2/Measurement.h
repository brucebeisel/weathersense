/* 
 * Copyright (C) 2022 Bruce Beisel
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
#ifndef MEASUREMENT_H
#define MEASUREMENT_H
#include <sstream>
#include <string>

namespace vp2 {

/**
  * Class to hold a measurement value and the state of its validity.
  */
template<typename T>
class Measurement {
public:
    /**
     * Construct an invalid Measurement.
     */
    Measurement() : value(static_cast<T>(0)), valid(false) {}

    /**
     * Construct a measurement with a valid value.
     *
     * @param value The value of the measure that is assumed to be in a valid range
     */
    Measurement(T value) : value(value), valid(true) {}

    /**
     * Invalidate a measurement, setting the internal value to 0.
     */
    void invalidate() {
        value = 0.0;
        valid = false;
    }

    /**
     * Set the value of the measurement with a valid value.
     *
     * @param value The value of the measure that is assumed to be in a valid range
     */
    void setValue(T value) {
        this->value = value;
        this->valid = true;
    }

    /**
     * Set the value and the validity of this measurement.
     *
     * @param value The value to be set, but is ignored if the valid flag is false
     * @param valid Whether the value is valid and should be used
     */
    void setValue(T value, bool valid) {
        if (valid) {
            this->valid = true;
            this->value = value;
        }
        else {
            this->value = static_cast<T>(0);
            this->valid = false;
        }
    }

    /**
     * Whether this measurement is valid.
     *
     * @return True if the measurement is valid
     */
    bool isValid() const {
        return valid;
    }

    /**
     * Get the value of the measurement, ignoring the validity of the value.
     *
     * @return The current value of the measurement
     */
    T getValue() const {
        return value;
    }

    /**
     * Conversion operator that allows an assignment from this class to a "typename T".
     *
     * @return The value of the measurement that is assumed to be in a valid range
     */
    operator T() const {
        return value;
    }

    /**
     * Assignment operator to set the measurement value to a valid value.
     *
     * @param value The value of the measurement that is assumed to be in a valid range
     */
    Measurement & operator=(const T & value) {
        this->value = value;
        this->valid = true;
        return *this;
    }

    /**
     * Format an XML string for the measurement.
     *
     * @param element The XML element name to be used
     * @return An XML element with the element name provided and the value OR a blank string if the measurement is not valid
     */
    std::string formatXML(const std::string & element) const {
        std::ostringstream ss;
        if (valid) 
            ss << "<" << element << ">" << value << "</" << element << ">";

        return ss.str();
    }
            
    /**
     * Format a JSON string for the measurement.
     *
     * @param element The JSON element name to be used
     * @return An JSON element with the element name provided and the value OR a blank string if the measurement is not valid
     */
    std::string formatJSON(const std::string & element) const {
        std::ostringstream ss;
        if (valid) 
            ss << '"' << element << "\" : " << value;

        return ss.str();
    }

    std::ostream & operator<<(std::ostream & os) const {
        if (valid)
            os << value;

        return os;
    }
            
private:
    T    value; // The value of the measure
    bool valid; // True if the value if valid, if false the value of the "value" member is undefined
};

}

#endif
