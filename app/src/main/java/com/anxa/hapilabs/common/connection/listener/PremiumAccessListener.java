package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 22/08/2016.
 */
public interface PremiumAccessListener {

    public void sendAccessCodeSuccess(String response);
    public void sendAccessCodeFailedWithError(MessageObj response);

    public void validateAccessCodeSuccess(String response);
    public void validateAccessCodeFailedWithError(MessageObj response);

}
