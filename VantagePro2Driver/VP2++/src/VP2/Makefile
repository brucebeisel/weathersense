CC= g++
CXXFLAGS= -g

SRCS=ArchiveManager.cpp \
	ArchivePacket.cpp \
	BitConverter.cpp \
	CurrentWeather.cpp \
	ForecastRule.cpp \
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
	Weather.cpp \
	WeatherSenseSocket.cpp \
	WindDirectionSlices.cpp \
	WindSlice.cpp

OBJDIR=../../target/VP2
OBJLIST=$(SRCS:.cpp=.o)
OBJS=$(addprefix $(OBJDIR)/, $(OBJLIST))

$(OBJDIR)/%.o : %.cpp
	$(CC) $(CXXFLAGS) -c $< -o $@
	

vp2: $(OBJDIR) $(OBJS)
	$(CC) -o vp2 $(OBJS)

$(OBJDIR):
	mkdir -p $(OBJDIR)

