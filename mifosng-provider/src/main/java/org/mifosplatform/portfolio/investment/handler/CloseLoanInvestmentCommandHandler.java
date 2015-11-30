package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CloseLoanInvestmentCommandHandler implements NewCommandSourceHandler{

	  final InvestmentWritePlatformService loanInvestment;

	@Autowired
	public CloseLoanInvestmentCommandHandler(InvestmentWritePlatformService loanInvestment){
		this.loanInvestment = loanInvestment;
	}
	  
	@Override
    @Transactional
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.loanInvestment.closeLoanInvestment(command.getLoanId(), command);
	}
}
