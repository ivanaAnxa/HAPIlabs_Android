package com.anxa.hapilabs.controllers.forgotpass;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ForgotPasswordListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;


public class ForgotPassController{

	
	Context context;
	
	ForgotPassImplementer forgotPassImpl;
	
	protected ProgressChangeListener progresslistener;

	ForgotPasswordListener forgotPasswordListener;
	public ForgotPassController(Context context,ProgressChangeListener progresslistener, ForgotPasswordListener listener) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.forgotPasswordListener =listener;
	}
	
	

	public void forgotPasswordStart(String username, String password){
		forgotPassImpl = new ForgotPassImplementer(context,username, password,progresslistener,forgotPasswordListener);
		
	}








	
}
