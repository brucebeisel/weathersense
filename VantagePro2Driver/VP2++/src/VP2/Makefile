CC= g++
#CXXFLAGS= -g -std=c++11
CXXFLAGS= -g -Wno-psabi

SRCS=\
	Alarm.cpp \
	Api.cpp \
	ArchiveManager.cpp \
	ArchivePacket.cpp \
	BitConverter.cpp \
	CommandSocket.cpp \
	CurrentWeather.cpp \
	CurrentWeatherPublisher.cpp \
	EventManager.cpp \
	ForecastRule.cpp \
    HiLowPacket.cpp \
	Loop2Packet.cpp \
	LoopPacket.cpp \
	main.cpp \
	ParametersMessage.cpp \
	ProtocolException.cpp \
	Sensor.cpp \
	SensorStation.cpp \
    SerialPort.cpp \
	UnitConverter.cpp \
	VantagePro2CRC.cpp \
	VantagePro2Driver.cpp \
	VantagePro2Station.cpp \
	VP2Constants.cpp \
	VP2Logger.cpp \
    VP2Decoder.cpp \
	Weather.cpp \
	WeatherSenseSocket.cpp \
	WindDirectionSlices.cpp \
	WindSlice.cpp


OBJDIR=../../target/VP2
OBJLIST=$(SRCS:.cpp=.o)
OBJS=$(addprefix $(OBJDIR)/, $(OBJLIST))

$(OBJDIR)/%.o : %.cpp
	$(CC) $(CXXFLAGS) -c $< -o $@
	
all: vp2

vp2: $(OBJDIR) $(OBJS)
	$(CC) -g -o vp2 $(OBJS)

clean:
	rm $(OBJS)

$(OBJDIR):
	mkdir -p $(OBJDIR)

depend:
	rm -f Makefile.depend; \
	for i in $(SRCS); do \
		$(CC) -MM -MT "$(OBJDIR)/`basename $$i .cpp`.o" $$i >> Makefile.depend; \
	done;

include Makefile.depend
