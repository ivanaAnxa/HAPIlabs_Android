package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.TimelineActivity;

/**
 * Created by aprilanxa on 08/03/2017.
 */

public interface GetTimelineActivityListener {
    public void getTimelineActivitySuccess(TimelineActivity response);
    public void getTimelineActivityFailedWithError(MessageObj response);
}
