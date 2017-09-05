package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.CoachProgram;
import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 18/08/2016.
 */
public class JsonCoachProgramResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;
    protected boolean isError = false;

    String OutputData = "";
    String strJson;
    JSONObject jsonResponse;
    Message msg = new Message();

    public JsonCoachProgramResponseHandler(Handler handler) {
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

        try {

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);
            System.out.println("JsonCoachProgramResponseHandler " + strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("coach_program")) {

                if (jsonResponse.has("coach_program")) {
                    JSONArray coaches = jsonResponse.getJSONArray("coach_program");
                    List<CoachProgram> coachProgramList = new ArrayList<CoachProgram>();

                    if (coaches != null && coaches.length() > 0) {
                        for (int i = 0; i < coaches.length(); i++) {

                            JSONObject coachJson = coaches.getJSONObject(i);
                            CoachProgram coachProgram = JsonUtil.getCoachProgram(coachJson);

                            if (coachProgram != null) {
                                coachProgramList.add(coachProgram);
                            }
                        }

                        setResponseObj(coachProgramList);
                        msg.what = JsonDefaultResponseHandler.COMPLETED;
                        // set the handler for the waiting class
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
