package com.conflux.mifosplatform.infrastructure.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotificationDetails;

public interface SmsNotificationDetailRepository extends
		JpaRepository<SmsNotificationDetails , Long>,JpaSpecificationExecutor<SmsNotificationDetails >{

}
