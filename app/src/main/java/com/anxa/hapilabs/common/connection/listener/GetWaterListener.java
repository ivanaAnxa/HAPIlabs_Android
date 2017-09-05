package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Water;

/**
 * Created by aprilanxa on 21/09/2016.
 */
public interface GetWaterListener {
    public void getWaterSuccess(Water waterResponse);
    public void getWaterFailedWithError(MessageObj response);
}
