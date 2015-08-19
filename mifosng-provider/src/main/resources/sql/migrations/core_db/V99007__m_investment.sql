CREATE TABLE `m_investment` (
 `id` INT(10) NOT NULL AUTO_INCREMENT,
 `saving_id` BIGINT(20) NOT NULL,
 `loan_id` BIGINT(20) NOT NULL,
 PRIMARY KEY (`id`, `saving_id`, `loan_id`),
 CONSTRAINT `FK__m_savings_account` FOREIGN KEY (`saving_id`) REFERENCES `m_savings_account` (`id`),
 CONSTRAINT `FK__m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;



INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'CREATE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_SAVINGINVESTMENT', 'SAVINGINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_LOANINVESTMENT', 'LOANINVESTMENT', 'DELETE', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_LOANINVESTMENT', 'LOANINVESTMENT', 'CREATE', 0);

