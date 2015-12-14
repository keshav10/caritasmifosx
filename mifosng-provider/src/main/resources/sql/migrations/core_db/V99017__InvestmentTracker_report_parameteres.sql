
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Maturity Start Date', 'maturityStartDate', 'Maturity Start Date', 'date', 'date', 'today', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Maturity End Date', 'maturityEndDate', 'Maturity End Date', 'date', 'date', 'today', NULL, NULL, NULL, NULL, NULL);


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Investment Start Date', 'investedStartDate', 'Investment Start Date', 'date', 'date', 'today', NULL, NULL, NULL, NULL, NULL);


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Investment End Date', 'investedEndDate', 'Investment End Date', 'date', 'date', 'today', NULL, NULL, NULL, NULL, NULL);


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Investment Product', 'loanProductId', 'Product', 'select', 'number', '0', NULL, NULL, NULL, 'select p.id, p.`name`\r\nfrom m_product_loan p\r\norder by 2', NULL);


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('Investment Account No', 'investmentAccountNo', 'Investment Account No', 'text', 'string', 'n/a', NULL, NULL, NULL, NULL, NULL);
