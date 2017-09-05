package com.anxa.hapilabs.controllers.images;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GenericImageUploadListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Photo;

import java.util.List;

/**
 * Created by angelaanxa on 8/10/2016.
 */
public class GenericImageUploadController {
    GenericImageUploadListener listener;
    Context context;
    GenericImageUploadImplementer imageUploadImpl;

    protected ProgressChangeListener progresslistener;


    public GenericImageUploadController(Context context, ProgressChangeListener progresslistener, GenericImageUploadListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.listener = listener;

    }

    public void startImageUpload(String userid, String hapiId, List<Photo> photos){


        imageUploadImpl = new GenericImageUploadImplementer(context, photos, hapiId, userid, listener);
    }




}
