CREATE TABLE `m_ext_txn` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`ipn_id` BIGINT(20) NOT NULL,
	`office_Id` BIGINT(20) NULL DEFAULT NULL,
	`orig` VARCHAR(50) NULL DEFAULT NULL,
	`dest` VARCHAR(50) NULL DEFAULT NULL,
	`client_id` BIGINT(10) NULL DEFAULT NULL,
	`client_name` VARCHAR(100) NULL DEFAULT NULL,
	`time_stamp` VARCHAR(50) NULL DEFAULT NULL,
	`text` VARCHAR(250) NULL DEFAULT NULL,
	`mpesa_transaction_code` VARCHAR(50) NOT NULL,
	`mpesauser` VARCHAR(50) NOT NULL,
	`mpesapassword` VARCHAR(50) NOT NULL,
	`mpesa_msisdn` VARCHAR(50) NULL DEFAULT NULL,
	`mpesa_account_name` VARCHAR(250) NULL DEFAULT NULL,
	`mpesa_trx_date` DATE NULL DEFAULT NULL,
	`mpesa_trx_time` VARCHAR(50) NULL DEFAULT NULL,
	`mpesa_amount` DECIMAL(19,6) NOT NULL,
	`mpesa_sender` VARCHAR(250) NULL DEFAULT NULL,
	`status` VARCHAR(10) NOT NULL,
	`mpesa_trx_type` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;


CREATE TABLE `m_ext_unmapped_txn` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`txn_json` TEXT NOT NULL,
	`status` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
