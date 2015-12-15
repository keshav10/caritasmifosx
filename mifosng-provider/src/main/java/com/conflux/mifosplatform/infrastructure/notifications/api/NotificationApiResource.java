package com.conflux.mifosplatform.infrastructure.notifications.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("notification")
@Component
@Scope("singleton")
public class NotificationApiResource {

	private final PlatformSecurityContext context;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;

	@Autowired
	public NotificationApiResource(
			final PlatformSecurityContext context,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ToApiJsonSerializer<ClientData> toApiJsonSerializer) {
		this.context = context;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;

	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String update(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.sendNotification()//
				.withJson(apiRequestBodyAsJson) //
				.build(); //

		final CommandProcessingResult result = this.commandsSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

}
