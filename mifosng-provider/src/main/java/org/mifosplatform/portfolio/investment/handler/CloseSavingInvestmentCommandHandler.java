package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CloseSavingInvestmentCommandHandler implements NewCommandSourceHandler{

	  final InvestmentWritePlatformService savingInvestment;
	  
	@Autowired  
	public CloseSavingInvestmentCommandHandler(InvestmentWritePlatformService savingInvestment){
		super();
		this.savingInvestment = savingInvestment;
	}

	@Override
    @Transactional
	public CommandProcessingResult processCommand(JsonCommand command) {
		 return this.savingInvestment.closeSavingInvestment(command.getSavingsId(),command);

	}
}
