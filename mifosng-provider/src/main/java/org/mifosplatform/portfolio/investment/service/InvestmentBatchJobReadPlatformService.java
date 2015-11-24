package org.mifosplatform.portfolio.investment.service;

import java.util.List;

import org.mifosplatform.portfolio.investment.data.InvestmentBatchJobData;

public interface InvestmentBatchJobReadPlatformService {
	
	List<InvestmentBatchJobData> validateForInvestmentSplit(String[] productId, String Date, String investmentId);
	List<InvestmentBatchJobData> getAllInvestementDataWithMaturedStatus(Long investmentId);
	List<InvestmentBatchJobData> getInvestmentIdsWithMaturedStatus(Long investmentId);
	List<InvestmentBatchJobData> getAllInvestmentIdsWithMaturedStatus();
	
	InvestmentBatchJobData getInterestDetails();
	InvestmentBatchJobData getLoanClosedDate(Long loanId);
    
	InvestmentBatchJobData getPaymentType();
	
	InvestmentBatchJobData getInvestmentStatus(Long investmentId);
	InvestmentBatchJobData getLoanIdStatus(Long loanId);
	
	InvestmentBatchJobData getTotalInvestedAmount(Long loanId);
}
