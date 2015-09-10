package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class MoreThanLoanAmmountException extends AbstractPlatformDomainRuleException {
	public MoreThanLoanAmmountException(){
		super("error.msg.more.loanammount","investing more than loanamount");
	}

}
