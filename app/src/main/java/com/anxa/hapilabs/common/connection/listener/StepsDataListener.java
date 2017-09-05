package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 28/10/2016.
 */

public interface StepsDataListener {
    public void postStepsDataSuccess(String response);
    public void postStepsDataFailedWithError(MessageObj response);
}
