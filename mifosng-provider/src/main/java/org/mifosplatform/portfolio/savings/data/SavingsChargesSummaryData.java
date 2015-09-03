package org.mifosplatform.portfolio.savings.data;

import java.math.BigDecimal;

public class SavingsChargesSummaryData {
    
	private final Long id;
    private final String accountNo;	
	private final String chargeName;
	private final BigDecimal chargeDue;
	private final Long chargeId;
	private final String  date; 
	
	
       
	public SavingsChargesSummaryData(Long id, String accountNo,
			String chargeName, BigDecimal chargeDue, Long chargeId, String date) {
		super();
		this.id = id;
		this.accountNo = accountNo;
		this.chargeName = chargeName;
		this.chargeDue = chargeDue;
		this.chargeId = chargeId;
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public String getChargeName() {
		return this.chargeName;
	}

	public BigDecimal getChargeDue() {
		return this.chargeDue;
	}

	public Long getChargeId() {
		return this.chargeId;
	}
	

}
