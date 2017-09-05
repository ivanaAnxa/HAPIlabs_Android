package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 26/08/2016.
 */
public interface PostSurveyAnswerListener {

    public void postSurveyAnswerSuccess(String response);
    public void postSurveyAnswerFailedWithError(MessageObj response);
}