package com.anxa.hapilabs.controllers.surveyquestions;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.PostSurveyAnswerListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostSurveyAnswerResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;


/**
 * Created by aprilanxa on 26/08/2016.
 */
public class PostSurveyAnswerImplementer {

    JsonPostSurveyAnswerResponseHandler jsonPostSurveyAnswerResponseHandler;
    //Handler responseHandler;
    protected ProgressChangeListener progresslistener;

    PostSurveyAnswerListener listener;

    Context context;
    String questionId = "31";


    public PostSurveyAnswerImplementer(Context context, String answer, ProgressChangeListener progressChangeListener, PostSurveyAnswerListener listener) {
        this.context = context;
        this.listener = listener;
        this.progresslistener = progressChangeListener;

        String data = JsonRequestWriter.getInstance().createPostSurveyAnswerJson(answer, questionId);

        System.out.println("createPostSurveyAnswerJson: " + data);
        jsonPostSurveyAnswerResponseHandler = new JsonPostSurveyAnswerResponseHandler(postSurveyAnswerHandler);
        postSurveyAnswer(ApplicationEx.getInstance().userProfile.getRegID(), data, jsonPostSurveyAnswerResponseHandler);
    }

    public void postSurveyAnswer(String username, String data, Handler responseHandler){

        String url = WebServices.getURL(WebServices.SERVICES.POST_SURVEY_ANSWER);
        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_SURVEY_ANSWER) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final  Handler postSurveyAnswerHandler = new Handler(){
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here

                    MessageObj mesObj = (MessageObj)jsonPostSurveyAnswerResponseHandler.getResponseObj();
                    listener.postSurveyAnswerSuccess(mesObj.getMessage_string());

                    //STEP 3:
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObjFailed = (MessageObj)jsonPostSurveyAnswerResponseHandler.getResponseObj();
                    listener.postSurveyAnswerFailedWithError(mesObjFailed);
                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
