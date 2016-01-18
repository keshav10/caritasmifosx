INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Investment Status Report', 'Table', NULL, 'Investment Status Report', 'select msa.account_no as \'Group Account\',\nmg.display_name as \'Group Name\',\nmi.invested_amount as \'Invested Amount\',\nctie.investment_start_date as \'Investment Start Date\',\nctie.investment_close_date as \'Investment Close Date\',\ncts.earning_status as \'Status\',\nml.account_no as \'Loan Account\',\nmpl.name as \'Product Name\',\nml.approved_principal as \'Investment Amount\',\nml.maturedon_date as \'Maturity Date\'\n\n\nfrom m_investment mi\nleft join m_loan ml on mi.loan_id = ml.id\nleft join m_product_loan mpl on mpl.id = ml.product_id\nleft join ct_posted_investment_earnings ctie on ctie.loan_id = mi.loan_id and ctie.saving_id = mi.saving_id\nleft join m_savings_account msa on msa.id = mi.saving_id\nleft join m_group mg on mg.id = msa.group_id\nleft join ct_investment_status cts on cts.loan_id = mi.loan_id\n\n\nWHERE\n    CASE\n 	 WHEN \'${investmentAccountNo}\' != \'NULL\'  THEN\n  	 	ml.account_no = \'${investmentAccountNo}\'\n 	 ELSE\n   	  1=1 \n	END\n	\n	AND\n	\n	CASE \n	 WHEN \'${investedStartDate}\' != \'NULL\' THEN\n         CASE WHEN \'${investedEndDate}\' != \'NULL\' THEN\n        		 ctie.investment_start_date  BETWEEN \'${investedStartDate}\' AND \'${investedEndDate}\'\n		    ELSE\n           ctie.investment_start_date  BETWEEN \'${investedStartDate}\' AND curDate()\n         END\n		 \n     ELSE 1=1\n   \n   END\n   \n   AND \n   \n   CASE WHEN \'${loanProductId}\' != \'NULL\' THEN\n	    mpl.id = \'${loanProductId}\'\n		ELSE 1=1\n   END  \n	  \n   AND\n	\n   CASE \n    WHEN \'${ml.maturedon_date}\' != \'NULL\'  THEN\n        CASE WHEN \'${maturityEndDate}\' != \'NULL\' THEN\n    	   ml.maturedon_date BETWEEN \'${maturityStartDate}\' AND \'${maturityEndDate}\'\n        ELSE\n          ml.maturedon_date BETWEEN \'${maturityStartDate}\' AND curDate()\n		  END	 	  \n     ELSE 1=1    \n  END\n\ngroup by  mi.loan_id,  mi.saving_id', 'Investment Status Report', 0, 1);


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Investment Distribution Report', 'Table', NULL, 'Investment Distribution Report', 'select mg.id as \'Group No\',\nmg.display_name as \'Group Name\',\nctie.investment_start_date as \'Investment Start Date\',\nctie.investment_close_date as \'Investment Close Date\',\nmi.invested_amount as \'Invested Amount\',\nctie.interest_earned as \'Total Interest Earned\',\nctie.gorup_interest_earned as \'Groups Interest Earned\',\n(ctie.interest_earned - ctie.gorup_interest_earned) as \'Caritas Interest Earned\', \nctie.number_of_days as \'No.Of Days\',\ncts.earning_status as \'Status\',\nml.maturedon_date as \'Maturity Date\'\n\n\nfrom m_investment mi\nleft join m_loan ml on mi.loan_id = ml.id\nleft join m_product_loan mpl on mpl.id = ml.product_id\nleft join ct_posted_investment_earnings ctie on ctie.loan_id = mi.loan_id and ctie.saving_id = mi.saving_id\nleft join m_savings_account msa on msa.id = mi.saving_id\nleft join m_group mg on mg.id = msa.group_id\nleft join ct_investment_status cts on cts.loan_id = mi.loan_id\n\n\nWHERE\n    CASE\n 	 WHEN \'${investmentAccountNo}\' != \'NULL\'  THEN\n  	 	ml.account_no = \'${investmentAccountNo}\'\n 	 ELSE\n   	  1=1 \n	END\n	\n	AND\n	\n	CASE \n	 WHEN \'${investedStartDate}\' != \'NULL\' THEN\n         CASE WHEN \'${investedEndDate}\' != \'NULL\' THEN\n        		 ctie.investment_start_date  BETWEEN \'${investedStartDate}\' AND \'${investedEndDate}\'\n		    ELSE\n          ctie.investment_start_date  BETWEEN \'${investedStartDate}\' AND curDate()\n         END\n		 \n     ELSE 1=1\n   \n   END\n   \n   AND \n   \n   CASE WHEN \'${loanProductId}\' != \'NULL\'  THEN\n	    mpl.id = \'${loanProductId}\'\n		ELSE 1=1\n   END  \n	  \n   AND\n	\n   CASE \n    WHEN \'${ml.maturedon_date}\' != \'NULL\'  THEN\n        CASE WHEN \'${maturityEndDate}\' !=\'NULL\' THEN\n    	   ml.maturedon_date BETWEEN \'${maturityStartDate}\' AND \'${maturityEndDate}\'\n        ELSE\n          ml.maturedon_date BETWEEN \'${maturityStartDate}\' AND curDate()\n		  END	 	  \n     ELSE 1=1    \n  END\n\ngroup by ml.id, msa.id', 'Investment Distribution Report', 0, 1);





