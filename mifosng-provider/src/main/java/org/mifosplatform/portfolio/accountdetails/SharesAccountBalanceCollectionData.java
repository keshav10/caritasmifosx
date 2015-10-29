package org.mifosplatform.portfolio.accountdetails;

import java.math.BigDecimal;

public class SharesAccountBalanceCollectionData {

	private final String accountNo;
	private final BigDecimal accountBalance;
	private final String firstName;
	private final String branch;
	public SharesAccountBalanceCollectionData(String accountNo,
			BigDecimal accountBalance, String firstName, String branch) {
		super();
		this.accountNo = accountNo;
		this.accountBalance = accountBalance;
		this.firstName = firstName;
		this.branch = branch;
	}
	public String getAccountNo() {
		return this.accountNo;
	}
	public BigDecimal getAccountBalance() {
		return this.accountBalance;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getBranch() {
		return this.branch;
	}
   
	
}
