package com.anxa.hapilabs.controllers.surveyquestions;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetSurveyQuestionListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by jas on 10/02/2016.
 */
public class GetSurveyQuestionsController {

    Context context;
    SurveyQuestionsImplementer questionsImpl;

    protected ProgressChangeListener progresslistener;

    GetSurveyQuestionListener listener;

    public GetSurveyQuestionsController(Context context,ProgressChangeListener progresslistener,GetSurveyQuestionListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.listener =listener;
    }



    public void getQuestions(String username){
        questionsImpl = new SurveyQuestionsImplementer(context,username,progresslistener,listener);

    }
}
