package com.anxa.hapilabs.controllers.addmeal;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealISHapiCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Comment;


public class AddISHapiCommentController{

	
	Context context;
	
	AddISHapiCommentImplementer isHapiImpl;
	
	protected ProgressChangeListener progresslistener;

	MealISHapiCommentListener listener;
	
	public AddISHapiCommentController(Context context,ProgressChangeListener progresslistener,MealISHapiCommentListener listener  ) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.listener =listener;
	}
	
	

	public void uploadMealiSHapiComment(String mealid,Comment comment,String username){
		isHapiImpl = new AddISHapiCommentImplementer(context,username,mealid,comment,progresslistener,listener);
	}








	
}
