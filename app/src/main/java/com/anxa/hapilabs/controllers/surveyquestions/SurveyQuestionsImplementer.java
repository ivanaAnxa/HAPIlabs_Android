package com.anxa.hapilabs.controllers.surveyquestions;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetSurveyQuestionListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;

import com.anxa.hapilabs.common.handlers.reader.JsonSurveyQuestionsResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.SurveyOption;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;


/**
 * Created by jas on 10/02/2016.
 */
public class SurveyQuestionsImplementer {

    JsonSurveyQuestionsResponseHandler jsonResponseHandler;

    //Handler responseHandler;
    protected ProgressChangeListener progresslistener;

    GetSurveyQuestionListener listener;

    Context context;

    public SurveyQuestionsImplementer(Context context,String username, /*Handler responseHandler,*/ProgressChangeListener progresslistener,GetSurveyQuestionListener listener) {
        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;

        jsonResponseHandler = new JsonSurveyQuestionsResponseHandler(questionsHandler);
        getQuestions(username, jsonResponseHandler);
    }

    public void getQuestions(String username, Handler responseHandler){

//        int userId = 1;
        String questionId = "31";

        String url = WebServices.getURL(SERVICES.GET_SURVEY_QUESTION);
        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", questionId);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_SURVEY_QUESTION) + username + questionId));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }

    final  Handler questionsHandler = new Handler(){
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() instanceof List<?>){

                        List<SurveyOption> options = (List<SurveyOption>)jsonResponseHandler.getResponseObj();

                        //update language
                        if (Locale.getDefault().getLanguage() !=null)
                            ApplicationEx.language = Locale.getDefault().getLanguage();

                        listener.getQuestionSuccess("SUCCESS", options);

                    }else if ( jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj){
                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj)jsonResponseHandler.getResponseObj();
                        mesObj.setType(MESSAGE_TYPE.FAILED);
                        listener.getQuestionFailedWithError(mesObj);

                    }

                    //STEP 3:
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);
                    listener.getQuestionFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
