package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 4/11/2016.
 */
public interface UpdateProfileListener {
    public void updateProfileSuccess(String response);
    public void updateProfileFailedWithError(MessageObj response);
}
