
INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name like 'Client Payments') , 1009, 'reciptNo');

INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name like 'Client Payments'), 1010, 'clientId');