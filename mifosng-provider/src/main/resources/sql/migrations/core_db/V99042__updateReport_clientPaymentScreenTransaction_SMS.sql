
 update stretchy_report sr

set sr.report_sql = "select a.*,sum(ifnull(a.TotalLoantrxnAmount + a.TotalSavingstrxnAmount,0)) 'total' 
from
(select  
 '${reciptNo}' ReceiptNo,
  o.name officeName,c.display_name ClientName,c.mobile_no MobileNo,
  round((ifnull( (select sum(mlt.amount) from
   m_payment_detail p
   left join m_loan_transaction mlt on p.id = mlt.payment_detail_id
   where p.receipt_number like '${reciptNo}'
  and mlt.amount is not null),0)),0)TotalLoantrxnAmount,
round((ifnull( (select sum(mlt.amount) from
m_payment_detail p
left join m_savings_account_transaction mlt on p.id = mlt.payment_detail_id
where p.receipt_number like '${reciptNo}'
and mlt.transaction_type_enum != 7
and mlt.amount is not null ),0)),0)TotalSavingstrxnAmount

from m_office o
left join m_client c on o.id = c.office_id

where c.id = '${clientId}'
group by c.id)a "

where sr.report_name like 'Client Payments'