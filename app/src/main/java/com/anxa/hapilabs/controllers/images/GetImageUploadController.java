package com.anxa.hapilabs.controllers.images;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.BitmapUploadListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.UserProfile;


public class GetImageUploadController{

	BitmapUploadListener listener;
	Context context;
	ImagUploadImplementer imageUploadImpl;
	
	protected ProgressChangeListener progresslistener;


	public GetImageUploadController(Context context,ProgressChangeListener progresslistener,BitmapUploadListener listener) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.listener = listener;
		
	}

	public void startImageUpload(String userid, String mealid){
		Meal meal = ApplicationEx.getInstance().tempList.get(mealid);
		
		if (meal==null)
			 return;
		imageUploadImpl = new ImagUploadImplementer(context,meal,mealid,userid,listener);
	}


	public void startImageUpload(String userid, UserProfile profile){
		imageUploadImpl = new ImagUploadImplementer(context,profile,userid,userid,listener);
	}





	
}
