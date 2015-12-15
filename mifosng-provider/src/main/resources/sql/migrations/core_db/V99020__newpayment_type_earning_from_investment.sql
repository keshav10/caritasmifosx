INSERT INTO `m_payment_type` (`value`, `description`, `is_cash_payment`, `order_position`) VALUES ('Earnings From Investment', 'Earnings From Investment', 0, (SELECT if(count(*) = 0 ,0, mpt.order_position + 1) as positio FROM   m_payment_type mpt)
);
