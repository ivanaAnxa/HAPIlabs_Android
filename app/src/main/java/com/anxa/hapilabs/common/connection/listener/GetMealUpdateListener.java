package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;


public interface GetMealUpdateListener {

	public void getMealUpdateSuccess(String response);
	public void getMealUpdateFailedWithError(MessageObj response);


}