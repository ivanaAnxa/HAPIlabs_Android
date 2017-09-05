package com.anxa.hapilabs.controllers.mymeals;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.SyncListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;


public class MyListMainController implements SyncListener, BitmapDownloadListener {


    Handler handler;
    Context context;

    TimerTask mTimerTask;
    Handler Timehandler;
    Timer t;

    GetSyncImplementer implementer;
    String toDate = null;
    String fromDate = null;

    public MyListMainController(Context context) {
        this.context = context;

        toDate = AppUtil.getDateOnlyinUTC(ApplicationEx.getInstance().toDateSyncCurrent);
        fromDate = AppUtil.getDateOnlyinUTC(ApplicationEx.getInstance().fromDateSyncCurrent);

        getSync();//run once the use timer for succeeding sync
    }

    public void cancelTimer() {
        try {
            mTimerTask.cancel();

        } catch (Exception e) {
        }
        try {
            t.cancel();
        } catch (Exception e) {
        }
    }

    public void run() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    getSync();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//end run

            // TODO Auto-generated method stub

        };
        thread.start();
    }

    private void getSync() {
		System.out.println("getSync@MyListMainController  SYNC : "
				+ "TO: "+toDate+" "
				+ "FROM:"+fromDate
				+ "REG: "+ ApplicationEx.getInstance().userProfile.getRegID());
        String userId = ApplicationEx.getInstance().userProfile.getRegID();
        implementer = new GetSyncImplementer(context, userId, toDate, fromDate, this);
    }

    private void download(String photoId, String url, String mealId) {

        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(context,
                url,
                photoId,
                mealId, this);


    }

    public void startImageDownload(final Hashtable<String, Meal> mealList) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Enumeration<Meal> mealEnum = mealList.elements();

                    while (mealEnum.hasMoreElements()) {

                        Meal meal = mealEnum.nextElement();
                        List<Photo> photos = meal.photos;

                        if (photos != null && photos.size() > 0) {

                            //download photo
                            for (int j = 0; j < photos.size(); j++) {

                                Photo photo = photos.get(j);

                                if (photo.image == null && photo.photo_id != null && photo.photo_url_large != null) {
                                    //download
                                    final String photoid = photo.photo_id;
                                    final String mealid = meal.meal_id;
                                    final String url = photo.photo_url_large;

                                    ((Activity) context).runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            download(photoid, url, mealid);
                                        }
                                    });
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }


    @Override
    public void getSyncSuccess(String response) {
        // TODO Auto-generated method stub
        //update last sync date
        //set the fromDate to the last toDate
        ApplicationEx.getInstance().setSyncDate(ApplicationEx.getInstance().toDateSyncCurrent, context);

        Intent broadint = new Intent();
        broadint.setAction(context.getResources().getString(R.string.broadcast_sync));
        //remove progressbar for the first sync
        ApplicationEx.getInstance().isLoginSync = false;
        context.sendBroadcast(broadint);

        //start image   here
        startImageDownload(ApplicationEx.getInstance().tempList);
    }

    @Override
    public void getSyncFailedWithError(MessageObj response) {
        //do not update the last sync date
    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub

        Intent broadint = new Intent();
        broadint.setAction(context.getResources().getString(R.string.meallist_photo_download));
        broadint.putExtra("MEAL_ID", mealId);
        broadint.putExtra("PHOTO_ID", photoId);

        context.sendBroadcast(broadint);
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }


}
