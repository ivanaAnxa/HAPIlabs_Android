package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 14/09/2016.
 */
public interface GetHapiMomentListener {
    public void getHapiMomentSuccess(HapiMoment hapiMoment);
    public void getHapiMomentFailedWithError(MessageObj response);
}
