package com.conflux.mifosplatform.infrastructure.notifications.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import com.conflux.mifosplatform.infrastructure.notifications.api.NotificationApiConstants;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class NotificationDataValidator {
	
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public NotificationDataValidator(final FromJsonHelper fromApiJsonHelper){
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	public void validateForSend(final String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, NotificationApiConstants.NOTIFICATION_SEND_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(NotificationApiConstants.NOTIFICATION_RESOURCE_NAME);
        
       

        if (this.fromApiJsonHelper.parameterExists(NotificationApiConstants.type, element)) {
            final String type = this.fromApiJsonHelper.extractStringNamed(NotificationApiConstants.type, element);
            Object[] values = new String[]{"sms", "email"};
            baseDataValidator.reset().parameter(NotificationApiConstants.type).value(type).notNull().isOneOfTheseStringValues(values);
        }

       
        
        final Long entityId = this.fromApiJsonHelper.extractLongNamed(NotificationApiConstants.entitiyId, element);
        baseDataValidator.reset().parameter(NotificationApiConstants.entitiyId).value(entityId).notNull().integerGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
		
	}

}
