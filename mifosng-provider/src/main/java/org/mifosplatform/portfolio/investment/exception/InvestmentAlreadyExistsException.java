package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentAlreadyExistsException extends AbstractPlatformDomainRuleException {
	public InvestmentAlreadyExistsException(){
	super("error.msg.loan.exists", "loan with loanId  already exists");
	}

}
