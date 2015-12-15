
ALTER TABLE `event_sourcing_details`
	CHANGE COLUMN `entity_id` `entity_id` BIGINT(20) NULL AFTER `tenant_id`,
	ADD COLUMN `message` VARCHAR(255) NULL DEFAULT NULL AFTER `report_name`;