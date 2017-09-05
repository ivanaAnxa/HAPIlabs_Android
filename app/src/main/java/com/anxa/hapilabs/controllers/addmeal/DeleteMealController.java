package com.anxa.hapilabs.controllers.addmeal;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealDeleteListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Meal;


public class DeleteMealController{
	
	Context context;
	DeleteMealImplementer deleteMealImplementer;
	
	protected ProgressChangeListener progressListener;

	MealDeleteListener mealDeleteListener;
	byte mealCommand;
	
	public DeleteMealController(Context context, ProgressChangeListener progressListener, MealDeleteListener mealDeleteListener ,byte mealCommand ) {
		this.context = context;
		this.progressListener = progressListener;
		this.mealDeleteListener = mealDeleteListener;
		this.mealCommand = mealCommand;
	}
	

	public void deleteMeal(Meal meal,String username){
		deleteMealImplementer = new DeleteMealImplementer(context, username, meal, progressListener, mealDeleteListener ,mealCommand);
	}








	
}
