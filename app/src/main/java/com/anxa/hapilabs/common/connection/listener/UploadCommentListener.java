package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.MessageObj;

 interface UploadCommentListener {

		public void uploadCommentSuccess(String response, Comment comment, String mealID);
		public void uploadCommentFailedWithError(MessageObj response, String mealID);


		
		
}