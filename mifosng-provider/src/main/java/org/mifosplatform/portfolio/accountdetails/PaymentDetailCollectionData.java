package org.mifosplatform.portfolio.accountdetails;


public class PaymentDetailCollectionData {

	private final Long amount;
	private final String TransactionDate;
	private final String receiptNumber;
	private final String firstname;
	private final String branch;

	public PaymentDetailCollectionData(Long amount, String transactionDate,
			String receiptNumber, String firstname, String branch) {
		super();
		this.amount = amount;
		this.TransactionDate = transactionDate;
		this.receiptNumber = receiptNumber;
		this.firstname = firstname;
		this.branch = branch;
	}

	public Long getAmount() {
		return this.amount;
	}

	public String getTransactionDate() {
		return this.TransactionDate;
	}

	public String getReceiptNumber() {
		return this.receiptNumber;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getBranch() {
		return this.branch;
	}

}
