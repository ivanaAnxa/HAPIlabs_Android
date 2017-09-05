package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/****
 * Notes:
 * - Return =  UserProfileObj for successful request
 * - MessageObj for failed request
 */
public class JsonImageUploadResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    JSONObject jsonResponse;
    String strJson;
    String tempPhotoId;
    Message msg = new Message();


    /******
     * STEP1:
     * PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     */

    public JsonImageUploadResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void start(String strJson, String tempid) {
        this.strJson = strJson;
        this.tempPhotoId = tempid;
        start();
    }

    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {

        /****** STEP3: START JSON PARSING HERE */
        try {
            /****** inform handler that the jsn processing has started; this is optional *********/
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            Log.i("jsonResponse", "" + strJson);

            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            try {
                jsonResponse = new JSONObject(strJson);
            } catch (JSONException e) {
                e.printStackTrace();
                msg.what = JsonDefaultResponseHandler.ERROR;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            }


            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
            /*******  Returns null otherwise.  *******/
            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String requestStatus = api_response.optString("message_detail");
            String message = api_response.optString("message");

            if (requestStatus.equalsIgnoreCase("Successful")) {
                JSONArray data = jsonResponse.getJSONArray("photo");

                if (data != null) {
                    List<Photo> photos = new ArrayList<Photo>();
                    for (int index = 0; index < data.length(); index++) {
                        JSONObject json = data.getJSONObject(index);
                        photos.add(JsonUtil.getPhoto(json, tempPhotoId));
                    }

                    //transfer the message obj to the response handler
                    setResponseObj(photos);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.COMPLETED;

                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }
            } else if (message.equalsIgnoreCase("failed")) {
                //set message obj for the failed request
                MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
                //transfer the message obj to the response handler
                setResponseObj(messageObj);
                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.ERROR;
                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            } else {
                //for failed request
                // set message obj for the failed request
                    MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
                    //transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
            }
        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }
    }

}