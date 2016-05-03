    
	ALTER TABLE `ct_posted_investment_earnings`
	ADD COLUMN `office_id` BIGINT(20) NULL DEFAULT NULL AFTER `saving_id`,
	ADD COLUMN `caritas_interest_earned` DECIMAL(10,2) NULL DEFAULT NULL AFTER `gorup_interest_earned`,
	ADD CONSTRAINT `FK_ct_posted_investment_earnings_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`);
