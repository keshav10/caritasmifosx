ALTER TABLE `event_sourcing_details`
	CHANGE COLUMN `entity_id` `entity_id` VARCHAR(50) NULL DEFAULT NULL AFTER `tenant_id`;
