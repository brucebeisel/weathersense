#ifndef VP2_DECODER_H
#define VP2_DECODER_H

#include "Weather.h"

namespace vp2 {

class VP2Decoder {
public:
    static Temperature decode16BitTemperature(const byte buffer[], int offset, bool & valid);
    static Temperature decodeNonScaled16BitTemperature(const byte buffer[], int offset, bool & valid);
    static Temperature decode8BitTemperature(const byte buffer[], int offset, bool &valid); 

    static Humidity decodeHumidity(const byte buffer[], int offset, bool &valid);

    static Pressure decodeBarometricPressure(const byte buffer[], int offset, bool &valid);

    static UvIndex decodeUvIndex(const byte buffer[], int offset, bool &valid);

    static Evapotranspiration decodeDayET(const byte buffer[], int offset, bool &valid);
    static Evapotranspiration decodeMonthYearET(const byte buffer[], int offset, bool &valid);

    static SolarRadiation decodeSolarRadiation(const byte buffer[], int offset, bool &valid);

    static Speed decodeWindSpeed(const byte buffer[], int offset, bool &valid);
    static Speed decodeAvgWindSpeed(const byte buffer[], int offset, bool &valid);

    static Heading decodeWindDirectionSlice(const byte buffer[], int offset, bool &valid);
    static Heading decodeWindDirection(const byte buffer[], int offset, bool &valid);

    static Rainfall decodeStormRain(const byte buffer[], int offset);
    static void setRainCollectorSize(Rainfall collectorSize);
    static Rainfall decodeRain(const byte buffer[], int offset);

    static DateTime decodeStormStartDate(const byte buffer[], int offset);

    static float decodeConsoleBatteryVoltage(const byte buffer[], int offset);

    static LeafWetness decodeLeafWetness(const byte buffer[], int offset, bool &valid);
    static SoilMoisture decodeSoilMoisture(const byte buffer[], int offset, bool &valid);
    

    static DateTime decodeTime(const byte buffer[], int offset);
    static DateTime decodeDate(const byte buffer[], int offset);
    static DateTime decodeDateTime(const byte buffer[], int dateOffset, int timeOffset);

private:
    static Rainfall rainCollectorSize;

    VP2Decoder();
    ~VP2Decoder();
    VP2Decoder(const VP2Decoder &);
};

}

#endif