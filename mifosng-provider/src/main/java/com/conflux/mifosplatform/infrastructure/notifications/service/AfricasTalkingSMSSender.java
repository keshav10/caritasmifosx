package com.conflux.mifosplatform.infrastructure.notifications.service;

import africastalking.sms.AfricasTalkingGateway;
import org.json.*;

public class AfricasTalkingSMSSender extends AbstractSMSSender implements
		SMSSender {

	protected AfricasTalkingSMSSender() {

	}

	@Override
	public JSONArray sendmsg(String to, String message) {

		// Uses Africa's talking JAVA classes to send SMS

		String user = getSMSConfig("africastalking.user");				
		String key =getSMSConfig("africastalking.key");
				
		JSONArray response=null; 
		try {
			
			AfricasTalkingGateway gwy = new AfricasTalkingGateway(user,key);
			response=gwy.sendMessage(to, message);			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public void send(String to, String message) {
		// TODO Auto-generated method stub
		
	}

}
