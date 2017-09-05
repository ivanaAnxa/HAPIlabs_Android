package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 public interface BitmapUploadListener {

		public void BitmapUploadSuccess(Boolean forUpload,String mealId);
	
		public void BitmapUploadFailedWithError(MessageObj response);

}