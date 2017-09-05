package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;


public interface GetNotificationListener {

	public void getNotificationSuccess(String response);
	public void getNotificationFailedWithError(MessageObj response);


	public void markNotifAsReadSuccess(String response);
	public void markNotifAsReadFailedWithError(MessageObj response);


    public void markAllNotifAsReadSuccess(String response);
	public void markAllNotifAsReadFailedWithError(MessageObj response);


    public void clearAllNotifSuccess(String response);
	public void clearAllNotifFailedWithError(MessageObj response);
}