package com.anxa.hapilabs.ui.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.DateHeaderEventListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.Meal.STATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MealListAdapter extends ArrayAdapter<Meal> implements  DateHeaderEventListener{
	Context context;

	List<Meal> items;
	OnClickListener listener;
	LayoutInflater layoutInflater;
	String inflater = Context.LAYOUT_INFLATER_SERVICE;


	
	public MealListAdapter(Context context, 
			List<Meal> tempList, 
			OnClickListener listener) {
			super(context, R.layout.mymeals_itemcell, tempList);
			layoutInflater = (LayoutInflater)context.getSystemService( inflater );
			items = tempList;
this.context = context;
this.listener = listener;
	
	}
	
	static class ViewHolder {  //use a viewholder to avoid re initializing stuff
		
		TextView title_tv;
		TextView mealtime_tv;
		ImageView photomain_iv;
		ImageView photocount_iv;
		
		LinearLayout iconContainer;
		
		TextView approved_tv;
		ImageView approved_iv;
		TextView commented_tv;
		ImageView  commented_iv;
		
		TextView mealdesc_tv;
	}


	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
	  	 if (row == null) {
		    row = LayoutInflater.from(getContext()).inflate(R.layout.mymeals_itemcell, parent,false);
	  	 }
	 	
	  	Meal currentMeal =  items.get(position);
	 
	  	
	 	if (currentMeal.state == STATE.EMPTY)
	 	{
	 		return row;
	 		
	 	}
	    
	 	((TextView)row.findViewById(R.id.mealtitle)).setText(getMealTitle(currentMeal.meal_type));
	 	((TextView)row.findViewById(R.id.mealtime)).setVisibility(View.VISIBLE);
		((TextView)row.findViewById(R.id.mealtime)).setText(getMealTime(currentMeal.timestamp));
		 
	 	ImageView photoMain = ((ImageView)row.findViewById(R.id.mealphoto));
 		photoMain.setOnClickListener(listener);
//	 	ImageView photoCount = ((ImageView)row.findViewById(R.id.mealphotoCount));
	 
	 	if (currentMeal.photos != null  && currentMeal.photos.size() >0 ) {
	 		photoMain.setTag(position);
	 		if (currentMeal.photos.size() > 0 )
	 			updatePhotoMain(currentMeal.photos.get(0), photoMain,currentMeal.meal_type);
	 		
//	 		photoCount = updatePhotoCount(currentMeal.photos.size(), photoCount);
	 
	 	}else {
	 		updatePhotoMain(null, photoMain,currentMeal.meal_type);
//	 		photoCount = updatePhotoCount(0, photoCount);
	 	} 
	 	
	 	//approved & commented
		
	 	
	 	if (!currentMeal.isApproved){
			 ((TextView)row.findViewById(R.id.approved_text)).setVisibility(View.GONE);
			 ((ImageView)row.findViewById(R.id.approved_icon)).setVisibility(View.GONE);
			 //remove spacer too
			 ((View)row.findViewById(R.id.spacer)).setVisibility(View.GONE);
				
	 	}else {
	 		 ((TextView)row.findViewById(R.id.approved_text)).setVisibility(View.VISIBLE);
			 ((ImageView)row.findViewById(R.id.approved_icon)).setVisibility(View.VISIBLE);
			 //add spacer too
			 ((View)row.findViewById(R.id.spacer)).setVisibility(View.VISIBLE);
			
	 		
	 	}
		
		if (!currentMeal.isCommented){
			((TextView)row.findViewById(R.id.comment_text)).setVisibility(View.GONE);
			((ImageView)row.findViewById(R.id.comment_icon)).setVisibility(View.GONE);
		}else{
			((TextView)row.findViewById(R.id.comment_text)).setVisibility(View.VISIBLE);
			((ImageView)row.findViewById(R.id.comment_icon)).setVisibility(View.VISIBLE);
			
			
		}
		
		
	
		//text Description
		if (currentMeal.meal_description!=null && currentMeal.meal_description.length() >0){
			((TextView)row.findViewById(R.id.mealdesc)).setVisibility(View.VISIBLE);
		
		 ((TextView)row.findViewById(R.id.mealdesc)).setText(currentMeal.meal_description);
			
		}else
			((TextView)row.findViewById(R.id.mealdesc)).setVisibility(View.GONE);
			
		 
	 	//hide separator on the lastItem
		if (position == items.size()-1){
			((View)row.findViewById(R.id.separator)).setVisibility(View.GONE);
			
		}
		
		return row;
	}
	
	
	public String getMealTime(Date mealDate){
		
		//get time only for the dte format is HH:DD 
		Calendar c = Calendar.getInstance();
		c.setTime(mealDate);
		return AppUtil.getTimeOnly24(c.getTimeInMillis());
		
		
	}

	public String getMealTitle(MEAL_TYPE mealtype){
		
		switch (mealtype) {
		case BREAKFAST:
			return context.getResources().getString(R.string.MEALTYPE_BREAKFAST);
		case AM_SNACK:
			return context.getResources().getString(R.string.MEALTYPE_MORNINGSNACK);
		case LUNCH:
			return context.getResources().getString(R.string.MEALTYPE_LUNCH);
		case PM_SNACK:
			return context.getResources().getString(R.string.MEALTYPE_AFTERNOONSNACK);
		default:
			return context.getResources().getString(R.string.MEALTYPE_DINNER);
		}
	}
	public ImageView updatePhotoMain(Photo photo,ImageView item, MEAL_TYPE type){
		if (photo==null){
			//depending on type image dummy meal color chnanges
			switch (type) {
			case BREAKFAST:
				item.setImageResource(R.drawable.meal_default_breakfast);
				break;
			case AM_SNACK:
				item.setImageResource(R.drawable.meal_default_amsnack);
				break;
			case LUNCH:
				item.setImageResource(R.drawable.meal_default_lunch);
				break;
			case PM_SNACK:
				item.setImageResource(R.drawable.meal_default_pmsnack);
				break;

			default:
				item.setImageResource(R.drawable.meal_default_dinner);
				break;
			}
			
		}else {
			item.setImageBitmap(photo.image);
			
			
		}
		return item;
		
	}
	public ImageView updatePhotoCount(int count,ImageView item){
		
		//photo is more than one add a multiphoto count
		item.setVisibility(View.VISIBLE);
	 	
		if (count == 2 ) item.setImageResource(R.drawable.photo_count_bg_2);
	 	
		if (count == 3 ) item.setImageResource(R.drawable.photo_count_bg_3);
	 	
		if (count == 4 ) item.setImageResource(R.drawable.photo_count_bg_4);
	 	
		if (count == 5 ) item.setImageResource(R.drawable.photo_count_bg_5);
		
		else  item.setVisibility(View.GONE);
		
		return item;
	
	}
	
	@Override
	public int getCount() {
		return items.size();// more than zero
	}
	

	
	@Override
	public void dateSelectedChange(int weekIndex) {
		// TODO Auto-generated method stub
		
	}
}