package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

public interface MealDeleteListener {

	public void deleteMealSuccess(String response);

	public void deleteMealError(MessageObj response, String entryID);

}