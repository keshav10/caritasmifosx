
 INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
 VALUES ((select sh.id from stretchy_report sh
         where sh.report_name like 'Loan Repayment schedule'),
			(select sh.id  from stretchy_parameter sh
             where sh.parameter_name like 'Loan Account no' ), 
		 'selectLoan');