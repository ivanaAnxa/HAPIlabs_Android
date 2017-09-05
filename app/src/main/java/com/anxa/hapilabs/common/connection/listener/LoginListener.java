package com.anxa.hapilabs.common.connection.listener;

import android.os.Handler;

import com.anxa.hapilabs.models.MessageObj;

 public interface LoginListener {

		public void loginSuccess(String response);
		public void loginFailedWithError(MessageObj response);
		public void loginServices(String username, String password, String data, Handler responseHandler);

}