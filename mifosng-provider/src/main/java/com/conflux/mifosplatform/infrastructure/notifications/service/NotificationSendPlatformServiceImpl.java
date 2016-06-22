package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.util.ArrayList;
import java.util.Date;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conflux.mifosplatform.infrastructure.notifications.api.NotificationApiConstants;
import com.conflux.mifosplatform.infrastructure.notifications.data.NotificationDataValidator;
import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotification;
import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotificationDetails;
import com.conflux.mifosplatform.infrastructure.notifications.repository.SmsNotificationDetailRepository;
import com.conflux.mifosplatform.infrastructure.notifications.repository.SmsNotificationRepository;
import com.conflux.mifosplatform.infrastructure.notifications.service.email.EmailSender;
import org.json.*;

@Service
public class NotificationSendPlatformServiceImpl implements NotificationSendPlatformService {

	private final PlatformSecurityContext context;
	private final NotificationDataValidator fromApiJsonDeserializer;
	private final EmailSender emailSender;
	private final SmsNotificationRepository smsNotificationRepository;
	private final SmsNotificationDetailRepository smsNotificationDetailRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationSendPlatformServiceImpl.class);
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private final GenericDataService genericDataService;
	
	@Autowired
	public NotificationSendPlatformServiceImpl(
			final PlatformSecurityContext context, 
			final NotificationDataValidator fromApiJsonDeserializer,
			final EmailSender emailSender,
			final SmsNotificationRepository smsNotificationRepository,
			final SmsNotificationDetailRepository smsNotificationDetailRepository,
			final ReadWriteNonCoreDataService readWriteNonCoreDataService,
			final GenericDataService genericDataService) {
			
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.emailSender = emailSender;
		this.smsNotificationRepository = smsNotificationRepository;
		this.smsNotificationDetailRepository = smsNotificationDetailRepository;
		this.readWriteNonCoreDataService=readWriteNonCoreDataService;
		this.genericDataService=genericDataService;
	}
	
	@Override
	public CommandProcessingResult sendNotification(JsonCommand command) {
		// TODO Auto-generated method stub
		final AppUser currentUser = this.context.authenticatedUser();

        this.fromApiJsonDeserializer.validateForSend(command.json());
        
        final String type = command.stringValueOfParameterNamed(NotificationApiConstants.type);
        final String targetValueFromJson = command.stringValueOfParameterNamed(NotificationApiConstants.target);
        final String subject = command.stringValueOfParameterNamed(NotificationApiConstants.subject);
        final String message = command.stringValueOfParameterNamed(NotificationApiConstants.message);
        final Long officeId=   command.longValueOfParameterNamed(NotificationApiConstants.entitiyId);
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final String target = targetValueFromJson.replaceAll("\\r\\n|\\r|\\n",""); // this is added to removed new line from the number list.
        Boolean enabled =false;
        if (type.equalsIgnoreCase("email")) {
        	emailSender.sendEmail(target, subject, message);
        } else if(type.equalsIgnoreCase("sms")) {
            final GenericResultsetData results = readWriteNonCoreDataService.retrieveDataTableGenericResultSet("OfficeDetails", officeId,
	                null, null);		 
		ArrayList<ResultsetColumnHeaderData> header= (ArrayList<ResultsetColumnHeaderData>) results.getColumnHeaders();
		ArrayList<ResultsetRowData> data  = (ArrayList<ResultsetRowData>) results.getData();
		if(results!=null && header.size()>0&& data.size()>0)
		{		
		for(int i=0;i<header.size();i++)
		{
			if(header.get(i).getColumnName().equalsIgnoreCase("sms_enabled"))
			{				
			  if(data.get(0).getRow().get(i).equals("true"))
				{
					enabled=true;
					break;
				}
				
			}
		}
		}	
		if(enabled){
        	Date now =new Date();
        	
        	final SmsNotification smsNotification=new SmsNotification();
        	
        	smsNotification.setEntity("Notification");
        	smsNotification.setAction("Send");
        	smsNotification.setTenantId(tenantIdentifier);
        	smsNotification.setPayload("manual sms");
        	smsNotification.setProcessed(Boolean.TRUE);
        	smsNotification.setCreatedOn(now);
        	smsNotification.setLastModifiedOn(now);
        	long id =this.smsNotificationRepository.save(smsNotification).getId();
        	SMSSender smsSender = NotificationsConfiguration.getInstance().getSenderForSMSProvider();   
        	String[] clientDetails=target.split(",");

        	for(int i=0;i<clientDetails.length;i++)
        	{
        		String clientName=" ";
            	String clientId=" ";
        		String[] clientMobileNoAndName = clientDetails[i].split("-");
        		int length=clientMobileNoAndName.length;
        		if(length>=2){
        		clientName= clientMobileNoAndName[1];
        		clientId  = clientMobileNoAndName[2];
        		}
        		String mobileNo=clientMobileNoAndName[0];        		
        		logger.info("clientName-" +clientName+"," +"mobileNo"+mobileNo);
        		SmsNotificationDetails SmsNotificationDetails = new SmsNotificationDetails();
        		SmsNotificationDetails.setAction("Send");
        		SmsNotificationDetails.setEntity("Notification");
        		SmsNotificationDetails.setEntity_Mobile_No(mobileNo);
        		SmsNotificationDetails.setEventId(id);
        		SmsNotificationDetails.setPayload("clientName:"+clientName+" "+"MobileNo:"+mobileNo);
        		SmsNotificationDetails.setMessage(message);
        		SmsNotificationDetails.setEntityName("Manual");
        		SmsNotificationDetails.setEntitydescription("clientId:" + clientId + " " + "clientName:" + clientName);
        		SmsNotificationDetails.setTenantId(tenantIdentifier);
        		SmsNotificationDetails.setProcessed(Boolean.FALSE);
        		SmsNotificationDetails.setCreatedOn(now);
        		SmsNotificationDetails.setLastModifiedOn(now);
        		if (mobileNo.equals("null") || mobileNo.equalsIgnoreCase("NA") || mobileNo.equalsIgnoreCase("  ") || mobileNo.length() <= 0) {
        			SmsNotificationDetails.setErrorMessage("Mobile number is not Valid");
                    this.smsNotificationDetailRepository.save(SmsNotificationDetails);
        		}
        		else{
        			try{
           	     JSONArray response =smsSender.sendmsg(mobileNo, message);
           	     
           	     if(response!=null && response.length()!=0){
           	     JSONObject result = response.getJSONObject(0); 
           	      logger.info(result.getString("status") + ","); 
                  logger.info(result.getString("number") + ",");
                  logger.info(result.getString("messageId") + ",");
                  logger.info(result.getString("cost"));
        
                if(result.getString("status").equals("success")||result.getString("status").equalsIgnoreCase("success"))
                {
            	  SmsNotificationDetails.setProcessed(Boolean.TRUE);
                 }
      		     SmsNotificationDetails.setCreatedOn(now);
      		     SmsNotificationDetails.setLastModifiedOn(now);
                 this.smsNotificationDetailRepository.save(SmsNotificationDetails);
           	     }
        			}
        			catch (JSONException e) {
        				e.printStackTrace();
        			}
        		}
        	 
        	}
        }
        }

		return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .build();
	}

	}

