/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.hooks.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface HookConfigurationRepository extends
		JpaRepository<HookConfiguration, Long>,
		JpaSpecificationExecutor<HookConfiguration> {

	@Query("select config.fieldValue from HookConfiguration config where config.hook.id = :hookId and config.fieldName = :fieldName")
	String findOneByHookIdAndFieldName(@Param("hookId") Long hookId,
			@Param("fieldName") String fieldName);
	
	@Query(" from HookConfiguration config where config.hook.id in " +
	                               "(select hr.hook from HookResource hr where hr.entityName='SCHEDULER' and hr.actionName='EXECUTEJOB')")
	              ArrayList<HookConfiguration> retriveDetail();
	               
	               @Query("select config.fieldName,config.fieldValue from HookConfiguration config where config.hook.id in " +
	                               "(select hr.hook from HookResource hr where hr.entityName='SCHEDULER' and hr.actionName='EXECUTEJOB')")
	               HookConfiguration  retriveDetail1();

}
