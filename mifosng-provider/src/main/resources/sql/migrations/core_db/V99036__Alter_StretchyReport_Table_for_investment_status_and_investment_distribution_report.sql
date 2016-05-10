
update stretchy_report sr

set sr.report_sql = 'select msa.account_no as 'Group Account',
mg.display_name as 'Group Name',
mi.invested_amount as 'Invested Amount',
ctie.investment_start_date as 'Investment Start Date',
ctie.investment_close_date as 'Investment Close Date',
cts.earning_status as 'Status',
concat(ml.account_no, '-' , ml.external_id) as 'Loan Account',
mpl.name as 'Product Name',
ml.approved_principal as 'Investment Amount',
ctie.group_charge_applied as 'Charge Amount',
ml.maturedon_date as 'Maturity Date'


from  m_office mo 
join m_office office on office.hierarchy like concat(mo.hierarchy, '%') 
and office.hierarchy like concat('${currentUserHierarchy}','%')
 
left join m_group mg on mg.office_id = office.id
left join m_client mc on mc.office_id = office.id
left join m_loan ml on ml.client_id = mc.id
left join m_savings_account msa on msa.group_id = mg.id
inner join m_investment mi on ml.id = mi.loan_id and msa.id = mi.saving_id
 
left join ct_posted_investment_earnings ctie on ctie.loan_id = mi.loan_id 
and ctie.saving_id = msa.id and ctie.office_id = office.id
left join ct_investment_status cts on cts.loan_id = mi.loan_id
left join m_product_loan mpl on mpl.id = ml.product_id
WHERE
    CASE
 	 WHEN '${investmentAccountNo}' != 'NULL'  THEN
  	 	ml.account_no = '${investmentAccountNo}'
 	 ELSE
   	  1=1 
	END
	
	AND
	
	CASE 
	 WHEN '${investedStartDate}' != 'NULL' THEN
         CASE WHEN '${investedEndDate}' != 'NULL' THEN
        		 ctie.investment_start_date  BETWEEN '${investedStartDate}' AND '${investedEndDate}'
		    ELSE
           ctie.investment_start_date  BETWEEN '${investedStartDate}' AND curDate()
         END
		 
     ELSE 1=1
   
   END
   
   AND 
   
   CASE WHEN '${loanProductId}' != 'NULL' THEN
	    mpl.id = '${loanProductId}'
		ELSE 1=1
   END  
	  
   AND
	
   CASE 
    WHEN '${ml.maturedon_date}' != 'NULL'  THEN
        CASE WHEN '${maturityEndDate}' != 'NULL' THEN
    	   ml.maturedon_date BETWEEN '${maturityStartDate}' AND '${maturityEndDate}'
        ELSE
          ml.maturedon_date BETWEEN '${maturityStartDate}' AND curDate()
		  END	 	  
     ELSE 1=1    
  END

group by  mi.loan_id,  mi.saving_id'
where sr.report_name = 'Investment Status Report' and sr.report_category = 'Investment Status Report';




update stretchy_report sr
set sr.report_sql = 'select mg.id as 'Group No',
mg.display_name as 'Group Name',
concat(mpl.name, ' - ', ml.external_id) as 'Invested In',
ctie.investment_start_date as 'Investment Start Date',
ctie.investment_close_date as 'Investment Close Date',
mi.invested_amount as 'Invested Amount',
ctie.interest_earned as 'Interest Earned on Investment',
ctie.gorup_interest_earned as 'Groups Interest Earned',
ctie.caritas_interest_earned as 'Caritas Interest Earned', 
ctie.group_charge_applied as 'Total Charge Amount',
(ifnull(ctie.gorup_interest_earned,0) + ifnull(ctie.caritas_interest_earned,0) + ifnull(ctie.group_charge_applied,0) ) as 'Total Interest Earning',
ctie.number_of_days as 'No.Of Days',
cts.earning_status as 'Status',
ml.maturedon_date as 'Maturity Date'


from m_office mo 
join m_office office on office.hierarchy like concat(mo.hierarchy, '%') 
and office.hierarchy like concat('${currentUserHierarchy}','%')
left join m_group mg on mg.office_id = office.id
left join m_client mc on mc.office_id = office.id
left join m_loan ml on ml.client_id = mc.id
left join m_savings_account msa on msa.group_id = mg.id
inner join m_investment mi on ml.id = mi.loan_id and msa.id = mi.saving_id
 
left join ct_posted_investment_earnings ctie on ctie.loan_id = mi.loan_id 
and ctie.saving_id = msa.id and ctie.office_id = office.id
left join ct_investment_status cts on cts.loan_id = mi.loan_id
left join m_product_loan mpl on mpl.id = ml.product_id


WHERE
    CASE
 	 WHEN '${investmentAccountNo}' != 'NULL'  THEN
  	 	ml.account_no = '${investmentAccountNo}'
 	 ELSE
   	  1=1 
	END
	
	AND
	
	CASE 
	 WHEN '${investedStartDate}' != 'NULL' THEN
         CASE WHEN '${investedEndDate}' != 'NULL' THEN
        		 ctie.investment_start_date  BETWEEN '${investedStartDate}' AND '${investedEndDate}'
		    ELSE
          ctie.investment_start_date  BETWEEN '${investedStartDate}' AND curDate()
         END
		 
     ELSE 1=1
   
   END
   
   AND 
   
   CASE WHEN '${loanProductId}' != 'NULL'  THEN
	    mpl.id = '${loanProductId}'
		ELSE 1=1
   END  
	  
   AND
	
   CASE 
    WHEN '${ml.maturedon_date}' != 'NULL'  THEN
        CASE WHEN '${maturityEndDate}' !='NULL' THEN
    	   ml.maturedon_date BETWEEN '${maturityStartDate}' AND '${maturityEndDate}'
        ELSE
          ml.maturedon_date BETWEEN '${maturityStartDate}' AND curDate()
		  END	 	  
     ELSE 1=1    
  END 
group by mi.saving_id, mi.loan_id'
where sr.report_name = 'Investment Distribution Report' 
and sr.report_category = 'Investment Distribution Report';