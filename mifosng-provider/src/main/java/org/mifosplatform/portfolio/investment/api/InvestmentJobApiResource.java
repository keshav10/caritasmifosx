package org.mifosplatform.portfolio.investment.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.investment.data.LoanInvestmentData;
import org.mifosplatform.scheduledjobs.service.ScheduledJobRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/investmentBatchJob/")
@Component
@Scope("singleton")
public class InvestmentJobApiResource  {
	
	    private final PlatformSecurityContext context;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	    private final DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService;

	
	    @Autowired
	    public InvestmentJobApiResource(
				PlatformSecurityContext context,
				ApiRequestParameterHelper apiRequestParameterHelper,
				PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
				DefaultToApiJsonSerializer<LoanInvestmentData> apiJsonSerializerService) {
			super();
			this.context = context;
			this.apiRequestParameterHelper = apiRequestParameterHelper;
			this.commandSourceWritePlatformService = commandSourceWritePlatformService;
			this.apiJsonSerializerService = apiJsonSerializerService;
		
		}
		
	    
	    @POST
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String runInvestmentJob(final String apiStringBodyAsJson){
	   
	    	this.context.authenticatedUser().validateHasReadPermission(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME);
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().investmentBatchJob().withJson(apiStringBodyAsJson).build();
	    	final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
	    	
	    	
	    	return this.apiJsonSerializerService.serialize(result);
	    	
	    }
  
	    
	    

}
