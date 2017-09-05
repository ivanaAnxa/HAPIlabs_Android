package com.anxa.hapilabs.controllers.addmeal;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.MealGraphListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Meal;


public class AddMealController{

	
	Context context;
	AddMealImplementer addMealImpl;
	
	protected ProgressChangeListener progresslistener;

	MealAddListener mealAddlistener;
	MealGraphListener mealGraphListener;
	byte mealCommand;
	
	public AddMealController(Context context,
			ProgressChangeListener progresslistener,
			MealGraphListener mealGraphListener,
			MealAddListener mealAddlistener,byte mealCommand  ) {
		this.context = context;
		this.progresslistener = progresslistener;
		this.mealGraphListener = mealGraphListener;
		this.mealAddlistener = mealAddlistener;
		this.mealCommand = mealCommand;
	}
	
	

	public void uploadMeal(Meal meal,String username){
		
		addMealImpl = new AddMealImplementer(context,username,meal,progresslistener,mealAddlistener,mealGraphListener,mealCommand);
		
	}








	
}
