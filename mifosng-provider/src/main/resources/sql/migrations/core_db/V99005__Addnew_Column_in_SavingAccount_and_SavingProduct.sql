

ALTER TABLE `m_savings_account` ADD COLUMN `release_guarantor` TINYINT(1) NOT NULL DEFAULT '0' AFTER `version`;

ALTER TABLE `m_savings_product`  ADD COLUMN `release_guarantor` TINYINT(1) NOT NULL DEFAULT '0' AFTER `min_balance_for_interest_calculation`;