package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.SurveyOption;

import java.util.List;

public interface GetSurveyQuestionListener {

	public void getQuestionSuccess(String response,List<SurveyOption> options);
	public void getQuestionFailedWithError(MessageObj response);

}