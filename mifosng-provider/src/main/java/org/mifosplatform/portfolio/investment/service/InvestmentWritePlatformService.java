package org.mifosplatform.portfolio.investment.service;

import java.sql.SQLException;
import java.text.ParseException;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface InvestmentWritePlatformService {

    
    CommandProcessingResult addSavingsInvestment(Long savingsId, JsonCommand command);
    CommandProcessingResult deleteSavingInvestment(Long savingId, JsonCommand command);
    CommandProcessingResult deleteLoanInvestment(Long loanId,JsonCommand command);
    CommandProcessingResult addLoanInvestment(Long loanId, JsonCommand command);
    CommandProcessingResult updateSavingInvestment(Long savingsAccountId,JsonCommand command);
    CommandProcessingResult updateLoanInvestment(Long loanId,JsonCommand command);
    CommandProcessingResult closeSavingInvestment(Long savingId, JsonCommand command);
    CommandProcessingResult closeLoanInvestment(Long loanId, JsonCommand command);
}
