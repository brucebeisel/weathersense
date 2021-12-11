#ifndef VANTAGE_PRO2_CONFIGURATION_H
#define VANTAGE_PRO2_CONFIGURATION_H

#include "SensorStation.h"
#include "Sensor.h"
#include "Weather.h"
#include "VantagePro2Station.h"

namespace vp2 {

class VantagePro2Configuration {
public:
    VantagePro2Configuration();
    virtual ~VantagePro2Configuration();

    enum RainCupSizeType {
        POINT_01_INCH = 0,
        POINT_02_MM = 1,
        POINT_01_MM = 2
    };

    static constexpr Rainfall POINT_01_INCH_SIZE = 0.1;        // Inches
    static constexpr Rainfall POINT_02_MM_SIZE   = 0.2 / 25.4; // Inches
    static constexpr Rainfall POINT_01_MM_SIZE   = 0.1 / 25.4; // Inches


public:
    double                     issLatitude;
    double                     issLongitude;
    int                        issElevation;
    int                        timezoneIndex;
    bool                       manualDaylightSavingsTime;
    bool                       manualDaylightSavingsTimeOn;
    int                        gmtOffsetMinutes;
    bool                       useTimezone;
    bool                       amPmMode;
    bool                       isAM;
    bool                       monthDayFormat;
    bool                       windCupLarge;
    Rainfall                   rainCollectorSize;
    int                        archivePeriod;
    bool                       latitudeNorth;
    bool                       longitudeEast;

    // Units Barometer, temperature, elevation, rain, wind
    // rain season start month
    // archive period
    // inside temperature calibration
    // outside temperature calibration
    // other temperatures calibration
    // inside humidity calibration
    // outside humidity calibration
    // wind direction calibration


    //int                        retransmitId;                 // 0 = no retransmission
    //std::vector<SensorStation> sensorStations;
    //std::string                firmwareDate;
    //std::string                firmwareVersion;
    //VP2Logger                  log;

    /**
     * Get the list of sensor stations.
     *
     * @return the list of sensor stations
     */
    const std::vector<SensorStation> & getSensorStations() const;

    /**
     * Get the list of sensors attached to the sensor stations.
     *
     * @return The list of sensors
     */
    const std::vector<Sensor> & getSensors() const;

    void decodeData(const byte buffer[]);

private:
    VantagePro2Station station;
};

}

#endif /* SRC_VP2_VANTAGEPRO2CONFIGURATION_H_ */
