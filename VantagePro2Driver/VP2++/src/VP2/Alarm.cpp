#include "BitConverter.h"
#include "Alarm.h"

using namespace std;

namespace vp2 {

AlarmManager AlarmManager::instance;

static const AlarmProperties alarmProperties[] = {
    {
        "Barometer Rising",
         0,    1,
         0, 1000,
         0,
         0,    1
    },
    {
        "Barometer Falling",
         1,    1,
         0, 1000,
         0,
         0,    0
    },
    {
        "Time Alarm",
         2,     2,
         0,     1,
         0xffff,
         0,     6
    },
    {
        "Time Alarm 2s-Compliment",
         4,     2,
         0,     1,
         0xffff,
         0,     0
    },
    {
        "Low Indoor Temperature",
          6,    1,
         90,    1,
        255,
          0,    2
    },
    {
        "High Indoor Temperature",
          7,    1,
         90,    1,
        255,
          0,    3
    },
    {
        "Low Outdoor Temperature",
          8,    1,
         90,    1,
        255,
          2,    0
    },
    {
        "High Outdoor Temperature",
          9,   1,
         90,   1,
        255,
          2,   1
    },
    {
        "Extra Temperature/Humidity 1 - Low Temperature",
         10,    1,
         90,    1,
        255,
          5,    0
    },
    {
        "Extra Temperature/Humidity 2 - Low Temperature",
         11,    1,
         90,    1,
        255,
          6,    0
    },
    {
        "Extra Temperature/Humidity 3 - Low Temperature",
         12,    1,
         90,    1,
        255,
          7,    0
    },
    {
        "Extra Temperature/Humidity 4 - Low Temperature",
         13,    1,
         90,    1,
        255,
          8,    0
    },
    {
        "Extra Temperature/Humidity 5 - Low Temperature",
         14,    1,
         90,    1,
        255,
          9,    0
    },
    {
        "Extra Temperature/Humidity 6 - Low Temperature",
         15,    1,
         90,    1,
        255,
         10,    0
    },
    {
        "Extra Temperature/Humidity 7 - Low Temperature",
         16,    1,
         90,    1,
        255,
         11,    0
    },
    {
        "Soil/Leaf 1 - Low Soil Temperature",
         17,    1,
         90,    1,
        255,
         12,    6
    },
    {
        "Soil/Leaf 2 - Low Soil Temperature",
         18,    1,
         90,    1,
        255,
         13,    6
    },
    {
        "Soil/Leaf 3 - Low Soil Temperature",
         19,    1,
         90,    1,
        255,
         14,    6
    },
    {
        "Soil/Leaf 4 - Low Soil Temperature",
         20,    1,
         90,    1,
        255,
         15,    6
    },
    {
        "Soil/Leaf 1 - Low Leaf Temperature",
         21,    1,
         90,    1,
        255,
         12,    4
    },
    {
        "Soil/Leaf 2 - Low Leaf Temperature",
         22,    1,
         90,    1,
        255,
         13,    4
    },
    {
        "Soil/Leaf 3 - Low Leaf Temperature",
         23,    1,
         90,    1,
        255,
         14,    4
    },
    {
        "Soil/Leaf 4 - Low Leaf Temperature",
         24,   1,
         90,   1,
        255,
         15,   4
    },
    {
        "Extra Temperature/Humidity 1 - High Temperature",
         25,    1,
         90,    1,
        255,
          5,    1
    },
    {
        "Extra Temperature/Humidity 2 - High Temperature",
         26,    1,
         90,    1,
        255,
          6,    1
    },
    {
        "Extra Temperature/Humidity 3 - High Temperature",
         27,    1,
         90,    1,
        255,
          7,    1
    },
    {
        "Extra Temperature/Humidity 4 - High Temperature",
         28,    1,
         90,    1,
        255,
          8,    1
    },
    {
        "Extra Temperature/Humidity 5 - High Temperature",
         29,    1,
         90,    1,
        255,
          9,    1
    },
    {
        "Extra Temperature/Humidity 6 - High Temperature",
         30,    1,
         90,    1,
        255,
         10,    1
    },
    {
        "Extra Temperature/Humidity 7 - High Temperature",
         31,    1,
         90,    1,
        255,
         11,    1
    },
    {
        "Soil/Leaf 1 - High Soil Temperature",
         32,    1,
         90,    1,
        255,
         12,    7
    },
    {
        "Soil/Leaf 2 - High Soil Temperature",
         33,    1,
         90,    1,
        255,
         13,    7
    },
    {
        "Soil/Leaf 3 - High Soil Temperature",
         34,    1,
         90,    1,
        255,
         14,    7
    },
    {
        "Soil/Leaf 4 - High Soil Temperature",
         35,    1,
         90,    1,
        255,
         15,    7
    },
    {
        "Soil/Leaf 1 - High Leaf Temperature",
         36,    1,
         90,    1,
        255,
         12,    5
    },
    {
        "Soil/Leaf 2 - High Leaf Temperature",
         37,    1,
         90,    1,
        255,
         13,    5
    },
    {
        "Soil/Leaf 3 - High Leaf Temperature",
         38,    1,
         90,    1,
        255,
         14,    5
    },
    {
        "Soil/Leaf 4 - High Leaf Temperature",
         39,   1,
         90,   1,
        255,
         15,   5
    },
    {
        "Low Indoor Humidity",
         40,   1,
          0,   1,
        255,
          0,   4
    },
    {
        "High Indoor Humidity",
         41,   1,
          0,   1,
        255,
          0,   5
    },
    {
        "Low Outdoor Humidity",
         42,   1,
          0,   1,
        255,
          4,   2
    },
    {
        "Extra Temperature/Humidity 1 - Low Humidity",
         43,   1,
          0,   1,
        255,
          5,   2
    },
    {
        "Extra Temperature/Humidity 2 - Low Humidity",
         44,   1,
          0,   1,
        255,
          6,   2
    },
    {
        "Extra Temperature/Humidity 3 - Low Humidity",
         45,   1,
          0,   1,
        255,
          7,   2
    },
    {
        "Extra Temperature/Humidity 4 - Low Humidity",
         46,   1,
          0,   1,
        255,
          8,   2
    },
    {
        "Extra Temperature/Humidity 5 - Low Humidity",
         47,   1,
          0,   1,
        255,
          9,   2
    },
    {
        "Extra Temperature/Humidity 6 - Low Humidity",
         48,   1,
          0,   1,
        255,
          0,   2
    },
    {
        "Extra Temperature/Humidity 7 - Low Humidity",
         49,   1,
          0,   1,
        255,
          1,   2
    },
    {
        "High Outdoor Humidity",
         50,   1,
          0,   1,
        255,
          4,   3
    },
    {
        "Extra Temperature/Humidity 1 - High Humidity",
         51,   1,
          0,   1,
        255,
          5,   3
    },
    {
        "Extra Temperature/Humidity 2 - High Humidity",
         52,   1,
          0,   1,
        255,
          6,   3
    },
    {
        "Extra Temperature/Humidity 3 - High Humidity",
         43,   1,
          0,   1,
        255,
          7,   3
    },
    {
        "Extra Temperature/Humidity 4 - High Humidity",
         54,   1,
          0,   1,
        255,
          8,   3
    },
    {
        "Extra Temperature/Humidity 5 - High Humidity",
         55,   1,
          0,   1,
        255,
          9,   3
    },
    {
        "Extra Temperature/Humidity 6 - High Humidity",
         56,   1,
          0,   1,
        255,
         10,   3
    },
    {
        "Extra Temperature/Humidity 7 - High Humidity",
         57,   1,
          0,   1,
        255,
         11,   3
    },
    {
        "Low Dew Point",
         58,   1,
        120,   1,
        255,
          2,   4
    },
    {
        "High Dew Point",
         59,   1,
        120,   1,
        255,
          2,   5
    },
    {
        "Low Wind Chill",
         60,   1,
        120,   1,
        255,
          2,   7
    },
    {
        "High Heat Index",
         61,   1,
         90,   1,
        255,
          2,   6
    },
    {
        "High THSW",
         62,   1,
         90,   1,
        255,
          3,   0
    },
    {
        "Wind Speed",
         63,   1,
          0,   1,
        255,
          2,   2
    },
    {
        "10 Minute Average Wind Speed",
         64,   1,
          0,   1,
        255,
          2,   3
    },
    {
        "High UV",
         65,   1,
          0,  10,
        255,
          3,   2
    },
    {
        "UNAVAILABLE",
         66,   1,
          0,   1,
          0,
         -1,   0
    },
    {
        "Soil/Leaf 1 - Low Soil Moisture",
         67,   1,
          0,   1,
        255,
         12,   2
    },
    {
        "Soil/Leaf 2 - Low Soil Moisture",
         68,   1,
          0,   1,
        255,
         13,   2
    },
    {
        "Soil/Leaf 3 - Low Soil Moisture",
         69,   1,
          0,   1,
        255,
         14,   2
    },
    {
        "Soil/Leaf 4 - Low Soil Moisture",
         70,   1,
          0,   1,
        255,
         15,   2
    },
    {
        "Soil/Leaf 1 - High Soil Moisture",
         71,   1,
          0,   1,
        255,
         12,   3
    },
    {
        "Soil/Leaf 2 - High Soil Moisture",
         72,   1,
          0,   1,
        255,
         13,   3
    },
    {
        "Soil/Leaf 3 - High Soil Moisture",
         73,   1,
          0,   1,
        255,
         14,   3
    },
    {
        "Soil/Leaf 4 - High Soil Moisture",
         74,   1,
          0,   1,
        255,
         15,   3
    },
    {
        "Soil/Leaf 1 - Low Leaf Wetness",
         75,   1,
          0,   1,
        255,
         12,   0
    },
    {
        "Soil/Leaf 2 - Low Leaf Wetness",
         76,   1,
          0,   1,
        255,
         13,   0
    },
    {
        "Soil/Leaf 3 - Low Leaf Wetness",
         77,   1,
          0,   1,
        255,
         14,   0
    },
    {
        "Soil/Leaf 4 - Low Leaf Wetness",
         78,   1,
          0,   1,
        255,
         15,   0
    },
    {
        "Soil/Leaf 1 - High Leaf Wetness",
         79,   1,
          0,   1,
        255,
         12,   1
    },
    {
        "Soil/Leaf 2 - High Leaf Wetness",
         80,   1,
          0,   1,
        255,
         13,   1
    },
    {
        "Soil/Leaf 3 - High Leaf Wetness",
         81,   1,
          0,   1,
        255,
         14,   1
    },
    {
        "Soil/Leaf 4 - High Leaf Wetness",
         82,   1,
          0,   1,
        255,
         15,   1
    },
    {
        "High Solar Radiation",
         83,   2,
          0,   1,
      65535,
          3,   1
    },
    { // TBD, rate alarm need rain collector size
        "High Rain Rate",
         85,   2,
          0,   1,
      65535,
          1,   0
    },
    { // TBD, rate alarm need rain collector size
        "15 Minute Rain",
         87,   2,
          0,   1,
      65535,
          1,   1
    },
    { // TBD, rate alarm need rain collector size
        "24 Hour Rain",
         89,   2,
          0,   1,
      65535,
          1,   1
    },
    { // TBD, rate alarm need rain collector size
        "Storm Total Rain",
         91,   2,
          0,   1,
      65535,
          1,   3
    },
    { 
        "Daily ET",
         93,   2,
          0,1000,
        255,
          1,   4
    }
};

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Alarm::Alarm(const AlarmProperties & properties) : properties(properties),
                                                   eepromThreshold(properties.eepromNotSetThreshold),
                                                   actualThreshold(0.0),
                                                   alarmThresholdSet(false),
                                                   alarmTriggered(false) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Alarm::setThreshold(int eepromThreshold) {
    this->eepromThreshold = eepromThreshold;
    if (this->eepromThreshold == properties.eepromNotSetThreshold) {
        alarmThresholdSet = false;
        alarmTriggered = false;
        actualThreshold = 0.0;
    }
    else {
        alarmThresholdSet = true;
        alarmTriggered = false;
        actualThreshold = static_cast<float>(eepromThreshold - properties.eepromThresholdOffset) / properties.eepromThresholdScale;
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Alarm::setTriggered(bool triggered) {
    alarmTriggered = triggered;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
AlarmProperties
Alarm::getAlarmProperties() const {
    return properties;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
AlarmManager &
AlarmManager::getInstance() {
    return instance;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
AlarmManager::initialize() {
    for (int i = 0; i < sizeof(alarmProperties) / sizeof(alarmProperties[0]); i++) {
        Alarm alarm(alarmProperties[i]);
        alarms.push_back(alarm);
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
AlarmManager::loadThresholds(const byte buffer[]) {
    for (vector<Alarm>::iterator it = alarms.begin(); it != alarms.end(); ++it) {
        AlarmProperties props = it->getAlarmProperties();
        int offset = props.eepromThresholdByte;
        int thresholdValue = 0;

        if (props.eepromThresholdSize == 1) 
            thresholdValue = BitConverter::toInt8(buffer, offset);
        else
            thresholdValue = BitConverter::toInt16(buffer, offset);

        it->setThreshold(thresholdValue);
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
AlarmManager::setAlarmStates(const byte buffer[]) {
    for (vector<Alarm>::iterator it = alarms.begin(); it != alarms.end(); ++it) {
        AlarmProperties props = it->getAlarmProperties();
        byte b = buffer[props.loopByte];
        it->setTriggered((b & (1 << props.loopBit)) != 0);
    }
}

}