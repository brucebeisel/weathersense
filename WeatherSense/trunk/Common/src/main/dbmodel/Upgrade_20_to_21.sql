delete from weathersense21.history where date<"2006-1-1";
insert into weathersense21.history (date,duration,avg_wind_speed,prevailing_wind_direction,high_wind_speed,high_wind_direction,wind_gust,wind_gust_direction,baro_pressure,rainfall)
select date,duration,wind_speed,wind_dir,wind_gust,wind_dir,wind_gust,wind_dir,baro_pressure,rainfall from weathersense20.history
where station_id="beisel";

update weathersense21.history inner join weathersense20.temperature_humidity_history
on weathersense21.history.date=weathersense20.temperature_humidity_history.date
set low_outdoor_temperature=weathersense20.temperature_humidity_history.temperature,
    avg_outdoor_temperature=weathersense20.temperature_humidity_history.temperature,
    high_outdoor_temperature=weathersense20.temperature_humidity_history.temperature,
    outdoor_humidity=weathersense20.temperature_humidity_history.humidity
where weathersense20.temperature_humidity_history.sensor_name="outdoor";

update weathersense21.history inner join weathersense20.temperature_humidity_history
on weathersense21.history.date=weathersense20.temperature_humidity_history.date
set indoor_temperature=weathersense20.temperature_humidity_history.temperature,
    indoor_humidity=weathersense20.temperature_humidity_history.humidity
where weathersense20.temperature_humidity_history.sensor_name="indoor";

insert into sensors values(100, "TH", "Pool");

insert into weathersense21.sensor_value_history (date,sensor_id,sensor_type,measurement)
select date,100,"TH",temperature from weathersense20.temperature_humidity_history where station_id="beisel" and sensor_name="pool";

insert into weathersense21.daily_seasonal_averages(month,day,high_temperature,low_temperature,mean_temperature,rainfall)
select month,day,high_temperature,low_temperature,mean_temperature,rainfall from weathersense20.daily_seasonal_averages;

insert into weathersense21.daily_records (month,day,record_max_high,record_max_high_year,record_min_high,record_min_high_year,
                           record_max_low,record_max_low_year,record_min_low,record_min_low_year,record_rainfall, record_rainfall_year)
select month,day,record_max_high,record_max_high_year,record_min_high,record_min_high_year,
                           record_max_low,record_max_low_year,record_min_low,record_min_low_year,record_rainfall, record_rainfall_year
from weathersense20.daily_records;

insert into weathersense21.monthly_seasonal_averages (month,high_temperature,low_temperature,mean_temperature,rainfall)
select month,high_temperature,low_temperature,mean_temperature,rainfall from weathersense20.monthly_seasonal_averages;

insert into weathersense21.temperature_records (date,record_type,value,previous_date,previous_value)
select date,record_type,value,previous_date,previous_value from weathersense20.temperature_records;

insert into weathersense21.temperature_bins (bin_type,threshold)
select bin_type,threshold from weathersense20.temperature_bins;

insert into weather_station (singleton_id, manufacturer, model, firmware_date, firmware_version, location_code, location_description, latitude, longitude, altitude, weather_year_start_month, wind_slice_count, wind_speed_bin_interval, num_wind_speed_bins, thermometer_min, thermometer_max, atm_pressure_min, atm_pressure_max, daily_rain_max, monthly_rain_max, yearly_rain_max, weather_underground_station_id, weather_underground_password, doppler_radar_url)
values('1', 'Davis Instruments', 'Vantage Pro 2', 'Sep 29 2009', '1.90', 'null', 'null', '32.900000', '-117.100000', '147.8', '7', '16', '2.24', '5', '-28.889', '48.889', '948.19', '1083.64', '254.000', '508.000', '2540.000', NULL, NULL, NULL);
