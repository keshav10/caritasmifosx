package org.mifosplatform.portfolio.accountdetails;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentDetailCollectionData {

	private final Long amount;
	private final String TransactionDate;
	private final String receiptNumber;
	private final String Type;

	

	
	public String getTransactionDate() {
		return this.TransactionDate;
	}

	public PaymentDetailCollectionData(Long amount,
			String transactionDate, String receiptNumber, String type) {
		super();
		this.amount = amount;
		this.TransactionDate = transactionDate;
		this.receiptNumber = receiptNumber;
		this.Type = type;
	}

	public String getType() {
		return this.Type;
	}

	public Long getAmount() {
		return this.amount;
	}

	public String getReceiptNumber() {
		return this.receiptNumber;
	}

}
