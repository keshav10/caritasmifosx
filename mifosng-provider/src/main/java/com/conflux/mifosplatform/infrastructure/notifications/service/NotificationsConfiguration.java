/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conflux.mifosplatform.infrastructure.notifications.repository.SmsNotificationRepository;


/**
 * Configuration for Notifications.
 * @see NotificationsProperties about how to configure Notifications
 */
public class NotificationsConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(NotificationsConfiguration.class);

	private static NotificationsConfiguration INSTANCE = null ;
	
	private Properties notificationConfigs;
	
    private MVayooSMSSender mVayooSMSSender;
    private AfricasTalkingSMSSender africasTalkingSMSSender;
	
    public void logDebug (String logStr) {
    	logger.debug(logStr);
    }

    public final static NotificationsConfiguration getInstance() {
    	synchronized (NotificationsConfiguration.class) {
			if(INSTANCE == null) INSTANCE = new NotificationsConfiguration() ;
			InputStream in = INSTANCE.getClass().getClassLoader()
					.getResourceAsStream("config/Notifications.properties");
			INSTANCE.notificationConfigs = new Properties();
			try {
				INSTANCE.notificationConfigs.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return INSTANCE ;
    }

    private  NotificationsConfiguration() {
    	mVayooSMSSender = new MVayooSMSSender() ;
    	africasTalkingSMSSender = new AfricasTalkingSMSSender() ;
    }
    
	public SMSSender getSenderForSMSProvider() {
		SMSSender smsSender = null;
		
		if (getNotificationConfig("smsprovider").equals("mVaayoo")) {
			smsSender = mVayooSMSSender;
		} else if (getNotificationConfig("smsprovider").equals("smsZone")) {
			logger.info("Not yet implemented via smsZone");
		} else if (getNotificationConfig("smsprovider")
				.equals("africastalking")) {
			smsSender = africasTalkingSMSSender;
		} else {
			logger.info("Unknown SMS Provider");
		}
		
		return smsSender;
	}
	
	public String getNotificationConfig(String key) {
		return (String) notificationConfigs
				.get("com.conflux.mifosplatform.notifications." + key);
	}

	public boolean checkIfNotificationConfigEnabled(String key) {
		return ((String) notificationConfigs
				.get("com.conflux.mifosplatform.notifications." + key))
				.equals("yes");
	}

}