insert into weathersense24.history select * from weathersense22.history;

insert into weathersense24.daily_seasonal_averages select * from weathersense22.daily_seasonal_averages;

insert into weathersense24.daily_records select * from weathersense22.daily_records;

insert into weathersense24.monthly_seasonal_averages select * from weathersense22.monthly_seasonal_averages;

insert into weathersense24.temperature_records select * from weathersense22.temperature_records;

insert into weathersense24.temperature_bins (bin_type,threshold) select * from weathersense22.temperature_bins;

insert into weathersense24.weather_station select * from weathersense22.weather_station;

insert into weathersense24.sensors select * from weathersense22.sensors;

insert into weathersense24.sensor_value_history select * from weathersense22.sensor_value_history;

insert into weathersense24.sensor_stations select * from weathersense22.sensor_stations;

insert into weathersense24.sensor_station_status select * from weathersense22.sensor_station_status;

insert into weathersense24.storms (storm_start,total_rainfall) select * from weathersense22.storms;

insert into weathersense24.storm_doppler_images select * from weathersense22.storm_doppler_images; 