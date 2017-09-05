package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.MessageObj;

 interface UploadCommentHapiListener {

		public void uploadCommentHapiSuccess(String response, Comment comment, String mealID);
		public void uploadCommentHapiFailedWithError(MessageObj response, String mealID);
		
}