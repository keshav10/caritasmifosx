package com.conflux.mifosplatform.infrastructure.notifications.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NotificationApiConstants {
	
	public static final String NOTIFICATION_RESOURCE_NAME = "notification";
	public static final String type = "type";
    public static final String entityType = "entity_type";
    public static final String entitiyId = "entity_id";
    public static final String target = "target";
    public static final String subject = "subject";
    public static final String message = "message";
	
	public static final Set<String> NOTIFICATION_SEND_REQUEST_DATA_PARAMETERS = new HashSet<String>(
            Arrays.asList(type, entityType, entitiyId, target, subject,
            		message));

}
