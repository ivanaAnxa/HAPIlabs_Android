package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by angelaanxa on 8/9/2016.
 */
public interface AddHapiMomentListener {
    public void addHapiMomentSuccess(String response);
    public void addHapiMomentFailedWithError(MessageObj response);

}
