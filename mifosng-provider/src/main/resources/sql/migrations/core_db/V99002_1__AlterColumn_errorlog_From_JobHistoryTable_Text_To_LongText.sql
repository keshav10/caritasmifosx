    
	
	ALTER TABLE `job_run_history`
	CHANGE COLUMN `error_message` `error_message` LONGTEXT NULL AFTER `status`,
	CHANGE COLUMN `error_log` `error_log` LONGTEXT NULL AFTER `trigger_type`;