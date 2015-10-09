package org.mifosplatform.batch.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class TransactionRollBackException extends AbstractPlatformResourceNotFoundException{

    public TransactionRollBackException(final String action, final String postFix, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super(action,postFix,defaultUserMessage,defaultUserMessageArgs);
    }

}
