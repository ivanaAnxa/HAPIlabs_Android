package com.anxa.hapilabs.ui.adapters;

import java.util.List;

import com.hapilabs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AboutViewLinksAdapter extends ArrayAdapter<String> implements OnClickListener{
	Context context;

	List<String> items;
	LayoutInflater layoutInflater;
	
	String inflater = Context.LAYOUT_INFLATER_SERVICE;

	TextView name_tv;
	TextView value_tv;
	List<String> actions;
	OnClickListener listener;
	public AboutViewLinksAdapter(Context context, 
			List<String> items, 
			List<String> actions, 
			
			OnClickListener listener) {
			super(context, R.layout.list_item_generic, items);
			layoutInflater = (LayoutInflater)context.getSystemService( inflater );
			this.context = context;
			this.items= items;
			this.actions = actions;
			this.listener = listener;
			
	
	}


	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View row = convertView;
	  	 if (row == null) {
		    row = LayoutInflater.from(getContext()).inflate(R.layout.list_item_generic, parent,false);
		 }
	 	   
	  	
	     name_tv  = ((TextView)row.findViewById(R.id.text_label));
	     name_tv.setText(items.get(position));
	     
	     if (position >= getCount())
	 		 ((View)row.findViewById(R.id.separator)).setVisibility(View.GONE);
	 
	 	 row.setOnClickListener(this);
	 	if(actions.get(position).equals("LOGOUT")){
					 
	 		//remove arrow
	 	   ((ImageView)row.findViewById(R.id.imageview)).setVisibility(View.INVISIBLE);
		 	       
	 		 
	 	 } 
	 	row.setTag(R.id.weblinksurl,actions.get(position)); //urls
	 	
	 	 row.setTag(R.id.weblinkslabel,items.get(position));//labels
		
	 	 
	 	 return row;
	}
	
	


	
	@Override
	public int getCount() {
		return items.size();// more than zero
	}
	

	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}
}