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
     *
     * @return The converted measurement value along with a valid flag
     */
    static Measurement<Temperature> decode16BitTemperature(const byte buffer[], int offset);

    /**
     * Decode a Vantage Pro 2 16 bit temperature.
     *
     * @param buffer      The buffer from which to decode the temperature
     * @param offset      The offset into "buffer" from which to decode the temperature
     * @param measurement The measurement object used to store the results of the decoding
     *
     * @return The converted measurement value along with a valid flag
     */
    static const Measurement<Temperature> & decode16BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement);

    /**
     * Decode a Vantage Pro 2 16 bit temperature that is not scaled.
     *
     * @param buffer The buffer from which to decode the temperature
     * @param offset The offset into "buffer" from which to decode the temperature
     * @param valid  Reference to a boolean that returns whether the temperature value is NOT the "dashed" value
     *
     * @return The converted temperture or 0.0 if not valid
     */
    static Measurement<Temperature> decodeNonScaled16BitTemperature(const byte buffer[], int offset);
    static const Measurement<Temperature> & decodeNonScaled16BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement);

    static Measurement<Temperature> decode8BitTemperature(const byte buffer[], int offset);
    static const Measurement<Temperature> & decode8BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement);


    static Measurement<Humidity> decodeHumidity(const byte buffer[], int offset);
    static const Measurement<Humidity> & decodeHumidity(const byte buffer[], int offset, Measurement<Humidity> & measurement);

    static Measurement<Pressure> decodeBarometricPressure(const byte buffer[], int offset);
    static const Measurement<Pressure> & decodeBarometricPressure(const byte buffer[], int offset, Measurement<Pressure> & measurement);

    static Measurement<UvIndex> decodeUvIndex(const byte buffer[], int offset);
    static const Measurement<UvIndex> & decodeUvIndex(const byte buffer[], int offset, Measurement<UvIndex> & measurement);

    static Measurement<Evapotranspiration> decodeDayET(const byte buffer[], int offset);
    static const Measurement<Evapotranspiration> & decodeDayET(const byte buffer[], int offset, Measurement<Evapotranspiration> & measurement);

    static Measurement<Evapotranspiration> decodeMonthYearET(const byte buffer[], int offset);
    static const Measurement<Evapotranspiration> & decodeMonthYearET(const byte buffer[], int offset, Measurement<Evapotranspiration> & measurement);

    static Measurement<SolarRadiation> decodeSolarRadiation(const byte buffer[], int offset);
    static const Measurement<SolarRadiation> & decodeSolarRadiation(const byte buffer[], int offset, Measurement<SolarRadiation> & measurement);

    static Measurement<Speed> decodeWindSpeed(const byte buffer[], int offset);
    static const Measurement<Speed> & decodeWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement);

    static Measurement<Speed> decode16BitWindSpeed(const byte buffer[], int offset);
    static const Measurement<Speed> & decode16BitWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement);

    static Measurement<Speed> decodeAvgWindSpeed(const byte buffer[], int offset);
    static const Measurement<Speed> & decodeAvgWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement);

    static Measurement<Heading> decodeWindDirectionSlice(const byte buffer[], int offset);
    static const Measurement<Heading> & decodeWindDirectionSlice(const byte buffer[], int offset, Measurement<Heading> & measurement);

    static Measurement<Heading> decodeWindDirection(const byte buffer[], int offset);
    static const Measurement<Heading> & decodeWindDirection(const byte buffer[], int offset, Measurement<Heading> & measurement);

    static Rainfall decodeStormRain(const byte buffer[], int offset);

    static void setRainCollectorSize(Rainfall collectorSize);

    static Rainfall decodeRain(const byte buffer[], int offset);

    static DateTime decodeStormStartDate(const byte buffer[], int offset);

    static float decodeConsoleBatteryVoltage(const byte buffer[], int offset);

    static Measurement<LeafWetness> decodeLeafWetness(const byte buffer[], int offset);
    static const Measurement<LeafWetness> & decodeLeafWetness(const byte buffer[], int offset, Measurement<LeafWetness> & measurement);

    static Measurement<SoilMoisture> decodeSoilMoisture(const byte buffer[], int offset);
    static const Measurement<SoilMoisture> & decodeSoilMoisture(const byte buffer[], int offset, Measurement<SoilMoisture> & measurement);
    

    static DateTime decodeTime(const byte buffer[], int offset);
    static DateTime decodeDate(const byte buffer[], int offset);
    static DateTime decodeDateTime(const byte buffer[], int dateOffset, int timeOffset);

private:
    static Rainfall rainCollectorSize;
    static bool     rainCollectorSizeSet;

    VP2Decoder();
    ~VP2Decoder();
    VP2Decoder(const VP2Decoder &);
};

}

#endif
