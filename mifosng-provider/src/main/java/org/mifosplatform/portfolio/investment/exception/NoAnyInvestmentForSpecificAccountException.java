package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoAnyInvestmentForSpecificAccountException  extends AbstractPlatformDomainRuleException{

	public NoAnyInvestmentForSpecificAccountException() {
		super("No Any Investment Earning For Given Account Number", "");
		// TODO Auto-generated constructor stub
	}
}
