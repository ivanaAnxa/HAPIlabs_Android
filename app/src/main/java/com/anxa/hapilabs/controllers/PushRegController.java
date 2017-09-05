package com.anxa.hapilabs.controllers;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PushRegListener;
import com.anxa.hapilabs.controllers.login.PushRegImplementer;
import com.anxa.hapilabs.models.UserProfile;


public class PushRegController{

	
	Context context;
	PushRegImplementer impl;
	

	PushRegListener listener;
	
	public PushRegController(Context context,PushRegListener listener) {
		this.context = context;
		this.listener =listener;
	}
	
	

	public void startPushReg(String senderID, String deviceToken,String appVersion,String sharedKey,UserProfile userprofile){
		
//		System.out.println("STEP 5: senderID: "+senderID);
//		System.out.println("STEP 5: deviceToken: "+deviceToken);
//		System.out.println("STEP 5: appVersion: "+appVersion);
//		System.out.println("STEP 5: sharedKey: "+sharedKey);
//		
		impl = new PushRegImplementer(context,appVersion,sharedKey,senderID, deviceToken,listener,userprofile);
		
	}








	
}
