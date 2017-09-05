package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 interface UploadPhotoListener {

		public void uploadPhotoSuccess(String response);
		public void uploadPhotoFailedWithError(MessageObj response);
		
}