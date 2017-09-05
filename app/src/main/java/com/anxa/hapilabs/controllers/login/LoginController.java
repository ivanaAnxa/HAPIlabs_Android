package com.anxa.hapilabs.controllers.login;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;


public class LoginController{
	
	Context context;
	LoginImplementer loginImpl;
	
	protected ProgressChangeListener progresslistener;

	LoginListener loginlistener;
	public LoginController(Context context,ProgressChangeListener progresslistener,LoginListener loginlistener) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.loginlistener =loginlistener;
	}

	public void startLogin(String username, String password, Boolean fbConnect){
		if (username != null && password != null && username.length() > 1 && password.length() > 1){
			loginImpl = new LoginImplementer(context, username, password, progresslistener, loginlistener, fbConnect);
		}
	}
}
