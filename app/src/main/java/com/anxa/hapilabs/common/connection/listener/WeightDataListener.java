package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Weight;

/**
 * Created by aprilanxa on 13/10/2016.
 */

public interface WeightDataListener {
    public void postWeightDataSuccess(String response);
    public void postWeightDataFailedWithError(MessageObj response);

    public void getWeightDataSuccess(Weight response);
    public void getWeightDataFailedWithError(MessageObj response);
}
