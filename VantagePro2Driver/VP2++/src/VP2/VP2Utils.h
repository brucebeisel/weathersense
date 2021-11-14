#ifndef VP2_UTILS_H
#define VP2_UTILS_H

#include "Weather.h"

namespace vp2 {

class VP2Utils {
public:
    static Temperature decode16BitTemperature(const byte buffer[], int offset, bool & valid);
    static Temperature decode8BitTemperature(const byte buffer[], int offset, bool &valid); 

    static Humidity decodeHumidity(const byte buffer[], int offset, bool & valid);

    static Pressure decodeBarometricPressure(const byte buffer[], int offset, bool &valid);

    static UvIndex decodeUvIndex(const byte buffer[], int offset, bool &valid);

    static Evapotranspiration decodeET(const byte buffer[], int offset, bool &valid);
    static SolarRadiation decodeSolarRadiation(const byte buffer[], int offset, bool &valid);

    static Speed decodeWindSpeed(const byte buffer[], int offset, bool &valid);
    static Heading decodeWindDirectionSlice(const byte buffer[], int offset, bool &valid);

    static DateTime decodeTime(const byte buffer[], int offset);
    static DateTime decodeDate(const byte buffer[], int offset);
    static DateTime decodeDateTime(const byte buffer[], int dateOffset, int timeOffset);

private:
    VP2Utils();
    ~VP2Utils();
    VP2Utils(const VP2Utils &);
};

}

#endif