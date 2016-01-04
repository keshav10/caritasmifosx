package org.mifosplatform.portfolio.investment.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.service.LoanProductReadPlatformServiceImpl;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.investment.api.InvestmentConstants;
import org.mifosplatform.portfolio.investment.data.InvestmentDataValidator;
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.investment.domain.Investment;
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.investment.exception.InvestmentAlreadyExistsException;
import org.mifosplatform.portfolio.investment.exception.InvestmentCloseDateOnOrAfterInvestmentStartDateException;
import org.mifosplatform.portfolio.investment.exception.MoreThanLoanAmmountException;
import org.mifosplatform.portfolio.investment.exception.NoFundsAvailableException;
import org.mifosplatform.portfolio.savings.data.SavingsAccountData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountSummaryData;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.mifosplatform.portfolio.savings.service.SavingsAccountReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class InvestmentWritePlatformServiceRepositoryImpl implements InvestmentWritePlatformService {

    private final PlatformSecurityContext context;
    private final InvestmentRepositoryWrapper repositoryWrapper;
    private final SavingsAccountRepositoryWrapper savingsaccount;
    private final SavingsAccountRepository savingAccount;
    private final LoanRepositoryWrapper loanRepository;
    private final InvestmentReadPlatformService savingInvestment;
    private final SavingsAccountReadPlatformServiceImpl savingAccountDetails;
    private final LoanReadPlatformService loanReadPlatformService;
    private final InvestmentDataValidator fromApiJsonDeserializer;
    private final InvestmentReadPlatformService investmentReadPlatformService;
    
    
       
  
    @Autowired
    public InvestmentWritePlatformServiceRepositoryImpl(PlatformSecurityContext context,
            InvestmentRepositoryWrapper repositoryWrapper, SavingsAccountRepositoryWrapper savingsaccount,
            LoanRepositoryWrapper loanRepository, SavingsAccountRepository savingAccount,
            InvestmentReadPlatformService savingInvestment,
            SavingsAccountReadPlatformServiceImpl savingAccountDetails,
           LoanReadPlatformService loanReadPlatformService,
           InvestmentDataValidator fromApiJsonDeserializer,
           InvestmentReadPlatformService investmentReadPlatformService) {

        this.context = context;
        this.repositoryWrapper = repositoryWrapper;
        this.loanRepository = loanRepository;
        this.savingsaccount = savingsaccount;
        this.savingAccount = savingAccount;
        this.savingInvestment = savingInvestment;
        this.savingAccountDetails = savingAccountDetails;
        this.loanReadPlatformService = loanReadPlatformService;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.investmentReadPlatformService = investmentReadPlatformService;
        
    }

    @Override
    public CommandProcessingResult addSavingsInvestment(Long savingsId, JsonCommand command){
        // TODO Auto-generated method stub

        this.context.authenticatedUser();
         
        this.fromApiJsonDeserializer.validateForCreateSavingInvestment(command.json());

        final Long savingId = command.longValueOfParameterNamed("savingId");
        final String[] loanIds = command.arrayValueOfParameterNamed("loanId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> loanId = new ArrayList<Long>();
        List<Long> existingLoanIds = new ArrayList<Long>();
        List<Long> newLoanIds = new ArrayList<Long>();
        List<Long> newInvestedAmount = new ArrayList<Long>();
        Long totalAmount = null;
        
        
        final String[] startDate = command.arrayValueOfParameterNamed("startDate");
        
        final JsonArray dateL = command.arrayOfParameterNamed("startDate");        
        final List<Date> sDate = new ArrayList<Date>();
        final List<Date> cDate = new ArrayList<Date>();
        final List<Date> newStartDate = new ArrayList<Date>();
        final List<Date> newCloseDate = new ArrayList<Date>();
        
        DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        
        BigDecimal minRequiredBalance = BigDecimal.ZERO;
        SavingsAccount account = this.savingAccount.findOne(savingId);
        BigDecimal availableMinBal = BigDecimal.ZERO;
        
        if(account.getMinRequiredBalance()!=null){
        	availableMinBal = account.getMinRequiredBalance();
        };
        
        
        Long totalInvestment = 0L;

        Long id = 0L;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;
        boolean isSavingInvestmentIsAlreadyDoneWithSameDate = false;
        
        Date date = new Date();
     
        if (loanIds != null){
			 for (String Id : loanIds) {
				        id = Long.parseLong(Id);
				        loanId.add(id);
				    }
	   	    for(String investment : investedAmounts){
		        totalAmount = Long.parseLong(investment);	
		        	investedAmount.add(totalAmount);
		    }
		     for(String start : startDate){
			
		    	 try {
				 date = formateDate.parse(start);
				
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		    	  sDate.add( date);
				 
			 }
		
        existingLoanIds = this.savingInvestment.retriveLoanIdBySavingId(savingId);
        savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId);
        
        for(Long investment :savingInvestedAmount){
        	savingSum = savingSum + investment;       	
        }
        
        SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId);        
        BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
       
        
        if(!(savingAccountBalance.compareTo(BigDecimal.ZERO)>0)){
        	throw new NoFundsAvailableException();
        }
        
        
        
        
        if (existingLoanIds.isEmpty()) {       	
        	for(int i = 0; i < investedAmount.size(); i++){
        		loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
            	for(Long investment :loanInvestedAmount){
                	loanSum = loanSum + investment;       	
                }
            	
            LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));  
            BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
        
            if(investedAmount.get(i) <= (savingAccountBalance.longValue()-savingSum)&&(investedAmount.get(i)<=(loanAccountBalance.longValue()-loanSum))){
        	        newLoanIds.add(loanId.get(i));
                    newInvestedAmount.add(investedAmount.get(i));
                    savingSum = savingSum + investedAmount.get(i);
                    minRequiredBalance = new BigDecimal(savingSum);
                    newStartDate.add(sDate.get(i));
                  //  newCloseDate.add(cDate.get(i));
        	 }
        	else{
        	    	check++;
        	    } 
        	}
        	
        	BigDecimal newMinBal = BigDecimal.ZERO;
        	newMinBal = availableMinBal.add(minRequiredBalance);
        	
       	
            account.setMinRequiredBalance(newMinBal);
            this.savingAccount.save(account);

        }

        else {
            for (int i = 0; i < loanId.size(); i++) {
                int count = 0;
                for (int j = 0; j < existingLoanIds.size(); j++) {
                    if (loanId.get(i).equals(existingLoanIds.get(j))) {
                        //
                        count++;
                    }                    
                }

                if (count == 0) {
                	loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
                	for(Long investment :loanInvestedAmount){
                    	loanSum = loanSum + investment;       	
                    }
                	
                	LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));  
                    BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
                    
                	
                	if((investedAmount.get(i)<= (savingAccountBalance.longValue()-savingSum))&&(investedAmount.get(i)<=(loanAccountBalance.longValue()-loanSum))){
                         newLoanIds.add(loanId.get(i));
                         newInvestedAmount.add(investedAmount.get(i));
                         savingSum = savingSum + investedAmount.get(i);
                        // minRequiredBalance = new BigDecimal(savingSum);
                         newStartDate.add(sDate.get(i));
                 	}
                	else{
                		 check++;
                	}
                    totalInvestment = totalInvestment + investedAmount.get(i);
                }
                /*else{
                	throw new InvestmentAlreadyExistsException();
                }*/

               if(count>0){
            	   
            	   LocalDate investmentStartDate = new LocalDate(sDate.get(i));
            	   isSavingInvestmentIsAlreadyDoneWithSameDate = this.investmentReadPlatformService.isSavingInvestmentAlreadyDoneWithSameDate(savingId, investmentStartDate);
            	   if(!(isSavingInvestmentIsAlreadyDoneWithSameDate)){
            		   
            		   loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
                   	for(Long investment :loanInvestedAmount){
                       	loanSum = loanSum + investment;       	
                       }
                   	
                   	LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));  
                       BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
                       
                   	
                   	if((investedAmount.get(i)<= (savingAccountBalance.longValue()-savingSum))){
                            newLoanIds.add(loanId.get(i));
                            newInvestedAmount.add(investedAmount.get(i));
                            savingSum = savingSum + investedAmount.get(i);
                           // minRequiredBalance = new BigDecimal(savingSum);
                            newStartDate.add(sDate.get(i));
                    	}
                   	else{
                   		 check++;
                   	}
                       totalInvestment = totalInvestment + investedAmount.get(i);
            	   }
            	   
            	   
               }
                
                
            }
            
        	//BigDecimal newMinBal = availableMinBal.add(minRequiredBalance);
            BigDecimal availbaleMinBalance = account.getMinRequiredBalance();
            BigDecimal newBalance = availbaleMinBalance.add(new BigDecimal(totalInvestment));
            account.setMinRequiredBalance(newBalance);
            this.savingAccount.save(account);
        }

        for (int i = 0; i < newLoanIds.size(); i++) {
        	if(check == 0 || isSavingInvestmentIsAlreadyDoneWithSameDate == false){
                   Investment savingInvestment = new Investment(savingId, newLoanIds.get(i), newInvestedAmount.get(i), newStartDate.get(i), null);
                   this.repositoryWrapper.save(savingInvestment);
        	}
        	else{
        		throw new NoFundsAvailableException();
        	}

        }

       
      }
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult addLoanInvestment(Long loanId, JsonCommand command){
        // TODO Auto-generated method stub

    	
    	this.context.authenticatedUser();
    	this.fromApiJsonDeserializer.validateForCreateLoanInvestment(command.json());
    	
        Long loanid = command.longValueOfParameterNamed("loanId");
        final String[] savingIds = command.arrayValueOfParameterNamed("savingId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> savingId = new ArrayList<Long>();
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
        List<Long> existingSavingId = new ArrayList<Long>();
        List<Long> newSavingId = new ArrayList<Long>();
        List<Long> newInvestedAmount = new ArrayList<Long>();        
        Long id = 0L;
        Long totalAmount = 0L;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;
        boolean isLoanInvestmentIsAlreadyDoneWithSameDate = false;
        

        
        final String[] startDate = command.arrayValueOfParameterNamed("startDate");
        
        final List<Date> sDate = new ArrayList<Date>();
        final List<Date> cDate = new ArrayList<Date>();
        final List<Date> newStartDate = new ArrayList<Date>();
        final List<Date> newCloseDate = new ArrayList<Date>();
        Date date = new Date();
        
        
        BigDecimal minRequiredBalance = BigDecimal.ZERO;
      
        Long totalInvestment = 0L;
        
        
        DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        
        if (savingIds != null) {
            for (String Id : savingIds) {
                id = Long.parseLong(Id);
                savingId.add(id);         
            }
            for(String investment : investedAmounts){
              totalAmount = Long.parseLong(investment);
              investedAmount.add(totalAmount);
            }
            
            for(String start : startDate){

		    	 try {
					date = formateDate.parse(start);
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		    	  sDate.add( date);

            	
            	
            }
		
        }

        existingSavingId = this.savingInvestment.retriveSavingIdByLoanId(loanid);
        loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanid);
        
        for(Long investment :loanInvestedAmount){
        	loanSum = loanSum + investment;       	
        }
        
        LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanid);  
        BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
       
        
        if(existingSavingId.isEmpty()){
        	for(int i = 0; i < investedAmount.size(); i++){
        		savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
            	for(Long investment :savingInvestedAmount){
                	savingSum = savingSum + investment;       	
                }
            	
            	SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId.get(i));        
                BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
    	if(investedAmount.get(i) <= (loanAccountBalance.longValue()-loanSum)&&investedAmount.get(i) <= (savingAccountBalance.longValue()-savingSum)){
    		newSavingId.add(savingId.get(i));
            newInvestedAmount.add(investedAmount.get(i));
            loanSum = loanSum + investedAmount.get(i);
            newStartDate.add(sDate.get(i));
          //  newCloseDate.add(cDate.get(i));
    	  }
      else{
    		check++;
         }
       	}
       }
        else{
            
            for (int i = 0; i < savingId.size(); i++) {
                int count = 0;
                for (int j = 0; j < existingSavingId.size(); j++) {
                    if (savingId.get(i).equals(existingSavingId.get(j))) {
                        //
                        count++;
                    }
                }

                if (count == 0) {
                	savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
                	for(Long investment :savingInvestedAmount){
                    	savingSum = savingSum + investment;       	
                    }
                	
                	SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId.get(i));        
                    BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
                    if(investedAmount.get(i) <= (loanAccountBalance.longValue()-loanSum)&&investedAmount.get(i) <= (savingAccountBalance.longValue()-savingSum)){
                    newSavingId.add(savingId.get(i));
                    newInvestedAmount.add(investedAmount.get(i));
                    loanSum = loanSum + investedAmount.get(i);
                    newStartDate.add(sDate.get(i));
                //    newCloseDate.add(cDate.get(i));
                    }
                    else{
                    	check++;
                    }
                }
                
                
                if(count>0){

                   LocalDate investmentStartDate = new LocalDate(sDate.get(i));
                  
                   isLoanInvestmentIsAlreadyDoneWithSameDate = this.investmentReadPlatformService.isLoanInvestmentAlreadyDoneOnSameDate(loanid, investmentStartDate);
                   if(!(isLoanInvestmentIsAlreadyDoneWithSameDate)){
                	   
                	   
                	   savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
                   	for(Long investment :savingInvestedAmount){
                       	savingSum = savingSum + investment;       	
                       }
                   	
                       SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId.get(i));        
                       BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
                       if(investedAmount.get(i) <= (loanAccountBalance.longValue()-loanSum)&&investedAmount.get(i) <= (savingAccountBalance.longValue()-savingSum)){
                       newSavingId.add(savingId.get(i));
                       newInvestedAmount.add(investedAmount.get(i));
                       loanSum = loanSum + investedAmount.get(i);
                       newStartDate.add(sDate.get(i));
                	   
                    }
                   
                   }
                
                }    
               /* else{
                	throw new InvestmentAlreadyExistsException();
                }*/
                
                totalInvestment = investedAmount.get(i) + totalInvestment;
            }
        }
        
        
        for (int i = 0; i < newSavingId.size(); i++) {
        	  SavingsAccount account = this.savingAccount.findOne(newSavingId.get(i));
              BigDecimal availableMinBal = account.getMinRequiredBalance();
              BigDecimal investementAmount = new BigDecimal(newInvestedAmount.get(i));
              BigDecimal newMinBal = availableMinBal.add(investementAmount);
              account.setMinRequiredBalance(newMinBal);
             
        	
        	if(check == 0 || isLoanInvestmentIsAlreadyDoneWithSameDate==false){
            Investment savingInvestment = new Investment(newSavingId.get(i), loanid, newInvestedAmount.get(i),
            		newStartDate.get(i), null);
              this.repositoryWrapper.save(savingInvestment);
              this.savingAccount.save(account);
        	}
        	else{
        		throw new NoFundsAvailableException();
        	}

        }

        return new CommandProcessingResultBuilder().build();
        
    }
    
    @Override
    
    public CommandProcessingResult updateSavingInvestment(Long savingsAccountId,JsonCommand command){
    	List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
    	 
    	Long id = 0L;
    	Long newid = 0L;
    	Long loanSum = 0L;
    	Long savingSum = 0L;
    	Date date = new Date ();

    	Long loanId = command.longValueOfParameterNamed("loanId");
        Long ammount = command.longValueOfParameterNamed("investedAmounts");
        Long oldAmount = command.longValueOfParameterNamed("oldAmount");
        Long oldLoanId = command.longValueOfParameterNamed("oldLoanId");
        String investmentStartDate = command.stringValueOfParameterNamed("startDate");
        SavingsAccount account = this.savingAccount.findOne(savingsAccountId);
        BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
      
        
        id = this.savingInvestment.retriveSavingInvestmentIdForUpdate(savingsAccountId, oldLoanId,investmentStartDate);
        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        Long availableInvestedAmount = savingInvestment.getInvestedAmount();
         
         
         final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
         final Map<String, Object> changes = investment.update(command);
         int x=changes.size();
         if (changes.containsKey(InvestmentConstants.loanIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.loanIdParamName);
            newid = this.savingInvestment.retriveSavingInvestmentId(savingsAccountId, newValue, null);
            if(newid == null)
             investment.updateLoanId(newValue);
            else
            	throw new InvestmentAlreadyExistsException();
         }
         if (changes.containsKey(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME)){
             final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME);
             SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingsAccountId);        
             BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
             savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingsAccountId);
             
             for(Long savingammount :savingInvestedAmount){
             	savingSum = savingSum + savingammount;       	
             }
             
             LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId);  
             BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
             loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId);             
             for(Long loanammount :loanInvestedAmount){
             	loanSum = loanSum + loanammount;       	
             }
            if((newValue <= loanAccountBalance.longValue()-loanSum+oldAmount)&&(newValue<=savingAccountBalance.longValue()-savingSum+oldAmount) ){

            	BigDecimal newMinBalance = null;
            	if(ammount > availableInvestedAmount){
            		Long newMinBal = ammount - availableInvestedAmount;
            		newMinBalance = availableMinRequiredBal.add(new BigDecimal(newMinBal));
            	}
            	else if(ammount < availableInvestedAmount){
            		Long newMinBal = availableInvestedAmount - ammount;
            		newMinBalance = availableMinRequiredBal.subtract(new BigDecimal(newMinBal));
            	}
            	else if(ammount == availableInvestedAmount){
            		newMinBalance = new BigDecimal(ammount);
            	}
            	
            	account.setMinRequiredBalance(newMinBalance);
            	this.savingAccount.saveAndFlush(account);
                investment.updateInvestedAmount(newValue);
            } 
            else{
            	if(newValue > (loanAccountBalance.longValue()-loanSum+oldAmount))
            		throw new MoreThanLoanAmmountException();
            	if(newValue>savingAccountBalance.longValue()-savingSum+oldAmount)
            		throw new NoFundsAvailableException();
            	
            }
            
         }
         if (!changes.isEmpty()) {
             this.repositoryWrapper.saveAndFlush(investment);
         }

  
		return  new CommandProcessingResultBuilder().build();
    	
    }
    
    @Override
    
    public CommandProcessingResult updateLoanInvestment(Long loanId,JsonCommand command){
    	List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
    	 
    	Long id = null;
    	Long newid = null;
    	Long loanSum = 0L;
    	Long savingSum = 0L;
    	

    	Long savingId = command.longValueOfParameterNamed("savingId");
        Long ammount = command.longValueOfParameterNamed("investedAmounts");
        Long oldAmount = command.longValueOfParameterNamed("oldAmount");
        Long oldSavingId = command.longValueOfParameterNamed("oldSavingId");
        
        String startDate = command.stringValueOfParameterNamed("startDate");
        
        id = this.savingInvestment.retriveLoanInvestmentIdForUpdate(loanId,oldSavingId,startDate);
         Investment loanInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
         
         
         
         final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
         final Map<String, Object> changes = investment.update(command);
         int x=changes.size();
         if (changes.containsKey(InvestmentConstants.savingIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.savingIdParamName);
            newid = this.savingInvestment.retriveLoanInvestmentId(loanId,newValue, startDate);
            if(newid == null)
             investment.updateSavingId(newValue);
            else
            	throw new InvestmentAlreadyExistsException();
         }
         if (changes.containsKey(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME)){
        	 final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME);
        	 SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId);        
             BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
             savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId);
             
             for(Long savingammount :savingInvestedAmount){
             	savingSum = savingSum + savingammount;       	
             }
             
             LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId);  
             BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
             loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId);             
             for(Long loanammount :loanInvestedAmount){
             	loanSum = loanSum + loanammount;       	
             }

             if((newValue <= loanAccountBalance.longValue()-loanSum+oldAmount)&&(newValue<=savingAccountBalance.longValue()-savingSum+oldAmount) ){
            	 investment.updateInvestedAmount(newValue);
             }
             else{
            	 if(newValue > (loanAccountBalance.longValue()-loanSum+oldAmount))
             		throw new MoreThanLoanAmmountException();
             	if(newValue>savingAccountBalance.longValue()-savingSum+oldAmount)
             		throw new NoFundsAvailableException();
            	 
             }
            
            
         }
         if (!changes.isEmpty()) {
             this.repositoryWrapper.saveAndFlush(investment);
         }

  
		return  new CommandProcessingResultBuilder().build();
    	
    }
    
    

    @Override
    public CommandProcessingResult deleteSavingInvestment(Long savingId, JsonCommand command) {

        int id;
        Long idAsLongValue = 0L;
        String startDate = command.stringValueOfParameterNamed("startDate");
        Long loanId = command.longValueOfParameterNamed("loanId");
        id = this.savingInvestment.retriveSavingInvestmentIdForClose(savingId, loanId, startDate);
        idAsLongValue = new Long(id);
        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(idAsLongValue);
        Long investedAmount = savingInvestment.getInvestedAmount();
        
        SavingsAccount account = this.savingAccount.findOne(savingId);
        BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
        BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
       /* 
        Long minBalance = newMinBal.longValue();

        if(minBalance >= 0){
           account.setMinRequiredBalance(newMinBal);
           this.savingAccount.save(account);
        } */
        this.repositoryWrapper.delete(savingInvestment);
 
        // TODO Auto-generated method stub
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult deleteLoanInvestment(Long loanId, JsonCommand command) {

        Long id = null;
        Long savingId = command.longValueOfParameterNamed("savingId");
        String startDate = command.stringValueOfParameterNamed("startDate");
        id = this.savingInvestment.retriveLoanInvestmentId(loanId, savingId, startDate);
        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        this.repositoryWrapper.delete(savingInvestment);

        return new CommandProcessingResultBuilder().build();
    }

    
    @Override
    public CommandProcessingResult closeSavingInvestment(Long savingId, JsonCommand command){
    	
  //  	Long id = 0L;
    	Long loanId = command.longValueOfParameterNamed("loanId");
    	String closeDate = command.stringValueOfParameterNamed("closeDate");
    	String startDate = command.stringValueOfParameterNamed("startDate");
    //	String startDate = new String();
    	
        DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            	
        try {
        
        	
        	
        	Date closedDate = formateDate.parse(closeDate);
        	Date startedDate = formateDate.parse(startDate);
        	
        	LocalDate investmentClosedDate = new LocalDate(closedDate);
        	LocalDate investmentStartedDate = new LocalDate(startedDate);
        	
        	if(investmentClosedDate.isAfter(investmentStartedDate) || investmentClosedDate.equals(investmentStartedDate)){
        	
			Long id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId, investmentStartedDate);
	    	Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
	        savingInvestment.setCloseDate(closedDate);
	        Long investedAmount = savingInvestment.getInvestedAmount();
	        SavingsAccount account = this.savingAccount.findOne(savingId);
	        
	        
	        
	        BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
	        BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
            Long minBal = newMinBal.longValue();
	        
	        if(minBal>=0){
	             account.setMinRequiredBalance(newMinBal);
	             this.savingAccount.save(account);
	        }  
	        
	        
	    	
	    	this.repositoryWrapper.save(savingInvestment);
          }else{
        	  throw new InvestmentCloseDateOnOrAfterInvestmentStartDateException();
          }
		} catch (ParseException e) {
			// TODO Auto-generated catch blocky
			e.printStackTrace();
		}
        
        return new CommandProcessingResultBuilder().build();
    }

	@Override
	public CommandProcessingResult closeLoanInvestment(Long loanId,
			JsonCommand command) {

        Long savingId = command.longValueOfParameterNamed("savingId");
        String closeDate = command.stringValueOfParameterNamed("closeDate");
    	String startDate = command.stringValueOfParameterNamed("startDate");
    	
    	DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        
    	try {
        		
        	Date closedDate = formateDate.parse(closeDate);
        	Date startedDate = formateDate.parse(startDate);
        	
        	LocalDate investmentClosedDate = new LocalDate(closedDate);
        	LocalDate investmentStartDate = new LocalDate(startedDate);
        	
        	if(investmentClosedDate.isAfter(investmentStartDate) || investmentClosedDate.equals(investmentStartDate)){
        	
        	
			Long id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId, investmentStartDate);
	    	Investment loanInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
	        loanInvestment.setCloseDate(closedDate);
	        Long investedAmount = loanInvestment.getInvestedAmount();
	        SavingsAccount account = this.savingAccount.findOne(savingId);
	        BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
	        BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
            Long minBal = newMinBal.longValue();
	        
	        if(minBal>=0){
	             account.setMinRequiredBalance(newMinBal);
	             this.savingAccount.save(account);
	        }  
	        
	    	this.repositoryWrapper.save(loanInvestment);
	    	
		 }else{
			 throw new InvestmentCloseDateOnOrAfterInvestmentStartDateException();
		 }
    	}	catch (ParseException e) {
			// TODO Auto-generated catch blocky
			e.printStackTrace();
		}
        
        return new CommandProcessingResultBuilder().build();
		
	}
}
