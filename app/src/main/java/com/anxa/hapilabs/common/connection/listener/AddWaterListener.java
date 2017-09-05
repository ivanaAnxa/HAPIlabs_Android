package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 25/07/2016.
 */
public interface AddWaterListener {

    public void addWaterSuccess(String response);
    public void addWaterFailedWithError(MessageObj response);

}
