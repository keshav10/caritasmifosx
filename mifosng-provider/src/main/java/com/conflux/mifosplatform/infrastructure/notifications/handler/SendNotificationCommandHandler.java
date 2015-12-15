package com.conflux.mifosplatform.infrastructure.notifications.handler;



import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;

import africastalking.sms.AfricasTalkingGateway;

import com.conflux.mifosplatform.infrastructure.notifications.service.NotificationSendPlatformService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@CommandType(entity = "NOTIFICATION", action = "SEND")
public class SendNotificationCommandHandler implements NewCommandSourceHandler {

	private final NotificationSendPlatformService notificationSendPlatformService;
    private static final Logger logger = LoggerFactory.getLogger(SendNotificationCommandHandler.class);

	
	@Autowired
	public SendNotificationCommandHandler (final NotificationSendPlatformService notificationSendPlatformService){
		this.notificationSendPlatformService = notificationSendPlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		logger.info(command.json());
		return notificationSendPlatformService.sendNotification(command);
		
	}

}
