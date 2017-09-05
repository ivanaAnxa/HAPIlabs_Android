package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 public interface PushRegListener {

		public void pushRegSuccess(String response);
		public void pushRegFailedWithError(MessageObj response);
	
}