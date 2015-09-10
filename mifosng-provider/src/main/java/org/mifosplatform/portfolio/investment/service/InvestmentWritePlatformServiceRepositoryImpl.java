package org.mifosplatform.portfolio.investment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.investment.domain.Investment;
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.investment.exception.InvestmentAlreadyExistsException;
import org.mifosplatform.portfolio.investment.exception.MoreThanLoanAmmountException;
import org.mifosplatform.portfolio.investment.exception.NoFundsAvailableException;
import org.mifosplatform.portfolio.savings.data.SavingsAccountData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountSummaryData;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.mifosplatform.portfolio.savings.service.SavingsAccountReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    
       
  
    @Autowired
    public InvestmentWritePlatformServiceRepositoryImpl(PlatformSecurityContext context,
            InvestmentRepositoryWrapper repositoryWrapper, SavingsAccountRepositoryWrapper savingsaccount,
            LoanRepositoryWrapper loanRepository, SavingsAccountRepository savingAccount,
            InvestmentReadPlatformService savingInvestment,
            SavingsAccountReadPlatformServiceImpl savingAccountDetails,
           LoanReadPlatformService loanReadPlatformService) {

        this.context = context;
        this.repositoryWrapper = repositoryWrapper;
        this.loanRepository = loanRepository;
        this.savingsaccount = savingsaccount;
        this.savingAccount = savingAccount;
        this.savingInvestment = savingInvestment;
        this.savingAccountDetails = savingAccountDetails;
        this.loanReadPlatformService = loanReadPlatformService;
        
    }

    @Override
    public CommandProcessingResult addSavingsInvestment(Long savingsId, JsonCommand command) {
        // TODO Auto-generated method stub

        // final AppUser user = this.context.authenticatedUser();

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

        Long id = null;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;

        if (loanIds != null) {
            for (String Id : loanIds) {
                id = Long.parseLong(Id);
                loanId.add(id);
            }
            for(String investment : investedAmounts){
                totalAmount = Long.parseLong(investment);
                
                investedAmount.add(totalAmount);
              }
        }

        existingLoanIds = this.savingInvestment.retriveLoanIdBySavingId(savingId);
        savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId);
        
        for(Long investment :savingInvestedAmount){
        	savingSum = savingSum + investment;       	
        }
        
        SavingsAccountData savingBalance = savingAccountDetails.retrieveOne(savingId);        
        BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
       
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
        	}
        	else{
        		check++;
        	}
        	}

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
                	}
                	else{
                		check++;
                	}
                }
                else{
                	throw new InvestmentAlreadyExistsException();
                }

            }
        }

        for (int i = 0; i < newLoanIds.size(); i++) {
        	if(check == 0){
            Investment savingInvestment = new Investment(savingId, newLoanIds.get(i), newInvestedAmount.get(i));
            this.repositoryWrapper.save(savingInvestment);
        	}
        	else{
        		throw new NoFundsAvailableException();
        	}

        }

        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult addLoanInvestment(Long loanId, JsonCommand command) {
        // TODO Auto-generated method stub

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
        Long id = null;
        Long totalAmount = null;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;
        if (savingIds != null) {
            for (String Id : savingIds) {
                id = Long.parseLong(Id);
                savingId.add(id);         
            }
            for(String investment : investedAmounts){
              totalAmount = Long.parseLong(investment);
              investedAmount.add(totalAmount);
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
                    }
                    else{
                    	check++;
                    }
                }
                else{
                	throw new InvestmentAlreadyExistsException();
                }
            }
        }
        
        
        for (int i = 0; i < newSavingId.size(); i++) {
        	
        	if(check == 0){
            Investment savingInvestment = new Investment(newSavingId.get(i), loanid, newInvestedAmount.get(i));
            this.repositoryWrapper.save(savingInvestment);
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
    	 
    	Long id = null;
    	Long newid = null;
    	Long loanSum = 0L;
    	Long savingSum = 0L;
    	

    	Long loanId = command.longValueOfParameterNamed("loanId");
        Long ammount = command.longValueOfParameterNamed("investedAmounts");
        Long oldAmount = command.longValueOfParameterNamed("oldAmount");
        Long oldLoanId = command.longValueOfParameterNamed("oldLoanId");
        
        id = this.savingInvestment.retriveSavingInvestmentId(savingsAccountId, oldLoanId);
         Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
         
         
         
         final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
         final Map<String, Object> changes = investment.update(command);
         int x=changes.size();
         if (changes.containsKey(InvestmentConstants.loanIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.loanIdParamName);
            newid = this.savingInvestment.retriveSavingInvestmentId(savingsAccountId, newValue);
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
            if((newValue <= loanAccountBalance.longValue()-loanSum+oldAmount)&&(newValue<=savingAccountBalance.longValue()-savingSum+oldAmount) )
             investment.updateInvestedAmount(newValue);
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
        
        
        id = this.savingInvestment.retriveLoanInvestmentId(loanId,oldSavingId);
         Investment loanInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
         
         
         
         final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
         final Map<String, Object> changes = investment.update(command);
         int x=changes.size();
         if (changes.containsKey(InvestmentConstants.savingIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.savingIdParamName);
            newid = this.savingInvestment.retriveLoanInvestmentId(loanId,newValue);
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
    public CommandProcessingResult deleteInvestmentBasedOnMapping(Long savingId, Long loanId) {

        Long id = null;

        id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId);

        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        this.repositoryWrapper.delete(savingInvestment);
 
        // TODO Auto-generated method stub
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult deleteLoanInvestment(Long loanId, Long savingId) {

        Long id = null;
        id = this.savingInvestment.retriveLoanInvestmentId(loanId, savingId);
        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        this.repositoryWrapper.delete(savingInvestment);

        return new CommandProcessingResultBuilder().build();
    }

}
