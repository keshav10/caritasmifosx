package org.mifosplatform.portfolio.loanaccount.guarantor.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidGuarantorSavingAccountException extends AbstractPlatformDomainRuleException {

    public InvalidGuarantorSavingAccountException(final String globalisationMessageCode, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super(globalisationMessageCode, defaultUserMessage, defaultUserMessageArgs);
    }

}
