package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.scheduledjobs.service.ScheduledJobRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class RunInvestmentBatchJobCommandHandler implements NewCommandSourceHandler {

	private final ScheduledJobRunnerService scheduledJobRunnerService ;
	
	@Autowired 
	public RunInvestmentBatchJobCommandHandler(final ScheduledJobRunnerService scheduledJobRunnerService){
		this.scheduledJobRunnerService = scheduledJobRunnerService;
	}
	
    
	@Override
	public CommandProcessingResult processCommand(JsonCommand command){
    	return this.scheduledJobRunnerService.doInvestmentTracker(command);
    }
	
}
