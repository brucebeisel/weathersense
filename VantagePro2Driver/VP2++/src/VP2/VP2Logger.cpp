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
#include <iomanip>
#include <fstream>
#include <stdio.h>
#include <time.h>
#include "Weather.h"
#include "VP2Logger.h"

using namespace std;

namespace vp2 {
VP2Logger::Level VP2Logger::currentLevel = VP2Logger::VP2_INFO;
ostream * VP2Logger::loggerStream = &cerr;
ostream VP2Logger::nullStream(0);
map<string, VP2Logger *> VP2Logger::loggers;
int VP2Logger::maxFileSize;
int VP2Logger::maxFiles;
string VP2Logger::logFilePattern;

const static char *LEVEL_STRINGS[] = {"ERROR  ", "WARNING", "INFO   ", "DEBUG1 ", "DEBUG2 ", "DEBUG3 "};

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VP2Logger::VP2Logger(const string & name) : loggerName(name) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VP2Logger::~VP2Logger() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VP2Logger &
VP2Logger::getLogger(const std::string & name) {
    LogIterator logger = loggers.find(name);

    if (logger != loggers.end())
        return *(logger->second);

    VP2Logger * newLogger = new VP2Logger(name);
    loggers.insert(std::pair<string,VP2Logger *>(name, newLogger));

    return *newLogger;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Logger::setLogLevel(Level level) {
    currentLevel = level;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Logger::setLogStream(std::ostream &stream) {
    loggerStream = &stream;
    maxFileSize = MAX_FILE_SIZE_INFINITE;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Logger::setLogFilePattern(std::string& pattern, int maxFiles, int maxFileSizeMb) {
    VP2Logger::logFilePattern = pattern;
    VP2Logger::maxFiles = maxFiles;
    VP2Logger::maxFileSize = maxFileSizeMb;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VP2Logger::isLogEnabled(Level level) const {
    return level <= currentLevel;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Logger::openLogFile() {
    char filename[1024];

    snprintf(filename, sizeof(filename), logFilePattern.c_str(), 0);
    loggerStream = new ofstream(filename, ios::app | ios::ate | ios::out);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Logger::checkFileSize() {

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ostream &
VP2Logger::log(Level level) const {
    char buffer[100];
    if (isLogEnabled(level)) {
        time_t now = time(0);
        struct tm tm;
        Weather::localtime(now, tm);
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &tm);
        *loggerStream << setw(20) << loggerName << ": " << buffer << " --- " << LEVEL_STRINGS[level] << " --- ";
        return *loggerStream;
    }
    else
        return nullStream;
}
} /* End namespace */
