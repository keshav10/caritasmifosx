package org.mifosplatform.portfolio.investment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NoFundsAvailableException extends AbstractPlatformDomainRuleException {
	public NoFundsAvailableException() {
        super("error.msg.funds.NotAvailable","fundsNotAvailable");
    }
	

}
