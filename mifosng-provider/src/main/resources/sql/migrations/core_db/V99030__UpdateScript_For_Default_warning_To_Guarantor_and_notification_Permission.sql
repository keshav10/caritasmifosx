INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Payment Receipts', 'Pentaho', NULL, 'Client', NULL, 'Payment Receipts', 0, 1) ON DUPLICATE KEY UPDATE report_sql=NULL;
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('special', 'SEND_NOTIFICATION', 'NOTIFICATION', 'SEND', 0);
update  stretchy_report set report_sql= 'select a .client_name,a.Guarantor_name,a.Guarantor_Branch,a.client_mobile_no,a.loan_id,a.client_id,productshort_Name,a.Guarantor_mobile_no,a.comitted_Shares
from
(select cl.display_name as client_name
,ifnull(go.display_name,concat(gu.firstname,gu.lastname)) as Guarantor_name
,ifnull(o.name,\'NA \') as Guarantor_Branch
,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
,co.name as Client_Branch
,ifnull(cl.mobile_no,\'NA \') as client_mobile_no
,min(lrc.duedate)as mindate,
cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,gfd.amount_remaining_derived as comitted_Shares, 
if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)+ifnull(lrc.interest_completed_derived,0))),(datediff(\'${startDate} \',date_add(date_add(makedate(extract(year from lrc.duedate),extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +3 month),interval 5 day))),null) as days  
from m_loan l
inner join m_product_loan mp on mp.id=l.product_id 
inner join m_client cl on cl.id=l.client_id 
inner join m_guarantor gu on gu.loan_id=l.id
left join m_guarantor_funding_details gfd on gfd.guarantor_id=gu.id
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
   and a.days<5'
 where report_name ='DefaultWarning -  guarantors';
