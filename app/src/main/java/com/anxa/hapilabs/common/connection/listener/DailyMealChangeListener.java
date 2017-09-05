package com.anxa.hapilabs.common.connection.listener;

import java.util.List;

import com.anxa.hapilabs.models.Meal;

 public interface DailyMealChangeListener {

		public boolean refreshUI();
		public boolean updateData(List<Meal> items);
		
}