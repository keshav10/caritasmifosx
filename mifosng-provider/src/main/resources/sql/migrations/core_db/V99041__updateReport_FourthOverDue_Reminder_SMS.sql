

update stretchy_report sr
set sr.report_sql = "select a.client_name,a.Guarantor_name,a.Guarantor_Branch,a.client_mobile_no,a.loan_id,a.client_id,
a.productshort_Name,a.Guarantor_mobile_no  from
(select cl.display_name as client_name
,ifnull(go.display_name,concat(gu.firstname,'',gu.lastname)) as Guarantor_name
,ifnull(o.name,'NA ') as Guarantor_Branch
,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
,co.name as Client_Branch
,ifnull(cl.mobile_no,'NA ') as client_mobile_no
,min(lrc.duedate)as mindate
,lrc.duedate as duedate,
cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff('${startDate} ',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +2 month),interval 5 day))),null) as days  
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
and a.days<5 "

where sr.report_name like 'Loan Fourth Overdue Repayment Reminder'