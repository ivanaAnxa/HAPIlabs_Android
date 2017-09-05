package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;


public interface GetMessagesListener {

	public void getMessagesSuccess(String response);
	public void getMessagesFailedWithError(MessageObj response);
	public void postMessagesSuccess(String response);
	public void postMessagesError(MessageObj response);

}