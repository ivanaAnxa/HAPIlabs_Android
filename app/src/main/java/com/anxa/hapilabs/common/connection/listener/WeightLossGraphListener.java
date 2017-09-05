package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 11/10/2016.
 */

public interface WeightLossGraphListener {
    public void getWeightGraphSuccess(String response);
    public void getWeightGraphFailedWithError(MessageObj response);
}
