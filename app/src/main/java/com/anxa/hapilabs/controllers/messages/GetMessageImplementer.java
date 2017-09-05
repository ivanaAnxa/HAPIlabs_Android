package com.anxa.hapilabs.controllers.messages;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonMessageResponseHandler;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import org.json.JSONException;
import org.json.JSONObject;
//import com.google.android.gms.internal.ms;

public class GetMessageImplementer {

    JsonMessageResponseHandler jsonResponseHandler;

    Context context;
    GetMessagesListener listener;

//    public GetMessageImplementer(Context context, String userId, String toDate,
//                                 String fromDate, GetMessagesListener listener) {
//        this.context = context;
//
//        jsonResponseHandler = new JsonMessageResponseHandler(mainHandler, "");
//        syncServices(userId, fromDate, toDate, jsonResponseHandler);
//        this.listener = listener;
//
//    }

    public GetMessageImplementer(Context context, String userId, String before, String after, String limit, GetMessagesListener listener) {
        this.context = context;

        jsonResponseHandler = new JsonMessageResponseHandler(mainHandler, "");
        syncServices(userId, before, after, limit, jsonResponseHandler);
        this.listener = listener;

    }


    //where data = xml string format post data
    //old: http://api.hapilabs.com/hapicoach/message?lang=en&command=get_coachmessages&signature=7088d734db26557437174cb7ee20194d6920a5d6&userid=6&to=&from=1453615805
    //new: http://dev.api.hapilabs.com/hapicoach/message.json?command=get_coachmessages_paged&userid=6&before=1463981160&limit=5

    public void syncServices(String userID, String before, String after, String limit, Handler responseHandler) {

        String url = WebServices.getURL(SERVICES.GET_MESSAGES_PAGED);
//        WebServices.ConnectionType = WebServices.CONNECTION.STAGING;
        Connection connection = new Connection(responseHandler);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.GET_MESSAGES_PAGED) + userID + before + after + limit));
        connection.addParam("userid", userID);
        connection.addParam("before", before);
        connection.addParam("after", after);
        connection.addParam("limit", limit);

        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.GET, url, "");
    }


    final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>) {

                        List<Message> _messageList = (List<Message>) jsonResponseHandler.getResponseObj();

                        MessageDAO msgDao = new MessageDAO(context, null);
                        //not clearing the table, instead just adding the new messages from the server
                        //clearing the table will happen after login
//                        msgDao.clearTable();

                        DaoImplementer implDao = new DaoImplementer(msgDao, context);

                        Message message;

                        for (int i = 0; i < _messageList.size(); i++) {
                            message = _messageList.get(i);
                            implDao.add(message);
                            ApplicationEx.getInstance().messageList.put(message.message_id, message);
                        }
                    }
                    try {
                        JSONObject api_paging = new JSONObject(msg.obj.toString());
                        JSONObject api_cursor = api_paging.getJSONObject("cursor");
                        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (api_cursor.optString("after") == null || api_cursor.optString("after").length()<1){
                            editor.putString("cursor_after", "");
                        }else{
                            editor.putString("cursor_after", api_cursor.optString("after"));
                        }
                        if (api_cursor.optString("before") == null || api_cursor.optString("before").length()<1){
                            editor.putString("cursor_before", "");
                        }else{
                            editor.putString("cursor_before", api_cursor.optString("before"));
                        }
                        if (api_cursor.optString("next") == null || api_cursor.optString("next").length()<1){
                            editor.putString("cursor_next", "");
                        }else{
                            editor.putString("cursor_next", api_cursor.optString("next"));
                        }
                        if (api_cursor.optString("previous") == null || api_cursor.optString("previous").length()<1){
                            editor.putString("cursor_previous", "");
                        }else{
                            editor.putString("cursor_previous", api_cursor.optString("previous"));
                        }


                        editor.commit();

                        Log.d(api_cursor.optString("previous"), "Success!");
                     } catch (JSONException e) {
                          Log.d("", "Fail!", e);
                     }




//                    JSONObject api_paging = (JSONObject)msg.obj;
//                    JSONObject cursor = api_paging.getJSONObject("cursor");
//                    String afterParam = cursor.optString("after");
//                    String beforeParam = cursor.optString("before");
//                    String nextRecord = cursor.optString("next");
//                    String previousRecord = cursor.optString("previous");
//                    SharedPreferences sharedPreferences = context.getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("cursor_after", afterParam);
//                    editor.putString("cursor_before", beforeParam);
//                    editor.putString("cursor_next", nextRecord);
//                    editor.putString("cursor_previous", previousRecord);
//                    editor.commit();

                    //STEP 3:
                    //launch my meal list
                    listener.getMessagesSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage());

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.getMessagesFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}