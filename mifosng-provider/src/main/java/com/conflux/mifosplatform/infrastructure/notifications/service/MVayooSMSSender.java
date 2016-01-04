package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.pentaho.reporting.libraries.formula.util.URLEncoder;

public class MVayooSMSSender extends AbstractSMSSender implements SMSSender {

	protected MVayooSMSSender() {
		super(); 
	}

	@Override
	public void  send(String to, String message) {
		
		/* EXAMPLE
		http://api.mVaayoo.com/mvaayooapi/MessageCompose?
			user=Username:password&
			senderID=mVaayoo&
			receipientno=mobilenumber&
			msgtxt=TestMessage&
			state=4 */
		
		String baseurl = getSMSConfig("mVayoo.url");  
		String user = getSMSConfig("mVayoo.user");
		String password = getSMSConfig("mVayoo.password");
		String sender = getSMSConfig("mVayoo.sender");
		String state = getSMSConfig("mVayoo.state");
		
		try {
			
			String parameters = 
					"user=" + URLEncoder.encode(user + ":" + password, "UTF-8") + "&" +
					"senderID=" + URLEncoder.encode(sender, "UTF-8") + "&" +
					"receipientno=" + URLEncoder.encode(to, "UTF-8") + "&" +
					"dcs=0&" + 
					"msgtxt=" + URLEncoder.encode(message, "UTF-8") + "&" +
					"state=" + URLEncoder.encode(state, "UTF-8")
				;
			
			String urlToSend = baseurl + parameters;
				
			sendHttpGet (urlToSend);
		
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public JSONArray sendmsg(String to, String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
