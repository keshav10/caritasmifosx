package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentCloseDateOnOrAfterInvestmentStartDateException extends AbstractPlatformDomainRuleException{

	public InvestmentCloseDateOnOrAfterInvestmentStartDateException(){
		super("Investment close date should be on or after investment start date","");
	}
}
