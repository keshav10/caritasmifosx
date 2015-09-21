package org.mifosplatform.portfolio.investment.data;

import java.util.List;

public class SavingInvestmentData implements Comparable<SavingInvestmentData> {

    final Long loan_id;
    final Long client_id;
    final String name;
    final String accountno;
    final Long loanammount;
    final String productname;
    final Long savigId;
    final List<Long> loanId;
    final Long investedAmount;

    
    public SavingInvestmentData(Long loan_id,Long client_id, String name, String accountno, Long loanammount, String productname, Long savigId,
            List<Long> loanId,  Long investedAmount) {
        super();
        this.loan_id = loan_id;
        this.client_id = client_id;
        this.name = name;
        this.accountno = accountno;
        this.loanammount = loanammount;
        this.productname = productname;
        this.savigId = savigId;
        this.loanId = loanId;
        this.investedAmount = investedAmount;
    }

    
    public Long getClient_id() {
		return this.client_id;
	}


	public Long getSavigId() {
        return this.savigId;
    }

    
    public List<Long> getLoanId() {
        return this.loanId;
    }

    public String getAccountno() {
        return this.accountno;
    }

    public Long getLoan_id() {
        return this.loan_id;
    }
    
    
    public String getProductname() {
        return this.productname;
    }

    public String getName() {
        return this.name;
    }

    public Long getLoanammount() {
        return this.loanammount;
    }

    public Long getInvestedAmount() {
		return this.investedAmount;
	}
 

    @Override
    public int compareTo(SavingInvestmentData o) {
       
        return 0;
    }
    public static SavingInvestmentData instance(Long loan_id,Long client_id, String name, String accountno, Long loanammount, String productname, Long savingId,
            List<Long> loanId,  Long investedAmount) {
       
        return new SavingInvestmentData(loan_id,client_id, accountno, name, loanammount, productname, savingId, loanId, investedAmount);
    }


	

}
