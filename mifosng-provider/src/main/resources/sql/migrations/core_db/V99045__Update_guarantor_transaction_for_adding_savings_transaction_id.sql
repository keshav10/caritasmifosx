    
	ALTER TABLE `m_guarantor_transaction`
	ADD COLUMN `saving_transaction_id` BIGINT(20) NULL DEFAULT NULL AFTER `loan_transaction_id`,
	ADD INDEX `saving_transaction_id` (`saving_transaction_id`),
	ADD CONSTRAINT `FK_m_guarantor_transaction_m_savings_account_transaction` FOREIGN KEY (`saving_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`);
