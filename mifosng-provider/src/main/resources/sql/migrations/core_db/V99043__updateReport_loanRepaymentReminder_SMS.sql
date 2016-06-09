

update stretchy_report sr

set sr.report_sql = "select a.client_name,a.branch_name,a.due_date,a.mobile_no,a.loan_id ,a.client_id ,a.productshort_name from
(select c.display_name as client_name,o.name as branch_name, lrc.duedate as due_date,c.mobile_no as mobile_no,lrc.loan_id as loan_id,c.id as client_id,
mp.short_name as productShort_name,
if (((ifnull(lrc.principal_amount,0)+ ifnull(lrc.interest_amount,0))>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(lrc.duedate,'${startDate}')),null) as days 
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
  and  a.days<=5 "
where sr.report_name like 'Loan Repayment Reminders'