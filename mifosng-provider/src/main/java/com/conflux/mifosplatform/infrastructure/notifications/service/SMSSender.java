package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.util.Properties;
import org.json.*;

public interface SMSSender {
	public JSONArray sendmsg (String to, String message);
	public void  send(String to, String message);
}
