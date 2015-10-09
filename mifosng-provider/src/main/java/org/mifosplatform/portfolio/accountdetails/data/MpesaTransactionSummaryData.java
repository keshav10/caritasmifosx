package org.mifosplatform.portfolio.accountdetails.data;

import java.math.BigDecimal;
public class MpesaTransactionSummaryData {

	private final Long id;
	private final String accountNo;
	private final BigDecimal chargeAmount;
	private final String TxnDate;
	private final BigDecimal Amount;
	private final String clientName;
	
	public MpesaTransactionSummaryData(Long id, String accountNo,
			BigDecimal chargeAmount, String txnDate, BigDecimal amount,
			String clientName) {
		super();
		this.id = id;
		this.accountNo = accountNo;
		this.chargeAmount = chargeAmount;
		this.TxnDate = txnDate;
		this.Amount = amount;
		this.clientName = clientName;
	}

	public Long getId() {
		return this.id;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public BigDecimal getChargeAmount() {
		return this.chargeAmount;
	}

	public String getTxnDate() {
		return this.TxnDate;
	}

	public BigDecimal getAmount() {
		return this.Amount;
	}

	public String getClientName() {
		return this.clientName;
	} 
	
	
	
		
}
