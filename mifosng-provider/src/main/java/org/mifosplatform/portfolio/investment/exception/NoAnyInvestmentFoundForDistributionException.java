package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoAnyInvestmentFoundForDistributionException extends AbstractPlatformDomainRuleException{

	public NoAnyInvestmentFoundForDistributionException() {
		super("No any investment earning found for investment distribution", "");
		// TODO Auto-generated constructor stub
	}

	
	
}
