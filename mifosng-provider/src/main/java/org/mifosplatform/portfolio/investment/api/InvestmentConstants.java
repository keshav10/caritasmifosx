package org.mifosplatform.portfolio.investment.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



public class InvestmentConstants {
    
	public static final String INVESTMET_RESOURCE_NAME = "investment";
    public static final String savingIdParamName = "savingId";
    public static final String loanIdParamName = "loanId";
    public static final String SAVINGINVESTMENT_RESOURCE_NAME = "investedAmounts";
    public static final String LOANINVESTMENT_RESOURCE_NAME = "investedAmounts";
    
    public static final String invesetedAmountParamName = "investedAmounts";
    public static final String startInvestmentDateParamName = "startDate";
    public static final String closeInvestmentDateParamName = "closeDate";
    
    
    
    
    //Saving Investment data request parameter
    
    public static final Set<String> CREATE_SAVING_INVESTMENT_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(savingIdParamName,
    		loanIdParamName,startInvestmentDateParamName,invesetedAmountParamName));
    
    public static final Set<String> CREATE_LOAN_INVESTMENT_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(loanIdParamName, savingIdParamName,
    		startInvestmentDateParamName,invesetedAmountParamName));
   
}