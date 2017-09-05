package com.anxa.hapilabs.controllers.images;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GenericImageUploadListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonImageUploadResponseHandler;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by angelaanxa on 8/10/2016.
 */
public class GenericImageUploadImplementer {
    Context context;
    String hapiId;

    GenericImageUploadListener listener;
    JsonImageUploadResponseHandler jsonImageHandler;
    String boundary = "12345";


    public GenericImageUploadImplementer(Context context, List<Photo> photos, String hapiMomentId, String userID, GenericImageUploadListener listener) {
        this.context = context;
        this.listener = listener;

        hapiId = hapiMomentId;

        jsonImageHandler = new JsonImageUploadResponseHandler(uploadImageHandler);
        startImageUpload(photos, userID);
    }


    public void startImageUpload(final List<Photo> list, final String userid) {
        Photo photo;
        for (int i = 0; i < list.size(); i++) {
            photo = list.get(i);
            if (photo.state != Photo.PHOTO_STATUS.SYNC_UPLOADPHOTO) {
                uploadPhoto(photo.image, userid, photo.photo_id);
            }
        }
    }

    public void uploadPhoto(Bitmap bmp, String userid, String photoid) {
        Connection connection = new Connection(jsonImageHandler);

        String url = WebServices.getURL(WebServices.SERVICES.UPLOAD_PHOTO);
        connection.addParam("userid", userid);
        String signature = "";
        String hashInput = WebServices.getCommand(WebServices.SERVICES.UPLOAD_PHOTO) + userid + ApplicationEx.sharedKey;

        try {
            signature = AppUtil.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            //TODO: call error display UI here
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //TODO: call error display UI here
        }
        connection.addParam("signature", signature);
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.bmp = bmp;
        connection.textContext = context;
        connection.multipost = true;
        connection.setEntryString(photoid);
        connection.create(Connection.POST, url, "data");
    }


    @SuppressLint("HandlerLeak")
    final Handler uploadImageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    if (jsonImageHandler.getResponseObj() != null && jsonImageHandler.getResponseObj() instanceof List<?>) {
                        List<Photo> _photos = (List<Photo>) jsonImageHandler.getResponseObj();
                        for (int i = 0; i < _photos.size(); i++) {

                            String tempPhoto = ((Photo) _photos.get(i)).tempId;
                            String photoID = ((Photo) _photos.get(i)).photo_id;
                            String photourl = ((Photo) _photos.get(i)).photo_url_large;

                            updateCurrentHAPImoment(tempPhoto, photoID, photourl);
                        }
                    }
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //dismiss progress here
                    MessageObj msgObj = (MessageObj) jsonImageHandler.getResponseObj();
                    listener.ImageUploadFailedWithError(msgObj);
                    break;

            }//end switch
        }

    };

    private void updateCurrentHAPImoment(String tempPhoto, String photoId, String photourl) {

        HapiMoment hapiMoment = ApplicationEx.getInstance().currentHapiMoment;

        if (hapiMoment != null) {
            //if meal cannot be found ignore data
            //set the photos in the activity album
            List<Photo> photos = hapiMoment.photos;

            //updatePhoto
            for (int i = 0; i < photos.size(); i++) {

                Photo photo = photos.get(i);

                if (photo.photo_id == tempPhoto) {
                    photo.photo_id = photoId;
                    photo.photo_url_large = photourl;
                    photo.state = Photo.PHOTO_STATUS.SYNC_UPLOADPHOTO;
                    photos.set(i, photo);
                    break;
                }
            }
            //update the  runtime list
            hapiMoment.photos = photos;
            ApplicationEx.getInstance().currentHapiMoment = hapiMoment;

            //check if all images now has URL; if yes, upload them to server
            Boolean isReady = true;
            for (Photo photo : photos) {
                if (photo.photo_url_large == null || photo.photo_url_large.length() <= 0) {
                    isReady = false;
                }
            }

            if (isReady) {
                listener.ImageUploadSuccess(isReady, hapiId);
            }
        }
    }
}
