package org.mifosplatform.portfolio.investment.service;

import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.portfolio.investment.data.LoanInvestmentData;
import org.mifosplatform.portfolio.investment.data.SavingInvestmentData;


public interface InvestmentReadPlatformService {

    List<SavingInvestmentData> retriveLoanAccountsBySavingId(final Long savingId) throws SQLException;
    List<Long> retriveLoanIdBySavingId(final Long savingId);
    Long retriveSavingInvestmentId(final Long savingId, Long loanId);
    List<LoanInvestmentData> retriveSavingAccountsByLoanId(final Long laonId);
    Long retriveLoanInvestmentId(final Long loanId, Long svingId);
    List<Long> retriveSavingIdByLoanId(final Long loanId);
}
