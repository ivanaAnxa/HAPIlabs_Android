package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by elaineanxa on 08/08/2016.
 */
public interface GetAllMealsListener
{
    public void getAllMealsSuccess(String response, String command);
    public void getAllMealsFailedWithError(MessageObj response);
}
