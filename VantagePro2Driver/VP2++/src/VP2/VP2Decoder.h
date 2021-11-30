#ifndef VP2_DECODER_H
#define VP2_DECODER_H

#include "Weather.h"
#include "Measurement.h"
#include "VP2Logger.h"

namespace vp2 {

/**
 * Class that contains static methods for converting bytes in a buffer into measurement values
 */
class VP2Decoder {
public:
    /**
     * Decode a Vantage Pro 2 16 bit temperature.
     *
     * @param buffer The buffer from which to decode the temperature
     * @param offset The offset into "buffer" from which to decode the temperature
     * @param valid  Reference to a boolean that returns whether the temperature value is NOT the "dashed" value
     *
     * @return The converted temperture or 0.0 if not valid
     */
    static Temperature decode16BitTemperature(const byte buffer[], int offset, bool & valid);

    static bool decode16BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement);

    /**
     * Decode a Vantage Pro 2 16 bit temperature that is not scaled.
     *
     * @param buffer The buffer from which to decode the temperature
     * @param offset The offset into "buffer" from which to decode the temperature
     * @param valid  Reference to a boolean that returns whether the temperature value is NOT the "dashed" value
     *
     * @return The converted temperture or 0.0 if not valid
     */
    static Temperature decodeNonScaled16BitTemperature(const byte buffer[], int offset, bool & valid);

    static Temperature decode8BitTemperature(const byte buffer[], int offset, bool &valid); 

    static bool decode8BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement);

    static Humidity decodeHumidity(const byte buffer[], int offset, bool &valid);

    static Pressure decodeBarometricPressure(const byte buffer[], int offset, bool &valid);

    static UvIndex decodeUvIndex(const byte buffer[], int offset, bool &valid);

    static Evapotranspiration decodeDayET(const byte buffer[], int offset, bool &valid);
    static Evapotranspiration decodeMonthYearET(const byte buffer[], int offset, bool &valid);

    static SolarRadiation decodeSolarRadiation(const byte buffer[], int offset, bool &valid);

    static Speed decodeWindSpeed(const byte buffer[], int offset, bool &valid);
    static Speed decode16BitWindSpeed(const byte buffer[], int offset, bool &valid);
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
