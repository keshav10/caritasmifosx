package com.conflux.mifosplatform.infrastructure.notifications.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Service;

public interface NotificationSendPlatformService {
	
	CommandProcessingResult sendNotification(JsonCommand command);

}
