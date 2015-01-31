SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `weathersense20` ;
USE `weathersense20`;

-- -----------------------------------------------------
-- Table `weathersense20`.`locations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`locations` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`locations` (
  `location_code` VARCHAR(20) NOT NULL ,
  `doppler_radar_url` VARCHAR(100) NULL DEFAULT NULL ,
  `weather_year_start_month` INT(2) NULL DEFAULT 1 ,
  PRIMARY KEY (`location_code`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`weather_stations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`weather_stations` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`weather_stations` (
  `station_id` VARCHAR(20) NOT NULL ,
  `description` VARCHAR(100) NULL ,
  `location_code` VARCHAR(20) NOT NULL ,
  `location_description` VARCHAR(100) NULL ,
  `latitude` DOUBLE NULL DEFAULT 0.0 ,
  `longitude` DOUBLE NULL DEFAULT 0.0 ,
  `altitude` DOUBLE NULL DEFAULT 0.0 ,
  `model` VARCHAR(100) NULL ,
  `weather_sense_model_string` VARCHAR(45) NULL ,
  `wind_slice_count` INT(11) NULL DEFAULT 16 ,
  `thermometer_min` DOUBLE NULL DEFAULT -10.0 ,
  `thermometer_max` DOUBLE NULL DEFAULT 50.0 ,
  `atm_pressure_min` DOUBLE NULL DEFAULT 900.0 ,
  `atm_pressure_max` DOUBLE NULL DEFAULT 1200.0 ,
  `daily_rain_max` DOUBLE NULL DEFAULT .0 ,
  `monthly_rain_max` DOUBLE NULL DEFAULT 1000.0 ,
  `yearly_rain_max` DOUBLE NULL DEFAULT 10000.0 ,
  `weather_underground_station_id` VARCHAR(100) NULL DEFAULT NULL ,
  `weather_underground_password` VARCHAR(100) NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`) ,
  INDEX `fk_weather_stations_location` (`location_code` ASC) ,
  CONSTRAINT `fk_weather_stations_location`
    FOREIGN KEY (`location_code` )
    REFERENCES `weathersense20`.`locations` (`location_code` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`current_weather`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`current_weather` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`current_weather` (
  `station_id` VARCHAR(20) NOT NULL ,
  `time` DATETIME NOT NULL ,
  `indoor_temp` DOUBLE NULL DEFAULT NULL ,
  `indoor_humidity` FLOAT NULL DEFAULT NULL ,
  `outdoor_temp` DOUBLE NULL DEFAULT NULL ,
  `heat_index` DOUBLE NULL DEFAULT NULL ,
  `wind_chill` DOUBLE NULL DEFAULT NULL ,
  `dew_point` DOUBLE NULL DEFAULT NULL ,
  `outdoor_humidity` FLOAT NULL DEFAULT NULL ,
  `wind_speed` DOUBLE NULL DEFAULT NULL ,
  `wind_speed_10_min_avg` DOUBLE NULL DEFAULT NULL ,
  `wind_speed_2_min_avg` DOUBLE NULL DEFAULT NULL ,
  `wind_dir` FLOAT NULL DEFAULT NULL ,
  `wind_gust` DOUBLE NULL DEFAULT NULL ,
  `wind_dir2` FLOAT NULL DEFAULT NULL ,
  `wind_dir3` FLOAT NULL DEFAULT NULL ,
  `wind_dir4` FLOAT NULL DEFAULT NULL ,
  `wind_dir5` FLOAT NULL DEFAULT NULL ,
  `atm_pressure` DOUBLE NULL DEFAULT NULL ,
  `baro_pressure` DOUBLE NULL DEFAULT NULL ,
  `baro_tendency` VARCHAR(15) NULL DEFAULT NULL ,
  `forecast` VARCHAR(15) NULL DEFAULT NULL ,
  `uv_index` DOUBLE NULL DEFAULT NULL ,
  `uv_dose` DOUBLE NULL DEFAULT NULL ,
  `solar_radiation` DOUBLE NULL DEFAULT NULL ,
  `rain_hour` DOUBLE NULL DEFAULT NULL ,
  `rain_24_hour` DOUBLE NULL DEFAULT NULL ,
  `rain_today` DOUBLE NULL DEFAULT NULL ,
  `rain_month` DOUBLE NULL DEFAULT NULL ,
  `rain_weather_year` DOUBLE NULL DEFAULT NULL ,
  `rain_calendar_year` DOUBLE NULL DEFAULT NULL ,
  `storm_start` DATE NULL DEFAULT NULL ,
  `storm_end` DATE NULL DEFAULT NULL ,
  `storm_rain` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`) ,
  INDEX `fk_current_weather_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_current_weather_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`daily_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`daily_records` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`daily_records` (
  `station_id` VARCHAR(20) NOT NULL ,
  `month` INT(11) NOT NULL ,
  `day` INT(11) NOT NULL ,
  `record_max_high` DOUBLE NULL DEFAULT NULL ,
  `record_max_high_year` INT(4) NULL DEFAULT NULL ,
  `record_min_high` DOUBLE NULL DEFAULT NULL ,
  `record_min_high_year` INT(4) NULL DEFAULT NULL ,
  `record_max_low` DOUBLE NULL DEFAULT NULL ,
  `record_max_low_year` INT(4) NULL DEFAULT NULL ,
  `record_min_low` DOUBLE NULL DEFAULT NULL ,
  `record_min_low_year` INT(4) NULL DEFAULT NULL ,
  `record_rainfall` DOUBLE NULL DEFAULT NULL ,
  `record_rainfall_year` INT(4) NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `month`, `day`) ,
  INDEX `fk_daily_records_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_daily_records_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`daily_seasonal_averages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`daily_seasonal_averages` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`daily_seasonal_averages` (
  `location_code` VARCHAR(20) NOT NULL ,
  `month` INT(2) NOT NULL ,
  `day` INT(2) NOT NULL ,
  `high_temperature` DOUBLE NULL DEFAULT NULL ,
  `low_temperature` DOUBLE NULL DEFAULT NULL ,
  `mean_temperature` DOUBLE NULL DEFAULT NULL ,
  `rainfall` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`month`, `day`, `location_code`) ,
  INDEX `fk_daily_averages_location` (`location_code` ASC) ,
  CONSTRAINT `fk_daily_averages_location`
    FOREIGN KEY (`location_code` )
    REFERENCES `weathersense20`.`locations` (`location_code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`daily_summary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`daily_summary` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`daily_summary` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `total_duration` INT(11) NULL DEFAULT 0 ,
  `outdoor_temp_duration` INT(11) NULL DEFAULT 0 ,
  `high_outdoor_temp` DOUBLE NULL DEFAULT NULL ,
  `high_outdoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `low_outdoor_temp` DOUBLE NULL DEFAULT NULL ,
  `low_outdoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `avg_outdoor_temp` DOUBLE NULL DEFAULT NULL ,
  `high_heat_index` DOUBLE NULL DEFAULT NULL ,
  `high_heat_index_time` DATETIME NULL DEFAULT NULL ,
  `low_wind_chill` DOUBLE NULL DEFAULT NULL ,
  `low_wind_chill_time` DATETIME NULL DEFAULT NULL ,
  `low_dew_point` DOUBLE NULL DEFAULT NULL ,
  `low_dew_point_time` DATETIME NULL DEFAULT NULL ,
  `high_dew_point` DOUBLE NULL DEFAULT NULL ,
  `high_dew_point_time` DATETIME NULL DEFAULT NULL ,
  `indoor_temp_duration` INT(11) NULL DEFAULT 0 ,
  `high_indoor_temp` DOUBLE NULL DEFAULT NULL ,
  `high_indoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `low_indoor_temp` DOUBLE NULL DEFAULT NULL ,
  `low_indoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `avg_indoor_temp` DOUBLE NULL DEFAULT NULL ,
  `outdoor_humid_duration` INT(11) NULL DEFAULT 0 ,
  `high_outdoor_humid` INT(11) NULL DEFAULT NULL ,
  `high_outdoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `low_outdoor_humid` INT(11) NULL DEFAULT NULL ,
  `low_outdoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `avg_outdoor_humid` FLOAT NULL DEFAULT NULL ,
  `indoor_humid_duration` INT(11) NULL DEFAULT 0 ,
  `high_indoor_humid` INT(11) NULL DEFAULT NULL ,
  `high_indoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `low_indoor_humid` INT(11) NULL DEFAULT NULL ,
  `low_indoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `avg_indoor_humid` FLOAT NULL DEFAULT NULL ,
  `pressure_duration` INT(11) NULL DEFAULT 0 ,
  `high_atm_pressure` DOUBLE NULL DEFAULT NULL ,
  `high_baro_pressure` DOUBLE NULL DEFAULT NULL ,
  `high_pressure_time` DATETIME NULL DEFAULT NULL ,
  `low_atm_pressure` DOUBLE NULL DEFAULT NULL ,
  `low_baro_pressure` DOUBLE NULL DEFAULT NULL ,
  `low_pressure_time` DATETIME NULL DEFAULT NULL ,
  `avg_atm_pressure` DOUBLE NULL DEFAULT NULL ,
  `avg_baro_pressure` DOUBLE NULL DEFAULT NULL ,
  `wind_duration` INT(11) NULL DEFAULT 0 ,
  `max_wind_speed` DOUBLE NULL DEFAULT NULL ,
  `max_wind_speed_time` DATETIME NULL DEFAULT NULL ,
  `max_wind_gust` DOUBLE NULL DEFAULT NULL ,
  `max_wind_gust_time` DATETIME NULL DEFAULT NULL ,
  `avg_wind_speed` DOUBLE NULL DEFAULT NULL ,
  `rainfall` DOUBLE NULL DEFAULT NULL ,
  `max_rainfall_rate` DOUBLE NULL DEFAULT NULL ,
  `max_rainfall_rate_time` DATETIME NULL DEFAULT NULL ,
  `comments` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `date`) ,
  INDEX `fk_daily_summary_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_daily_summary_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`doppler_radar_images`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`doppler_radar_images` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`doppler_radar_images` (
  `location_code` VARCHAR(20) NOT NULL ,
  `sequence` INT(11) NOT NULL ,
  `time` DATETIME NULL DEFAULT NULL ,
  `image` LONGBLOB NULL DEFAULT NULL ,
  PRIMARY KEY (`location_code`, `sequence`) ,
  INDEX `fk_doppler_radar_images_location` (`location_code` ASC) ,
  CONSTRAINT `fk_doppler_radar_images_location`
    FOREIGN KEY (`location_code` )
    REFERENCES `weathersense20`.`locations` (`location_code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`history` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`history` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATETIME NOT NULL ,
  `duration` INT(11) NULL DEFAULT 0 ,
  `wind_speed` DOUBLE NULL DEFAULT NULL ,
  `wind_dir` FLOAT NULL DEFAULT NULL ,
  `wind_gust` DOUBLE NULL DEFAULT NULL ,
  `atm_pressure` DOUBLE NULL DEFAULT NULL ,
  `baro_pressure` DOUBLE NULL DEFAULT NULL ,
  `rainfall` DOUBLE NULL DEFAULT NULL ,
  `uv_index` DOUBLE NULL DEFAULT NULL ,
  `uv_dose` DOUBLE NULL DEFAULT NULL ,
  `solar_radiation` DOUBLE NULL DEFAULT NULL ,
  `evapotranspiration` DOUBLE NULL DEFAULT NULL ,
  INDEX `fk_history_weather_stations` (`station_id` ASC) ,
  PRIMARY KEY (`date`, `station_id`) ,
  CONSTRAINT `fk_history_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`rain_hour`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`rain_hour` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`rain_hour` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `hour` INT(11) NOT NULL ,
  `rainfall` DOUBLE NULL DEFAULT 0.0 ,
  PRIMARY KEY (`station_id`, `date`, `hour`) ,
  INDEX `fk_rain_hour_daily_summary` (`station_id` ASC, `date` ASC) ,
  CONSTRAINT `fk_rain_hour_daily_summary`
    FOREIGN KEY (`station_id` , `date` )
    REFERENCES `weathersense20`.`daily_summary` (`station_id` , `date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`rainfall_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`rainfall_records` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`rainfall_records` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `record_type` CHAR(20) NULL DEFAULT NULL ,
  `value` DOUBLE NULL DEFAULT NULL ,
  `previous_date` DATE NULL DEFAULT NULL ,
  `previous_value` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `date`) ,
  INDEX `fk_rainfall_record_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_rainfall_record_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_bins`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_bins` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_bins` (
  `station_id` VARCHAR(20) NOT NULL ,
  `bin_type` VARCHAR(20) NOT NULL ,
  `threshold` DOUBLE NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`station_id`, `bin_type`, `threshold`) ,
  INDEX `fk_temperature_bin_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_temperature_bin_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_bin_durations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_bin_durations` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_bin_durations` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `threshold` DOUBLE NOT NULL ,
  `type` CHAR(20) NOT NULL ,
  `duration` INT(11) NULL DEFAULT 0 ,
  INDEX `fk_temperature_bin_duration_daily_summary` (`station_id` ASC, `date` ASC) ,
  PRIMARY KEY (`station_id`, `date`, `threshold`, `type`) ,
  CONSTRAINT `fk_temperature_bin_duration_daily_summary`
    FOREIGN KEY (`station_id` , `date` )
    REFERENCES `weathersense20`.`daily_summary` (`station_id` , `date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_records`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_records` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_records` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `record_type` CHAR(20) NOT NULL ,
  `value` DOUBLE NULL DEFAULT NULL ,
  `previous_date` DATE NULL DEFAULT NULL ,
  `previous_value` DOUBLE NULL DEFAULT NULL ,
  INDEX `fk_temperature_record_weather_stations` (`station_id` ASC) ,
  PRIMARY KEY (`station_id`, `record_type`, `date`) ,
  CONSTRAINT `fk_temperature_record_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`wind_slices`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`wind_slices` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`wind_slices` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATE NOT NULL ,
  `heading` FLOAT NOT NULL ,
  `arc_length` FLOAT NULL DEFAULT NULL ,
  `slice_duration` INT(11) NULL DEFAULT 0 ,
  `windy_duration` INT(11) NULL DEFAULT 0 ,
  `total_duration` INT(11) NULL DEFAULT 0 ,
  `avg_speed` DOUBLE NULL DEFAULT NULL ,
  `max_speed` DOUBLE NULL DEFAULT NULL ,
  `windy_percentage` FLOAT NULL DEFAULT NULL ,
  `total_percentage` FLOAT NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `heading`, `date`) ,
  INDEX `fk_wind_slice_daily_summary` (`station_id` ASC, `date` ASC) ,
  CONSTRAINT `fk_wind_slice_daily_summary`
    FOREIGN KEY (`station_id` , `date` )
    REFERENCES `weathersense20`.`daily_summary` (`station_id` , `date` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`wind_speed_bins`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`wind_speed_bins` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`wind_speed_bins` (
  `station_id` VARCHAR(20) NOT NULL ,
  `min_bin_value` DOUBLE NOT NULL DEFAULT 0.0 ,
  `max_bin_value` DOUBLE NULL DEFAULT 0.0 ,
  PRIMARY KEY (`station_id`, `min_bin_value`) ,
  INDEX `fk_wind_speed_bin_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_wind_speed_bin_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense20`.`wind_speed_bin_durations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`wind_speed_bin_durations` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`wind_speed_bin_durations` (
  `station_id` VARCHAR(20) NOT NULL ,
  `min_bin_value` DOUBLE NOT NULL ,
  `max_bin_value` DOUBLE NULL ,
  `date` DATE NOT NULL ,
  `heading` FLOAT NOT NULL ,
  `duration` INT(11) NULL DEFAULT 0 ,
  PRIMARY KEY (`station_id`, `date`, `heading`, `min_bin_value`) ,
  INDEX `fk_wind_slice_wind_speed_bin_duration` (`station_id` ASC, `date` ASC, `heading` ASC) ,
  CONSTRAINT `fk_wind_slice_wind_speed_bin_duration`
    FOREIGN KEY (`station_id` , `date` , `heading` )
    REFERENCES `weathersense20`.`wind_slices` (`station_id` , `date` , `heading` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`monthly_seasonal_averages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`monthly_seasonal_averages` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`monthly_seasonal_averages` (
  `location_code` VARCHAR(20) NOT NULL ,
  `month` INT(2) NOT NULL ,
  `high_temperature` DOUBLE NULL ,
  `low_temperature` DOUBLE NULL ,
  `mean_temperature` DOUBLE NULL ,
  `rainfall` DOUBLE NULL ,
  PRIMARY KEY (`location_code`, `month`) ,
  INDEX `fk_monthly_averages_locations` (`location_code` ASC) ,
  CONSTRAINT `fk_monthly_averages_locations`
    FOREIGN KEY (`location_code` )
    REFERENCES `weathersense20`.`locations` (`location_code` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`storms`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`storms` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`storms` (
  `station_id` VARCHAR(20) NOT NULL ,
  `start_time` DATETIME NOT NULL ,
  `duration` INT NOT NULL ,
  `total_rainfall` DOUBLE NOT NULL ,
  PRIMARY KEY (`station_id`, `start_time`) ,
  INDEX `fk_storms_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_storms_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`sensors`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`sensors` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`sensors` (
  `station_id` VARCHAR(20) NOT NULL ,
  `name` VARCHAR(20) NOT NULL ,
  `type` ENUM('T','TH','UV','SR','SM','LW','AN','RC','HY') NOT NULL ,
  PRIMARY KEY (`station_id`, `name`) ,
  INDEX `fk_sensors_weather_stations` (`station_id` ASC) ,
  CONSTRAINT `fk_sensors_weather_stations`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`weather_stations` (`station_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_humidity_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_humidity_history` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_humidity_history` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATETIME NOT NULL ,
  `sensor_name` VARCHAR(20) NOT NULL ,
  `temperature` DOUBLE NULL DEFAULT NULL ,
  `humidity` FLOAT NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `date`, `sensor_name`) ,
  INDEX `fk_temp_humid_history` (`station_id` ASC, `date` ASC) ,
  INDEX `fk_temperature_humidity_history_sensors` (`station_id` ASC, `sensor_name` ASC) ,
  CONSTRAINT `fk_temp_humid_history`
    FOREIGN KEY (`station_id` , `date` )
    REFERENCES `weathersense20`.`history` (`station_id` , `date` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_temperature_humidity_history_sensors`
    FOREIGN KEY (`station_id` , `sensor_name` )
    REFERENCES `weathersense20`.`sensors` (`station_id` , `name` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_history` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_history` (
  `station_id` VARCHAR(20) NOT NULL ,
  `date` DATETIME NOT NULL ,
  `sensor_name` VARCHAR(20) NOT NULL ,
  `temperature` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `date`, `sensor_name`) ,
  INDEX `fk_temp_history` (`station_id` ASC, `date` ASC) ,
  INDEX `fk_temperature_history_sensors` (`station_id` ASC, `sensor_name` ASC) ,
  CONSTRAINT `fk_temp_history`
    FOREIGN KEY (`station_id` , `date` )
    REFERENCES `weathersense20`.`history` (`station_id` , `date` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_temperature_history_sensors`
    FOREIGN KEY (`station_id` , `sensor_name` )
    REFERENCES `weathersense20`.`sensors` (`station_id` , `name` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_current`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_current` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_current` (
  `station_id` VARCHAR(20) NOT NULL ,
  `sensor_name` VARCHAR(20) NOT NULL ,
  `temperature` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `sensor_name`) ,
  INDEX `fk_temperature_current_current_weather` (`station_id` ASC) ,
  INDEX `fk_temperature_current_sensors` (`station_id` ASC, `sensor_name` ASC) ,
  CONSTRAINT `fk_temperature_current_current_weather`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`current_weather` (`station_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_temperature_current_sensors`
    FOREIGN KEY (`station_id` , `sensor_name` )
    REFERENCES `weathersense20`.`sensors` (`station_id` , `name` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense20`.`temperature_humidity_current`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `weathersense20`.`temperature_humidity_current` ;

CREATE  TABLE IF NOT EXISTS `weathersense20`.`temperature_humidity_current` (
  `station_id` VARCHAR(20) NOT NULL ,
  `sensor_name` VARCHAR(20) NOT NULL ,
  `temperature` DOUBLE NULL DEFAULT NULL ,
  `humidity` FLOAT NULL DEFAULT NULL ,
  PRIMARY KEY (`station_id`, `sensor_name`) ,
  INDEX `fk_temperature_humidity_current_current_weather` (`station_id` ASC) ,
  INDEX `fk_temperature_humidity_current_sensors` (`station_id` ASC, `sensor_name` ASC) ,
  CONSTRAINT `fk_temperature_humidity_current_current_weather`
    FOREIGN KEY (`station_id` )
    REFERENCES `weathersense20`.`current_weather` (`station_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_temperature_humidity_current_sensors`
    FOREIGN KEY (`station_id` , `sensor_name` )
    REFERENCES `weathersense20`.`sensors` (`station_id` , `name` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
