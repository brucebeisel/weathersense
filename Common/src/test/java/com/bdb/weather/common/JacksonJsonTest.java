package com.bdb.weather.common;

import java.io.File;
import java.util.List;

import com.bdb.util.measurement.Measurement;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Temperature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonJsonTest {
	static class IndexedValue<T extends Measurement> {
		public int index;
		public T value;
	}
	public Temperature temperature;
	public Humidity humidity;
	public List<IndexedValue<Temperature>> extraTemperatures;
	public List<String> dominantWindDirections;
		
	public static void main(String[] args) {

		ObjectMapper objectMapper = new ObjectMapper();
		String s = new String("{ \"temperature\" : 12, \"humidity\" : 90.0, \"extraTemperatures\" : [ {\"index\" : 1, \"value\" : 22.0} ], \"dominantWindDirections\" : [ \"N\", \"W\", \"NW\" ]}");
		
		Temperature.Unit unit = Temperature.getDefaultUnit();
		
		try {
			Temperature.setDefaultUnit(Temperature.Unit.FAHRENHEIT);
			JacksonJsonTest current = objectMapper.readValue(s, JacksonJsonTest.class);
			Temperature.setDefaultUnit(unit);
			System.out.println("Temperature: " + current.temperature.toString(Temperature.Unit.FAHRENHEIT));
			System.out.println("Extra temperature count: " + current.extraTemperatures.size());
			System.out.println("Extra temperature: Index = " + current.extraTemperatures.get(0).index + " Value = " + current.extraTemperatures.get(0).value);
			for (String d : current.dominantWindDirections) {
				System.out.println("Dominant Wind Direction: " + d);
			}

			System.out.println("Current Dir: " + System.getProperty("user.dir"));
			objectMapper.registerModule(new JavaTimeModule());
		    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			CurrentWeather cw = objectMapper.readValue(new File("src/test/java/com/bdb/weather/common/CurrentWeather.json"), CurrentWeather.class);
			System.out.println(cw);
		}
		catch (Exception e) {
			System.out.println("Caught exception: " + e);
		}

	}

}
