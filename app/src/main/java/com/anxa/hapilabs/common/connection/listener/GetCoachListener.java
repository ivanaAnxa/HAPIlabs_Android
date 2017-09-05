package com.anxa.hapilabs.common.connection.listener;

import java.util.List;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;

public interface GetCoachListener {

		public void getCoachSuccess(String response,List<Coach> coaches);
		public void getCoachFailedWithError(MessageObj response);


}