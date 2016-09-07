/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StandingInstructionRepository extends JpaRepository<AccountTransferStandingInstruction, Long>,
        JpaSpecificationExecutor<AccountTransferStandingInstruction> {
	
	 @Query("from AccountTransferStandingInstruction atsi where (atsi.accountTransferDetails.fromLoanAccount.id= :accountNumber or atsi.accountTransferDetails.toLoanAccount.id=:accountNumber) and atsi.status= :status")
	    List<AccountTransferStandingInstruction> findAllByLoanId(@Param("accountNumber") Long accountNumber, @Param("status") Integer status);

}