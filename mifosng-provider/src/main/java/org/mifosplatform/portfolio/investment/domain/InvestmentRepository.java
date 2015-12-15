package org.mifosplatform.portfolio.investment.domain;

import org.mifosplatform.portfolio.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvestmentRepository extends JpaRepository<Investment, Long>, JpaSpecificationExecutor<Investment> {

    //
}
