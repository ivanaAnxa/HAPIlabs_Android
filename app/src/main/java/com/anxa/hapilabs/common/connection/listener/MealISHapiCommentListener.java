package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

public interface MealISHapiCommentListener {

		public void uploadISHapiCommentSuccess(String response);
		public void uploadISHapiCommentFailedWithError(MessageObj response, String entryID);
		
}