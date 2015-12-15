package org.mifosplatform.portfolio.investment.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentRepositoryWrapper {

    private final InvestmentRepository repository;

    @Autowired
    public InvestmentRepositoryWrapper(InvestmentRepository repository) {
        super();
        this.repository = repository;
    }
    
    public void save(final Investment saving) {
        this.repository.save(saving);
    }

    public void saveAndFlush(final Investment saving) {
        this.repository.saveAndFlush(saving);
    }

    public void delete(final Investment saving) {
        this.repository.delete(saving);
    }

   public Investment findWithNotFoundDetection(final Long id){
       final Investment investment = this.repository.findOne(id);
       return investment;
       
   }
}
