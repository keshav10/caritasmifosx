/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.scheduledjobs.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;

public interface ScheduledJobRunnerService {

    void updateLoanSummaryDetails();

    void updateLoanPaidInAdvance();

    void applyAnnualFeeForSavings();

    void applyDueChargesForSavings() throws JobExecutionException;

    void updateNPA();

    void updateMaturityDetailsOfDepositAccounts();

    void generateRDSchedule();

    void updateClientSubStatus();
    
    void loanRepaymentSmsReminder();
    
    void loanFirstOverdueRepaymentReminder();
    
    void loanSecondOverdueRepaymentReminder();
    
    void loanThirdOverdueRepaymentReminder();
    
    void loanFourthOverdueRepaymentReminder();
    
    void defaultWarningToClients(); 
    
    void defaultWarningToGuarantors();
    
    void dormancyWarningToClients();
    

    void doAppySavingLateFeeCharge() throws JobExecutionException;

    //the following method will call if any user manually wanted to run the job by passing some parameter from front end
    CommandProcessingResult doInvestmentTracker(JsonCommand json);
    
    void distributeInvestmentEarning();
}
