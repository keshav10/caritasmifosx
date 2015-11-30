/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.accountdetails.service;

import java.util.Collection;
import java.util.Date;

import org.mifosplatform.portfolio.accountdetails.PaymentDetailCollectionData;
import org.mifosplatform.portfolio.accountdetails.SharesAccountBalanceCollectionData;
import org.mifosplatform.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.mifosplatform.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.mifosplatform.portfolio.accountdetails.data.MpesaTransactionSummaryData;

public interface AccountDetailsReadPlatformService {

    public AccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId);

    public AccountSummaryCollectionData retrieveGroupAccountDetails(final Long groupId);

    public Collection<LoanAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(final Long clientId, final Long loanOfficerId);

    public Collection<LoanAccountSummaryData> retrieveGroupLoanAccountsByLoanOfficerId(final Long groupId, final Long loanOfficerId);

    public Collection<PaymentDetailCollectionData> retrivePaymentDetail(final Long clientId); 

    public Collection<SharesAccountBalanceCollectionData> retriveSharesBalance(final Long clientId);

    public Collection<MpesaTransactionSummaryData>retriveMpesaTransactionDetail(Long clientId,String TxnDate,String ReceiptNo);

    public AccountSummaryCollectionData retriveClientAccountAndChargeDetails(final Long clientId,final String chargeonDate);

}
