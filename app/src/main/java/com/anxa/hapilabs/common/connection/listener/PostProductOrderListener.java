package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 19/08/2016.
 */
public interface PostProductOrderListener {

    public void postProductOrderSuccess(String response);
    public void postProductOrderFailedWithError(MessageObj response);
}
