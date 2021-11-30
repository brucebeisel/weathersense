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
#ifndef ALARM_H
#define ALARM_H
#include <string>
#include <vector>
#include "Weather.h"

namespace vp2 {

struct AlarmProperties {
    std::string alarmName;
    int         eepromThresholdByte;
    int         eepromThresholdSize;
    int         eepromThresholdOffset;
    int         eepromThresholdScale;
    int         eepromNotSetThreshold;
    int         loopByte;
    int         loopBit;
};

class Alarm {
public:
    Alarm(const AlarmProperties & properties);


    std::string getAlarmName() const;
    AlarmProperties getAlarmProperties() const;

    void setThreshold(int eepromThreshold);

    void setTriggered(bool triggered);
    bool isTriggered() const;
private:
    AlarmProperties properties;
    int             eepromThreshold;
    float           actualThreshold;   // This can be either a float or an integer
    bool            alarmThresholdSet; // Whether the alarm threshold is set to a value other that the "not set" value
    bool            alarmTriggered;    // Whether the alarm is currently triggered
};

class AlarmManager {
public:
    static const int NUM_ALARMS = 86;

    static AlarmManager & getInstance();
    void initialize();

    void loadThresholds(const byte buffer[]);
    void setAlarmStates(const byte buffer[]);

    void getTriggeredList(std::vector<Alarm> & triggeredList) const;

private:
    static AlarmManager instance;
    std::vector<Alarm>  alarms;
};

}

#endif