package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aprilanxa on 02/03/2017.
 */

public class JsonCommunityCommentResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonCommunityCommentResponseHandler(Handler handler) {
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

            System.out.println("JsonCommunityCommentResponseHandler: " + jsonResponse);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("comment_id")) {
                String comment_id = jsonResponse.optString("comment_id");

                setResponseObj(comment_id);
                msg.what = JsonDefaultResponseHandler.COMPLETED;
                handler.handleMessage(msg);
                return;
            } else {
                if (jsonResponse.optString("message").equalsIgnoreCase("Successful")){
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }else {
                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED,
                            jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
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
