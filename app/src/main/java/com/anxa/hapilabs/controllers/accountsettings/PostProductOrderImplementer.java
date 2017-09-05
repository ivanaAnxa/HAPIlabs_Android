package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.PostProductOrderListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostProductOrderResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 19/08/2016.
 */
public class PostProductOrderImplementer {

    ProgressChangeListener progressChangeListener;
    PostProductOrderListener postProductOrderListener;
    JsonPostProductOrderResponseHandler jsonPostProductOrderResponseHandler;
    Handler responseHandler;
    Context context;

    public PostProductOrderImplementer(Context context, String coachProgramId,
                                          ProgressChangeListener progressChangeListener,
                                          PostProductOrderListener listener) {

        String username = ApplicationEx.getInstance().userProfile.getRegID();

        this.context = context;
        this.postProductOrderListener = listener;
        this.progressChangeListener = progressChangeListener;

        jsonPostProductOrderResponseHandler = new JsonPostProductOrderResponseHandler(postProductOrderHandler, "");
        postProductOrder(username, coachProgramId, jsonPostProductOrderResponseHandler);

    }

    //where data = xml string format post data
    private void postProductOrder(String username, String coachProgramId, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_PRODUCT_ORDER);

        Connection connection = new Connection(responseHandler);
        connection.addParam("id", coachProgramId);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_PRODUCT_ORDER) + username + coachProgramId));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, "");
    }

    final Handler postProductOrderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    postProductOrderListener.postProductOrderSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id("");
                    mesObj.setMessage_string("");
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    postProductOrderListener.postProductOrderFailedWithError(mesObj);

                    break;
            }//end Switch
        }
    };
}
