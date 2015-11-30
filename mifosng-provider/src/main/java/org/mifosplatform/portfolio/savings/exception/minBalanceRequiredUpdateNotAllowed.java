package org.mifosplatform.portfolio.savings.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class minBalanceRequiredUpdateNotAllowed extends AbstractPlatformResourceNotFoundException{

	public minBalanceRequiredUpdateNotAllowed() {
		super("Saving Amount is invested, not allow to update to decrease, You can update to increase amount", "Saving Amount is invested, not allow to update");
		// TODO Auto-generated constructor stub
	}

}
