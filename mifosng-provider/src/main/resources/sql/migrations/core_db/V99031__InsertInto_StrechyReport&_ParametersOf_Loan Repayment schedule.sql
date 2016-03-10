

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) 
VALUES ('Loan Repayment schedule', 'Pentaho', NULL, 'Loan', NULL, 'Loan repay mentschudel', 0, 1);



INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) 
VALUES ('Loan Account no', 'account no', 'Account Number', 'text', 'string', 'n/a', NULL, NULL, NULL, '', NULL);