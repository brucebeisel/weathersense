
namespace vp2 {

class VP2Utils {
public:
    static Temperature parse16BitTemperature(const byte buffer[], int offset, bool & valid);
    static Temperature parse8BitTemperature(const byte buffer[] int offset, bool &valid); 

    static Pressure parseBarometericPressure(const byte buffer[], int offset, bool &valid);

    static DateTime parseTime(const byte buffer[], int offset);
    static DateTime parseDate(const byte buffer[], int offset);
    static DateTime parseDateTime(const byte buffer[], int dateOffset, int timeOffset);

private:
    VP2Utils();
};

}