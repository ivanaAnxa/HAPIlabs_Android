package com.anxa.hapilabs.ui.adapters;


import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hapilabs.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


public class ProfileViewAdapter extends ArrayAdapter<NameValuePair> implements OnClickListener,android.content.DialogInterface.OnClickListener{
	Context context;

	List<NameValuePair> profileitems;
	LayoutInflater layoutInflater;
	
	String inflater = Context.LAYOUT_INFLATER_SERVICE;

	TextView name_tv;
	TextView value_tv;
	EditText value_et;
	List<Boolean> isEditable;
	
	final static int  DIALOG_DATEPICKER = 1;
	final static int  DIALOG_COUNTRY = 2;
	final static int  DIALOG_GENDER = 3;
	int dialog_flag;
	
	ArrayAdapter<String> stringArr;
	Dialog dialog;
	public ProfileViewAdapter(Context context, 
			List<NameValuePair> profileitems, 
			
			List<Boolean> isEditable, 
			OnClickListener listener) {
			super(context, R.layout.profile_item, profileitems);
			layoutInflater = (LayoutInflater)context.getSystemService( inflater );
			this.context = context;
			this.profileitems= profileitems;
			this.isEditable =isEditable;
	
	}
	
	static class ViewHolder {  //use a viewholder to avoid re initializing stuff
		

		TextView name_tv;
		TextView value_tv;
		EditText value_et;
		
		
	}


	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View row = convertView;
	  	 if (row == null) {
		    row = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent,false);
		 }
	 	   
	 	
	     name_tv  = ((TextView)row.findViewById(R.id.text_label));
	     name_tv.setText(profileitems.get(position).getName());
	     
	     if (isEditable.get(position)){
	    	 value_tv  = ((TextView)row.findViewById(R.id.label_value));
		     value_tv.setText(profileitems.get(position).getValue());
		    value_tv.setVisibility(View.GONE);
		   
		    value_et  = ((EditText)row.findViewById(R.id.text_value));
		     value_et.setText(profileitems.get(position).getValue());
		     value_et.setVisibility(View.VISIBLE);
		   
	     }else {
	    	 value_tv  = ((TextView)row.findViewById(R.id.label_value));
		     value_tv.setText(profileitems.get(position).getValue());
		    value_tv.setVisibility(View.VISIBLE);
		    value_tv.setTag(position);
		    value_tv.setOnClickListener(this);
		    
		    value_et  = ((EditText)row.findViewById(R.id.text_value));
	 	     value_et.setText(profileitems.get(position).getValue());
	 	    value_et.setVisibility(View.GONE);
	 	    
	 	    
			    	 
	     }
	     
	    
	 	 if (position >= getCount()){
	 		 ((View)row.findViewById(R.id.separator)).setVisibility(View.GONE);
	 	 }
		return row;
	}
	
	


	
	@Override
	public int getCount() {
		return profileitems.size();// more than zero
	}
	
	private void showDialog(int tag){
		if (tag == 3 ){
		//show bday
			dialog_flag = DIALOG_DATEPICKER;
		}else {
			stringArr = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
			
		switch (tag) {
		case 2:
			stringArr.add("Female");
			stringArr.add("Male");
			dialog_flag = DIALOG_GENDER;
			break;

		default:

			stringArr.add("Singapore");
			stringArr.add("Philippines");
			stringArr.add("France");

			dialog_flag = DIALOG_COUNTRY;
			
			break;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.icon);
		builder.setAdapter(stringArr, this);
		dialog = builder.create();
		dialog.show();
		
		
		
		}	
	}
	
	private void getData(int which){
		if (dialog_flag ==  DIALOG_DATEPICKER){
			//show bday
		
		}else if (dialog_flag ==  DIALOG_GENDER){
			
			String data = stringArr.getItem(which);
			NameValuePair name = new BasicNameValuePair(profileitems.get(2).getName(),data);
			profileitems.set(2, name);
			notifyDataSetChanged();
			
			
			
		}else if (dialog_flag ==  DIALOG_COUNTRY){
			String data = stringArr.getItem(which);
			NameValuePair name = new BasicNameValuePair(profileitems.get(4).getName(),data);
			profileitems.set(4, name);
			notifyDataSetChanged();
		
		}	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//2 = getnder
		//3 = bday
		//4 = country
		showDialog((Integer)v.getTag());
		
	}





	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		 if(dialog!=null)
			 dialog.dismiss();
		 getData(which);
	}
}