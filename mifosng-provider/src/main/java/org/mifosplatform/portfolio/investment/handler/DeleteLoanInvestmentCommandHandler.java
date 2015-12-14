package org.mifosplatform.portfolio.investment.handler;


import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 @Service
public class DeleteLoanInvestmentCommandHandler implements NewCommandSourceHandler  {
     
     private final InvestmentWritePlatformService loanInvestment;
    
     @Autowired
     public DeleteLoanInvestmentCommandHandler(InvestmentWritePlatformService loanInvestment) {
         super();
         this.loanInvestment = loanInvestment;
     }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(JsonCommand command) {
       
        return this.loanInvestment.deleteLoanInvestment(command.getLoanId(), command);
    }

   

}
