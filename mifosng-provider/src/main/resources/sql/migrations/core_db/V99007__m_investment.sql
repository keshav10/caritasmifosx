
DROP TABLE IF EXISTS m_investment;

  CREATE TABLE `m_investment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`saving_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`start_date` DATE NULL DEFAULT NULL,
	`close_date` DATE NULL DEFAULT NULL,
	`invested_amount` DECIMAL(10,0) NULL DEFAULT NULL,
	PRIMARY KEY (`id`,`saving_id`, `loan_id`),
	INDEX `FK__m_savings_account` (`saving_id`),
	INDEX `FK__m_loan` (`loan_id`),
	CONSTRAINT `FK__m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK__m_savings_account` FOREIGN KEY (`saving_id`) REFERENCES `m_savings_account` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;







