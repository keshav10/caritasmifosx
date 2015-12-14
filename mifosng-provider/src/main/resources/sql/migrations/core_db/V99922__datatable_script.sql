CREATE TABLE `officedetails` (
	`office_id` BIGINT(20) NOT NULL,
	`sms_enabled` BIT(1) NOT NULL,
	PRIMARY KEY (`office_id`),
	CONSTRAINT `fk_officedetails_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;