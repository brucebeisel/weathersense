SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `weathersense22` ;
CREATE SCHEMA IF NOT EXISTS `weathersense22` ;
USE `weathersense22` ;

-- -----------------------------------------------------
-- Table `weathersense22`.`daily_records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`daily_records` (
  `month` INT(2) NOT NULL ,
  `day` INT(2) NOT NULL ,
  `record_max_high` DECIMAL(6,3) NULL ,
  `record_max_high_year` INT(4) NULL DEFAULT NULL ,
  `record_min_high` DECIMAL(6,3) NULL DEFAULT NULL ,
  `record_min_high_year` INT(4) NULL DEFAULT NULL ,
  `record_max_low` DECIMAL(6,3) NULL DEFAULT NULL ,
  `record_max_low_year` INT(4) NULL DEFAULT NULL ,
  `record_min_low` DECIMAL(6,3) NULL DEFAULT NULL ,
  `record_min_low_year` INT(4) NULL DEFAULT NULL ,
  `record_rainfall` DECIMAL(7,3) NULL DEFAULT NULL ,
  `record_rainfall_year` INT(4) NULL DEFAULT NULL ,
  PRIMARY KEY (`month`, `day`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`daily_seasonal_averages`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`daily_seasonal_averages` (
  `month` INT(2) NOT NULL ,
  `day` INT(2) NOT NULL ,
  `high_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `low_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `mean_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `rainfall` DECIMAL(7,3) NULL DEFAULT NULL ,
  PRIMARY KEY (`month`, `day`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense22`.`daily_summary`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`daily_summary` (
  `date` DATE NOT NULL ,
  `total_duration` INT(5) NULL DEFAULT 0 ,
  `outdoor_temp_duration` INT(5) NULL DEFAULT 0 ,
  `high_outdoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_outdoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `low_outdoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `low_outdoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `avg_outdoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_heat_index` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_heat_index_time` DATETIME NULL DEFAULT NULL ,
  `low_wind_chill` DECIMAL(6,3) NULL DEFAULT NULL ,
  `low_wind_chill_time` DATETIME NULL DEFAULT NULL ,
  `low_dew_point` DECIMAL(6,3) NULL DEFAULT NULL ,
  `low_dew_point_time` DATETIME NULL DEFAULT NULL ,
  `high_dew_point` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_dew_point_time` DATETIME NULL DEFAULT NULL ,
  `indoor_temp_duration` INT(5) NULL DEFAULT 0 ,
  `high_indoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_indoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `low_indoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `low_indoor_temp_time` DATETIME NULL DEFAULT NULL ,
  `avg_indoor_temp` DECIMAL(6,3) NULL DEFAULT NULL ,
  `outdoor_humid_duration` INT(5) NULL DEFAULT 0 ,
  `high_outdoor_humid` TINYINT NULL DEFAULT NULL ,
  `high_outdoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `low_outdoor_humid` TINYINT NULL DEFAULT NULL ,
  `low_outdoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `avg_outdoor_humid` TINYINT NULL DEFAULT NULL ,
  `indoor_humid_duration` INT(5) NULL DEFAULT 0 ,
  `high_indoor_humid` TINYINT NULL DEFAULT NULL ,
  `high_indoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `low_indoor_humid` TINYINT NULL DEFAULT NULL ,
  `low_indoor_humid_time` DATETIME NULL DEFAULT NULL ,
  `avg_indoor_humid` TINYINT NULL DEFAULT NULL ,
  `baro_pressure_duration` INT(5) NULL DEFAULT 0 ,
  `high_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL ,
  `high_baro_pressure_time` DATETIME NULL DEFAULT NULL ,
  `low_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL ,
  `low_baro_pressure_time` DATETIME NULL DEFAULT NULL ,
  `avg_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL ,
  `wind_duration` INT(5) NULL DEFAULT 0 ,
  `max_wind_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `max_wind_speed_time` DATETIME NULL DEFAULT NULL ,
  `max_wind_gust` DECIMAL(4,2) NULL DEFAULT NULL ,
  `max_wind_gust_time` DATETIME NULL DEFAULT NULL ,
  `avg_wind_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `rainfall` DECIMAL(7,3) NULL DEFAULT NULL ,
  `max_rainfall_rate` DECIMAL(7,3) NULL DEFAULT NULL ,
  `max_rainfall_rate_time` DATETIME NULL DEFAULT NULL ,
  `avg_solar_radiation` DECIMAL(4) NULL DEFAULT NULL ,
  `solar_radiation_duration` INT(5) NULL DEFAULT NULL ,
  `max_solar_radiation` DECIMAL(4) NULL DEFAULT NULL ,
  `max_solar_radiation_time` DATETIME NULL DEFAULT NULL ,
  `total_evapotranspiration` DECIMAL(7,3) NULL DEFAULT NULL ,
  `avg_uv_index` DECIMAL(4,1) NULL DEFAULT NULL ,
  `uv_duration` INT(5) NULL DEFAULT NULL ,
  `max_uv_index` DECIMAL(3) NULL DEFAULT NULL ,
  `max_uv_index_time` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`date`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`doppler_radar_images`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`doppler_radar_images` (
  `sequence` INT(11) NOT NULL ,
  `time` DATETIME NULL DEFAULT NULL ,
  `image` LONGBLOB NULL DEFAULT NULL ,
  PRIMARY KEY (`sequence`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`history`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`history` (
  `date` DATETIME NOT NULL ,
  `duration` INT(5) NULL DEFAULT 0 ,
  `low_outdoor_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `avg_outdoor_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `high_outdoor_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `outdoor_humidity` INT(3) NULL DEFAULT NULL ,
  `indoor_temperature` DECIMAL(6,3) NULL DEFAULT NULL ,
  `indoor_humidity` INT(3) NULL DEFAULT NULL ,
  `avg_wind_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `prevailing_wind_direction` DECIMAL(4,1) NULL DEFAULT NULL ,
  `high_wind_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `high_wind_direction` DECIMAL(4,1) NULL DEFAULT NULL ,
  `wind_gust` DECIMAL(4,2) NULL DEFAULT NULL ,
  `wind_gust_direction` DECIMAL(4,1) NULL DEFAULT NULL ,
  `baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL ,
  `rainfall` DECIMAL(7,3) NULL DEFAULT NULL ,
  `high_rain_rate` DECIMAL(7,3) NULL DEFAULT NULL ,
  `avg_uv_index` DECIMAL(3,1) NULL DEFAULT NULL ,
  `high_uv_index` DECIMAL(3,1) NULL DEFAULT NULL ,
  `avg_solar_radiation` DOUBLE NULL DEFAULT NULL ,
  `high_solar_radiation` DOUBLE NULL DEFAULT NULL ,
  `evapotranspiration` DOUBLE NULL DEFAULT NULL ,
  PRIMARY KEY (`date`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`rain_hour`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`rain_hour` (
  `date` DATE NOT NULL ,
  `day_hour` INT(2) NOT NULL ,
  `rainfall` DECIMAL(7,3) NULL DEFAULT 0.0 ,
  PRIMARY KEY (`date`, `day_hour`) ,
  INDEX `fk_rain_hour_daily_summary_idx` (`date` ASC) ,
  CONSTRAINT `fk_rain_hour_daily_summary`
    FOREIGN KEY (`date` )
    REFERENCES `weathersense22`.`daily_summary` (`date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`rainfall_records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`rainfall_records` (
  `date` DATE NOT NULL ,
  `record_type` CHAR(20) NULL DEFAULT NULL ,
  `value` DECIMAL(7,3) NULL DEFAULT NULL ,
  `previous_date` DATE NULL DEFAULT NULL ,
  `previous_value` DECIMAL(7,3) NULL DEFAULT NULL ,
  PRIMARY KEY (`date`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense22`.`temperature_bins`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`temperature_bins` (
  `bin_type` VARCHAR(20) NOT NULL ,
  `threshold` DECIMAL(6,3) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`bin_type`, `threshold`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense22`.`temperature_bin_durations`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`temperature_bin_durations` (
  `date` DATE NOT NULL ,
  `threshold` DECIMAL(6,3) NOT NULL ,
  `type` VARCHAR(20) NOT NULL ,
  `duration` INT(5) NULL DEFAULT 0 ,
  INDEX `fk_temperature_bin_duration_daily_summary_idx` (`date` ASC) ,
  PRIMARY KEY (`date`, `threshold`, `type`) ,
  CONSTRAINT `fk_temperature_bin_duration_daily_summary`
    FOREIGN KEY (`date` )
    REFERENCES `weathersense22`.`daily_summary` (`date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense22`.`temperature_records`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`temperature_records` (
  `date` DATE NOT NULL ,
  `record_type` CHAR(20) NOT NULL ,
  `value` DECIMAL(6,3) NULL DEFAULT NULL ,
  `previous_date` DATE NULL DEFAULT NULL ,
  `previous_value` DECIMAL(6,3) NULL DEFAULT NULL ,
  PRIMARY KEY (`record_type`, `date`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `weathersense22`.`weather_station`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`weather_station` (
  `singleton_id` INT NOT NULL DEFAULT 0 ,
  `manufacturer` VARCHAR(100) NULL ,
  `model` VARCHAR(100) NULL ,
  `firmware_date` VARCHAR(45) NULL ,
  `firmware_version` VARCHAR(45) NULL ,
  `location_code` VARCHAR(20) NULL ,
  `location_description` VARCHAR(100) NULL ,
  `latitude` DECIMAL(9,6) NOT NULL DEFAULT 0.0 ,
  `longitude` DECIMAL(9,6) NOT NULL DEFAULT 0.0 ,
  `altitude` DECIMAL(6,1) NOT NULL DEFAULT 0.0 ,
  `weather_year_start_month` INT(2) NULL DEFAULT 1 ,
  `wind_slice_count` INT(2) NOT NULL DEFAULT 16 ,
  `wind_speed_bin_interval` DECIMAL(4,2) NULL DEFAULT 5.0 ,
  `num_wind_speed_bins` INT(1) NULL DEFAULT 4 ,
  `thermometer_min` DECIMAL(6,3) NULL DEFAULT -10.0 ,
  `thermometer_max` DECIMAL(6,3) NULL DEFAULT 50.0 ,
  `atm_pressure_min` DECIMAL(6,2) NULL DEFAULT 900.0 ,
  `atm_pressure_max` DECIMAL(6,2) NULL DEFAULT 1200.0 ,
  `daily_rain_max` DECIMAL(7,3) NULL DEFAULT .0 ,
  `monthly_rain_max` DECIMAL(7,3) NULL DEFAULT 1000.0 ,
  `yearly_rain_max` DECIMAL(7,3) NULL DEFAULT 9999.0 ,
  `weather_underground_station_id` VARCHAR(100) NULL DEFAULT NULL ,
  `weather_underground_password` VARCHAR(100) NULL DEFAULT NULL ,
  `doppler_radar_url` VARCHAR(100) NULL DEFAULT NULL ,
  UNIQUE INDEX `singleton_id_UNIQUE` (`singleton_id` ASC) ,
  PRIMARY KEY (`singleton_id`) )
AUTO_INCREMENT = 0;


-- -----------------------------------------------------
-- Table `weathersense22`.`wind_slices`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`wind_slices` (
  `date` DATE NOT NULL ,
  `heading` DECIMAL(4,1) NOT NULL ,
  `arc_length` FLOAT NULL DEFAULT NULL ,
  `slice_duration` INT(5) NULL DEFAULT 0 ,
  `windy_duration` INT(5) NULL DEFAULT 0 ,
  `total_duration` INT(5) NULL DEFAULT 0 ,
  `avg_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `max_speed` DECIMAL(4,2) NULL DEFAULT NULL ,
  `windy_percentage` DECIMAL(4,1) NULL DEFAULT NULL ,
  `total_percentage` DECIMAL(4,1) NULL DEFAULT NULL ,
  PRIMARY KEY (`heading`, `date`) ,
  INDEX `fk_wind_slice_daily_summary_idx` (`date` ASC) ,
  CONSTRAINT `fk_wind_slice_daily_summary`
    FOREIGN KEY (`date` )
    REFERENCES `weathersense22`.`daily_summary` (`date` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`wind_speed_bin_durations`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`wind_speed_bin_durations` (
  `date` DATE NOT NULL ,
  `heading` DECIMAL(4,1) NOT NULL ,
  `min_bin_value` DECIMAL(6,3) NOT NULL ,
  `max_bin_value` DECIMAL(6,3) NULL ,
  `duration` INT(5) NULL DEFAULT 0 ,
  PRIMARY KEY (`date`, `heading`, `min_bin_value`) ,
  INDEX `fk_wind_slice_wind_speed_bin_duration_idx` (`date` ASC, `heading` ASC) ,
  CONSTRAINT `fk_wind_slice_wind_speed_bin_duration`
    FOREIGN KEY (`date` , `heading` )
    REFERENCES `weathersense22`.`wind_slices` (`date` , `heading` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`monthly_seasonal_averages`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`monthly_seasonal_averages` (
  `month` INT(2) NOT NULL ,
  `high_temperature` DECIMAL(6,3) NULL ,
  `low_temperature` DECIMAL(6,3) NULL ,
  `mean_temperature` DECIMAL(6,3) NULL ,
  `rainfall` DECIMAL(7,3) NULL ,
  PRIMARY KEY (`month`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`storms`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`storms` (
  `start_time` DATE NOT NULL ,
  `total_rainfall` DECIMAL(7,3) NOT NULL ,
  PRIMARY KEY (`start_time`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`sensors`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`sensors` (
  `sensor_id` INT(2) NOT NULL COMMENT 'Generated ID.' ,
  `type` VARCHAR(10) NOT NULL ,
  `name` VARCHAR(100) NULL COMMENT 'The name that will be displayed by the GUI.' ,
  PRIMARY KEY (`sensor_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`sensor_value_history`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`sensor_value_history` (
  `date` DATETIME NOT NULL ,
  `sensor_id` INT(2) NOT NULL ,
  `sensor_type` ENUM('TH','HY','LW','SM','LT','ST','UV','SR') NOT NULL ,
  `measurement` DOUBLE NULL ,
  PRIMARY KEY (`date`, `sensor_id`) ,
  INDEX `fk_temp_humid_history_idx` (`date` ASC) ,
  INDEX `fk_sensor_value_history_sensors_idx` (`sensor_id` ASC) ,
  CONSTRAINT `fk_sensor_value_history`
    FOREIGN KEY (`date` )
    REFERENCES `weathersense22`.`history` (`date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sensor_value_history_sensors`
    FOREIGN KEY (`sensor_id` )
    REFERENCES `weathersense22`.`sensors` (`sensor_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`sensor_stations`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`sensor_stations` (
  `sensor_station_id` INT(1) NOT NULL ,
  `type` ENUM('ISS','TEMP','TEMP/HUMID','LEAF WETNESS','SOIL MOISTURE','LW/SM') NOT NULL ,
  `name` VARCHAR(20) NULL ,
  PRIMARY KEY (`sensor_station_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`storm_doppler_images`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`storm_doppler_images` (
  `storm_start` DATE NOT NULL ,
  `image_time` DATETIME NOT NULL ,
  `image` LONGBLOB NOT NULL ,
  PRIMARY KEY (`image_time`) ,
  INDEX `fk_storm_doppler_images_storms1_idx` (`storm_start` ASC) ,
  CONSTRAINT `fk_storm_doppler_images_storms`
    FOREIGN KEY (`storm_start` )
    REFERENCES `weathersense22`.`storms` (`start_time` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`collector_commands`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`collector_commands` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `command` VARCHAR(100) NOT NULL ,
  `state` ENUM('NEW','EXECUTING','COMPLETE') NULL DEFAULT 'NEW' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`sensor_value_summary`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`sensor_value_summary` (
  `date` DATE NOT NULL ,
  `sensor_id` INT(2) NOT NULL ,
  `duration` INT(2) NOT NULL ,
  `sensor_type` ENUM('TH','HY','LW','SM','LT','ST','UV','SR') NOT NULL ,
  `high_measurement` DECIMAL(6,3) NULL ,
  `high_measurement_time` DATETIME NULL ,
  `low_measurement` DECIMAL(6,3) NULL ,
  `low_measurement_time` DATETIME NULL ,
  `avg_measurement` DECIMAL(6,3) NULL ,
  PRIMARY KEY (`date`, `sensor_id`) ,
  INDEX `fk_sensor_value_summary_idx` (`date` ASC) ,
  INDEX `fk_sensor_value_summary_sensor_idx` (`sensor_id` ASC) ,
  CONSTRAINT `fk_sensor_value_summary`
    FOREIGN KEY (`date` )
    REFERENCES `weathersense22`.`daily_summary` (`date` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sensor_value_summary_sensor`
    FOREIGN KEY (`sensor_id` )
    REFERENCES `weathersense22`.`sensors` (`sensor_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`sensor_station_status`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`sensor_station_status` (
  `sensor_station_id` INT NOT NULL ,
  `time` TIMESTAMP NOT NULL ,
  `battery_voltage` FLOAT NULL ,
  `battery_ok` TINYINT(1) NULL ,
  `link_quality_percentage` INT NULL ,
  PRIMARY KEY (`sensor_station_id`, `time`) ,
  INDEX `fk_sensor_station_status_sensor_stations1_idx` (`sensor_station_id` ASC) ,
  CONSTRAINT `fk_sensor_station_status_sensor_stations1`
    FOREIGN KEY (`sensor_station_id` )
    REFERENCES `weathersense22`.`sensor_stations` (`sensor_station_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense22`.`weather_station_parameters`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `weathersense22`.`weather_station_parameters` (
  `key` VARCHAR(100) NOT NULL ,
  `value` VARCHAR(200) NOT NULL ,
  PRIMARY KEY (`key`) )
ENGINE = InnoDB;

USE `weathersense22` ;

CREATE USER 'weather';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
