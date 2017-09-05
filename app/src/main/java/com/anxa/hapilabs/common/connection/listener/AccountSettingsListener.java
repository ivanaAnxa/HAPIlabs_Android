package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 03/08/2016.
 */
public interface AccountSettingsListener {
    public void accountSettingsSuccess(String response);
    public void accountSettingsFailedWithError(MessageObj response);
}
