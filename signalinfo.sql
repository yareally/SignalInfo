SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`device_info`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`device_info` (
  `device_id` INT NOT NULL AUTO_INCREMENT ,
  `os_version` REAL NOT NULL ,
  `device_name` TEXT NOT NULL ,
  `device_model` TEXT NOT NULL ,
  `device_oem` TEXT NOT NULL ,
  `device_product` TEXT NOT NULL ,
  `device_device` TEXT NOT NULL ,
  `build_id` TEXT NOT NULL ,
  `carrier_name` TEXT NOT NULL ,
  PRIMARY KEY (`device_id`) )



-- -----------------------------------------------------
-- Table `mydb`.`network_location`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`network_location` (
  `location_id` INT NOT NULL AUTO_INCREMENT ,
  `location_name` TEXT NOT NULL DEFAULT 'n/a' COMMENT 'some city' ,
  `location_region` TEXT NOT NULL DEFAULT 'n/a' COMMENT 'some state' ,
  `tower_lat` REAL NOT NULL DEFAULT 0 ,
  `tower_long` REAL NOT NULL DEFAULT 0 ,
  `uploaded` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`location_id`) )



-- -----------------------------------------------------
-- Table `mydb`.`cdma_stats`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`cdma_stats` (
  `cdma_stat_id` INT NOT NULL AUTO_INCREMENT ,
  `location_id` INT NOT NULL ,
  `cdma_rssi` INT NOT NULL DEFAULT 0 ,
  `cdma_ecio` INT NOT NULL DEFAULT 0 ,
  `uploaded` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cdma_stat_id`) ,
  INDEX `fk_cdma_stats_network_location` (`location_id` ASC) ,
  CONSTRAINT `fk_cdma_stats_network_location`
    FOREIGN KEY (`location_id` )
    REFERENCES `mydb`.`network_location` (`location_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



-- -----------------------------------------------------
-- Table `mydb`.`lte_stats`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`lte_stats` (
  `lte_stat_id` INT NOT NULL AUTO_INCREMENT ,
  `location_id` INT NOT NULL ,
  `lte_rsrp` INT NOT NULL DEFAULT 0 ,
  `lte_rssi` INT NOT NULL DEFAULT 0 ,
  `lte_rsrq` INT NOT NULL DEFAULT 0 ,
  `lte_snr` INT NOT NULL DEFAULT 0 ,
  `lte_sig_str` INT NOT NULL DEFAULT 0 ,
  `gsm_sig_str` INT NOT NULL DEFAULT 0 ,
  `gsm_bit_error_rate` INT NOT NULL DEFAULT 0 ,
  `uploaded` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`lte_stat_id`) ,
  INDEX `fk_lte_stats_network_location1` (`location_id` ASC) ,
  CONSTRAINT `fk_lte_stats_network_location1`
    FOREIGN KEY (`location_id` )
    REFERENCES `mydb`.`network_location` (`location_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



-- -----------------------------------------------------
-- Table `mydb`.`evdo_stats`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`evdo_stats` (
  `evdo_stat_id` INT NOT NULL AUTO_INCREMENT ,
  `location_id` INT NOT NULL ,
  `evdo_rssi` INT NOT NULL DEFAULT 0 ,
  `evdo_ecio` INT NOT NULL DEFAULT 0 ,
  `evdo_snr` INT NOT NULL DEFAULT 0 ,
  `uploaded` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`evdo_stat_id`) ,
  INDEX `fk_evdo_stats_network_location1` (`location_id` ASC) ,
  CONSTRAINT `fk_evdo_stats_network_location1`
    FOREIGN KEY (`location_id` )
    REFERENCES `mydb`.`network_location` (`location_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)




SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
