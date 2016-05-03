

CREATE TABLE `mpesa_txn_branch_mapping` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`office_id` BIGINT(20) NOT NULL,
	`mpesa_pay_bill_number` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`, `office_id`),
	UNIQUE INDEX `mpesa_pay_bill_number` (`mpesa_pay_bill_number`),
	INDEX `fk_mpesatxnbranchmapping_office_id1` (`office_id`),
	CONSTRAINT `fk_mpesatxnbranchmapping_office_id1` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;



INSERT INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`) 
VALUES ('mpesa_txn_branch_mapping', 'm_office', 100);