CREATE TABLE IF NOT EXISTS `event_sourcing` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`tenant_id` VARCHAR(32) NOT NULL,
	`entity` VARCHAR(32) NOT NULL,
	`action` VARCHAR(32) NOT NULL,
	`payload` VARCHAR(4096) NOT NULL,
	`processed` TINYINT(1) NOT NULL,
	`error_message` VARCHAR(256) NULL DEFAULT NULL,
	`created_on` DATETIME NOT NULL,
	`last_modified_on` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
)