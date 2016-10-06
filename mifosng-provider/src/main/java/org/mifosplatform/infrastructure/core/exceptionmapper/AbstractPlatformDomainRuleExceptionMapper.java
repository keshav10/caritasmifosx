package org.mifosplatform.infrastructure.core.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.mifosplatform.infrastructure.core.data.ApiGlobalErrorResponse;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AbstractPlatformDomainRuleExceptionMapper implements ExceptionMapper<AbstractPlatformDomainRuleException> {

	@Override
    public Response toResponse(final AbstractPlatformDomainRuleException exception) {
        		final ApiGlobalErrorResponse platformDomainRuleviolationResponse = ApiGlobalErrorResponse.domainRuleViolation(
                exception.getGlobalisationMessageCode(), exception.getDefaultUserMessage(), exception.getDefaultUserMessageArgs());     
                return Response.status(Status.FORBIDDEN).entity(platformDomainRuleviolationResponse).type(MediaType.APPLICATION_JSON).build();
    		
	}
}
