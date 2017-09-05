package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

 public interface SyncListener {

		void getSyncSuccess(String response);
		void getSyncFailedWithError(MessageObj response);
}