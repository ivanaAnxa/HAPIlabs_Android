package com.anxa.hapilabs.controllers.water;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.AddWaterListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonAddWaterResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.Water;


/**
 * Created by aprilanxa on 25/07/2016.
 */
public class AddWaterImplementer {

    Handler responseHandler;

    ProgressChangeListener progressChangeListener;

    AddWaterListener addWaterListener;
    JsonAddWaterResponseHandler jsonAddWaterResponseHandler;
    Context context;
    private Water water;


    public AddWaterImplementer(Context context, String username, Water water,
                               ProgressChangeListener progressChangeListener,
                               AddWaterListener addWaterListener) {

        this.context = context;
        this.addWaterListener = addWaterListener;
        this.progressChangeListener = progressChangeListener;
        this.water = water;
        String data = JsonRequestWriter.getInstance().createAddWaterJson(water);
        System.out.println("data: " + data);

        jsonAddWaterResponseHandler = new JsonAddWaterResponseHandler(addWaterHandler, "");
        addWaterService(username, data, jsonAddWaterResponseHandler);
    }

    //where data = xml string format post data
    private void addWaterService(String username, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_HAPI_WATER);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_HAPI_WATER) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler addWaterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonAddWaterResponseHandler != null) {
//                        jsonAddWaterResponseHandler.getResponseObj()
                        //STEP 1: stop progress here
//                        Water water = new Water();
//                        water = (Water) jsonAddWaterResponseHandler.getResponseObj();
//                        ApplicationEx.getInstance().waterList.add(water);
//                        System.out.println("waterID: " + water.getWater_id());
                    }

                    addWaterListener.addWaterSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:

                    break;
            }//end Switch
        }
    };

}
