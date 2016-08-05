package org.mifosplatform.portfolio.loanaccount.guarantor.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when self guarantor doing undo transaction
 * of saving account which is associated with released other guarantor of his/her loan
 *  and if that loan is closed.
 */
public class UndoReleasedGuarantorNotAllowed extends AbstractPlatformDomainRuleException{
	 public UndoReleasedGuarantorNotAllowed(Long loanAccountNumber) {
	        super("Undo transaction is not allowed as its associated with"
	        		+ " released other guarantor for loan account : "+ loanAccountNumber + " which is either closed or other"
	        				+ " guarantor savings account doesn't have sufficient balance to hold the funds","");
	    }
}
