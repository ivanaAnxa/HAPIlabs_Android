package com.anxa.hapilabs.controllers.surveyquestions;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PostSurveyAnswerListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 26/08/2016.
 */
public class PostSurveyAnswerController {

    Context context;
    PostSurveyAnswerImplementer postSurveyAnswerImplementer;
    protected ProgressChangeListener progresslistener;
    PostSurveyAnswerListener postSurveyAnswerListener;

    public PostSurveyAnswerController(Context context, ProgressChangeListener progresslistener, PostSurveyAnswerListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.postSurveyAnswerListener = listener;
    }



    public void postSurveyAnswer(String answer){
        postSurveyAnswerImplementer = new PostSurveyAnswerImplementer(context, answer, progresslistener, postSurveyAnswerListener);

    }
}
