/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.Guarantor;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.GuarantorFundingDetails;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.GuarantorFundingRepository;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.GuarantorFundingTransaction;
import org.mifosplatform.portfolio.loanaccount.guarantor.domain.GuarantorRepository;
import org.mifosplatform.portfolio.loanaccount.service.LoanAssembler;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.paymentdetail.domain.PaymentDetail;
import org.mifosplatform.portfolio.savings.SavingsTransactionBooleanValues;
import org.mifosplatform.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.mifosplatform.portfolio.savings.exception.DepositAccountTransactionNotAllowedException;
import org.mifosplatform.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavingsAccountDomainServiceJpa implements
		SavingsAccountDomainService {

	private final PlatformSecurityContext context;
	private final SavingsAccountRepositoryWrapper savingsAccountRepository;
	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
	private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
	private final JournalEntryWritePlatformService journalEntryWritePlatformService;
	private final ConfigurationDomainService configurationDomainService;

	private final LoanReadPlatformService loanReadPlatformService;
	private final LoanAssembler loanAssembler;
	private final GuarantorRepository guarantorRepository;
	private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
	private final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository;
	private final GuarantorFundingRepository guarantorFundingRepository;
	private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;

	@Autowired
	public SavingsAccountDomainServiceJpa(
			PlatformSecurityContext context,
			SavingsAccountRepositoryWrapper savingsAccountRepository,
			SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
			JournalEntryWritePlatformService journalEntryWritePlatformService,
			ConfigurationDomainService configurationDomainService,
			LoanReadPlatformService loanReadPlatformService,
			LoanAssembler loanAssembler,
			GuarantorRepository guarantorRepository,
			DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository,
			GuarantorFundingRepository guarantorFundingRepository,
			SavingsAccountReadPlatformService savingsAccountReadPlatformService) {
		super();
		this.context = context;
		this.savingsAccountRepository = savingsAccountRepository;
		this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
		this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
		this.journalEntryWritePlatformService = journalEntryWritePlatformService;
		this.configurationDomainService = configurationDomainService;
		this.loanReadPlatformService = loanReadPlatformService;
		this.loanAssembler = loanAssembler;
		this.guarantorRepository = guarantorRepository;
		this.depositAccountOnHoldTransactionRepository = depositAccountOnHoldTransactionRepository;
		this.guarantorFundingRepository = guarantorFundingRepository;
		this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
	}

	@Transactional
	@Override
	public SavingsAccountTransaction handleWithdrawal(
			final SavingsAccount account, final DateTimeFormatter fmt,
			final LocalDate transactionDate,
			final BigDecimal transactionAmount,
			final PaymentDetail paymentDetail,
			final SavingsTransactionBooleanValues transactionBooleanValues) {

		AppUser user = getAppUserIfPresent();
		final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
				.isSavingsInterestPostingAtCurrentPeriodEnd();
		final Integer financialYearBeginningMonth = this.configurationDomainService
				.retrieveFinancialYearBeginningMonth();

		if (transactionBooleanValues.isRegularTransaction()
				&& !account.allowWithdrawal()) {
			throw new DepositAccountTransactionNotAllowedException(
					account.getId(), "withdraw", account.depositAccountType());
		}
		final Set<Long> existingTransactionIds = new HashSet<>();
		final Set<Long> existingReversedTransactionIds = new HashSet<>();
		updateExistingTransactionsDetails(account, existingTransactionIds,
				existingReversedTransactionIds);
		final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(
				fmt, transactionDate, transactionAmount, paymentDetail,
				new Date(), user);
		final SavingsAccountTransaction withdrawal = account.withdraw(
				transactionDTO, transactionBooleanValues.isApplyWithdrawFee(),paymentDetail);

		final MathContext mc = MathContext.DECIMAL64;
		if (account.isBeforeLastPostingPeriod(transactionDate)) {
			final LocalDate today = DateUtils.getLocalDateOfTenant();
			account.postInterest(mc, today,
					transactionBooleanValues.isInterestTransfer(),
					isSavingsInterestPostingAtCurrentPeriodEnd,
					financialYearBeginningMonth);
		} else {
			final LocalDate today = DateUtils.getLocalDateOfTenant();
			account.calculateInterestUsing(mc, today,
					transactionBooleanValues.isInterestTransfer(),
					isSavingsInterestPostingAtCurrentPeriodEnd,
					financialYearBeginningMonth);
		}
		account.validateAccountBalanceDoesNotBecomeNegative(transactionAmount,
				transactionBooleanValues.isExceptionForBalanceCheck());
		saveTransactionToGenerateTransactionId(withdrawal);
		this.savingsAccountRepository.save(account);

		postJournalEntries(account, existingTransactionIds,
				existingReversedTransactionIds,
				transactionBooleanValues.isAccountTransfer());

		return withdrawal;
	}

	private AppUser getAppUserIfPresent() {
		AppUser user = null;
		if (this.context != null) {
			user = this.context.getAuthenticatedUserIfPresent();
		}
		return user;
	}

	@Transactional
	@Override
	public SavingsAccountTransaction handleDeposit(
			final SavingsAccount account, final DateTimeFormatter fmt,
			final LocalDate transactionDate,
			final BigDecimal transactionAmount,
			final PaymentDetail paymentDetail, final boolean isAccountTransfer,
			final boolean isRegularTransaction, final boolean isEarningFromInvestment) {

		AppUser user = getAppUserIfPresent();
		final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
				.isSavingsInterestPostingAtCurrentPeriodEnd();
		final Integer financialYearBeginningMonth = this.configurationDomainService
				.retrieveFinancialYearBeginningMonth();

		if (isRegularTransaction && !account.allowDeposit()) {
			throw new DepositAccountTransactionNotAllowedException(
					account.getId(), "deposit", account.depositAccountType());
		}

		boolean isInterestTransfer = false;
		final Set<Long> existingTransactionIds = new HashSet<>();
		final Set<Long> existingReversedTransactionIds = new HashSet<>();
		updateExistingTransactionsDetails(account, existingTransactionIds,
				existingReversedTransactionIds);
		final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(
				fmt, transactionDate, transactionAmount, paymentDetail,
				new Date(), user);
		
		//following code change from transaction type earning from investment 
		 SavingsAccountTransaction deposit = null;
		if(isEarningFromInvestment == true){
			deposit = account.earningFromInvestment(transactionDTO);
		}else{
			deposit = account.deposit(transactionDTO);
		}

		final MathContext mc = MathContext.DECIMAL64;
		if (account.isBeforeLastPostingPeriod(transactionDate)) {
			final LocalDate today = DateUtils.getLocalDateOfTenant();
			account.postInterest(mc, today, isInterestTransfer,
					isSavingsInterestPostingAtCurrentPeriodEnd,
					financialYearBeginningMonth);
		} else {
			final LocalDate today = DateUtils.getLocalDateOfTenant();
			account.calculateInterestUsing(mc, today, isInterestTransfer,
					isSavingsInterestPostingAtCurrentPeriodEnd,
					financialYearBeginningMonth);
		}

		saveTransactionToGenerateTransactionId(deposit);

		postJournalEntries(account, existingTransactionIds,
				existingReversedTransactionIds, isAccountTransfer);

	//	Long clientId = account.clientId();
		Long savingId = account.getId();

		long isReleaseGuarantor = this.savingsAccountReadPlatformService
				.getIsReleaseGuarantor(savingId);

		List<Long> totalLoanId = null;  // list of loan id is for to get total loan account associated for this self guarantee
		Long loanId = null;
		if(isReleaseGuarantor == 1){
			totalLoanId = this.loanReadPlatformService
					.retriveLoanAccountId(savingId);
			if(!totalLoanId.isEmpty()){
			   loanId = totalLoanId.get(0);
			}
		}
				
		
	    BigDecimal totalTransactionAmount = null;

		if (!(loanId == null) && isReleaseGuarantor == 1) {

			final Loan loan = this.loanAssembler.assembleFrom(loanId);
			
			if(loan.isDisbursed() == true){
			
			final List<Guarantor> existGuarantorList = this.guarantorRepository
					.findByLoan(loan);
			boolean allowToInsert = false;
			boolean allowToOnHold = false;  // this flag is for checking to allow on hold self saving account on specific condition. 
			BigDecimal loanAmount = loan.getApprovedPrincipal();
			BigDecimal onHoldAmount = account.getOnHoldFunds();
			if(onHoldAmount.compareTo(loanAmount)<1){

			List<GuarantorFundingDetails> externalGuarantorList = new ArrayList<>();
			List<GuarantorFundingDetails> selfGuarantorList = new ArrayList<>();
			BigDecimal selfGuarantee = BigDecimal.ZERO;
			BigDecimal guarantorGuarantee = BigDecimal.ZERO;
			BigDecimal remainingAmount = BigDecimal.ZERO;
			List<DepositAccountOnHoldTransaction> accountOnHoldTransactions = new ArrayList<>();

			
			// following checking if any external guarantor's are there
			//if yes - then check the the remaining balance to be release, if its zero then allow to onhold self saving account becomes false
			
			for (Guarantor guarantor1 : existGuarantorList) {
				
				
				   final List<GuarantorFundingDetails> fundingDetails = guarantor1.getGuarantorFundDetails();
				 
				   
				   for(GuarantorFundingDetails guarantorFD : fundingDetails){
					   
					   if(!(guarantorFD.getLinkedSavingsAccount().accountNumber.equals(account.accountNumber))){
				    	 remainingAmount = guarantorFD.getAmountRemaining();
				    
				    	 if(remainingAmount.compareTo(BigDecimal.ZERO)==0){
						     allowToOnHold = false;
						}else{
							
							allowToOnHold = true;
							
						}
				  	
				     }
				   }	   
				   
				   if(allowToOnHold == true){
					   break;
				   }
			 }
 

		 
		if(allowToOnHold == true){	
			for (Guarantor guarantor : existGuarantorList) {
				final List<GuarantorFundingDetails> fundingDetails = guarantor
						.getGuarantorFundDetails();
				for (GuarantorFundingDetails guarantorFundingDetails : fundingDetails) {
					
					if (guarantorFundingDetails.getStatus().isActive()) {

						if (guarantor.isSelfGuarantee() ) {
							if(guarantorFundingDetails.getLinkedSavingsAccount().accountNumber.equals(account.accountNumber)){	
								selfGuarantorList.add(guarantorFundingDetails);
								selfGuarantee = selfGuarantee
										.add(guarantorFundingDetails
												.getAmountRemaining());
								BigDecimal  totalOnHoldAmount = account.getOnHoldFunds();
								BigDecimal remainingOnHoldAmount = loanAmount.subtract(totalOnHoldAmount);
								boolean allowToAddOnHold = false;
							    if(totalOnHoldAmount.compareTo(loanAmount)<1){
							    	allowToAddOnHold = true;
							    }
							    
							    if(transactionAmount.compareTo(remainingOnHoldAmount)==1){
								  totalTransactionAmount = remainingOnHoldAmount;	
								}else{
									totalTransactionAmount = transactionAmount;
								}
								
								
								
							    if (allowToOnHold == true && allowToAddOnHold == true) {
								
								
								
							    	DepositAccountOnHoldTransaction onHoldTransaction = DepositAccountOnHoldTransaction
											.hold(account, totalTransactionAmount,
													transactionDate);
	
									accountOnHoldTransactions
											.add(onHoldTransaction);
									GuarantorFundingTransaction guarantorFundingTransaction = new GuarantorFundingTransaction(
											guarantorFundingDetails, null,
											onHoldTransaction);
									guarantorFundingDetails
											.addGuarantorFundingTransactions(guarantorFundingTransaction);
								}
							}

						} else if (guarantor.isExistingCustomer()) {
							externalGuarantorList.add(guarantorFundingDetails);
							guarantorGuarantee = guarantorGuarantee
									.add(guarantorFundingDetails
											.getAmountRemaining());
							allowToInsert = true;

						}
					}

				}
			}
		}	
			if (transactionAmount != null && allowToOnHold == true) {

				BigDecimal amountLeft = calculateAndRelaseGuarantorFunds(
						externalGuarantorList, guarantorGuarantee,
						transactionAmount, deposit, accountOnHoldTransactions);

				BigDecimal totalGuaranteeAmount = selfGuarantee
						.add(guarantorGuarantee);
				BigDecimal availableOnHoladAmount = account.getOnHoldFunds();

				// BigDecimal addRemainingAmount =
				// loanAmount.subtract(totalGuaranteeAmount);

				if (transactionAmount.longValue() > totalGuaranteeAmount
						.longValue()) {
					BigDecimal newOnHoldAmount = totalGuaranteeAmount
							.subtract(availableOnHoladAmount);
					account.holdFunds(newOnHoldAmount);
				} else {
					if (availableOnHoladAmount.longValue() <= totalGuaranteeAmount
							.longValue()) {
						account.holdFunds(totalTransactionAmount);
					}

				}

				
				 if (amountLeft.compareTo(BigDecimal.ZERO) == 1){
				 
					 externalGuarantorList.addAll(selfGuarantorList); 
				 }
				 

				if (allowToInsert == true) {
					calculateAndIncrementSelfGuarantorFunds(selfGuarantorList,
							totalTransactionAmount, loanAmount);
				}

				if (!externalGuarantorList.isEmpty()) {
					this.depositAccountOnHoldTransactionRepository
					.save(accountOnHoldTransactions);
					this.guarantorFundingRepository.save(externalGuarantorList);

				}
			  }
	    	}
		  }	
		}		

	  	
		this.savingsAccountRepository.save(account);
		return deposit;
	}

	private void calculateAndIncrementSelfGuarantorFunds(
			List<GuarantorFundingDetails> guarantorList,
			BigDecimal amountForAdd, BigDecimal loanAmount) {
		for (GuarantorFundingDetails fundingDetails : guarantorList) {
			fundingDetails.addSelfAmmount(amountForAdd, loanAmount);
		}
	}

	private BigDecimal calculateAndRelaseGuarantorFunds(
			List<GuarantorFundingDetails> guarantorList,
			BigDecimal totalGuaranteeAmount,
			BigDecimal amountForRelease,
			SavingsAccountTransaction deposite,
			final List<DepositAccountOnHoldTransaction> accountOnHoldTransactions) {
		BigDecimal amountLeft = amountForRelease;
		for (GuarantorFundingDetails fundingDetails : guarantorList) {
			BigDecimal guarantorAmount = amountForRelease.multiply(
					fundingDetails.getAmountRemaining()).divide(
					totalGuaranteeAmount, roundingMode);
			if (fundingDetails.getAmountRemaining().compareTo(guarantorAmount) < 1) {
				guarantorAmount = fundingDetails.getAmountRemaining();
			}
			fundingDetails.releaseFunds(guarantorAmount);
			SavingsAccount savingsAccount = fundingDetails
					.getLinkedSavingsAccount();
			savingsAccount.releaseFunds(guarantorAmount);
			DepositAccountOnHoldTransaction onHoldTransaction = DepositAccountOnHoldTransaction
					.release(savingsAccount, guarantorAmount,
							deposite.transactionLocalDate());
			accountOnHoldTransactions.add(onHoldTransaction);
			GuarantorFundingTransaction guarantorFundingTransaction = new GuarantorFundingTransaction(
					fundingDetails, null, onHoldTransaction);
			fundingDetails
					.addGuarantorFundingTransactions(guarantorFundingTransaction);
			amountLeft = amountLeft.subtract(guarantorAmount);
		}
		return amountLeft;
	}

	private Long saveTransactionToGenerateTransactionId(
			final SavingsAccountTransaction transaction) {
		this.savingsAccountTransactionRepository.save(transaction);
		return transaction.getId();
	}

	private void updateExistingTransactionsDetails(SavingsAccount account,
			Set<Long> existingTransactionIds,
			Set<Long> existingReversedTransactionIds) {
		existingTransactionIds.addAll(account.findExistingTransactionIds());
		existingReversedTransactionIds.addAll(account
				.findExistingReversedTransactionIds());
	}

	private void postJournalEntries(final SavingsAccount savingsAccount,
			final Set<Long> existingTransactionIds,
			final Set<Long> existingReversedTransactionIds,
			boolean isAccountTransfer) {

		final MonetaryCurrency currency = savingsAccount.getCurrency();
		final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper
				.findOneWithNotFoundDetection(currency);

		final Map<String, Object> accountingBridgeData = savingsAccount
				.deriveAccountingBridgeData(applicationCurrency.toData(),
						existingTransactionIds, existingReversedTransactionIds,
						isAccountTransfer);
		this.journalEntryWritePlatformService
				.createJournalEntriesForSavings(accountingBridgeData);
	}

	@Transactional
	@Override
	public void postJournalEntries(final SavingsAccount account,
			final Set<Long> existingTransactionIds,
			final Set<Long> existingReversedTransactionIds) {

		final boolean isAccountTransfer = false;
		postJournalEntries(account, existingTransactionIds,
				existingReversedTransactionIds, isAccountTransfer);
	}

}