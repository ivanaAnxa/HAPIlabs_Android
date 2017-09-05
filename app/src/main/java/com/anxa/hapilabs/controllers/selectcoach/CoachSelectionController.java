package com.anxa.hapilabs.controllers.selectcoach;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetCoachListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;


public class CoachSelectionController{

	
	Context context;
	CoachSelectionImplementer coachImpl;
	
	protected ProgressChangeListener progresslistener;

	GetCoachListener listener;
	
	public CoachSelectionController(Context context,ProgressChangeListener progresslistener,GetCoachListener listener) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.listener =listener;
	}
	
	

	public void getCoach(String username){
		coachImpl = new CoachSelectionImplementer(context,username,progresslistener,listener);
		
	}








	
}
