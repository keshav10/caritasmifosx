package org.mifosplatform.portfolio.investment.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.portfolio.investment.data.LoanInvestmentData;
import org.mifosplatform.portfolio.investment.data.SavingInvestmentData;


public interface InvestmentReadPlatformService {

    List<SavingInvestmentData> retriveLoanAccountsBySavingId(final Long savingId) throws SQLException;
    List<Long> retriveLoanIdBySavingId(final Long savingId);
    List<Long> retriveInvestedAmountBySavingId(final Long savingId);
    List<Long> retriveInvestedAmountByLoanId(final Long loanId);
    Long retriveSavingInvestmentId(final Long savingId, Long loanId, String startDate);
    List<LoanInvestmentData> retriveSavingAccountsByLoanId(final Long laonId);
    Long retriveLoanInvestmentId(final Long loanId, Long svingId, String startDate);
    List<Long> retriveSavingIdByLoanId(final Long loanId);
    
    Integer retriveSavingInvestmentIdForClose(final Long savingId, Long loanId, String startDate);
    Long retriveSavingInvestmentIdForUpdate(final Long savingId, Long loanId, String startDate);
    
    Long retriveLoanInvestmentIdForUpdate(final Long loanId, Long savingId, String startDate);
    
}
