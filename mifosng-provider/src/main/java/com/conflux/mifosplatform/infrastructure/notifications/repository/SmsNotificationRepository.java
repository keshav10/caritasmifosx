package com.conflux.mifosplatform.infrastructure.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.conflux.mifosplatform.infrastructure.notifications.domain.SmsNotification;

@Repository
public interface SmsNotificationRepository extends JpaRepository<SmsNotification, Long>, JpaSpecificationExecutor<SmsNotification>{
		//CrudRepository<SmsNotification, Long> {

}
