CREATE TABLE `OfficeDetails` (
	`office_id` BIGINT(20) NOT NULL,
	`sms_enabled` BIT(1) NOT NULL,
	PRIMARY KEY (`office_id`),
	CONSTRAINT `fk_officedetails_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`) VALUES ('OfficeDetails', 'm_office', 100);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'CREATE_OfficeDetails', 'OfficeDetails', 'CREATE', 1);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'CREATE_OfficeDetails_CHECKER', 'OfficeDetails', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'READ_OfficeDetails', 'OfficeDetails', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'UPDATE_OfficeDetails', 'OfficeDetails', 'UPDATE', 1);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'UPDATE_OfficeDetails_CHECKER', 'OfficeDetails', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'DELETE_OfficeDetails', 'OfficeDetails', 'DELETE', 1);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('datatable', 'DELETE_OfficeDetails_CHECKER', 'OfficeDetails', 'DELETE', 0);
