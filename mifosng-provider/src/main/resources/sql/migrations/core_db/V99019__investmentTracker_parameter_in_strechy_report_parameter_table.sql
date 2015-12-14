

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Maturity Start Date')
,'maturityStartDate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Maturity End Date'),
'maturityEndDate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Start Date'),
'investedStartDate');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment End Date'),
'investedEndDate');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Product'),
'loanProductId');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Distribution Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Account No'),
'investmentAccountNo');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Maturity Start Date'),
'maturityStartDate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Maturity End Date'),
'maturityEndDate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Start Date'),
'investedStartDate');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment End Date'),
'investedEndDate');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Product'),
'loanProductId');



INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ( (select sr.id from stretchy_report sr where sr.report_name ='Investment Status Report'),
(select sp.id   from stretchy_parameter sp where sp.parameter_name = 'Investment Account No'),
'investmentAccountNo');






