package org.mifosplatform.portfolio.accountdetails;

import java.math.BigDecimal;

public class PaymentDetailCollectionData {

	private final BigDecimal amount;
	private final String TransactionDate;
	private final String receiptNumber;

	public PaymentDetailCollectionData(BigDecimal amount,
			String transactionDate, String receiptNumber) {
		super();
		this.amount = amount;
		this.TransactionDate = transactionDate;
		this.receiptNumber = receiptNumber;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public String getTransactionDate() {
		return this.TransactionDate;
	}

	public String getReceiptNumber() {
		return this.receiptNumber;
	}

}
