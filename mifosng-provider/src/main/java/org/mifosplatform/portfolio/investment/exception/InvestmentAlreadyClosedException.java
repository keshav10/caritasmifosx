package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvestmentAlreadyClosedException extends AbstractPlatformDomainRuleException{
	public InvestmentAlreadyClosedException(){
		
		super( " Investment already closed ", "");
		
	}
	
}
