

    ALTER TABLE `m_ext_txn`
	ADD COLUMN `client_external_id` VARCHAR(100) NULL DEFAULT NULL AFTER `client_name`;