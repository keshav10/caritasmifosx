package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DeleteSavingInvestmentCommandHandler implements NewCommandSourceHandler{

    private final InvestmentWritePlatformService savingInvestment;
  
    
    @Autowired
    public DeleteSavingInvestmentCommandHandler(InvestmentWritePlatformService savingInvestment) {
        super();
        // TODO Auto-generated constructor stub
        this.savingInvestment = savingInvestment;
    }




    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
       return this.savingInvestment.deleteSavingInvestment(command.getSavingsId(), command);
    }
    

}
