package org.mifosplatform.portfolio.savings.data;


public class SavingsIdOfChargeData {

    final Long savingId;

    public SavingsIdOfChargeData(Long savingId) {
        super();
        this.savingId = savingId;
    }

    
    public Long getSavingId() {
        return this.savingId;
    }
    
    public static SavingsIdOfChargeData instance(Long savingId){
        return new SavingsIdOfChargeData(savingId);
    }
    
    
}
