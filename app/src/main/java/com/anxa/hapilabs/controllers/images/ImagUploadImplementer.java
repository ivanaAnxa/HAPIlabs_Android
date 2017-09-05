package com.anxa.hapilabs.controllers.images;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.BitmapUploadListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonImageUploadResponseHandler;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Photo.PHOTO_STATUS;
import com.anxa.hapilabs.models.UserProfile;

import android.os.Message;

public class ImagUploadImplementer {


    Context context;
    String photoId;
    String mealID;
    //Meal meal;
    BitmapUploadListener listener;
    JsonImageUploadResponseHandler jsonImageHandler;
    String boundary = "12345";

    public ImagUploadImplementer(Context context, Meal meal, String mealID, String userID, BitmapUploadListener listener) {
        this.context = context;
        this.mealID = mealID;
        this.listener = listener;
        //this.meal = meal;

        jsonImageHandler = new JsonImageUploadResponseHandler(uploadImageHandler);
        startImageUpload(meal.photos, mealID, userID);
    }

    public ImagUploadImplementer(Context context, UserProfile profile, String mealID, String userID, BitmapUploadListener listener) {
        this.context = context;
        this.mealID = mealID;
        this.listener = listener;
        //this.meal = meal;

        jsonImageHandler = new JsonImageUploadResponseHandler(uploadImageHandler);

        List<Photo> list = new ArrayList<Photo>();
        Photo photo = new Photo();
        photo.image = profile.getUserProfilePhoto();
        photo.photo_id = userID;
        photo.state = PHOTO_STATUS.ONGOING_UPLOADPHOTO;
        list.add(photo);
        startImageUpload(list, mealID, userID);
    }

    public void startImageUpload(final List<Photo> list, final String mealID, final String userid) {
        Photo photo;

        for (int i = 0; i < list.size(); i++) {
            photo = list.get(i);
            if (photo.state != PHOTO_STATUS.SYNC_UPLOADPHOTO) {
                uploadPhoto(photo.image, userid, mealID, photo.photo_id);
            }
        }
    }


    public void uploadPhoto(Bitmap bmp, String userid, String mealID, String photoid) {

        Connection connection = new Connection(jsonImageHandler);

        String url = WebServices.getURL(SERVICES.UPLOAD_PHOTO);
        connection.addParam("userid", userid);
        String signature = "";
        String hashInput = WebServices.getCommand(SERVICES.UPLOAD_PHOTO) + userid + ApplicationEx.sharedKey;

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

    public void updateMeal(String mealId, String tempPhoto, String photoId, String photourl) {
        //find the meal in the list
        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(mealId);
        if (meal != null) {
            //if meal cannot be found ignore data
            //set the photos in the activity album
            List<Photo> photos = meal.photos;

            //updatePhoto
            for (int i = 0; i < photos.size(); i++) {

                Photo photo = photos.get(i);

                if (photo.photo_id == tempPhoto) {

                    photo.photo_id = photoId;
                    photo.photo_url_large = photourl;
                    photo.state = PHOTO_STATUS.SYNC_UPLOADPHOTO;
                    photos.set(i, photo);
                    break;
                }
            }

            //update the  runtime list
            meal.photos = photos;

            ApplicationEx.getInstance().mealsToAdd.remove(mealId);
            ApplicationEx.getInstance().mealsToAdd.put(mealId, meal);

            ApplicationEx.getInstance().tempList.remove(mealId);
            ApplicationEx.getInstance().tempList.put(mealId, meal);

            //check if all images now has URL; if yes, upload them to server
            Boolean isReady = true;
            for (Photo photo : photos) {
                if (photo.photo_url_large == null || photo.photo_url_large.length() <= 0) {
                    isReady = false;
                }
            }

            if (isReady) {
                listener.BitmapUploadSuccess(isReady, mealId);
            }

        } else {
            if (mealId == tempPhoto) { //this is a userprofile
                //temp id is userid
                //this is a user profile photo
                ApplicationEx.getInstance().userProfile.setPic_url_large(photourl);
                listener.BitmapUploadSuccess(true, mealId);
            }
        }
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
                        //get Photo and update the image uploader;
                        List<Photo> _photos = (List<Photo>) jsonImageHandler.getResponseObj();
                        for (int i = 0; i < _photos.size(); i++) {

                            String tempPhoto = ((Photo) _photos.get(i)).tempId;
                            String photoID = ((Photo) _photos.get(i)).photo_id;
                            String photourl = ((Photo) _photos.get(i)).photo_url_large;

                            updateMeal(mealID, tempPhoto, photoID, photourl);
                        }
                    }
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //dismiss progress here
                    MessageObj msgObj = (MessageObj) jsonImageHandler.getResponseObj();
                    listener.BitmapUploadFailedWithError(msgObj);
                    break;

            }//end switch
        }

    };


}
