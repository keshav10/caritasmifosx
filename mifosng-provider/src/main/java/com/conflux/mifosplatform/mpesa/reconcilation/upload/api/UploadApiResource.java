package com.conflux.mifosplatform.mpesa.reconcilation.upload.api;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



import com.conflux.mifosplatform.mpesa.reconcilation.upload.command.FileCommand;
import com.conflux.mifosplatform.mpesa.reconcilation.upload.service.UploadWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/uploadxls")
@Component
@Scope("singleton")

public class UploadApiResource {
	private final UploadWritePlatformService uploadWritePlatformService;
		
		@Autowired
		public UploadApiResource(final UploadWritePlatformService uploadWritePlatformService){
			this.uploadWritePlatformService = uploadWritePlatformService;
			
		}
		
	    
	
		@POST
	    @Consumes({MediaType.MULTIPART_FORM_DATA})
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String uploadTransactionDetails(@HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
	            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart, @FormDataParam("officeId") final String officeId) {
			
	
			final FileCommand fileCommand = new FileCommand(officeId,fileDetails.getFileName(),fileSize, bodyPart.getMediaType().toString(),null );
	
			 final String result = this.uploadWritePlatformService.uploadDetails(fileCommand, inputStream);
	    	
	         return result;
	    
	    }
	
	
	}


