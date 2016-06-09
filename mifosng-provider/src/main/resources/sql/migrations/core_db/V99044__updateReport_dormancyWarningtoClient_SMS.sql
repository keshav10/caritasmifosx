update stretchy_report sr

set sr.report_sql = "select a.display_name,a.branch_name,a.last_transaction_date,ifnull(a.mobile_no, 'NA') as mobile_no,a.savings_Id,a.client_Id,a.product_Short_Name from
(select cl.display_name,sa.product_id,cl.id as client_Id ,cl.default_savings_product,sa.id as savings_Id
 ,mo.name as branch_name,mp.short_name as product_Short_Name,cl.mobile_no as mobile_no,
MAX(transaction_date)as last_transaction_date 
from m_client cl 
inner join m_office mo on mo.id=cl.office_id
inner join OfficeDetails od on od.office_id=mo.id
inner join m_savings_account sa on sa.client_id=cl.id 
inner join m_savings_product mp on mp.id=sa.product_id
left join m_savings_account_transaction msa on msa.savings_account_id=sa.id
where  sa.id=cl.default_savings_account
and (msa.transaction_type_enum=1 or msa.transaction_type_enum is null)
and 
cl.status_enum = 300 and
cl.sub_status =27 
and sa.status_enum=300
and od.sms_enabled=true
group by sa.account_no)a
where 
if(a.last_transaction_date is null, TIMESTAMPDIFF (MONTH,curDate(),'${startDate}')=0,
 TIMESTAMPDIFF (MONTH,a.last_transaction_date,'${startDate}')=4)"

where sr.report_name like 'DormancyWarning - Clients'