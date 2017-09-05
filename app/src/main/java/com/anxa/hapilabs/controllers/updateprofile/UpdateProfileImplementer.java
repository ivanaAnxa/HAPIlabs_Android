package com.anxa.hapilabs.controllers.updateprofile;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.UpdateProfileListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonUpdateProfileResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;

/**
 * Created by aprilanxa on 4/11/2016.
 */
public class UpdateProfileImplementer {

    protected ProgressChangeListener progresslistener;

    JsonUpdateProfileResponseHandler jsonResponseHandler;
    UpdateProfileListener listener;
    Context context;

    public UpdateProfileImplementer(Context context,
                                   UserProfile userprofile,
                                   ProgressChangeListener progresslistener,
                                   UpdateProfileListener listener) {

        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;

//        String data = JsonRequestWriter.getInstance().createRegistrationRequest(context,userprofile);
        String data = JsonRequestWriter.getInstance().createUpdateProfileJson(context, userprofile);
        System.out.println("data: " + data);

        jsonResponseHandler = new JsonUpdateProfileResponseHandler(updateProfileHandler);
        updateProfile(ApplicationEx.getInstance().userProfile.getRegID(), data, jsonResponseHandler);

    }

    public void updateProfile(String userid , String data, Handler responseHandler){
        String url = WebServices.getURL(WebServices.SERVICES.UPDATE_PROFILE);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userid);
        connection.addParam("signature",connection.createSignature(WebServices.getCommand(WebServices.SERVICES.UPDATE_PROFILE)+userid) );
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler updateProfileHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() instanceof UserProfile){

                        String password = ApplicationEx.getInstance().userProfile.getPassword();
                        String passwordplain = ApplicationEx.getInstance().userProfile.getPasswordPlain();

                        ApplicationEx.getInstance().userProfile = ((UserProfile)jsonResponseHandler.getResponseObj());
                        ApplicationEx.getInstance().userProfile.setPassword(password);
                        ApplicationEx.getInstance().userProfile.setPasswordPlain(passwordplain);


                        listener.updateProfileSuccess("SUCCESS");

                    }else if ( jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj){


                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj)jsonResponseHandler.getResponseObj();

                        if (mesObj.getType() == MessageObj.MESSAGE_TYPE.SUCCESS){
                            listener.updateProfileSuccess(mesObj.getMessage_string());
                        }
                        mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                        listener.updateProfileFailedWithError(mesObj);

                    }else{

                    }

                    //STEP 3:
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    listener.updateProfileFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
