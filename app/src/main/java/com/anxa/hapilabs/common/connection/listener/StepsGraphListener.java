package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public interface StepsGraphListener {
    public void getStepsGraphSuccess(String response);
    public void getStepsGraphFailedWithError(MessageObj response);
}
