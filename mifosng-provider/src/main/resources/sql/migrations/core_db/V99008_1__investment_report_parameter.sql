INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Saving Investment'),5,'officeId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Saving Investment'), 2,'endDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select sr.id from stretchy_report sr where sr.report_name ='Saving Investment'), 1,'startDate');
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'report', 'READ_Saving Investment', 'Saving Investment', 'READ', 0);
