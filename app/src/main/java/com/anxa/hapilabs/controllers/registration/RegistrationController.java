package com.anxa.hapilabs.controllers.registration;


import android.app.Activity;
import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.BitmapUploadListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.RegistrationListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.images.GetImageUploadController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;


public class RegistrationController implements BitmapUploadListener,ProgressChangeListener{

	Context context;

	RegistrationImplementer registrationImpl;

	protected ProgressChangeListener progresslistener;

	RegistrationListener listener;

	UserProfile profile;

	public RegistrationController(Context context,ProgressChangeListener progresslistener,RegistrationListener listener) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.listener =listener;
	}



	public void register(UserProfile profile){
		this.profile = profile;

		registerNow();
	}




	private void uploadPhoto(UserProfile profile){
		GetImageUploadController getImageUploadController = new GetImageUploadController(context,this,this);

		if (profile.getRegID() != null){
//		if (profile.getUsername() != null){
//			getImageUploadController.startImageUpload(profile.getUsername(),profile);
			getImageUploadController.startImageUpload(profile.getRegID(),profile);

		}else{
//			getImageUploadController.startImageUpload(profile.getUsername(),profile);
			getImageUploadController.startImageUpload("0", profile);

		}

	}



	public void registerNow() {
		((Activity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {

				registrationImpl = new RegistrationImplementer(context, profile ,progresslistener,listener);

			}
		} );


	}

	@Override
	public void BitmapUploadSuccess(Boolean forUpload, String mealId) {
		// TODO Auto-generated method stub

		if (forUpload){
			//find the meal in the meal list
			//update userprofile

			if (mealId != null){
				profile.setPic_url_large(ApplicationEx.getInstance().userProfile.getPic_url_large());
				registerNow();
			}
		}

	}



	@Override
	public void BitmapUploadFailedWithError(MessageObj response) {
		// TODO Auto-generated method stub

	}



	@Override
	public void startProgress() {
		// TODO Auto-generated method stub

	}



	@Override
	public void stopProgress() {
		// TODO Auto-generated method stub

	}




}
