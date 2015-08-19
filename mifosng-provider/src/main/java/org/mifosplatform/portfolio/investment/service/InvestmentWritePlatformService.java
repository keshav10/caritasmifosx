package org.mifosplatform.portfolio.investment.service;

import java.sql.SQLException;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface InvestmentWritePlatformService {

    
    CommandProcessingResult addSavingsInvestment(Long savingsId, JsonCommand command);
    CommandProcessingResult deleteInvestmentBasedOnMapping(Long savingId, Long loanId);
    CommandProcessingResult deleteLoanInvestment(Long loanId, Long savingId);
    CommandProcessingResult addLoanInvestment(Long loanId, JsonCommand command);
    
}
