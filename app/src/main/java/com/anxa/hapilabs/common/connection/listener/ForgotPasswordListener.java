package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 public interface ForgotPasswordListener {

		public void forgotPasswordSuccess(String response);
		public void forgotPasswordFailedWithError(MessageObj response);


}