package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;

/**
 * Created by aprilanxa on 28/10/2016.
 */

public interface StepsDataListener {
    public void postStepsDataSuccess(String response);
    public void postStepsDataFailedWithError(MessageObj response);

    public void getStepsDataSuccess(Steps response);
    public void getStepsDataFailedWithError(MessageObj response);
}
