package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by aprilanxa on 26/08/2016.
 */
public class JsonPostSurveyAnswerResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonPostSurveyAnswerResponseHandler(Handler handler/*, String strJson*/) {
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

            System.out.println("JsonPostSurveyAnswerResponseHandler: " + strJson);

            // returns null otherwise.
            if (jsonResponse.has("status")) {

                if (jsonResponse.optString("status").equalsIgnoreCase("Successful")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.SUCCESS,
                            jsonResponse);
                    msgObj.setMessage_string(jsonResponse.optString("message_detail"));
                    msgObj.setMessage_id(jsonResponse.optString("status"));
                    setResponseObj(msgObj);

                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }else{
                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED,
                            jsonResponse);
                    msgObj.setMessage_string(jsonResponse.optString("message_detail"));
                    msgObj.setMessage_id(jsonResponse.optString("status"));
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }
            }


            String error_count = jsonResponse.optString("error_count");
            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED,
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
