package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public interface PostStepsGoogleFitListener {

    public void postStepsGoogleFitSuccess(String response);
    public void postStepsGoogleFitFailedWithError(MessageObj response);
}
