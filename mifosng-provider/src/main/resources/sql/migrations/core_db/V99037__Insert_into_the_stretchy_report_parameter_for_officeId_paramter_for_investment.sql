

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'OfficeIdSelectOne'),
'officeId');
 
 
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'OfficeIdSelectOne'),
'officeId');
