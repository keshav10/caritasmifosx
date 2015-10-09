/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.accountdetails.data;

import java.util.Collection;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeSummaryData;
import org.mifosplatform.portfolio.savings.data.SavingsChargesSummaryData;

/**
 * Immutable data object representing a summary of various accounts.
 */
public class AccountSummaryCollectionData {

    @SuppressWarnings("unused")
    private final Collection<LoanAccountSummaryData> loanAccounts;
    @SuppressWarnings("unused")
    private final Collection<SavingsAccountSummaryData> savingsAccounts;
    @SuppressWarnings("unused")
    private final Collection<LoanAccountSummaryData> memberLoanAccounts;
    @SuppressWarnings("unused")
    private final Collection<SavingsAccountSummaryData> memberSavingsAccounts;
    @SuppressWarnings("unused")
    private final Collection<CodeValueData> paymentTypeOptions;    
    @SuppressWarnings("unused")
	private final Collection<LoanChargeSummaryData>loanCharges;    
    @SuppressWarnings("unused")
	private final Collection<SavingsChargesSummaryData>savingsCharges;
    
   

    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<CodeValueData> paymentTypeOptions) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.memberLoanAccounts = null;
        this.memberSavingsAccounts = null;
        this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);
        this.loanCharges = null;
		this.savingsCharges = null;
    }

    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<LoanAccountSummaryData> memberLoanAccounts,
            final Collection<SavingsAccountSummaryData> memberSavingsAccounts, final Collection<CodeValueData> paymentTypeOptions) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.memberLoanAccounts = defaultLoanAccountsIfEmpty(memberLoanAccounts);
        this.memberSavingsAccounts = defaultSavingsAccountsIfEmpty(memberSavingsAccounts);
        this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);
        this.loanCharges = null;
		this.savingsCharges = null;
    }
    

    public AccountSummaryCollectionData(
		final Collection<LoanAccountSummaryData> loanAccounts,
		final Collection<SavingsAccountSummaryData> savingsAccounts,
		final Collection<CodeValueData> paymentTypeOptions,
		final Collection<LoanChargeSummaryData> loanCharges,
		final Collection<SavingsChargesSummaryData> savingsCharges,
		final Collection<LoanAccountSummaryData> memberLoanAccounts,
        final Collection<SavingsAccountSummaryData> memberSavingsAccounts) {
		super();
		this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
		this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
		this.paymentTypeOptions = defaultPaymentTypeIfEmpty(paymentTypeOptions);		   
		this.loanCharges = loanCharges;
		this.savingsCharges = savingsCharges;
		this.memberLoanAccounts = null;
        this.memberSavingsAccounts = null;  
	}

	private Collection<LoanAccountSummaryData> defaultLoanAccountsIfEmpty(final Collection<LoanAccountSummaryData> collection) {
        Collection<LoanAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    private Collection<SavingsAccountSummaryData> defaultSavingsAccountsIfEmpty(final Collection<SavingsAccountSummaryData> collection) {
        Collection<SavingsAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }
    
    private Collection<CodeValueData> defaultPaymentTypeIfEmpty(final Collection<CodeValueData> collection) {
        Collection<CodeValueData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

}