package org.mifosplatform.portfolio.investment.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.investment.api.InvestmentConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class InvestmentDataValidator {

	 private final FromJsonHelper fromApiJsonHelper;
	 
	 @Autowired
	 public InvestmentDataValidator(final FromJsonHelper fromApiJsonHelper){
		 this.fromApiJsonHelper = fromApiJsonHelper;
	 }
	 
	 public void validateForCreateSavingInvestment(final String json){
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		 
		 final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		  this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, InvestmentConstants.CREATE_SAVING_INVESTMENT_REQUEST_PARAMETERS);

		  final JsonElement element = this.fromApiJsonHelper.parse(json);
		  
		  final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		  
		  final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
           .resource(InvestmentConstants.INVESTMET_RESOURCE_NAME);

		   
		  final Long savingId = this.fromApiJsonHelper.extractLongNamed(InvestmentConstants.savingIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.savingIdParamName).value(savingId).notNull();
		  
		  
		  final String[] loanId = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.loanIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.loanIdParamName).value(loanId).arrayNotEmpty();
		  
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.startInvestmentDateParamName, element)){
			  final String[] investmentStartDate = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.startInvestmentDateParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.startInvestmentDateParamName).value(investmentStartDate).arrayNotEmpty();
		  }
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.invesetedAmountParamName, element)){
			  final String[] investedAmount = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.invesetedAmountParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.invesetedAmountParamName).value(investedAmount).arrayNotEmpty();
		  }
		  
		  throwExceptionIfValidationWarningsExist(dataValidationErrors);
		  
	 }
	 
	 
	 public void validateForCreateLoanInvestment(final String json){
		 
		 final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		  this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, InvestmentConstants.CREATE_LOAN_INVESTMENT_REQUEST_PARAMETERS);

		  final JsonElement element = this.fromApiJsonHelper.parse(json);
		  
		  final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		  
		  final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
          .resource(InvestmentConstants.INVESTMET_RESOURCE_NAME);
		  
		  final Long loanId = this.fromApiJsonHelper.extractLongNamed(InvestmentConstants.loanIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.loanIdParamName).value(loanId).notNull();
		  
		  
		  final String[] savingId = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.savingIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.savingIdParamName).value(savingId).arrayNotEmpty();
		  
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.startInvestmentDateParamName, element)){
			  final String[] investmentStartDate = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.startInvestmentDateParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.startInvestmentDateParamName).value(investmentStartDate).arrayNotEmpty();
		  }
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.invesetedAmountParamName, element)){
			  final String[] investedAmount = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.invesetedAmountParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.invesetedAmountParamName).value(investedAmount).arrayNotEmpty();
		  }
		  
		  throwExceptionIfValidationWarningsExist(dataValidationErrors);
		  
	 }
	 
	   private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) {
	            //
	            throw new PlatformApiDataValidationException(dataValidationErrors);
	        }
	    }
}
