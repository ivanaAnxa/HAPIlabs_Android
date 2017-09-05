package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 public interface BitmapDownloadListener {

		public void BitmapDownloadSuccess(String photoId, String mealId);
	
		public void BitmapDownloadFailedWithError(MessageObj response);


}