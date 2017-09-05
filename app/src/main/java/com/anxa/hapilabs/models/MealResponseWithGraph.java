package com.anxa.hapilabs.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MealResponseWithGraph {
	
//	//comment message
//	public String message;
//
//	//comment comment_id
//	public String comment_id;
//
//	public Date timestamp;
//
//	
//	public String avatar_url;
//
//	public STATUS status;
//	  //message types
//	  //2 = coach comment
//	 	//3 = user comment
//	
//	public int comment_type;
//
//	
//	public static enum STATUS{
//		ONGOING_COMMENTUPLOAD,
//		SYNC_COMMENT,
//		FAILED_COMMENTUPLOAD
//	}
	public List<Meal> mealList = new ArrayList<Meal>();
	public List<GraphMeal> graphList = new ArrayList<GraphMeal>();
public int mealTotal = 0;
	public MealResponseWithGraph()
	{
		
	}
	
	


	
	
}
