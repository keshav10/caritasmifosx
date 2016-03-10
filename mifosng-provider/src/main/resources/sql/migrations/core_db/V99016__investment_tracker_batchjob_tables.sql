
CREATE TABLE IF NOT EXISTS  `ct_investment_split` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`date` DATE NULL DEFAULT NULL,
	`caritas_percentage` DECIMAL(10,1) NULL DEFAULT NULL,
	`group_percentage` DECIMAL(10,1) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;



CREATE TABLE IF NOT EXISTS  `ct_investment_status` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`loan_id` INT(11) NULL DEFAULT NULL,
	`earning_status` VARCHAR(50) NULL DEFAULT NULL,
	`status_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


CREATE TABLE IF NOT EXISTS  `ct_posted_investment_earnings` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`loan_id` INT(11) NULL DEFAULT NULL,
	`saving_id` INT(11) NULL DEFAULT NULL,
	`number_of_days` INT(11) NULL DEFAULT NULL,
	`invested_amount` DECIMAL(10,2) NULL DEFAULT NULL,
	`gorup_interest_rate` DECIMAL(10,2) NULL DEFAULT NULL,
	`gorup_interest_earned` DECIMAL(10,2) NULL DEFAULT NULL,
	`interest_earned` DECIMAL(10,2) NULL DEFAULT NULL,
	`date_of_interest_posting` DATE NULL DEFAULT NULL,
	`investment_start_date` DATE NULL DEFAULT NULL,
	`investment_close_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

INSERT INTO `ct_investment_split` (`date`, `caritas_percentage`, `group_percentage`) VALUES (curDate(), 7.5, 92.5);

