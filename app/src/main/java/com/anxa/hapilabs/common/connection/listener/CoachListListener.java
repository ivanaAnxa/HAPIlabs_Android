package com.anxa.hapilabs.common.connection.listener;

import java.util.List;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;

 interface CoachListListener {

		public void getCoachlistSuccess(List<Coach> list);
		public void getCoachlistFailedWithError(MessageObj response);


}