package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

public interface RegistrationListener {

		public void postRegistrationSuccess(String response);
		public void  postRegistrationFailedWithError(MessageObj response);


}