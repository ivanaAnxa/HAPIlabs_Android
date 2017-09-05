package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.CoachProgram;
import com.anxa.hapilabs.models.MessageObj;

import java.util.List;

/**
 * Created by aprilanxa on 18/08/2016.
 */
public interface GetCoachProgramListener {

        public void getCoachProgramSuccess(String response,List<CoachProgram> coachPrograms);
        public void getCoachProgramFailedWithError(MessageObj response);
}
