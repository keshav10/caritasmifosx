package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentIsNotClosedException extends AbstractPlatformDomainRuleException {

	public InvestmentIsNotClosedException() {
		super("Investment is not closed", "");
		// TODO Auto-generated constructor stub
	}
	

}
