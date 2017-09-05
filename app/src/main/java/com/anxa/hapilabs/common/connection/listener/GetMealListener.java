package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

public interface GetMealListener {

    public void getMealSuccess(String response);
    public void getMealFailedWithError(MessageObj messageObj);
}