#include "Weather.h"

namespace vp2 {
class EepromManager {
public:
    static constexpr int EEPROM_DATA_BLOCK_SIZE = 4096;
    static constexpr int EEPROM_NON_GRAPH_DATA_SIZE = 176;
    static constexpr int EEPROM_GRAPH_DATA_OFFSET = 176;
    static constexpr int EEPROM_GRAPH_DATA_SIZE = EEPROM_DATA_BLOCK_SIZE - EEPROM_GRAPH_DATA_OFFSET;

    static constexpr int ALARM_THRESHOLDS_OFFSET = 82;
    static constexpr int ALARM_THRESHOLDS_SIZE = 94;
    static constexpr int CRC_BYTES = 2;

    void setLatitude(double latitude);
    double getLatitude() const;

    void setLongitude(double longitude);
    double getLongitutde() const;

private:
    static constexpr int protectedBytes[] = {0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0x2d};
    byte eepromBuffer[EEPROM_DATA_BLOCK_SIZE + CRC_BYTES];
    byte eepromNonGraphData[EEPROM_NON_GRAPH_DATA_SIZE + CRC_BYTES];
    byte eepromGraphData[EEPROM_NON_GRAPH_DATA_SIZE + CRC_BYTES];
    byte alarmThresholds[ALARM_THRESHOLDS_SIZE];
};

}

struct EepromEntry {
     std::string vantagePro2Name;
     std::string readableName;
     std::string hexAddress;
     size_t      length;
     bool        editable;
     std::string description;
};

static const EepromEntry entries[] = {
    // These are the factory barometer calibration values.  Do not modify them!
    { "BAR_GAIN",   "Barometric gain",   "01", 2, false, "DO NOT CHANGE" },
    { "BAR_OFFSET", "Barometric offset", "03", 2, false, "DO NOT CHANGE" },
    { "BAR_CAL",    "Barometric offset", "05", 2, true,  "Barometer Offset calibration.  Use the 'BAR=' command to set this value!" },
    { "HUM33",      "Humidity 33",       "07", 2, false,  "These are the factory inside humidity calibration values. Do not modify them!" },
    { "HUM80",      "Humidity 80",       "09", 2, false,  "These are the factory inside humidity calibration values. Do not modify them!" },
    { "LATITUDE",   "Station Latitude",  "0B", 2, true,   "Station Latitude in tenths of a degree. Negative values = southern hemisphere" },
    { "LONGITUDE",  "Station Longitude", "0B", 2, true,   "Station Latitude in tenths of a degree. Negative values = southern hemisphere" },

/*
Name Hex Dec Size Description
BAR_GAIN 1 1 2 These are the factory barometer calibration values.  Do not modify them!
BAR_OFFSET 3 3 2
BAR_CAL 5 5 2 Barometer Offset calibration.  Use the "BAR=" command to set this value!
HUM33 7 7 2 These are the factory inside humidity calibration values. Do not modify them!
HUM80 9 9 2
LATITUDE 0B 11 2 Station Latitude in tenths of a degree. Negative values = southern hemisphere
LONGITUDE 0D 13 2 Station Longitude in tenths of a degree. Negative values = western hemisphere
ELEVATION 0F 15 2 Station elevation in feet.  Use the "BAR=" command to set this value!
TIME_ZONE 11 17 1 String number of the time zone selected on the setup screen.
MANUAL_OR_AUTO 12 18 1 1 = manual daylight savings, 0 = automatic daylight savings Rev 2.6.1 Page 40 of 60 March 29, 2013
DAYLIGHT_SAVINGS 13 19 1 This is the configuration bit for the day light savings mode when it is set in manual mode. 1 = daylight savings is now on, 0 = daylight savings is now off. When automatic daylight savings mode is selected, this bit is ignored and it does not indicate whether the daylight savings is on or not.
GMT_OFFSET 14 20 2 The time difference between GMT and local time (a 2-byte signed number in hundredths of hours. For example, a value of 850 would be +8.50 hours. Negative values in 2’s complements, represent western hemisphere.
GMT_OR_ZONE 16 22 1 1 = use the GMT_OFFSET value, 0 = use the TIME_ZONE value
USETX 17 23 1 Bitmapped field that indicates which DavisTalk transmitters to listen to. Bit 0 = ID 1.
RE_TRANSMIT_TX 18 24 1 "ID number to use for retransmit. 0 = don't retransmit, 1 = use ID 1, 2 = use ID 2, etc."
STATION_LIST 19 25 16 2 bytes per transmitter ID. First byte is station type, second byte is <temp sensor # | hum sensor #>. See section XIV.4 for more details.
UNIT_BITS 29 41 1 Barometer unit (bit 1:0):
                      0: 0.01 INCHES
                      1: 0.1 MM
                      2: 0.1 HPA
                      3: 0.1 MB
                  Temperature unit (Bit 3:2):
                      0: ?F (Whole degrees)
                      1: ?F (Tenths of a degree)
                      2: ?C (Whole degrees)
                      3: ?C (Tenths of a degree)
                      Elevation unit (Bit 4):
                      0: FEET
                      1: METERS
                  RAIN unit (Bit 5):
                      0: INCHES
                      1: MM
                  Wind unit (Bit 7:6):
                      0: MPH
                      1: M/S
                      2: Km/H
                      3: KNOTS
UNIT_BITS_COMP 2A 42 1 This should be the 1's complement of UNIT_BITS for validation.  Rev 2.6.1 Page 41 of 60 March 29, 2013
SETUP_BITS 2B 43 1 AM/PM Time Mode (Bit 0):
                       0: AM/PM Mode
                       1: 24-Hour Mode
                       Is AM or PM (Bit 1):
                       0: PM
                       1: AM
                   Month/Day Format (Bit 2):
                       0: Shown as Month/Day
                       1: Shown as Day/Month
                   Wind Cup Size (Bit 3): (VP and VP2 only)
                       0: Small Size
                       1: Large Size
                   Rain Collector Size (Bit 5:4):
                       0: 0.01 INCHES
                       1: 0.2 MM
                       2: 0.1 MM
                   Latitude (Bit 6):
                       0: South
                       1: North
                   Longitude (Bit 7):
                       0: West
                       1: East
RAIN_SEASON_START 2C 44 1 Month that the Yearly rain total is cleared. 1 = January, etc
ARCHIVE_PERIOD 2D 45 1 Number of minutes in the archive period.  Use "SETPER" to set this value.

Calibration values are 1 byte signed numbers that are offsets applied to the corresponding raw sensor value in the native sensor units (either 0.1 °F or 1 %)
TEMP_IN_CAL 32 50 1 The setting range is from (-12.8 °F to 12.7 °F) with the most significant byte as the sign bit.
TEMP_IN_COMP 33 51 1 1's compliment of TEMP_IN_CAL to validate calibration data
TEMP_OUT_CAL 34 52 1 The setting range is from (-12.8 °F to 12.7 °F) with the most significant byte as the sign bit.
TEMP_CAL 35 53 15 7 "extra" temperatures, 4 soil temperatures, and 4 leaf temperatures
HUM_IN_CAL 44 68 1 The inside humidity calibration value is ranged from 0 to 100%.
HUM_CAL 45 69 8 The first entry is the currently selected outside humidity sensor.
DIR_CAL 4D 77 2 2 byte wind direction calibration allows full 360° calibration in both directions.
DEFAULT_BAR_GRAPH 4F 79 1 These values control which time span to use on the console graph display when Rain, Barometer, or Wind Speed is shown.
DEFAULT_RAIN_GRAPH 50 80 1
DEFAULT_SPEED_GRAPH 51 81 1
ALARM_START 52 82 94
Starting location for the Alarm threshold data. See section XIV.5 for more details on setting alarm thresholds
BAR_RISE_ALARM 52 82 1 3 hour rising bar trend alarm. Units are in Hg * 1000
BAR_FALL_ALARM 53 83 1 3 hour falling bar trend alarm. Units are in Hg * 1000
TIME_ALARM 54 84 2 Time alarm. Hours * 100 + minutes
TIME_COMP_ALARM 56 86 2 1's compliment of TIME_ALARM to validate alarm entries
LOW_TEMP_IN_ALARM 58 88 1 Threshold is (data value – 90) °F
HIGH_TEMP_IN_ALARM 59 89 1 Threshold is (data value – 90) °F Rev 2.6.1 Page 42 of 60 March 29, 2013
LOW_TEMP_OUT_ALARM 5A 90 1 Threshold is (data value – 90) °F
HIGH_TEMP_OUT_ALARM 5B 91 1 Threshold is (data value – 90) °F
LOW_TEMP_ALARM 5C 92 15 7 extra temps, 4 soil temps, 4 leaf temps
HIGH_TEMP_ALARM 6B 107 15 7 extra temps, 4 soil temps, 4 leaf temps
LOW_HUM_IN_ALARM 7A 122 1 Low relative humidity alarm in %.
HIGH_HUM_IN_ALARM 7B 123 1 High relative humidity alarm in %.
LOW_HUM_ALARM 7C 124 8 First entry is the current Outside Humidity setting
HIGH_HUM_ALARM 84 132 8 First entry is the current Outside Humidity setting
LOW_DEW_ALARM 8C 140 1 Threshold is (data value – 120) °F
HIGH_DEW_ALARM 8D 141 1 Threshold is (data value – 120) °F
CHILL_ALARM 8E 142 1 Threshold is (data value – 120) °F
HEAT_ALARM 8F 143 1 Threshold is (data value – 90) °F
THSW_ALARM 90 144 1 Threshold is (data value – 90) °F
SPEED_ALARM 91 145 1 Current Wind Speed alarm. Units are MPH
SPEED_10MIN_ALARM 92 146 1 10 minute average Wind Speed alarm. Units are MPH
UV_ALARM 93 147 1 Current UV index alarm. Units are (UV Index * 10)
LOW_SOIL_ALARM 95 149 4 Low soil moisture alarm in centibar. It supports four soil moisture sensors.
HIGH_SOIL_ALARM 99 153 4 High soil moisture alarm in centibar. It supports four soil moisture sensors.
LOW_LEAF_ALARM 9D 157 4 Low leaf wetness alarm with index 0 to 15. 0 is very dry and 15 is very wet.
HIGH_LEAF_ALARM A1 161 4 High leaf wetness alarm with index 0 to 15. 0 is very dry and 15 is very wet.
SOLAR_ALARM A5 165 2 Solar energy alarm is set with watts/meter2.
RAIN_RATE_ALARM A7 167 2 Rate rain alarm is set with rain clicks/hour
RAIN_15MIN_ALARM A9 169 2 15-minute alarm is set with rain clicks.
RAIN_24HR_ALARM AB 171 2 24-hour alarm is set with rain clicks.
RAIN_STORM_ALARM AD 173 2 Rain storm alarm is set with rain clicks.
ET_DAY_ALARM AF 175 1 Evapotranspiration alarm is set with 1000th of an inch.
Graph Pointers 177 8 See section XV
Graph data (VP only)
185
See section XV
Wind Cup Size (VP2 & Vue only)
195
Only 2 least significant bits are used:
0 = undefined
1 = small;
2 = large;
3 = other (no speed correction applied by console);
Graph data (VP2 & Vue only)
325
See section XV
Log Average Temperature
FFC
4092
1
Set this value to zero to enable logging of average temperature values. A non-zero value causes the temperature at the end of the archive period to be logged.
Password CRC
FFE
4094
2
WeatherLink uses these two bytes to hold the CRC of a password in order to provide some protection from unauthorized access. This is only enforced by software implementation. The value 0xFFFF indicates that no password is set.
*/
