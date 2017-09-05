package com.anxa.hapilabs.controllers.addmeal;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Meal;


public class AddMealHapiController{

	
	Context context;
	AddMealHapiImplementer addMealImpl;
	
	protected ProgressChangeListener progresslistener;

	MealAddListener mealAddlistener;
	
	public AddMealHapiController(Context context,ProgressChangeListener progresslistener,MealAddListener mealAddlistener  ) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.mealAddlistener =mealAddlistener;
	}
	
	

	public void uploadCommentHapi(Meal meal,String username){
		addMealImpl = new AddMealHapiImplementer(context,username,meal,progresslistener,mealAddlistener);
		
	}








	
}
