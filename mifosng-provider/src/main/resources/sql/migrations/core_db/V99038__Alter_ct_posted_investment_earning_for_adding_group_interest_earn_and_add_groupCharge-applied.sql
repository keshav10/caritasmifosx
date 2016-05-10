

ALTER TABLE `ct_posted_investment_earnings`
	ADD COLUMN `group_charge_applied` DECIMAL(10,2) NULL DEFAULT NULL AFTER `gorup_interest_earned`;
	
	
	