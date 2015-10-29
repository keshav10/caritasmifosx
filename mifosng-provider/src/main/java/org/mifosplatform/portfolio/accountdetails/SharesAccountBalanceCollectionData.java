package org.mifosplatform.portfolio.accountdetails;

import java.math.BigDecimal;

public class SharesAccountBalanceCollectionData {

	private final String accountNo;
	private final BigDecimal accountBalance;

	public SharesAccountBalanceCollectionData(String accountNo,
			BigDecimal accountBalance) {
		super();
		this.accountNo = accountNo;
		this.accountBalance = accountBalance;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public BigDecimal getAccountBalance() {
		return this.accountBalance;
	}

}
