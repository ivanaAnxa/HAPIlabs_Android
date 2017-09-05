package com.anxa.hapilabs.activities;

import com.hapilabs.R;
import com.anxa.hapilabs.common.notification.local.NotificationManager;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MealReminderActivity extends HAPIActivity implements
		OnClickListener, OnTimeSetListener, OnCheckedChangeListener {

	TextView breakfastTime;
	TextView lunchTime;
	TextView dinnerTime;
	
	ToggleButton breakfastToggle ;
	ToggleButton lunchToggle ;
	ToggleButton dinnerToggle ;
	
	

	TimePickerDialog dialog;
	int hour;
	int minutes;
	int timePickerType;

	boolean[] settingsBoolean  = new boolean[3];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.localnotif_settings);

		breakfastToggle = (ToggleButton)findViewById(R.id.breakfastnotif);
		lunchToggle = (ToggleButton)findViewById(R.id.lunchnotif);
		dinnerToggle = (ToggleButton)findViewById(R.id.dinnernotif);
		
		breakfastToggle.setOnCheckedChangeListener(this);
		lunchToggle.setOnCheckedChangeListener(this);
		dinnerToggle.setOnCheckedChangeListener(this);
		
		
		
		breakfastTime = (TextView) findViewById(R.id.breakfasttime);
		lunchTime = (TextView) findViewById(R.id.lunchtime);
		dinnerTime = (TextView) findViewById(R.id.dinnertime);

		
		breakfastTime.setText(getTimerReminder(this, 0));
		lunchTime.setText(getTimerReminder(this, 1));
		dinnerTime.setText(getTimerReminder(this, 2));

		breakfastTime.setOnClickListener(this);
		lunchTime.setOnClickListener(this);
		dinnerTime.setOnClickListener(this);

		
		
		updateHeader(6, getString(R.string.HEADER_MEALREMINDER), this);
		
		settingsBoolean[0] = getOnOFFSettings(0, this);
		settingsBoolean[1] = getOnOFFSettings(1, this);
		settingsBoolean[2] = getOnOFFSettings(2, this);
										
		breakfastToggle.setChecked(getOnOFFSettings(0,this));
		lunchToggle.setChecked(getOnOFFSettings(1,this));
		dinnerToggle.setChecked(getOnOFFSettings(2,this));
	
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
			onBackPressed();
		} else if (v == breakfastTime) {
			hour = Integer.parseInt(breakfastTime.getText().toString().substring(0, 2).trim());
			minutes = Integer.parseInt(breakfastTime.getText().toString().substring(3, 5).trim());
			timePickerType = 0; //breakfast;
			dialog = new TimePickerDialog(this, this, hour, minutes, true);
			dialog.show();

		} else if (v == lunchTime) {
			hour = Integer.parseInt(lunchTime.getText().toString().substring(0, 2).trim());
			minutes = Integer.parseInt(lunchTime.getText().toString().substring(3, 5).trim());
			timePickerType = 1; //lunchTime;
			dialog = new TimePickerDialog(this, this, hour, minutes, true);
			dialog.show();

		} else if (v == dinnerTime) {
			hour = Integer.parseInt(dinnerTime.getText().toString().substring(0, 2).trim());
			minutes = Integer.parseInt(dinnerTime.getText().toString().substring(3, 5).trim());
			timePickerType = 2; //dinnerTime;
			dialog = new TimePickerDialog(this, this, hour, minutes, true);
			dialog.show();
		}
	}

	private SharedPreferences getPreferenceName(Context context) {
		return getSharedPreferences(MealReminderActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
	
private boolean getOnOFFSettings(int type,Context context){
		
	
		final SharedPreferences prefs = getPreferenceName(context);
		
		if (type == 0 ){
			String settings = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_BREAKFAST, "TRUE");
			
			if (settings == "TRUE"){
				
				return true;
			}else{
				return false;
			}
		}else if (type ==1){
			String settings = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_LUNCH, "TRUE");
			
if (settings == "TRUE"){
				return true;
			}else{
				return false;
			}
		}else if (type ==2){ 
			String settings = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_DINNER, "TRUE");
			
if (settings == "TRUE"){
				return true;
			}else{
				return false;
			}
		}
	 return true;
	 
		
	}
	
	private void setTimerReminder(Context context, int type) {

		final SharedPreferences prefs = getPreferenceName(context);
		SharedPreferences.Editor editor = prefs.edit();
		
		
		switch (type) {
		case 0:// breakfast
			try {
				editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_BREAKFAST, breakfastTime.getText().toString());
				
			} catch (Exception e2) {
			}
			break;
		case 1:// lunch
			try {
		
				editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_LUNCH, lunchTime.getText().toString());
					
			} catch (Exception e3) {
				// TODO: handle exception
			}
			break;
		default: // dinner
			try {
				editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_DINNER, dinnerTime.getText().toString());
				
			} catch (Exception e4) {
				// TODO: handle exception
			}
			break;
		}
		editor.commit();

	
	}
	private String getTimerReminder(Context context, int type) {

		final SharedPreferences prefs = getPreferenceName(context);
		String sharedTime = null;

		switch (type) {
		case 0:// breakfast
			try {
				sharedTime = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_BREAKFAST, "08:00");
			} catch (Exception e2) {
			}
			if (sharedTime == null)
				sharedTime = "08:00";
			break;
		case 1:// lunch
			try {
				sharedTime = prefs.getString(
						ApplicationEx.PROPERTY_MEAL_REMINDER_LUNCH, "13:00");
			} catch (Exception e3) {
				// TODO: handle exception
			}
			if (sharedTime == null)
				sharedTime = "13:00";
			break;
		default: // dinner
			try {
				sharedTime = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_DINNER, "20:00");
			} catch (Exception e4) {
				// TODO: handle exception
			}
			if (sharedTime == null)
				sharedTime = "20:00";
			break;
		}

		return sharedTime;
	}

	private void updateLocalNotif(){
		new NotificationManager(this);
	}
	
	private void turnOnOffLocalNotif(Boolean settings,int type){
		settingsBoolean[type] = settings;
		new NotificationManager(this,settings,type ,settingsBoolean);
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		
		hour = hourOfDay;
		
		minutes = minute;
		
		switch (timePickerType) {
		case 0:
			breakfastTime.setText((hourOfDay<10?"0"+hourOfDay:hourOfDay)+":"+(minutes<10?"0"+minutes:minutes));
			break;
		case 1:
			lunchTime.setText((hourOfDay<10?"0"+hourOfDay:hourOfDay)+":"+(minutes<10?"0"+minutes:minutes));
			break;
		default:
			dinnerTime.setText((hourOfDay<10?"0"+hourOfDay:hourOfDay)+":"+(minutes<10?"0"+minutes:minutes));
			
			break;
		}
		
		setTimerReminder(this, timePickerType);
		updateLocalNotif();
		
	}

	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (buttonView == breakfastToggle){
			if (buttonView.isChecked())
				turnOnOffLocalNotif(true, 0);
			else 
				turnOnOffLocalNotif(false, 0);
		}else if (buttonView == lunchToggle){
			if (buttonView.isChecked())
				turnOnOffLocalNotif(true, 1);
			else 
				turnOnOffLocalNotif(false, 1);
		}else if (buttonView == dinnerToggle){
			if (buttonView.isChecked())
				turnOnOffLocalNotif(true, 2);
			else 
				turnOnOffLocalNotif(false, 2);
		} 
		
		
		
	}
}
