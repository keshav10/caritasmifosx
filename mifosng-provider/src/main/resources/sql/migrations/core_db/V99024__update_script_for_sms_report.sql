update  stretchy_report set report_sql='select a.client_name,a.branch_name,a.due_date,a.mobile_no,a.loan_id ,a.client_id ,a.productshort_name from
(select c.display_name as client_name,o.name as branch_name, lrc.duedate as due_date,c.mobile_no as mobile_no,lrc.loan_id as loan_id,c.id as client_id,
mp.short_name as productShort_name,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(lrc.duedate,\'${startDate}\')),null) as days 
 from m_client c inner join m_loan l on c.id=l.client_id
     inner join m_product_loan mp on mp.id=l.product_id
     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id     
     inner join m_office o on c.office_id=o.id
     inner join OfficeDetails od on od.office_id=o.id
     where  lrc.completed_derived=0
     and l.loan_status_id=300
     and od.sms_enabled=true
	  group by lrc.loan_id)a
	  where a.days>=1
	  and  a.days<=5' where report_name ='Loan Repayment Reminders';
update  stretchy_report set report_sql='select a.client_name,a.branch_name,a.overdue_amount,a.mobile_no,a.loan_id,a.client_id,a.productshort_Name,a.month from
(select c.display_name as client_name,o.name as branch_name, c.mobile_no as mobile_no,date_format(lrc.duedate,\'%M \')as month,
c.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,
sum((ifnull(lrc.principal_amount,0)+ifnull(interest_amount,0))-(ifnull(lrc.principal_completed_derived,0)+ifnull(interest_completed_derived,0)))as overdue_amount,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate}\',lrc.duedate)),null) as days 
 
from m_client c inner join m_loan l on c.id=l.client_id
     inner join m_product_loan mp on mp.id=l.product_id
     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
     inner join m_office o on c.office_id=o.id
     inner join OfficeDetails od on od.office_id=o.id
     where datediff(\'${startDate}\',lrc.duedate)>=1	      
     and  lrc.completed_derived=0
    and l.loan_status_id=300 
    and od.sms_enabled=true
	  group by lrc.loan_id)a
	  where a.days>=5
	  and  a.days<10' where report_name ='Loan First Overdue Repayment Reminder';	
	  
update  stretchy_report set report_sql='select a.client_name,a.branch_name,a.overdue_amount,a.mobile_no,a.loan_id,a.client_id,a.productshort_Name, a.month from
(select c.display_name as client_name,o.name as branch_name, c.mobile_no as mobile_no,date_format(lrc.duedate,\'%M \')as month,
c.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,lrc.duedate as duedate, 
sum((ifnull(lrc.principal_amount,0)+ifnull(interest_amount,0))-(ifnull(lrc.principal_completed_derived,0)+ifnull(interest_completed_derived,0)))as overdue_amount,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) month),interval 5 day))),null) as days  
from m_client c inner join m_loan l on c.id=l.client_id
     inner join m_product_loan mp on mp.id=l.product_id 
     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
     inner join m_office o on c.office_id=o.id
     inner join OfficeDetails od on od.office_id=o.id
     where datediff(\'${startDate} \',lrc.duedate)>=1       
     and  lrc.completed_derived=0
    and l.loan_status_id=300
    and od .sms_enabled=true
	  group by lrc.loan_id)a
where	 a.days>=0
and   a.days<5' where report_name ='Loan Second Overdue Repayment Reminder'	; 

update  stretchy_report set report_sql='select a .client_name,a.Guarantor_name,a.Guarantor_Branch,a.client_mobile_no,a.loan_id,a.client_id,productshort_Name,a.Guarantor_mobile_no
from
(select cl.display_name as client_name
,ifnull(go.display_name, CONCAT(gu.firstname,\' \',gu.lastname)) as Guarantor_name
,ifnull(o.name,\'NA \') as Guarantor_Branch
,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
,co.name as Client_Branch
,ifnull(cl.mobile_no,\'NA\') as client_mobile_no
,min(lrc.duedate)as mindate
,lrc.duedate as duedate,
cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name, 
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +1 month),interval 5 day))),null) as days  
from m_loan l
inner join m_product_loan mp on mp.id=l.product_id 
inner join m_client cl on cl.id=l.client_id 
inner join m_guarantor gu on gu.loan_id=l.id
inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
left join m_client go on go.id=gu.entity_id
left join m_office o on o.id=go.office_id
left join m_office co on co.id=cl.office_id
inner join OfficeDetails od on od.office_id=co.id
where lrc.completed_derived=0
and   gu.is_active=1
and l.loan_status_id=300
and od.sms_enabled=true
group by gu.id
 )a
  where a.days>=0
 and a.days<5' where report_name ='Loan Third Overdue Repayment Reminder';	  
 
update  stretchy_report set report_sql='select a .client_name,a.Guarantor_name,a.Guarantor_Branch,a.client_mobile_no,a.loan_id,a.client_id,a.productshort_Name,a.Guarantor_mobile_no  from
(select cl.display_name as client_name
,ifnull(go.display_name,concat(gu.firstname,\' \',gu.lastname)) as Guarantor_name
,ifnull(o.name,\'NA \') as Guarantor_Branch
,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
,co.name as Client_Branch
,ifnull(cl.mobile_no,\'NA \') as client_mobile_no
,min(lrc.duedate)as mindate
,lrc.duedate as duedate,
cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +2 month),interval 5 day))),null) as days  
from m_loan l
inner join m_product_loan mp on mp.id=l.product_id
inner join m_client cl on cl.id=l.client_id 
inner join m_guarantor gu on gu.loan_id=l.id
inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
left join m_client go on go.id=gu.entity_id
left join m_office o on o.id=go.office_id
left join m_office co on co.id=cl.office_id
inner join OfficeDetails od on od.office_id=co.id
where lrc.completed_derived=0
and   gu.is_active=1
and l.loan_status_id=300
and od.sms_enabled=true
group by gu.id
 )a
   where a.days>=0
and a.days<5' where report_name ='Loan Fourth Overdue Repayment Reminder';

update  stretchy_report set report_sql='select a .client_name,a.Guarantor_name,a.Guarantor_Branch,a.client_mobile_no,a.loan_id,a.client_id,productshort_Name,a.Guarantor_mobile_no,a.comitted_Shares
from
(select cl.display_name as client_name
,ifnull(go.display_name,concat(gu.firstname,\' \',gu.lastname)) as Guarantor_name
,ifnull(o.name,\'NA \') as Guarantor_Branch
,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
,co.name as Client_Branch
,ifnull(cl.mobile_no,\'NA \') as client_mobile_no
,min(lrc.duedate)as mindate,
cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,sa.on_hold_funds_derived as comitted_Shares, 
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +3 month),interval 5 day))),null) as days  
from m_loan l
inner join m_product_loan mp on mp.id=l.product_id 
inner join m_client cl on cl.id=l.client_id 
inner join m_guarantor gu on gu.loan_id=l.id
left join m_savings_account sa on sa.client_id=gu.entity_id
inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
left join m_client go on go.id=gu.entity_id
left join m_office o on o.id=go.office_id
left join m_office co on co.id=cl.office_id
inner join OfficeDetails od on od.office_id=co.id
where lrc.completed_derived=0
and l.loan_status_id=300
and   gu.is_active=1
and od.sms_enabled=true
group by gu.id
 )a
	  where a.days>=0
   and a.days<5' where report_name ='DefaultWarning -  guarantors';

update  stretchy_report set report_sql='select a.client_name,a.branch_name,a.overdue_amount,a.mobile_no,a.loan_id,a.client_id,a.productshort_Name, a.month from
(select c.display_name as client_name,o.name as branch_name, c.mobile_no as mobile_no,date_format(lrc.duedate,\'%M \')as month,
c.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,lrc.duedate as duedate, 
(l.principal_outstanding_derived+l.interest_outstanding_derived) as overdue_amount,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +3 month),interval 5 day))),null) as days   
from m_client c inner join m_loan l on c.id=l.client_id
     inner join m_product_loan mp on mp.id=l.product_id 
     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
     inner join m_office o on c.office_id=o.id
     inner join OfficeDetails od on od.office_id=o.id
     where datediff(\'${startDate} \',lrc.duedate)>=1       
     and  lrc.completed_derived=0
    and l.loan_status_id=300
    and od.sms_enabled=true
	  group by lrc.loan_id)a
	  where a.days>=0
         and a.days<5' where report_name ='DefaultWarning - Clients';
		 
update  stretchy_report set report_sql='select a.display_name,a.branch_name,a.last_transaction_date,ifnull(a.mobile_no, \'NA\') as mobile_no,a.savings_Id,a.client_Id,a.product_Short_Name from
(select cl.display_name,sa.product_id,cl.id as client_Id ,cl.default_savings_product,sa.id as savings_Id
 ,mo.name as branch_name,mp.short_name as product_Short_Name,cl.mobile_no as mobile_no,
MAX(transaction_date)as last_transaction_date 
from m_client cl 
inner join m_office mo on mo.id=cl.office_id
inner join OfficeDetails od on od.office_id=mo.id
inner join m_savings_account sa on sa.client_id=cl.id 
inner join m_savings_product mp on mp.id=sa.product_id
inner join m_savings_account_transaction msa on msa.savings_account_id=sa.id
where  sa.id=cl.default_savings_account
and msa.transaction_type_enum=1
and 
cl.status_enum = 300 and
cl.sub_status =27 
and sa.status_enum=300
and od.sms_enabled=true
group by sa.account_no)a
where TIMESTAMPDIFF (MONTH,a.last_transaction_date,\'${startDate}\')=4' where report_name like 'DormancyWarning - Clients';		 
	  
	  