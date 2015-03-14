using System;

/// <summary>
/// Class that contains various constants for talking with the Vantage Pro 2 weather station console.
/// </summary>
public class CommandStrings
{
    public static readonly char LINE_FEED = '\n';
    public static readonly char CARRIAGE_RETURN = '\r';
    public static readonly char ACK = '\u0006';
    public static readonly char NACK = '\u0041'; // Not an ASCII NACK, but it is what is used
    public static readonly char CANCEL = '\u0030';
    public static readonly char ESCAPE = '\u0033';
    
    public static readonly String RESPONSE_FRAME = LINE_FEED.ToString() + CARRIAGE_RETURN.ToString();
    public static readonly String COMMAND_TERMINATOR = LINE_FEED.ToString();
    public static readonly String CRC_FAILURE = CANCEL.ToString();
    
    public static readonly String COMMAND_RECOGNIZED = RESPONSE_FRAME + "OK" + RESPONSE_FRAME;

    public static readonly String WAKEUP_COMMAND = LINE_FEED.ToString();
    public static readonly String WAKEUP_RESPONSE = LINE_FEED.ToString() + CARRIAGE_RETURN.ToString();

    public static readonly String TEST_COMMAND = "TEST";
    public static readonly String FIRMWARE_DATE_REQUEST = "VER";
    public static readonly String FIRMWARE_VERSION_REQUEST = "NVER";
    public static readonly String DUMP = "DMP";
    public static readonly String DUMP_AFTER = "DMPAFT";
    public static readonly String RECEIVERS = "RECEIVERS";
    public static readonly String GET_TIME = "GETTIME";
    public static readonly String SET_TIME = "SETTIME";
    public static readonly String GET_EEPROM_BLOCK = "GETEE";
    public static readonly String READ_EEPROM = "EEBRD";
    public static readonly String CONSOLE_RECEIVE_CHECK = "RXCHECK";
    public static readonly String LOOP = "LOOP 1";
    public static readonly String LOOP2 = "LPS 3 ";
    public static readonly String SEND_NEXT_PAGE = ACK.ToString();
    public static readonly String CANCEL_DOWNLOAD = ESCAPE.ToString();
    public static readonly String RESEND_PAGE = NACK.ToString();

    public static readonly String EE_LATITUDE = "0B";
    public static readonly String EE_LONGITUDE = "0D";
    public static readonly String EE_ELEVATION = "0F";
    public static readonly String EE_TIMEZONE = "11";
    public static readonly String EE_STATION_LIST = "19";
    public static readonly String EE_SETUP_BITS = "2B";
    public static readonly String EE_RAIN_SEASON_START = "2C";
    public static readonly String EE_ARCHIVE_PERIOD = "2D";
    
    public static readonly byte RAIN_BIT = 0x1;
    public static readonly byte CLOUDY_BIT = 0x2;
    public static readonly byte PARTLY_CLOUDY_BIT = 0x4;
    public static readonly byte SUNNY_BIT = 0x8;
    public static readonly byte SNOW_BIT = 0x10;

    public static readonly int MOSTLY_CLEAR_FORECAST = SUNNY_BIT;
    public static readonly int PARTLY_CLOUDY_FORECAST = PARTLY_CLOUDY_BIT | CLOUDY_BIT;
    public static readonly int MOSTLY_CLOUDY_FORECAST = CLOUDY_BIT;
    public static readonly int MOSTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = CLOUDY_BIT | RAIN_BIT;
    public static readonly int MOSTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = CLOUDY_BIT | SNOW_BIT;
    public static readonly int PARTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT;
    public static readonly int PARTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = PARTLY_CLOUDY_BIT | SNOW_BIT;
    public static readonly int PARTLY_CLOUDY_CHANCE_OF_RAIN_OR_SNOW_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT | SNOW_BIT;

    private CommandStrings()
    {
    }
}
