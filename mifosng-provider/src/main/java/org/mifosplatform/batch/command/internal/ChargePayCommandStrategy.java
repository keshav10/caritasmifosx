package org.mifosplatform.batch.command.internal;

import javax.ws.rs.core.UriInfo;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.savings.api.SavingsAccountChargesApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChargePayCommandStrategy  implements CommandStrategy {
	
	private final SavingsAccountChargesApiResource savingsAccountChargesApiResource;
 
	@Autowired
	public ChargePayCommandStrategy(
			final SavingsAccountChargesApiResource savingsAccountChargesApiResource) {		
		this.savingsAccountChargesApiResource = savingsAccountChargesApiResource;
	}
	@Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());
        
        final String[] pathParameters = request.getRelativeUrl().split("/");
        Long savingsAccountId = Long.parseLong(pathParameters[1]);
        Long chargeId= Long.parseLong(pathParameters[3].substring(0,pathParameters[3].indexOf("?")));
        String command="paycharge";        
        try
        {
        	responseBody =	savingsAccountChargesApiResource.payOrWaiveSavingsAccountCharge
        			       (savingsAccountId,chargeId,command,request.getBody());
        	
        	response.setStatusCode(200);
        	response.setBody(responseBody);
        }
        catch (RuntimeException e) {

            // Gets an object of type ErrorInfo, containing information about
            // raised exception
            ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(ex.getMessage());
        }

        return response;	 
	 
   }
}	
