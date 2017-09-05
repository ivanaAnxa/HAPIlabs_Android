package com.anxa.hapilabs.common.storage;

import com.anxa.hapilabs.models.MessageObj;



	public interface DaoListener {

			public void getSuccessDB(MessageObj response);
			public void detFailedDB(MessageObj response);

 
}