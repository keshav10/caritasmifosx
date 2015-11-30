CREATE TABLE `m_investment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`saving_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`start_date` DATE NULL DEFAULT NULL,
	`close_date` DATE NULL DEFAULT NULL,
	`invested_amount` DECIMAL(10,0) NULL DEFAULT NULL,
	PRIMARY KEY (`id`, `saving_id`, `loan_id`),
	INDEX `FK__m_savings_account` (`saving_id`),
	INDEX `FK__m_loan` (`loan_id`),
	CONSTRAINT `FK__m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK__m_savings_account` FOREIGN KEY (`saving_id`) REFERENCES `m_savings_account` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'CREATE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_LOANINVESTMENT', 'LOANINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_LOANINVESTMENT', 'LOANINVESTMENT', 'CREATE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CLOSE_LOANINVESTMENT', 'LOANINVESTMENT', 'CLOSE', 0);



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CLOSE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'CLOSE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_LOANINVESTMENT', 'LOANINVESTMENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'RUNBATCHJOB_INVESTMENT', 'INVESTMENT', 'RUNBATCHJOB', 0);





