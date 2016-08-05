/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.guarantor.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.savings.domain.DepositAccountOnHoldTransaction;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_guarantor_transaction")
public class GuarantorFundingTransaction extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "guarantor_fund_detail_id", nullable = false)
    private GuarantorFundingDetails guarantorFundingDetails;

    @ManyToOne
    @JoinColumn(name = "loan_transaction_id", nullable = true)
    private LoanTransaction loanTransaction;
    
    @ManyToOne
    @JoinColumn(name = "saving_transaction_id", nullable = true)
    private SavingsAccountTransaction savingTransaction;

	@OneToOne
    @JoinColumn(name = "deposit_on_hold_transaction_id", nullable = false)
    private DepositAccountOnHoldTransaction depositAccountOnHoldTransaction;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    protected GuarantorFundingTransaction() {}

    public GuarantorFundingTransaction(final GuarantorFundingDetails guarantorFundingDetails, final LoanTransaction loanTransaction,
            final DepositAccountOnHoldTransaction depositAccountOnHoldTransaction, final SavingsAccountTransaction savingTransaction ) {
        this.depositAccountOnHoldTransaction = depositAccountOnHoldTransaction;
        this.guarantorFundingDetails = guarantorFundingDetails;
        this.loanTransaction = loanTransaction;
        this.reversed = false;
        this.savingTransaction = savingTransaction;
    }

    public void reverseTransaction() {
        if (!this.reversed) {
            this.reversed = true;
            BigDecimal amountForReverse = this.depositAccountOnHoldTransaction.getAmount();
            this.depositAccountOnHoldTransaction.reverseTransaction();
            if (this.depositAccountOnHoldTransaction.getTransactionType().isRelease()) {
                this.guarantorFundingDetails.undoReleaseFunds(amountForReverse);
            }
        }
    }
    
  //following code change for if undo the deposit amount then release guarantor amount has to be undo (other guarantor)
    public void reverseTransactionIfDepositUndoTxn(){
    	   if(!this.reversed){
    			   this.reversed = true;
    			   BigDecimal amountForReverse = this.depositAccountOnHoldTransaction.getAmount();
    			   this.depositAccountOnHoldTransaction.reverseTxnIfUndoDepositTxn(amountForReverse);
    			   if(this.depositAccountOnHoldTransaction.getTransactionType().isRelease()){
    				   this.guarantorFundingDetails.undoReleaseFunds(amountForReverse);
    			   }
    		   }
       }
    
    //following code change if undo the self saving on hold amount transaction then on hold amount has to reduce to last txn amount.(self guarantor)
    public void undoDepositSavingAccTxnThenUndoOnhold(BigDecimal undoTxnAmount){
    	if(!this.reversed){
    		this.reversed = true;
    		BigDecimal selfRemainingAmount = this.guarantorFundingDetails.getAmountRemaining();
    		BigDecimal newSelfRemainingAmount = selfRemainingAmount.subtract(undoTxnAmount);
    		if(newSelfRemainingAmount.longValue()>0){
    			this.guarantorFundingDetails.setAmountRemaining(newSelfRemainingAmount);
    		}else{
    			this.guarantorFundingDetails.setAmountRemaining(BigDecimal.ZERO);
    		}
    		this.depositAccountOnHoldTransaction.removedOnholdsFundsWithTxnAmount(undoTxnAmount);
    	}
    }
  
   
	public DepositAccountOnHoldTransaction getDepositAccountOnHoldTransaction() {
		return this.depositAccountOnHoldTransaction;
	}

	public void setDepositAccountOnHoldTransaction(
			DepositAccountOnHoldTransaction depositAccountOnHoldTransaction) {
		this.depositAccountOnHoldTransaction = depositAccountOnHoldTransaction;
	}

	public GuarantorFundingDetails getGuarantorFundingDetails() {
		return this.guarantorFundingDetails;
	}

	public void setGuarantorFundingDetails(
			GuarantorFundingDetails guarantorFundingDetails) {
		this.guarantorFundingDetails = guarantorFundingDetails;
	}


}
