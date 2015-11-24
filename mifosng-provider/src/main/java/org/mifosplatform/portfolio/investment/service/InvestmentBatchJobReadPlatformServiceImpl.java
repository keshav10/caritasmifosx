package org.mifosplatform.portfolio.investment.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.investment.data.InvestmentBatchJobData;
import org.mifosplatform.portfolio.investment.exception.NoAnyInvestmentForGivenMaturityDateException;
import org.mifosplatform.portfolio.investment.exception.NoAnyInvestmentForSpecificAccountException;
import org.mifosplatform.portfolio.investment.exception.NoAnyInvestmentFoundForDistributionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InvestmentBatchJobReadPlatformServiceImpl implements InvestmentBatchJobReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	public static RoutingDataSource dataSource;
	    
    @Autowired
	public InvestmentBatchJobReadPlatformServiceImpl(RoutingDataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);	
	}
	
	@Override
	public List<InvestmentBatchJobData> validateForInvestmentSplit(String[] productId, String date, String investmentId) {

	  
       StringBuilder sb = new StringBuilder();
       InvestmentBatchJobDataMapper rm = new InvestmentBatchJobDataMapper();
       
           List<Long> pId = new ArrayList<Long>();
           Long id = null;
       
           if(productId != null){
        	   for(String product : productId){
          	     id = Long.parseLong(product);
                   pId.add(id);
                 }
                
           }
           
           sb.append(" select ml.id as investmentId from m_investment ms ");
           sb.append(" left join m_loan ml on ms.loan_id = ml.id ");
           sb.append(" left join m_product_loan mlp on ml.product_id = mlp.id ");
           int count = 0;
           int temp = 0;
           if(!(date.isEmpty()) || !(investmentId.isEmpty()) || productId != null){
        	   sb.append(" where ");
        	   count=1;
           }
           if(!(date.isEmpty())){
    	     sb.append(" ml.maturedon_date <= ? ");
    	     temp=1;
           }
           if(date.isEmpty() && count==0){
        	   sb.append(" where ml.maturedon_date <= curDate() "); 	   
           }else if(count==1 && temp==0){
        	   sb.append(" ml.maturedon_date <= curDate() ");
           }
          if(!(investmentId.isEmpty())){
    	      sb.append(" and ml.account_no = ? ");
           }
	      if(productId != null && productId.length > 0){
		     sb.append(" and mlp.id in (" );
		     for(int i=0; i< productId.length; i++){
			   if(i==0){
			 	  sb.append("'"+ pId.get(i) + "'");
				   
			   }else{
    			   sb.append(", '" + pId.get(i) + "'" );
    		   }
		      	   
		   }
		   sb.append(")");
		   
	      }
	 
	      sb.append(" group by ml.id ");
 	      String sql =  sb.toString();
 	      List<InvestmentBatchJobData> data = new ArrayList<InvestmentBatchJobData>();
 	      if(investmentId.isEmpty() && date.isEmpty()){
 	    	  data = this.jdbcTemplate.query(sql, rm, new Object[]{});
 	    	 
 	      }else if(!(investmentId.isEmpty()) && date.isEmpty()){
 	          data = this.jdbcTemplate.query(sql, rm, new Object[] {investmentId}) ;	
 	          if(data.isEmpty()){
 	        	  throw new NoAnyInvestmentForSpecificAccountException();
 	          }
 	      } else if(!(investmentId.isEmpty()) && !(date.isEmpty())){
 	    	  data = this.jdbcTemplate.query(sql, rm, new Object[] { date , investmentId});
 	      }else if(investmentId.isEmpty() && !(date.isEmpty())){
 	    	  data = this.jdbcTemplate.query(sql, rm, new Object[]{date});
 	    	 if(data.isEmpty()){
	    		  throw new NoAnyInvestmentForGivenMaturityDateException();
	    	  }
 	      }
 	      
 	  
 	       return data;
}	   
	

	private static final class InvestmentBatchJobDataMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum)
				throws SQLException {
			final Long investmentId = rs.getLong("investmentId");
		
			final InvestmentBatchJobData data = new InvestmentBatchJobData(investmentId);
			return data;
			
			
		}	
		
	}

    @Transactional
	@Override
	public List<InvestmentBatchJobData> getAllInvestementDataWithMaturedStatus(Long investmentId) {
		 StringBuilder sb = new StringBuilder();
		 InvestmentBatchJobMapper rm = new InvestmentBatchJobMapper();
		 List<InvestmentBatchJobData> data = new ArrayList<InvestmentBatchJobData>();
		 
		 sb.append(" select ms.saving_id as saving_id, ms.invested_amount as investmentedAmount,  ms.start_date as start_date, ");
		 sb.append(" ms.close_date as close_date, ms.loan_id as loanId from m_investment ms ");
		 /*sb.append(" left join ct_investment_status cis on ms.loan_id = cis.loan_id	 ");
		 sb.append(" where cis.earning_status = 'Matured' ");
	     */
		 sb.append(" where ms.loan_id =  "+ investmentId);
		 
		 data = this.jdbcTemplate.query(sb.toString(), rm, new Object[]{});
		 return data;
	}
	
	private static final class InvestmentBatchJobMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long savingId = rs.getLong("saving_id");
			final BigDecimal investedAmount = rs.getBigDecimal("investmentedAmount");
			final Date investmentStartDate = rs.getDate("start_date");
			final Date closedInvestmentDate = rs.getDate("close_date");
			final Long loanId = rs.getLong("loanId");
			
			final InvestmentBatchJobData data = new InvestmentBatchJobData(savingId, investmentStartDate, closedInvestmentDate,investedAmount, loanId);
			
			return data;
		}
		
	}


	@Transactional
	@Override
	public List<InvestmentBatchJobData> getInvestmentIdsWithMaturedStatus(
			Long investmentId) {
		 List<InvestmentBatchJobData> data = new ArrayList<InvestmentBatchJobData>();
		 InvestmentBatchJobGetInvestmentIdsMapper rm = new InvestmentBatchJobGetInvestmentIdsMapper();
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" select  ms.loan_id as entityId from m_investment ms ");
		 sb.append(" left join ct_investment_status cis on ms.loan_id = cis.loan_id	 ");
		 sb.append(" where cis.earning_status = 'Matured' ");
		 sb.append(" and ms.loan_id =  "+ investmentId);
		 String sql = sb.toString();
		 
		 data = this.jdbcTemplate.query(sql, new Object[]{}, rm );
			
		 return data; 
		
	 }
	
	@Transactional
	@Override
	public List<InvestmentBatchJobData> getAllInvestmentIdsWithMaturedStatus(){
		
		 List<InvestmentBatchJobData> data = new ArrayList<InvestmentBatchJobData>();
		 InvestmentBatchJobGetInvestmentIdsMapper rm = new InvestmentBatchJobGetInvestmentIdsMapper();
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" select  ms.loan_id as entityId from m_investment ms ");
		 sb.append(" left join ct_investment_status cis on ms.loan_id = cis.loan_id	 ");
		 sb.append(" where cis.earning_status = 'Matured' ");
		 String sql = sb.toString();
		 
		 data = this.jdbcTemplate.query(sql, rm , new Object[]{} );
			
		 return data; 
	}
	
	
	@Override
	public InvestmentBatchJobData getInterestDetails() {
		
		StringBuilder sb = new StringBuilder();
		InvestmentBatchJobInterestMapper rm = new InvestmentBatchJobInterestMapper();
		
		sb.append(" select sip.caritas_percentage as caritasPercentage, ");
		sb.append(" sip.group_percentage as groupPercentage, ");
		sb.append(" sip.date as date ");
		sb.append(" from ct_investment_split sip ");
		
		InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(), rm , new Object[]{});
		
		return data;
		
	}
	
	private static final class InvestmentBatchJobInterestMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final BigDecimal cartiasPercentage = rs.getBigDecimal("caritasPercentage");
			final BigDecimal groupPercentage = rs.getBigDecimal("groupPercentage");
			Date date = rs.getDate("date");
			
			final InvestmentBatchJobData data = new InvestmentBatchJobData(cartiasPercentage, groupPercentage , date);
			
			return data;
		}
		
	}
	
	private static final class InvestmentBatchJobGetInvestmentIdsMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long entityId = rs.getLong("entityId");
			
			final InvestmentBatchJobData data = new InvestmentBatchJobData(entityId);
			return data;
		}
		
	}


	@Override
	public InvestmentBatchJobData getLoanClosedDate(Long loanId) {

		InvestmentBatchJobGetCloseLoanDateMapper rm = new InvestmentBatchJobGetCloseLoanDateMapper();
         StringBuilder sb = new StringBuilder();
         sb.append(" select ml.closedon_date as closedDate, ml.interest_charged_derived as totalInterest, ");
         sb.append(" ml.principal_disbursed_derived as totalInvestmentAmount, ");
         sb.append(" ml.disbursedon_date as loanStartDate ");
         sb.append(" from m_loan ml ");
         sb.append(" where ml.id = " + loanId);
         
        InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(), rm, new Object[]{});
        return data;
	}

	
	private static final class InvestmentBatchJobGetCloseLoanDateMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Date closeDate = rs.getDate("closedDate");
			final BigDecimal totalInterest = rs.getBigDecimal("totalInterest");
			final BigDecimal totalInvestmentAmount = rs.getBigDecimal("totalInvestmentAmount");
			final Date loanStartDate = rs.getDate("loanStartDate");
			
			final InvestmentBatchJobData data = new InvestmentBatchJobData(closeDate, totalInterest, totalInvestmentAmount, loanStartDate);
			return data;
		}
		
	}


	@Override
	public InvestmentBatchJobData getPaymentType() {

		// here reusing the Row-mapper class and the method of investmentId to the getpaymentId
		
		InvestmentBatchJobGetInvestmentIdsMapper rm = new InvestmentBatchJobGetInvestmentIdsMapper();
		
         StringBuilder sb = new StringBuilder();
         
         sb.append("select mp.id as entityId ");
         sb.append("from m_payment_type mp ");
         sb.append("where mp.value like 'Earnings From Investment'");
         
         InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(),rm, new Object[]{}); 
         return data;
         
	}

	@Override
	public InvestmentBatchJobData getInvestmentStatus(Long investmentId) {

		try{
         StringBuilder sb = new StringBuilder();
         GetInvestmentStatusMapper rm = new GetInvestmentStatusMapper();
         
         sb.append(" select cis.loan_id as loanId , cis.earning_status as earning_status ");
         sb.append(" from ct_investment_status cis where cis.loan_id = " + investmentId);
         
         InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(), rm , new Object[]{});
         
         return data;
		}catch(Exception e){
        	return null;
         }
         
	}
	
	private static final class GetInvestmentStatusMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
        
		
			final Long loanId = rs.getLong("loanId");
			final String investmentStatus = rs.getString("earning_status");
			
			InvestmentBatchJobData data = new InvestmentBatchJobData(loanId, investmentStatus);
			return data ;    
			
		}
		
	}

	@Override
	public InvestmentBatchJobData getLoanIdStatus(Long loanId) {

	   GetLoanStatusMapper rm = new GetLoanStatusMapper();
	   
       StringBuilder sb = new StringBuilder();
       sb.append(" select ml.loan_status_id as statusId  from  m_loan ml ");
       sb.append(" where ml.id = " + loanId);
       
       InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(),rm, new Object[]{});
      
       return data;
      }
	

	private static final class GetLoanStatusMapper implements RowMapper<InvestmentBatchJobData>{

		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			 final int statusId = rs.getInt("statusId");
			 
			 InvestmentBatchJobData data = new InvestmentBatchJobData(statusId);
  
			 return data;

		}
		
	}

	@Override
	public InvestmentBatchJobData getTotalInvestedAmount(Long loanId) {
		
		GetTotalInvestment rm = new GetTotalInvestment();
		StringBuilder sb = new StringBuilder();
		sb.append(" select sum(mi.invested_amount) as totalInvestment from m_investment mi ");
		sb.append(" where mi.loan_id =  " + loanId);
		
		InvestmentBatchJobData data = this.jdbcTemplate.queryForObject(sb.toString(), rm , new Object[]{});
		
		return data;
	}
	
	
	private static final class GetTotalInvestment implements RowMapper<InvestmentBatchJobData>{
		@Override
		public InvestmentBatchJobData mapRow(ResultSet rs, int rowNum) throws SQLException{
			final BigDecimal totalInvestedAmount = rs.getBigDecimal("totalInvestment");
			//InvestmentBatchJobData data = new InvestmentBatchJobData(totalInvestedAmount);
			return null;
		}
	}
	
}
