package com.anxa.hapilabs.controllers.images;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;

import android.os.Message;

public class GetImageDownloadImplementer {

    Context context;
    String photoId;
    String mealID;
    BitmapDownloadListener bitmapListener;

    public GetImageDownloadImplementer(Context context, String url, String photoId, String mealID, BitmapDownloadListener bitmapListener) {
//        Log.i("GetImage", url + "-" + photoId);
        this.context = context;
        this.bitmapListener = bitmapListener;
        this.photoId = photoId;
        this.mealID = mealID;
        downloadImage(url, photoId, mealID);
    }

    //where data = xml string format post data
    public void downloadImage(String url, String photoID, String mealID) {
        Connection connection = new Connection(bitmapHandler);
        connection.getBitmap(url, mealID, photoID);
    }

    @SuppressLint("HandlerLeak")
    final Handler bitmapHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Connection.REQUEST_START:
                    //show progress bar here
                    break;
                case Connection.REQUEST_SUCCESS:
                    //dismiss progress bar here
                    bitmapListener.BitmapDownloadSuccess(photoId, mealID);

                    break;
                case Connection.REQUEST_ERROR:
                    //dismiss progress here
                    break;
            }//end switch
        }

    };
}