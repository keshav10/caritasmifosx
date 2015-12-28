package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingAccountNotClosedDueToInvestmentException extends AbstractPlatformDomainRuleException{

	public SavingAccountNotClosedDueToInvestmentException() {
		super("Closing of this saving account is not allowed since it linked with some active investment","");
	}

}
