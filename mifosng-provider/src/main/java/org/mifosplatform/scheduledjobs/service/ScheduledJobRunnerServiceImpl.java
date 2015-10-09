/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.scheduledjobs.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.hooks.domain.HookConfiguration;
import org.mifosplatform.infrastructure.hooks.domain.HookConfigurationRepository;
import org.mifosplatform.infrastructure.hooks.domain.HookRepository;
import org.mifosplatform.infrastructure.hooks.service.HookReadPlatformServiceImpl;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.springframework.context.ApplicationContext;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeReadPlatformService;
import org.mifosplatform.portfolio.common.domain.PeriodFrequencyType;
import org.mifosplatform.portfolio.savings.DepositAccountType;
import org.mifosplatform.portfolio.savings.DepositAccountUtils;
import org.mifosplatform.portfolio.savings.data.DepositAccountData;
import org.mifosplatform.portfolio.savings.data.SavingIdListData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.mifosplatform.portfolio.savings.data.SavingsIdOfChargeData;
import org.mifosplatform.portfolio.savings.service.DepositAccountReadPlatformService;
import org.mifosplatform.portfolio.savings.service.DepositAccountWritePlatformService;
import org.mifosplatform.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.mifosplatform.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "scheduledJobRunnerService")
public class ScheduledJobRunnerServiceImpl implements ScheduledJobRunnerService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduledJobRunnerServiceImpl.class);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterWithTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
    private final DepositAccountReadPlatformService depositAccountReadPlatformService;
    private final DepositAccountWritePlatformService depositAccountWritePlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer;
   	private final HookReadPlatformServiceImpl hookReadPlatformServiceImpl;
   	private final HookRepository hookRepository;
   	private final HookConfigurationRepository hookConfigurationRepository;
   
    


    @Autowired
    public ScheduledJobRunnerServiceImpl(final RoutingDataSourceServiceFactory dataSourceServiceFactory,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
            final DepositAccountReadPlatformService depositAccountReadPlatformService,
            final DepositAccountWritePlatformService depositAccountWritePlatformService,
            final ChargeReadPlatformService chargeReadPlatformService,
            final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer,
            			final HookReadPlatformServiceImpl hookReadPlatformServiceImpl,
            			final HookRepository hookRepository,
            			final HookConfigurationRepository hookConfigurationRepository) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.depositAccountReadPlatformService = depositAccountReadPlatformService;
        this.depositAccountWritePlatformService = depositAccountWritePlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.toApiResultJsonSerializer = toApiResultJsonSerializer;
        this.hookReadPlatformServiceImpl = hookReadPlatformServiceImpl;
        this.hookRepository = hookRepository;
        this.hookConfigurationRepository = hookConfigurationRepository;
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_SUMMARY)
    public void updateLoanSummaryDetails() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final StringBuilder updateSqlBuilder = new StringBuilder(900);
        updateSqlBuilder.append("update m_loan ");
        updateSqlBuilder.append("join (");
        updateSqlBuilder.append("SELECT ml.id AS loanId,");
        updateSqlBuilder.append("SUM(mr.principal_amount) as principal_disbursed_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_completed_derived,0)) as principal_repaid_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_writtenoff_derived,0)) as principal_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_amount,0)) as interest_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_completed_derived,0)) as interest_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_waived_derived,0)) as interest_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_writtenoff_derived,0)) as interest_writtenoff_derived,");
        updateSqlBuilder
                .append("SUM(IFNULL(mr.fee_charges_amount,0)) + IFNULL((select SUM(lc.amount) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_id=1),0) as fee_charges_charged_derived,");
        updateSqlBuilder
                .append("SUM(IFNULL(mr.fee_charges_completed_derived,0)) + IFNULL((select SUM(lc.amount_paid_derived) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_id=1),0) as fee_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_waived_derived,0)) as fee_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_writtenoff_derived,0)) as fee_charges_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_amount,0)) as penalty_charges_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_completed_derived,0)) as penalty_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_waived_derived,0)) as penalty_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_writtenoff_derived,0)) as penalty_charges_writtenoff_derived ");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append("WHERE ml.disbursedon_date is not null ");
        updateSqlBuilder.append("GROUP BY ml.id ");
        updateSqlBuilder.append(") x on x.loanId = m_loan.id ");

        updateSqlBuilder.append("SET m_loan.principal_disbursed_derived = x.principal_disbursed_derived,");
        updateSqlBuilder.append("m_loan.principal_repaid_derived = x.principal_repaid_derived,");
        updateSqlBuilder.append("m_loan.principal_writtenoff_derived = x.principal_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.principal_outstanding_derived = (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.interest_charged_derived = x.interest_charged_derived,");
        updateSqlBuilder.append("m_loan.interest_repaid_derived = x.interest_repaid_derived,");
        updateSqlBuilder.append("m_loan.interest_waived_derived = x.interest_waived_derived,");
        updateSqlBuilder.append("m_loan.interest_writtenoff_derived = x.interest_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.interest_outstanding_derived = (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.fee_charges_charged_derived = x.fee_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_repaid_derived = x.fee_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_waived_derived = x.fee_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_writtenoff_derived = x.fee_charges_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.fee_charges_outstanding_derived = (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.penalty_charges_charged_derived = x.penalty_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_repaid_derived = x.penalty_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_waived_derived = x.penalty_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_writtenoff_derived = x.penalty_charges_writtenoff_derived,");
        updateSqlBuilder
                .append("m_loan.penalty_charges_outstanding_derived = (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived)),");
        updateSqlBuilder
                .append("m_loan.total_expected_repayment_derived = (x.principal_disbursed_derived + x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder
                .append("m_loan.total_repayment_derived = (x.principal_repaid_derived + x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder
                .append("m_loan.total_expected_costofloan_derived = (x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder
                .append("m_loan.total_costofloan_derived = (x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder
                .append("m_loan.total_waived_derived = (x.interest_waived_derived + x.fee_charges_waived_derived + x.penalty_charges_waived_derived),");
        updateSqlBuilder
                .append("m_loan.total_writtenoff_derived = (x.interest_writtenoff_derived +  x.fee_charges_writtenoff_derived + x.penalty_charges_writtenoff_derived),");
        updateSqlBuilder.append("m_loan.total_outstanding_derived=");
        updateSqlBuilder.append(" (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)) + ");
        updateSqlBuilder
                .append(" (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)) +");
        updateSqlBuilder
                .append(" (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)) +");
        updateSqlBuilder
                .append(" (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived))");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_PAID_IN_ADVANCE)
    public void updateLoanPaidInAdvance() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        jdbcTemplate.execute("truncate table m_loan_paid_in_advance");

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder
                .append("INSERT INTO m_loan_paid_in_advance(loan_id, principal_in_advance_derived, interest_in_advance_derived, fee_charges_in_advance_derived, penalty_charges_in_advance_derived, total_in_advance_derived)");
        updateSqlBuilder.append(" select ml.id as loanId,");
        updateSqlBuilder.append(" SUM(ifnull(mr.principal_completed_derived, 0)) as principal_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.interest_completed_derived, 0)) as interest_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) as fee_charges_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.penalty_charges_completed_derived, 0)) as penalty_charges_in_advance_derived,");
        updateSqlBuilder
                .append(" (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) + SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append(" WHERE ml.loan_status_id = 300 ");
        updateSqlBuilder.append(" and mr.duedate >= CURDATE() ");
        updateSqlBuilder.append(" GROUP BY ml.id");
        updateSqlBuilder
                .append(" HAVING (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) +");
        updateSqlBuilder
                .append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0.0");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_ANNUAL_FEE_FOR_SAVINGS)
    public void applyAnnualFeeForSavings() {

        final Collection<SavingsAccountAnnualFeeData> annualFeeData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithAnnualFeeDue();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : annualFeeData) {
            try {
                this.savingsAccountWritePlatformService.applyAnnualFee(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply annual fee failed for account:" + savingsAccountReference.getAccountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + annualFeeData.size());
    }

    @Override
    @CronTarget(jobName = JobName.PAY_DUE_SAVINGS_CHARGES)
    public void applyDueChargesForSavings() throws JobExecutionException {
        final Collection<SavingsAccountAnnualFeeData> chargesDueData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithDue();
        final StringBuilder errorMsg = new StringBuilder();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : chargesDueData) {
            try {
                this.savingsAccountWritePlatformService.applyChargeDue(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply Charges due for savings failed for account:" + savingsAccountReference.getAccountNo()
                            + " with message " + error.getDeveloperMessage());
                    errorMsg.append("Apply Charges due for savings failed for account:").append(savingsAccountReference.getAccountNo())
                            .append(" with message ").append(error.getDeveloperMessage());
                }
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + chargesDueData.size());

        /*
         * throw exception if any charge payment fails.
         */
        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_NPA)
    public void updateNPA() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final StringBuilder resetNPASqlBuilder = new StringBuilder(900);
        resetNPASqlBuilder.append("update m_loan loan ");
        resetNPASqlBuilder.append("left join m_loan_arrears_aging laa on laa.loan_id = loan.id ");
        resetNPASqlBuilder.append("inner join m_product_loan mpl on mpl.id = loan.product_id and mpl.overdue_days_for_npa is not null ");
        resetNPASqlBuilder.append("set loan.is_npa = 0 ");
        resetNPASqlBuilder.append("where  loan.loan_status_id = 300 and mpl.account_moves_out_of_npa_only_on_arrears_completion = 0 ");
        resetNPASqlBuilder
                .append("or (mpl.account_moves_out_of_npa_only_on_arrears_completion = 1 and laa.overdue_since_date_derived is null)");

        jdbcTemplate.update(resetNPASqlBuilder.toString());

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder.append("UPDATE m_loan as ml,");
        updateSqlBuilder.append(" (select loan.id ");
        updateSqlBuilder.append("from m_loan_arrears_aging laa");
        updateSqlBuilder.append(" INNER JOIN  m_loan loan on laa.loan_id = loan.id ");
        updateSqlBuilder.append(" INNER JOIN m_product_loan mpl on mpl.id = loan.product_id AND mpl.overdue_days_for_npa is not null ");
        updateSqlBuilder.append("WHERE loan.loan_status_id = 300  and ");
        updateSqlBuilder.append("laa.overdue_since_date_derived < SUBDATE(CURDATE(),INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day) ");
        updateSqlBuilder.append("group by loan.id) as sl ");
        updateSqlBuilder.append("SET ml.is_npa=1 where ml.id=sl.id ");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS)
    public void updateMaturityDetailsOfDepositAccounts() {

        final Collection<DepositAccountData> depositAccounts = this.depositAccountReadPlatformService.retrieveForMaturityUpdate();

        for (final DepositAccountData depositAccount : depositAccounts) {
            try {
                final DepositAccountType depositAccountType = DepositAccountType.fromInt(depositAccount.depositType().getId().intValue());
                this.depositAccountWritePlatformService.updateMaturityDetails(depositAccount.id(), depositAccountType);
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Update maturity details failed for account:" + depositAccount.accountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Deposit accounts affected by update: " + depositAccounts.size());
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_CLIENT_SUB_STATUS)
    public void updateClientSubStatus() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final int result = jdbcTemplate.update("call doClientSubStatusUpdates()");

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.GENERATE_RD_SCEHDULE)
    public void generateRDSchedule() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        final Collection<Map<String, Object>> scheduleDetails = this.depositAccountReadPlatformService.retriveDataForRDScheduleCreation();
        String insertSql = "INSERT INTO `m_mandatory_savings_schedule` (`savings_account_id`, `duedate`, `installment`, `deposit_amount`, `completed_derived`, `created_date`, `lastmodified_date`) VALUES ";
        StringBuilder sb = new StringBuilder();
        String currentDate = formatterWithTime.print(DateUtils.getLocalDateTimeOfTenant());
        int iterations = 0;
        for (Map<String, Object> details : scheduleDetails) {
            Long count = (Long) details.get("futureInstallemts");
            if (count == null) {
                count = 0l;
            }
            final Long savingsId = (Long) details.get("savingsId");
            final BigDecimal amount = (BigDecimal) details.get("amount");
            final String recurrence = (String) details.get("recurrence");
            Date date = (Date) details.get("dueDate");
            LocalDate lastDepositDate = new LocalDate(date);
            Integer installmentNumber = (Integer) details.get("installment");
            while (count < DepositAccountUtils.GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS) {
                count++;
                installmentNumber++;
                lastDepositDate = DepositAccountUtils.calculateNextDepositDate(lastDepositDate, recurrence);

                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("(");
                sb.append(savingsId);
                sb.append(",'");
                sb.append(formatter.print(lastDepositDate));
                sb.append("',");
                sb.append(installmentNumber);
                sb.append(",");
                sb.append(amount);
                sb.append(", b'0','");
                sb.append(currentDate);
                sb.append("','");
                sb.append(currentDate);
                sb.append("')");
                iterations++;
                if (iterations > 200) {
                    jdbcTemplate.update(insertSql + sb.toString());
                    sb = new StringBuilder();
                }

            }
        }

        if (sb.length() > 0) {
            jdbcTemplate.update(insertSql + sb.toString());
        }
    }

    @Override
    @CronTarget(jobName = JobName.APPY_SAVING_DEPOSITE_LATE_FEE)
    public void doAppySavingLateFeeCharge() throws JobExecutionException {
        PeriodFrequencyType frequencyType = null;

        String startingDate = new String();
        SimpleDateFormat formateDate = new SimpleDateFormat("yyyy-MM-dd");

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final Collection<ChargeData> chargeData = this.chargeReadPlatformService.retriveAllChargeOfSavingLateFee();
        /**
         * above method retrun's all available charges for the saving late
         * deposit fee
         */

        for (final ChargeData oneCharge : chargeData) {
            int interval = oneCharge.getFeeInterval();
            int month = frequencyType.MONTHS.getValue();

            EnumOptionData data = oneCharge.getFeeFrequency();
            Long frequency = data.getId();
            Calendar aCalendar = Calendar.getInstance();

            if (month == frequency) {

                // added -interval for any previous month
                aCalendar.add(Calendar.MONTH, -interval);
                aCalendar.set(Calendar.DATE, 1);
                Date startDate = aCalendar.getTime();
                startingDate = formateDate.format(startDate);
            }

            final Collection<SavingIdListData> savingIdList = this.savingsAccountChargeReadPlatformService
                    .retriveAllSavingIdForApplyDepositeLateCharge();
            final Collection<SavingsIdOfChargeData> savingIdsInCharge = this.savingsAccountChargeReadPlatformService
                    .retriveAllSavingIdHavingDepositCharge(startingDate);
            final Collection<SavingIdListData> savingIdsFromTransaction = this.savingsAccountChargeReadPlatformService
                    .retriveSavingAccountForApplySavingDepositeFee(startingDate);

            for (final SavingIdListData savingId : savingIdList) {

                Long savingIdForGetMaxOfTxn = savingId.getSavingId();

                DateTime start = new DateTime();
                Date trasactionDate = new Date();
                Date startFeeCharge = new Date();
                LocalDate maxOfTransactionDate = new LocalDate();
                LocalDate maxOfchargeDueDate = new LocalDate();
                LocalDate startChargeDate = new LocalDate();
                boolean isInsert = true;
                boolean isValideForCharge = false;
                boolean isMaxOfChargeDue = false;
                boolean isPreviousTxn = false;
                boolean isAllowInsert = false;
                boolean isPriviousDueDate = false;
                boolean isSavingIdAvailable = false;
                int totalNoOfInsertion = 0;
                
                /**
                 * Following code will check if the start date of charge calculation is there or is saving account is active
                 * then it will return valid for apply charge 
                 */
                
                if (savingId.getStartFeeChargeDate() != null) {

                    if (savingId.getStartFeeChargeDate().isAfter(savingId.getActivateOnDate())
                            || savingId.getStartFeeChargeDate().equals(savingId.getActivateOnDate())) {
                        isValideForCharge = true;
                    }
                } else if (savingId.getActivateOnDate() != null) {
                    isValideForCharge = true;
                    startFeeCharge = savingId.getActivateOnDate().toDate();
                }
                
             
                /**
                 * Following code will return the boolean value true if there is any previous charge on 
                 * on saving id 
                 */

                SavingsIdOfChargeData maxOfChargeDueDate = this.savingsAccountChargeReadPlatformService
                        .retriveOneWithMaxOfDueDate(savingIdForGetMaxOfTxn);
                if (maxOfChargeDueDate.getDueDate() != null) {
                    maxOfchargeDueDate = new LocalDate(maxOfChargeDueDate.getDueDate());
                    isMaxOfChargeDue = true;
                    isPriviousDueDate = true;
                }
               

             
                /**
                 * if any savingAccount already having charge then isInsert
                 * become false else it will be true and it will allow to insert
                 * data
                 **/

                for (final SavingsIdOfChargeData savingData : savingIdsInCharge) {
                    if (savingId.getSavingId().equals(savingData.getSavingId())) {
                        isInsert = false;
                        break;
                    }
                }
                
 
             /** 
              * It checks the last transaction of saving Id is not in previous month those saving Id only
              * eligible to apply charge    
              */
                
                for(final SavingIdListData savingIdListData : savingIdsFromTransaction){
                	if(savingId.getSavingId().equals(savingIdListData.getSavingId())){
                		isAllowInsert = true;
                	}	
                }  	
                
                
                
                        
                     
                /** 
                 * Following loop condition it will just adjust the dates based on some condition  for the charge calculation
                 */
                
                
              
                
                
                for (final SavingIdListData savingIdListData : savingIdsFromTransaction) {

                    if (savingIdListData.getSavingId().equals(savingId.getSavingId())) {
                        isSavingIdAvailable = true;
                     
                        LocalDate dateOfTransaction = savingIdListData.getMaxTransactionDate();
                        if (dateOfTransaction.isAfter(maxOfchargeDueDate) || dateOfTransaction.equals(maxOfChargeDueDate)
                                || isMaxOfChargeDue == false) {
                            trasactionDate = dateOfTransaction.toDate();
                            maxOfTransactionDate = dateOfTransaction;
                            isPreviousTxn = true;

                        } else {
                            trasactionDate = maxOfchargeDueDate.toDate();
                            maxOfTransactionDate = maxOfchargeDueDate;
                            isPreviousTxn = true;
                        }

                        LocalDate startFeeChargeDate = savingId.getStartFeeChargeDate();
                        if(startFeeChargeDate == null){
                            startFeeChargeDate = savingId.getActivateOnDate();
                        }

                        if (isMaxOfChargeDue == true) {
                            if (dateOfTransaction.isAfter(maxOfchargeDueDate) || dateOfTransaction.equals(maxOfChargeDueDate)) {
                                startFeeCharge = dateOfTransaction.toDate();
                                startChargeDate = dateOfTransaction;
                            } else {
                                startFeeCharge = maxOfchargeDueDate.toDate();
                                startChargeDate = maxOfchargeDueDate;
                            }
                        } else if (dateOfTransaction.isAfter(startFeeChargeDate)) {
                            startFeeCharge = dateOfTransaction.toDate();
                            startChargeDate = dateOfTransaction;
                        } else {
                            startFeeCharge = startFeeChargeDate.toDate();
                            startChargeDate = startFeeChargeDate;
                        }

                        break;
                    }

                }


                                
                /**
                 * if there is no any single transaction of saving account then
                 * start date and number of insertion calculation done here
                 */
                
                SavingIdListData maxTransactionDate = this.savingsAccountChargeReadPlatformService.retriveMaxOfTransaction(savingIdForGetMaxOfTxn);
                if(maxTransactionDate.getMaxTransactionDate() == null){
                	isAllowInsert = true;
                }
                
                
                if (isSavingIdAvailable == false && isInsert == true && isValideForCharge == true) {
                	
                    LocalDate startFeeChargeDate = savingId.getStartFeeChargeDate();
                    if(startFeeChargeDate == null){
                        startFeeChargeDate = savingId.getActivateOnDate();
                    }

                    if (maxOfchargeDueDate.isAfter(startFeeChargeDate) && isMaxOfChargeDue == true) {
                        startFeeCharge = maxOfchargeDueDate.toDate();
                    } else {
                        startFeeCharge = startFeeChargeDate.toDate();
                    }

                    DateTime startFee = new DateTime(startFeeCharge);
                    start = new DateTime(startFee);
                    Date endDateAsCurrDate = new Date();
                    DateTime end = new DateTime(endDateAsCurrDate);

                    if (isMaxOfChargeDue == true) {
                        trasactionDate = maxOfchargeDueDate.toDate();
                        maxOfTransactionDate = maxOfchargeDueDate;
                        isPriviousDueDate = true;
                    } else {
                        trasactionDate = startFeeChargeDate.toDate();
                        maxOfTransactionDate = startFeeChargeDate;
                        isPriviousDueDate = false;
                    }

                    Months diffMonth = Months.monthsBetween(start, end);
                    totalNoOfInsertion = (diffMonth.getMonths()) / interval;
                }
                
                /** If there is previous transaction of saving Id then 
                 * here it will check how many charges will need be applied
                 * 
                 */

                if (isSavingIdAvailable == true && isInsert == true && isValideForCharge == true) {

                    DateTime transaction = new DateTime(trasactionDate);

                    DateTime startFee = new DateTime(startFeeCharge);

                    LocalDate startCharge = new LocalDate(startFeeCharge);
                    Date endDateAsCurrDate = new Date();
                    DateTime end = new DateTime(endDateAsCurrDate);

                    if (maxOfTransactionDate.isAfter(startCharge) || maxOfTransactionDate.isEqual(startCharge) && isMaxOfChargeDue == true) {
                        start = new DateTime(transaction);
                        Months diffMonth = Months.monthsBetween(start, end);
                        totalNoOfInsertion = (diffMonth.getMonths() - 1) / interval;

                    }else if(maxOfTransactionDate.isEqual(startCharge) && isMaxOfChargeDue == false){
                    	start = new DateTime(transaction);
                    	Months diffMonth = Months.monthsBetween(startFee, end);
                        totalNoOfInsertion = (diffMonth.getMonths() - 1)/ interval;	
                    }
                    else if (maxOfTransactionDate.isAfter(maxOfchargeDueDate) || maxOfTransactionDate.isEqual(maxOfchargeDueDate)
                            && isMaxOfChargeDue == true) {

                        start = new DateTime(transaction);
                        Months diffMonth = Months.monthsBetween(start, end);
                        totalNoOfInsertion = (diffMonth.getMonths()) / interval;

                    }

                }

                
             /**
              * If all boolean values are true then it will insert the charge into the m_savings_account_charge   
              */
                
                if (isInsert == true && isValideForCharge == true && isAllowInsert == true) {

                    for (int i = 0; i < totalNoOfInsertion; i++) {

                        String insertSql = " INSERT INTO `m_savings_account_charge` (`savings_account_id`, `charge_id`, `is_penalty`, `charge_time_enum`, "
                                + " `charge_due_date`, `fee_on_month`, `fee_on_day`, `fee_interval`, `charge_calculation_enum`, `calculation_percentage`, "
                                + " `calculation_on_amount`, `amount`, `amount_paid_derived`, `amount_waived_derived`, `amount_writtenoff_derived`, "
                                + " `amount_outstanding_derived`, `is_paid_derived`, `waived`, `is_active`, `inactivated_on_date`) VALUES ";

                        StringBuilder sb = new StringBuilder();
                        final Long savingAccId = savingId.getSavingId();
                        final Long chargId = oneCharge.getId();
                        final BigDecimal amount = oneCharge.getAmount();
                        LocalDate chargeDueDate = new LocalDate();
                        if (i == 0) {
                            if (isPriviousDueDate == true) {

                                if (maxOfTransactionDate.isAfter(maxOfchargeDueDate) || isMaxOfChargeDue == false) {
                                    aCalendar.setTime(trasactionDate);

                                    aCalendar.add(Calendar.MONTH, interval + 1);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

                                    Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);

                                } else if (maxOfchargeDueDate.isAfter(maxOfTransactionDate) && isMaxOfChargeDue == true
                                        || maxOfchargeDueDate.equals(maxOfTransactionDate)) {

                                    Date chargeDue = new Date();
                                    chargeDue = maxOfchargeDueDate.toDate();
                                    aCalendar.setTime(chargeDue);
                                    aCalendar.add(Calendar.MONTH, interval);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

                                    Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);
                                }

                            }

                            // in this if there is no any previous due date then
                            // calendar is going to set on available date

                            else {

                                if (isPreviousTxn == true) {
                                    aCalendar.setTime(startFeeCharge);

                                    aCalendar.add(Calendar.MONTH, interval + 1);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                                    Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);
                                }

                                else {

                                    aCalendar.setTime(startFeeCharge);

                                    aCalendar.add(Calendar.MONTH, interval);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                                    Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);

                                }

                            }
                        }

                        else {
                           
                                aCalendar.add(Calendar.MONTH, interval);
                                aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                                Date nextMonthFirstDay = aCalendar.getTime();
                                aCalendar.setTime(nextMonthFirstDay);
                                chargeDueDate = new LocalDate(nextMonthFirstDay);

                        }
                        sb.append("(");
                        sb.append(savingAccId);
                        sb.append(",");
                        sb.append(chargId);
                        sb.append(",");
                        sb.append("0");
                        sb.append(",");
                        sb.append("12");
                        sb.append(",'");
                        sb.append(formatter.print(chargeDueDate));
                        sb.append("',");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("1");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append(amount);
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append(amount);
                        sb.append(",");
                        sb.append("0");
                        sb.append(",");
                        sb.append("0");
                        sb.append(",");
                        sb.append("1");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(")");

                        if (sb.length() > 0) {
                            jdbcTemplate.update(insertSql + sb.toString());
                        }

                    }
                }
                // //
            }

        }

    }


   
    @Override
    @CronTarget(jobName = JobName.LOAN_REPAYMENT_SMS_REMINDER_TO_CLIENT)
    public void loanRepaymentSmsReminder() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"Loan Repayment Reminders\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_FIRST_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanFirstOverdueRepaymentReminder() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"Loan First Overdue Repayment Reminder\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_SECOND_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanSecondOverdueRepaymentReminder() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                                
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"Loan Second Overdue Repayment Reminder\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_THIRD_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanThirdOverdueRepaymentReminder() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject("{\"reportName\":\"Loan Third Overdue Repayment Reminder\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_FOURTH_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanFourthOverdueRepaymentReminder() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"Loan Fourth Overdue Repayment Reminder\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.DEFAULT_WARNING_SMS_TO_CLIENT)
    public void defaultWarningToClients() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"DefaultWarning - Clients\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.DEFAULT_WARNING_SMS_TO_GURANTOR)
    public void defaultWarningToGuarantors() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"DefaultWarning -  guarantors\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    @Override
    @CronTarget(jobName = JobName.DORMANCY_WARNING_SMS_TO_CLIENT)
    public void dormancyWarningToClients() {
            String payLoadUrl = "http://localhost:9191/modules/sms";
            String apikey = hookRepository.retriveApiKey();
            final String tenantIdentifier = ThreadLocalContextUtil.getTenant()
                            .getTenantIdentifier();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(payLoadUrl);
            httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
            httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
            httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("X-Mifos-API-Key", apikey);
            StringEntity entity;
            try {
                    Date now = new Date();                  
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");     
                    String date=df.format(now);
                    JSONObject jsonObj = new JSONObject(
                                    "{\"reportName\":\"DormancyWarning - Clients\",\"date\":\""+date+"\"}");
                    entity = new StringEntity(jsonObj.toString());
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (ClientProtocolException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (IOException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            } catch (JSONException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
            }
    }

    
}
