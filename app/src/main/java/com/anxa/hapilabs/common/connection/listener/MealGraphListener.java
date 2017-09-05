package com.anxa.hapilabs.common.connection.listener;

import java.util.List;

import com.anxa.hapilabs.models.GraphMeal;

public interface MealGraphListener {

		public void mealGraphSuccess(List<GraphMeal> response,int total);
		

}