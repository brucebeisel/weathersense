namespace vp2 {

struct AlarmProperties {
    std::string alarmName;
    int         eepromValue;
    int         eepromValueByte;
    int         eepromValueSize;
    int         eepromValueOffset;
    int         eepromValueScale;
    int         eepromNotSetValue;
};

class Alarm {
public:
private:
    AlarmProperties properties;
    float           actualValue; // This can be either a float or an integer
    bool            alarmSet;
    bool            alarmTriggered;
};
}
