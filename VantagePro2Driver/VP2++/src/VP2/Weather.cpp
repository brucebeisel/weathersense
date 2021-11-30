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
#ifdef _WIN32
#include <time.h>
#include <Windows.h>
#else
#include <unistd.h>
#endif
#include <sstream>
#include <iomanip>
#include "Weather.h"

using namespace std;

namespace vp2 {

static const char *DATE_FORMAT = "%Y-%m-%d";
static const char *DATE_TIME_FORMAT = "%Y-%m-%d %H:%M:%S";

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
doFormat(DateTime timeValue, const char *format) {
    char buffer[100];

    struct tm tm;
    Weather::localtime(timeValue, tm);
    strftime(buffer, sizeof(buffer), format, &tm);
    return string(buffer);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
Weather::formatDateTime(DateTime timeValue) {
    return doFormat(timeValue, DATE_TIME_FORMAT);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
Weather::formatDate(DateTime timeValue) {
    return doFormat(timeValue, DATE_FORMAT);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
Weather::dumpBuffer(const byte buffer[], int nbytes) {
    ostringstream ss;
    ss << endl << hex;
    for (int i = 0; i < nbytes; i++) {
        if (i % 16 == 0)
            ss << setw(3) << setfill('0') << dec << i << hex << ": ";

        ss <<  setw(2) << setfill('0') << (((int)buffer[i]) & 0xFF) << " ";

        if ((i + 1) % 16 == 0)
            ss << endl;
    }

    ss << dec << endl;
    return ss.str();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Weather::sleep(long millis) {
#ifdef __CYGWIN__
    sleep(millis);
#else
    usleep(millis * 1000);
#endif
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Weather::localtime(DateTime time, struct tm & tm) {
#ifdef _WIN32
    localtime_s(&tm, &time);
#else
    localtime_r(&time, &tm);
#endif
}
}
