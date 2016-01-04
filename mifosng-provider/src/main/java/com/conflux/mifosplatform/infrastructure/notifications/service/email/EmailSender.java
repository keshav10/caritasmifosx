package com.conflux.mifosplatform.infrastructure.notifications.service.email;

public interface EmailSender {
	
	public void sendEmail(String toEmail,String subject,String message);

}
