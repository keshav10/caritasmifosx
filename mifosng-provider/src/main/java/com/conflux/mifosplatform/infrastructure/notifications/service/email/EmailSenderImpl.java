package com.conflux.mifosplatform.infrastructure.notifications.service.email;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderImpl implements EmailSender {
	
	@Override
	public void sendEmail(String toEmail,String subject,String message){
		final StringBuilder subjectBuilder = new StringBuilder().append(subject);
		final String sendToEmail = toEmail;
        final StringBuilder messageBuilder = new StringBuilder().append(message);
		 try {
		 Properties prop=new Properties();
		 InputStream input = new FileInputStream("config/Email.properties");
		 prop.load(input);
	        final String authuserName =prop.getProperty("smtp_user");
	        final String authuser =prop.getProperty("smtp_user");	
	        final String authpwd = prop.getProperty("smtp_password");
	        final String host = prop.getProperty("smtp_host");
	        final String port = prop.getProperty("smtp_port");
	        
	        final Email email = new SimpleEmail();
	        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
	        email.setDebug(false); // true if you want to debug
	        email.setHostName(host);
	        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", true);
	        email.getMailSession().getProperties().put("mail.smtp.host", host);
	        email.getMailSession().getProperties().put("mail.smtp.port", port);
	        email.setFrom(authuser, authuserName);
	        email.setSubject(subjectBuilder.toString());
	        email.setMsg(messageBuilder.toString());
	        email.addTo(sendToEmail);
	        email.send();
	        } catch (final EmailException e) {
	        	e.printStackTrace();
	            System.out.println("excption occurred"+ e);
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	

}
