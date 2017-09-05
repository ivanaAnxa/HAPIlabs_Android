package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

public interface MealAddListener {

		public void uploadMealSuccess(String response);
		public void uploadMealFailedWithError(MessageObj response, String entryID);


}