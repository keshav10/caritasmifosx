package com.conflux.mifosplatform.infrastructure.notifications.service;

import java.util.Map;

import javax.annotation.PostConstruct;


import org.mifosplatform.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.mifosplatform.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.mifosplatform.portfolio.common.service.BusinessEventListner;
import org.mifosplatform.portfolio.common.service.BusinessEventNotifierService;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;

@Component
public class ListenerForSendingNotifications {

	private final BusinessEventNotifierService businessEventNotifierService;
    private static final Logger logger = LoggerFactory.getLogger(ListenerForSendingNotifications.class);


	@Autowired
	public ListenerForSendingNotifications(
			final BusinessEventNotifierService businessEventNotifierService) {
		this.businessEventNotifierService = businessEventNotifierService;
	}

	@PostConstruct
	public void addListners() {
		// Read from Properties file to check which events need notifications to
		// be sent out

		if (NotificationsConfiguration
				.getInstance().checkIfNotificationConfigEnabled("enablesms")) {
			if (NotificationsConfiguration
					.getInstance().checkIfNotificationConfigEnabled("sendsms.loan.approval")) {
				this.businessEventNotifierService.addBusinessEventPostListners(
						BUSINESS_EVENTS.LOAN_APPROVED, new ListenerSMSSender(
								BUSINESS_EVENTS.LOAN_APPROVED));
			}
			if (NotificationsConfiguration
					.getInstance().checkIfNotificationConfigEnabled("sendsms.loan.disbursal")) {
				this.businessEventNotifierService.addBusinessEventPostListners(
						BUSINESS_EVENTS.LOAN_DISBURSAL, new ListenerSMSSender(
								BUSINESS_EVENTS.LOAN_DISBURSAL));
			}
			if (NotificationsConfiguration
					.getInstance().checkIfNotificationConfigEnabled("sendsms.loan.repayment")) {
				this.businessEventNotifierService.addBusinessEventPostListners(
						BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
						new ListenerSMSSender(
								BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT));
			}
		}
	}

	private class ListenerSMSSender implements BusinessEventListner {

		BUSINESS_EVENTS event;

		ListenerSMSSender(BUSINESS_EVENTS event) {
			this.event = event;
		}

		@Override
		public void businessEventToBeExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			// do nothing
		}

		@Override
		public void businessEventWasExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			// TODO Auto-generated method stub, call to the relevant SMS class
			if (businessEventEntity.containsKey(BUSINESS_ENTITY.LOAN)) {
				Loan loan = (Loan) businessEventEntity
						.get(BUSINESS_ENTITY.LOAN);
				System.out.println("Send SMS: " + event.getValue() + ":"
						+ loan.getId() + ":" + loan.getAccountNumber() + ":"
						+ loan.getClient().getDisplayName() + ":"
						+ loan.getClient().mobileNo());
				if (loan.getClient().mobileNo() != null) {
					String message = "Completed " + this.event.getValue()
							+ ": " + "Loan No: " + loan.getAccountNumber()
							+ ", " + "Amount disbursed: "
							+ loan.getDisburseAmountForTemplate() + ", "
							+ "for Client: "
							+ loan.getClient().getDisplayName();
					String to = loan.getClient().mobileNo();

					SMSSender smsSender = NotificationsConfiguration
							.getInstance().getSenderForSMSProvider();
					smsSender.send(to, message);

				}

			} else {
				System.out.println("Send SMS: Could not process");
			}
		}

		@Override
		public void businessEventToBeExecuted(
				AbstractPersistable<Long> businessEventEntity) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void businessEventWasExecuted(
				AbstractPersistable<Long> businessEventEntity) {
			// TODO Auto-generated method stub
			
		}
	}

	private class EmailSender implements BusinessEventListner {
		@Override
		public void businessEventToBeExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			// do nothing

		}

		@Override
		public void businessEventWasExecuted(
				Map<BUSINESS_ENTITY, Object> businessEventEntity) {
			// TODO Auto-generated method stub, call to the relevant Email class
			System.out.println("Send Email");
		}

		@Override
		public void businessEventToBeExecuted(
				AbstractPersistable<Long> businessEventEntity) {
			// TODO Auto-generated method stub
			
		}
/* added by me*/
		@Override
		public void businessEventWasExecuted(
				AbstractPersistable<Long> businessEventEntity) {
			// TODO Auto-generated method stub
			
		}

		
	}

}