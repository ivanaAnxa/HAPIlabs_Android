package com.anxa.hapilabs.ui.adapters;


import java.util.List;

import com.hapilabs.R;
import com.anxa.hapilabs.models.Coach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CoachViewAdapter extends ArrayAdapter<Coach> implements OnClickListener{
	Boolean isEnglish = true; //default is english
	Context context;
	List<Coach> items;
	LayoutInflater layoutInflater;
	String inflater = Context.LAYOUT_INFLATER_SERVICE;

	int selectedIndex = 0;
	Coach  selectedCoach  = null;
	OnClickListener listener;
	
	public CoachViewAdapter(Context context, 
			List<Coach> items, 
			OnClickListener listener) {
		
			super(context, R.layout.coachitem, items);
			
			layoutInflater = (LayoutInflater)context.getSystemService( inflater );
			this.listener = listener;
			this.context = context;
			
			this.items = items;

			
	
	}
	
	public void setLanguage(Boolean isEnglish){
		this.isEnglish = isEnglish;
		
	}

public void update(List<Coach> items){
	this.items = items;
	notifyDataSetChanged();
}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		 View row = convertView;
	  	 if (row == null) {
		    row = LayoutInflater.from(getContext()).inflate(R.layout.coachitem, parent,false);
		 }
	 	   
			Coach coach = (Coach)items.get(position);
			 RelativeLayout container = (RelativeLayout)row.findViewById(R.id.profilecontainer);
			 container.setOnClickListener(listener);
			 
			 container.setTag(R.id.coachselect_coachindex,(Integer)position);
			 
			 container.setTag(R.id.coachselect_coachid,coach.coach_id);	
			  
			//language independent
	  	 ImageView avatar = (ImageView)row.findViewById(R.id.avatar);
	  	 
	  	 if (coach.coach_photo != null && coach.coach_photo.image !=null)
	  	 {
	  		avatar.setImageBitmap(coach.coach_photo.image);
		  	 
	  	 }	 
	  	 TextView name = (TextView)row.findViewById(R.id.name);
	  	
	  	 if (coach.firstname!=null && coach.lastname!=null)
	  		 name.setText(coach.firstname+" "+coach.lastname);
	  	  
	  	 //language dependent
	  	 TextView title = (TextView)row.findViewById(R.id.subtitle);
	  	
	  	 if (position != selectedIndex){
	  		((RelativeLayout)row.findViewById(R.id.subitem)).setVisibility(View.GONE);
	  	 }else {
	  		((RelativeLayout)row.findViewById(R.id.subitem)).setVisibility(View.VISIBLE);
	  		 
	  	 }
	               
	  	 TextView profile = (TextView)row.findViewById(R.id.profile);
	  	
	  	  	 if (isEnglish){
	  		 if (coach.coach_title_en!=null)
	  			 	title.setText(coach.coach_title_en);
	  		 
	  		if (coach.coach_style_en!=null)
	  			profile.setText(coach.coach_style_en+"\n\n"+coach.coach_profile_en);
	  		 
	  	 }else {
	  		if (coach.coach_title_fr!=null)
		  		
	  		 title.setText(coach.coach_title_fr);
	  		if (coach.coach_style_fr!=null)
		  		 profile.setText(coach.coach_style_fr+"\n\n" +coach.coach_profile_fr);
	  	 }
		return row;
	}

	@Override
	public int getCount() {
		return items.size();// more than zero
	}
	

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.profilecontainer){
			int indx = (Integer) v.getTag(R.id.coachselect_coachindex);
			selectedIndex = indx;
			notifyDataSetChanged();
		}
	}
}