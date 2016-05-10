package com.conflux.mifosplatform.mpesa.reconcilation.upload.service;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;




import com.conflux.mifosplatform.mpesa.reconcilation.upload.command.FileCommand;


import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.http.NameValuePair;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

@Service
public class UploadWritePlatformServiceJpaRepositoryImpl implements UploadWritePlatformService {

	 @Transactional
	 	 @Override
	 	    public String uploadDetails(FileCommand fileCommand,InputStream inputStream) {
	 		 StringBuffer result  =null;
	 		 ArrayList conflictMpesaCode = new ArrayList();
	 		
	 			try {
	 				 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	 			        StringBuilder out = new StringBuilder();
	 			        
	 			        String line;
	 			        int count = 0;
	 			        String destination=null,operator = null,amount,type,targetDate;
	 			         while ((line = reader.readLine()) != null) {
	 			        	 line = line.replace(",,", ", ,");
	 			        	 String[] removecommas = line.split("\"");
	 			        	 int j =removecommas.length;
	 			        	 if(j>=3){
	 			        		
	 			        		 while(j>=3){
	 			        		
	 			        			 removecommas[j-2] = removecommas[j-2].replace("," , "");
	 			        			 j=j-2;
	 			        		 }
	 			        		 for(int i=0;i<removecommas.length-1;i++){
	 			        		removecommas[i+1] = removecommas[i].concat(removecommas[i+1]);
	 			        		 }
	 			        		 line = removecommas[removecommas.length-1];
	 			        		
	 			        	 }
	 			        	 
	 		            out.append(line + "\n");
	 		            final String[] tokens = line.split(",");
	 		            
	 		            if((count == 1)&&(!tokens[1].equals(" "))){		  
	 		            	String[] senderandmobilenumber = {"null","null"} ;
	 		            	
	 		            	String url = "https://localhost:9292/mpesa/transactiondetail"; // this is for local host
	 		            //	String url = "http://localhost:9292/caritasmpesa/mpesa/transactiondetail";       //this for remote host
	 		            	
	 						SSLContext sslContext = null;
	 						try {
	 							sslContext = SSLContext.getInstance("SSL");
	 						} catch (NoSuchAlgorithmException e1) {
	 							// TODO Auto-generated catch block
	 							e1.printStackTrace();
	 						}
	 						try {
	 							sslContext.init(null, new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
	 						} catch (KeyManagementException e1) {
	 							// TODO Auto-generated catch block
	 							e1.printStackTrace();
	 						}
	  		        	//HttpClient client = HttpClientBuilder.create().setSSLContext(sslContext).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
	 						HttpClient client = HttpClientBuilder.create().build();
	 			        	HttpPost post = new HttpPost(url);
	 			        	
	 			     /*   	
	 			        	for(int i=0;i<tokens.length;i++){
	 			        		
	 			        		if(tokens[i].equalsIgnoreCase("Other Party Info")){
	 			        			
	 			        			if(tokens[i].equals(" ")){
	 			        				
	 			        				senderandmobilenumber[0] = " ";
	 		 			        		senderandmobilenumber[1] = " ";
	 			        			}else{
	 			        				senderandmobilenumber = tokens[i].split(" - ");
	 			        			}
	 			        			
	 			        		}
	 			        	}
	 			        	*/
	 			       
	 			        	if(tokens[10].equals(" ")){
	 			        		senderandmobilenumber[0] = " ";
	 			        		senderandmobilenumber[1] = " ";
	 			      
	 			        	}else{
	 			        		  senderandmobilenumber = tokens[10].split(" - ");
	 			        		
	 			        	}
	 			      
	 			        	
	 			        	
	 			        	
	 			        	if(tokens[5].equals(" ")){
	 			        		amount =tokens[6].substring(1);
	 			        		type = "WithDraw";
	 			        	
	 			        	}else{
	 			        		amount = tokens[5];
	 			        		type = "PaidIn";	
	 			        	}
	 
	  
	                        SimpleDateFormat[] source = new SimpleDateFormat[]{new SimpleDateFormat("dd-MM-yyyy HH:mm"), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"),
	                        		new SimpleDateFormat("dd/MM/yyyy HH:mm"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
	                        		new SimpleDateFormat("yyyy-MM-dd HH:mm")
	                        };
	                        
	                        SimpleDateFormat target = new SimpleDateFormat("MM/dd/yyyy");
	                        Date date = null;
	                        for(int i=0; i<source.length; i++){
	                        	boolean dateFormated = true;
	                        	try{
	                        		
	                        		 date = source[i].parse(tokens[1]);
	                        		
	                        	}catch(Exception e){
	                        		dateFormated = false;
	                        		continue;
	                        		
	                        	}    
	                        	
	                        	if(dateFormated == true){
	                        		break;
	                        	}
	                        	
	                        }
	                        
	 
	                        
	                         targetDate = target.format(date);
	 			        
	 			        	List<NameValuePair> urlParameters = new ArrayList<>();
	 			        	urlParameters.add(new BasicNameValuePair("id", destination));
	 			        	urlParameters.add(new BasicNameValuePair("orig", "Mpesa"));
	 			        	urlParameters.add(new BasicNameValuePair("dest", destination));
	 			        	urlParameters.add(new BasicNameValuePair("tstamp", tokens[1]));
	 			        	urlParameters.add(new BasicNameValuePair("text", tokens[3]));
	 			        	urlParameters.add(new BasicNameValuePair("user", "caritas"));
	 			        	urlParameters.add(new BasicNameValuePair("pass", "nairobi"));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_code", tokens[0]));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_acc", ""));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_msisdn", senderandmobilenumber[0]));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_trx_date",targetDate));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_trx_time", tokens[1]));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_trx_type", type));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_amt", amount));
	 			        	urlParameters.add(new BasicNameValuePair("mpesa_sender", senderandmobilenumber[1]));
	 			        	urlParameters.add(new BasicNameValuePair("office_Id" ,fileCommand.getOfficeId()));
	 			        	
	 			        	post.setEntity(new UrlEncodedFormEntity(urlParameters));
	 
	 			        	HttpResponse response = client.execute(post);
	 			        	
	 			        	
	 
	 			         	BufferedReader rd = new BufferedReader(
	 			        	        new InputStreamReader(response.getEntity().getContent()));
	 
	 			        	 result = new StringBuffer();
	 			        	 line = "";
	 			        	 
	 			        	
	 			       
	 			        while ((line = rd.readLine()) != null) {
	 			        	if(line.indexOf("CONFLICT:") > -1){
	 			        		String[] temp = line.split(":");
	 			        		conflictMpesaCode.add(temp[1]);
	 			        	}
	 	
	 	        		      result.append(line);
	 			        	}
	 			        	
	 		            }
	 			      
	 				if(tokens[0].equalsIgnoreCase("Short Code:")){
	 		            	 destination = tokens[1];
	 		            }else if(tokens[0].equalsIgnoreCase("Receipt No.")){
	 		            	count++;	
	 		            }
	 		            
	 		            
	 		        }
	 		     
	 		        reader.close();
	 			       
	 			
	 			} catch (Exception e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			}
	 			
	 			String mpesaCode = null;
	 			if(conflictMpesaCode.size() >0 ){
	 				mpesaCode = "CONFLICT:" + conflictMpesaCode.toString();
	 			}else{
	 				mpesaCode = result.toString();
	 			}
	 			
	 			return mpesaCode; 
	 		 
	 	 }
	 
	 private static class DefaultTrustManager implements X509TrustManager {


	        @Override
	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
	    }

	 
	 }
	
	

