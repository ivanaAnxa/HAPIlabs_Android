package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.SurveyOption;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.util.Log;


/**
 * Created by jas on 10/02/2016.
 */
public class JsonSurveyQuestionsResponseHandler extends JsonDefaultResponseHandler {
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonSurveyQuestionsResponseHandler(Handler handler/*, String strJson*/) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {

        /** STEP3: START JSON PARSING HERE: */

        try {

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            ApplicationEx.language = Locale.getDefault().getLanguage();

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("option")) {

                JSONArray options = jsonResponse.getJSONArray("option");

                List<SurveyOption> optionsList = new ArrayList<SurveyOption>();

                if (options != null && options.length() > 0) {
                    for (int i = 0; i < options.length(); i++) {

                        JSONObject optionJson = options.getJSONObject(i);
                        SurveyOption surveyOption = JsonUtil.getSurveyQuestions(optionJson);
                        Log.i("option here", surveyOption.option_text + ApplicationEx.language);

                        if (surveyOption != null) {

                            optionsList.add(surveyOption);
                        }
                    }

                    setResponseObj(optionsList);

                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }

            }

            String error_count = jsonResponse.optString("error_count");
            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED,
                        jsonResponse);
                setResponseObj(msgObj);
                msg.what = JsonDefaultResponseHandler.COMPLETED;
                handler.handleMessage(msg);
                return;
            }

        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }

    }

    @Override
    public void start(String strJson, String id) {
        // TODO Auto-generated method stub

    }

}
