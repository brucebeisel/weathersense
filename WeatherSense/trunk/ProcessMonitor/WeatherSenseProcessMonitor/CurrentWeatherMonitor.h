#ifndef CURRENT_WEATHER_MONITOR_H
#define	CURRENT_WEATHER_MONITOR_H
#include <string>

class CurrentWeatherMonitor {
public:
    static CurrentWeatherMonitor & getInstance();
    void initialize(std::string & host, int port);
    bool isCurrentWeatherRecent() const;
private:
    CurrentWeatherMonitor();
    virtual ~CurrentWeatherMonitor();
    static CurrentWeatherMonitor * instance;
    std::string host;
    int port;

};

#endif	/* CURRENTWEATHERMONITOR_H */