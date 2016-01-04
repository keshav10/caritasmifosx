package org.mifosplatform.portfolio.investment.data;

import java.util.Date;

import org.joda.time.LocalDate;


public class LoanInvestmentData {
    
    final Long saving_id;
    final Long group_id;
    final String name;
    final String accountno;
    final Long savingamount;
    final String productname;
    final Long investedAmount;
    final LocalDate startDate;
    final LocalDate closeDate;
    public LoanInvestmentData(Long saving_id,Long group_id, String name, String accountno, 
    		Long savingamount, String productname, Long investedAmount, LocalDate startDate, LocalDate closeDate) {
        super();
        this.saving_id = saving_id;
        this.name = name;
        this.accountno = accountno;
        this.savingamount = savingamount;
        this.productname = productname;
        this.investedAmount = investedAmount;
        this.group_id = group_id;
        this.startDate = startDate;
        this.closeDate = closeDate;
    }
    
    public Long getGroup_id() {
		return this.group_id;
	}

	public Long getSaving_id() {
        return this.saving_id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAccountno() {
        return this.accountno;
    }
    
    public Long getSavingamount() {
        return this.savingamount;
    }
    
    public String getProductname() {
        return this.productname;
    }
   
    public Long getInvestedAmount() {
		return this.investedAmount;
	}

	public static LoanInvestmentData intance (Long saving_id,Long group_id, String name, String accountno, Long savingamount,
            String productname, Long investedAmount, LocalDate startDate, LocalDate closeDate){
        return new LoanInvestmentData(saving_id,group_id, name, accountno, savingamount, productname, investedAmount, startDate, closeDate);
    }
}
