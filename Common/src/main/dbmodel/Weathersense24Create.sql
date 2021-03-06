-- MySQL Script generated by MySQL Workbench
-- 12/21/14 19:08:40
-- Model: WeatherSense    Version: 2.4
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema weathersense24
-- -----------------------------------------------------
-- Database schema for WeatherSense 2.4
DROP SCHEMA IF EXISTS `weathersense24` ;

-- -----------------------------------------------------
-- Schema weathersense24
--
-- Database schema for WeatherSense 2.4
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `weathersense24` ;
USE `weathersense24` ;

-- -----------------------------------------------------
-- Table `weathersense24`.`daily_records`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`daily_records` (
  `month` INT(2) NOT NULL,
  `day` INT(2) NOT NULL,
  `record_max_high` DECIMAL(7,4) NULL,
  `record_max_high_year` INT(4) NULL DEFAULT NULL,
  `record_min_high` DECIMAL(7,4) NULL DEFAULT NULL,
  `record_min_high_year` INT(4) NULL DEFAULT NULL,
  `record_max_low` DECIMAL(7,4) NULL DEFAULT NULL,
  `record_max_low_year` INT(4) NULL DEFAULT NULL,
  `record_min_low` DECIMAL(7,4) NULL DEFAULT NULL,
  `record_min_low_year` INT(4) NULL DEFAULT NULL,
  `record_rainfall` DECIMAL(7,3) NULL DEFAULT NULL,
  `record_rainfall_year` INT(4) NULL DEFAULT NULL,
  PRIMARY KEY (`month`, `day`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`daily_seasonal_averages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`daily_seasonal_averages` (
  `month` INT(2) NOT NULL,
  `day` INT(2) NOT NULL,
  `high_temperature` DECIMAL(7,4) NOT NULL,
  `low_temperature` DECIMAL(7,4) NOT NULL,
  `mean_temperature` DECIMAL(7,4) NOT NULL,
  `rainfall` DECIMAL(7,3) NOT NULL,
  PRIMARY KEY (`month`, `day`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`daily_summary`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`daily_summary` (
  `date` DATE NOT NULL,
  `total_duration` INT(5) NULL DEFAULT 0,
  `outdoor_temp_duration` INT(5) NULL DEFAULT 0,
  `high_outdoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_outdoor_temp_time` DATETIME NULL DEFAULT NULL,
  `low_outdoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `low_outdoor_temp_time` DATETIME NULL DEFAULT NULL,
  `avg_outdoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_heat_index` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_heat_index_time` DATETIME NULL DEFAULT NULL,
  `low_wind_chill` DECIMAL(7,4) NULL DEFAULT NULL,
  `low_wind_chill_time` DATETIME NULL DEFAULT NULL,
  `low_dew_point` DECIMAL(7,4) NULL DEFAULT NULL,
  `low_dew_point_time` DATETIME NULL DEFAULT NULL,
  `high_dew_point` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_dew_point_time` DATETIME NULL DEFAULT NULL,
  `indoor_temp_duration` INT(5) NULL DEFAULT 0,
  `high_indoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_indoor_temp_time` DATETIME NULL DEFAULT NULL,
  `low_indoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `low_indoor_temp_time` DATETIME NULL DEFAULT NULL,
  `avg_indoor_temp` DECIMAL(7,4) NULL DEFAULT NULL,
  `outdoor_humid_duration` INT(5) NULL DEFAULT 0,
  `high_outdoor_humid` TINYINT NULL DEFAULT NULL,
  `high_outdoor_humid_time` DATETIME NULL DEFAULT NULL,
  `low_outdoor_humid` TINYINT NULL DEFAULT NULL,
  `low_outdoor_humid_time` DATETIME NULL DEFAULT NULL,
  `avg_outdoor_humid` TINYINT NULL DEFAULT NULL,
  `indoor_humid_duration` INT(5) NULL DEFAULT 0,
  `high_indoor_humid` TINYINT NULL DEFAULT NULL,
  `high_indoor_humid_time` DATETIME NULL DEFAULT NULL,
  `low_indoor_humid` TINYINT NULL DEFAULT NULL,
  `low_indoor_humid_time` DATETIME NULL DEFAULT NULL,
  `avg_indoor_humid` TINYINT NULL DEFAULT NULL,
  `baro_pressure_duration` INT(5) NULL DEFAULT 0,
  `high_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL,
  `high_baro_pressure_time` DATETIME NULL DEFAULT NULL,
  `low_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL,
  `low_baro_pressure_time` DATETIME NULL DEFAULT NULL,
  `avg_baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL,
  `wind_duration` INT(5) NULL DEFAULT 0,
  `max_wind_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `max_wind_speed_time` DATETIME NULL DEFAULT NULL,
  `max_wind_gust` DECIMAL(5,2) NULL DEFAULT NULL,
  `max_wind_gust_time` DATETIME NULL DEFAULT NULL,
  `avg_wind_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `rainfall` DECIMAL(7,3) NULL DEFAULT NULL,
  `max_rainfall_rate` DECIMAL(7,3) NULL DEFAULT NULL,
  `max_rainfall_rate_time` DATETIME NULL DEFAULT NULL,
  `avg_solar_radiation` DECIMAL(4) NULL DEFAULT NULL,
  `solar_radiation_duration` INT(5) NULL DEFAULT NULL,
  `max_solar_radiation` DECIMAL(4) NULL DEFAULT NULL,
  `max_solar_radiation_time` DATETIME NULL DEFAULT NULL,
  `total_evapotranspiration` DECIMAL(7,3) NULL DEFAULT NULL,
  `avg_uv_index` DECIMAL(3,1) NULL DEFAULT NULL,
  `uv_duration` INT(5) NULL DEFAULT NULL,
  `max_uv_index` DECIMAL(3,1) NULL DEFAULT NULL,
  `max_uv_index_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`date`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`doppler_radar_images`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`doppler_radar_images` (
  `sequence` INT(11) NOT NULL,
  `time` DATETIME NULL DEFAULT NULL,
  `image` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`sequence`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`history` (
  `date` DATETIME NOT NULL,
  `duration` INT(5) NULL DEFAULT 0,
  `low_outdoor_temperature` DECIMAL(7,4) NULL DEFAULT NULL,
  `avg_outdoor_temperature` DECIMAL(7,4) NULL DEFAULT NULL,
  `high_outdoor_temperature` DECIMAL(7,4) NULL DEFAULT NULL,
  `outdoor_humidity` TINYINT NULL DEFAULT NULL,
  `indoor_temperature` DECIMAL(7,4) NULL DEFAULT NULL,
  `indoor_humidity` TINYINT NULL DEFAULT NULL,
  `avg_wind_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `prevailing_wind_direction` DECIMAL(4,1) NULL DEFAULT NULL,
  `high_wind_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `high_wind_direction` DECIMAL(4,1) NULL DEFAULT NULL,
  `wind_gust` DECIMAL(5,2) NULL DEFAULT NULL,
  `wind_gust_direction` DECIMAL(4,1) NULL DEFAULT NULL,
  `baro_pressure` DECIMAL(6,2) NULL DEFAULT NULL,
  `rainfall` DECIMAL(7,3) NULL DEFAULT NULL,
  `high_rain_rate` DECIMAL(7,3) NULL DEFAULT NULL,
  `avg_uv_index` DECIMAL(3,1) NULL DEFAULT NULL,
  `high_uv_index` DECIMAL(3,1) NULL DEFAULT NULL,
  `avg_solar_radiation` DECIMAL(4) NULL DEFAULT NULL,
  `high_solar_radiation` DECIMAL(4) NULL DEFAULT NULL,
  `evapotranspiration` DECIMAL(7,3) NULL DEFAULT NULL,
  PRIMARY KEY (`date`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`rain_hour`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`rain_hour` (
  `date` DATE NOT NULL,
  `day_hour` INT(2) NOT NULL,
  `rainfall` DECIMAL(7,3) NULL DEFAULT 0.0,
  PRIMARY KEY (`date`, `day_hour`),
  INDEX `fk_rain_hour_daily_summary_idx` (`date` ASC),
  CONSTRAINT `fk_rain_hour_daily_summary`
    FOREIGN KEY (`date`)
    REFERENCES `weathersense24`.`daily_summary` (`date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`rainfall_records`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`rainfall_records` (
  `date` DATE NOT NULL,
  `record_type` CHAR(20) NOT NULL,
  `value` DECIMAL(7,3) NOT NULL,
  `previous_date` DATE NULL DEFAULT NULL,
  `previous_value` DECIMAL(7,3) NULL DEFAULT NULL,
  PRIMARY KEY (`date`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`temperature_bins`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`temperature_bins` (
  `bin_id` INT(2) NOT NULL AUTO_INCREMENT,
  `bin_type` VARCHAR(20) NOT NULL,
  `threshold` DECIMAL(7,4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`bin_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`temperature_bin_durations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`temperature_bin_durations` (
  `date` DATE NOT NULL,
  `temperature_bin_id` INT(2) NOT NULL,
  `duration` INT(5) NOT NULL DEFAULT 0,
  INDEX `fk_temperature_bin_duration_daily_summary_idx` (`date` ASC),
  PRIMARY KEY (`date`, `temperature_bin_id`),
  INDEX `fk_temperature_bin_durations_temperature_bins1_idx` (`temperature_bin_id` ASC),
  CONSTRAINT `fk_temperature_bin_duration_daily_summary`
    FOREIGN KEY (`date`)
    REFERENCES `weathersense24`.`daily_summary` (`date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_temperature_bin_durations_temperature_bins1`
    FOREIGN KEY (`temperature_bin_id`)
    REFERENCES `weathersense24`.`temperature_bins` (`bin_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`temperature_records`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`temperature_records` (
  `date` DATE NOT NULL,
  `record_type` CHAR(20) NOT NULL,
  `value` DECIMAL(7,4) NOT NULL,
  `previous_date` DATE NULL DEFAULT NULL,
  `previous_value` DECIMAL(7,4) NULL DEFAULT NULL,
  PRIMARY KEY (`record_type`, `date`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`weather_station`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`weather_station` (
  `singleton_id` INT NOT NULL DEFAULT 0,
  `manufacturer` VARCHAR(100) NULL,
  `model` VARCHAR(100) NULL,
  `firmware_date` VARCHAR(45) NULL,
  `firmware_version` VARCHAR(45) NULL,
  `location_code` VARCHAR(20) NULL,
  `location_description` VARCHAR(100) NULL,
  `latitude` DECIMAL(9,6) NOT NULL DEFAULT 0.0,
  `longitude` DECIMAL(9,6) NOT NULL DEFAULT 0.0,
  `altitude` DECIMAL(6,1) NOT NULL DEFAULT 0.0,
  `weather_year_start_month` INT(2) NULL DEFAULT 1,
  `wind_slice_count` INT(2) NOT NULL DEFAULT 16,
  `wind_speed_bin_interval` DECIMAL(5,2) NULL DEFAULT 5.0,
  `num_wind_speed_bins` INT(1) NULL DEFAULT 4,
  `thermometer_min` DECIMAL(7,4) NULL DEFAULT -10.0,
  `thermometer_max` DECIMAL(7,4) NULL DEFAULT 50.0,
  `atm_pressure_min` DECIMAL(6,2) NULL DEFAULT 900.0,
  `atm_pressure_max` DECIMAL(6,2) NULL DEFAULT 1200.0,
  `daily_rain_max` DECIMAL(7,3) NULL DEFAULT .0,
  `monthly_rain_max` DECIMAL(7,3) NULL DEFAULT 1000.0,
  `yearly_rain_max` DECIMAL(7,3) NULL DEFAULT 9999.0,
  `weather_underground_station_id` VARCHAR(100) NULL DEFAULT NULL,
  `weather_underground_password` VARCHAR(100) NULL DEFAULT NULL,
  `doppler_radar_url` VARCHAR(100) NULL DEFAULT NULL,
  UNIQUE INDEX `singleton_id_UNIQUE` (`singleton_id` ASC),
  PRIMARY KEY (`singleton_id`))
AUTO_INCREMENT = 0;


-- -----------------------------------------------------
-- Table `weathersense24`.`wind_slices`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`wind_slices` (
  `date` DATE NOT NULL,
  `wind_heading_index` INT(2) NOT NULL,
  `slice_duration` INT(5) NULL DEFAULT 0,
  `windy_duration` INT(5) NULL DEFAULT 0,
  `total_duration` INT(5) NULL DEFAULT 0,
  `avg_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `max_speed` DECIMAL(5,2) NULL DEFAULT NULL,
  `windy_percentage` DECIMAL(4,1) NULL DEFAULT NULL,
  `total_percentage` DECIMAL(4,1) NULL DEFAULT NULL,
  PRIMARY KEY (`wind_heading_index`, `date`),
  INDEX `fk_wind_slice_daily_summary_idx` (`date` ASC),
  CONSTRAINT `fk_wind_slice_daily_summary`
    FOREIGN KEY (`date`)
    REFERENCES `weathersense24`.`daily_summary` (`date`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`wind_speed_bin_durations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`wind_speed_bin_durations` (
  `date` DATE NOT NULL,
  `wind_heading_index` INT(2) NOT NULL,
  `wind_speed_bin_index` INT(2) NOT NULL,
  `duration` INT(5) NOT NULL DEFAULT 0,
  PRIMARY KEY (`date`, `wind_heading_index`, `wind_speed_bin_index`),
  CONSTRAINT `fk_wind_slice_wind_speed_bin_duration`
    FOREIGN KEY (`date` , `wind_heading_index`)
    REFERENCES `weathersense24`.`wind_slices` (`date` , `wind_heading_index`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`monthly_seasonal_averages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`monthly_seasonal_averages` (
  `month` INT(2) NOT NULL,
  `high_temperature` DECIMAL(7,4) NOT NULL,
  `low_temperature` DECIMAL(7,4) NOT NULL,
  `mean_temperature` DECIMAL(7,4) NOT NULL,
  `rainfall` DECIMAL(7,3) NOT NULL,
  PRIMARY KEY (`month`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`storms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`storms` (
  `storm_start` DATETIME NOT NULL,
  `storm_end` DATETIME NULL,
  `total_rainfall` DECIMAL(7,3) NOT NULL,
  PRIMARY KEY (`storm_start`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`sensors`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`sensors` (
  `sensor_id` INT(2) NOT NULL COMMENT 'Generated ID.',
  `type` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NULL COMMENT 'The name that will be displayed by the GUI.',
  PRIMARY KEY (`sensor_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`sensor_value_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`sensor_value_history` (
  `date` DATETIME NOT NULL,
  `sensor_id` INT(2) NOT NULL,
  `sensor_type` ENUM('TH','HY','LW','SM','LT','ST','UV','SR') NOT NULL,
  `measurement` DOUBLE NULL,
  PRIMARY KEY (`date`, `sensor_id`),
  INDEX `fk_sensor_value_history_idx` (`date` ASC),
  INDEX `fk_sensor_value_history_sensors_idx` (`sensor_id` ASC),
  CONSTRAINT `fk_sensor_value_history`
    FOREIGN KEY (`date`)
    REFERENCES `weathersense24`.`history` (`date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sensor_value_history_sensors`
    FOREIGN KEY (`sensor_id`)
    REFERENCES `weathersense24`.`sensors` (`sensor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`sensor_stations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`sensor_stations` (
  `sensor_station_id` INT(1) NOT NULL,
  `type` ENUM('ISS','TEMP','TEMP/HUMID','LEAF WETNESS','SOIL MOISTURE','LW/SM') NOT NULL,
  `name` VARCHAR(20) NULL,
  PRIMARY KEY (`sensor_station_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`storm_doppler_images`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`storm_doppler_images` (
  `storm_start` DATETIME NOT NULL,
  `image_time` DATETIME NOT NULL,
  `image` LONGBLOB NOT NULL,
  PRIMARY KEY (`storm_start`, `image_time`),
  CONSTRAINT `fk_storm_doppler_images_storms`
    FOREIGN KEY (`storm_start`)
    REFERENCES `weathersense24`.`storms` (`storm_start`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`collector_commands`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`collector_commands` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `command` VARCHAR(100) NOT NULL,
  `state` ENUM('NEW','EXECUTING','COMPLETE') NULL DEFAULT 'NEW',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`sensor_value_summary`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`sensor_value_summary` (
  `date` DATE NOT NULL,
  `sensor_id` INT(2) NOT NULL,
  `duration` INT(5) NOT NULL,
  `sensor_type` ENUM('TH','HY','LW','SM','LT','ST','UV','SR') NOT NULL,
  `high_measurement` DOUBLE NULL,
  `high_measurement_time` DATETIME NULL,
  `low_measurement` DOUBLE NULL,
  `low_measurement_time` DATETIME NULL,
  `avg_measurement` DOUBLE NULL,
  PRIMARY KEY (`date`, `sensor_id`),
  INDEX `fk_sensor_value_summary_idx` (`date` ASC),
  INDEX `fk_sensor_value_summary_sensor_idx` (`sensor_id` ASC),
  CONSTRAINT `fk_sensor_value_summary`
    FOREIGN KEY (`date`)
    REFERENCES `weathersense24`.`daily_summary` (`date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sensor_value_summary_sensor`
    FOREIGN KEY (`sensor_id`)
    REFERENCES `weathersense24`.`sensors` (`sensor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`sensor_station_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`sensor_station_status` (
  `sensor_station_id` INT NOT NULL,
  `time` TIMESTAMP NOT NULL,
  `battery_voltage` FLOAT NULL,
  `battery_ok` TINYINT(1) NULL,
  `link_quality_percentage` INT NULL,
  PRIMARY KEY (`sensor_station_id`, `time`),
  INDEX `fk_sensor_station_status_sensor_stations1_idx` (`sensor_station_id` ASC),
  CONSTRAINT `fk_sensor_station_status_sensor_stations1`
    FOREIGN KEY (`sensor_station_id`)
    REFERENCES `weathersense24`.`sensor_stations` (`sensor_station_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `weathersense24`.`weather_station_parameters`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `weathersense24`.`weather_station_parameters` (
  `key` VARCHAR(100) NOT NULL,
  `value` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`key`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
