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
#ifndef PROTOCOL_EXCEPTION_H
#define PROTOCOL_EXCEPTION_H

#include <string>
#include <stdexcept>

namespace vp2 {

class ProtocolException : public std::exception {
public:
    /**
     * Constructor.
     *
     * @param what The message associated with exception
     */
    ProtocolException(const std::string & what);

    /**
     * Destructor.
     */
    virtual ~ProtocolException() throw();

    /**
     * Get the message associated with the exception.
     *
     * @return The message
     */
    virtual const char *what() const throw();

private:
    std::string whatMessage;

};
}
#endif /* PROTOCOL_EXCEPTION_H */
