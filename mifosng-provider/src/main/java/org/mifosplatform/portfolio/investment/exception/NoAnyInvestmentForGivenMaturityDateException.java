package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoAnyInvestmentForGivenMaturityDateException  extends AbstractPlatformDomainRuleException{
	public NoAnyInvestmentForGivenMaturityDateException() {
		super("No Any Investment Earning Found For Given Maturity Date", "");
		// TODO Auto-generated constructor stub
	}
}
