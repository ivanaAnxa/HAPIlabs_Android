package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public interface RegisterDeviceListener {
    public void registerDeviceSuccess(String response);
    public void registerDeviceFailedWithError(MessageObj response);
}
