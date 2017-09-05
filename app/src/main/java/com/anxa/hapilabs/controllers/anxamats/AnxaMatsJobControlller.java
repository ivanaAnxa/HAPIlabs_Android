package com.anxa.hapilabs.controllers.anxamats;


import java.util.Date;

import com.anxa.hapilabs.activities.MealReminderActivity;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;


public class AnxaMatsJobControlller {

	final int MIN_SESSION_TIME = 8000;
	Handler handler  = new Handler();
	Context context;
	String userID;
	boolean loginSend = false;
	
	  AnxaMatsJobImplementer  implementer;
		//getLastTime
		
		Date lastSession;
		
		Date newSession;
		//saveLastTime

	public AnxaMatsJobControlller(Context context,String userId) {
		this.context = context;
		this.userID = userId;
		
	}
	public AnxaMatsJobControlller() {
		
		
	}
	public void isLogin(Boolean loginSend){
		this.loginSend = loginSend;
		
	}
	private SharedPreferences getPreferenceName(Context context) {
		return context.getSharedPreferences(MealReminderActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
	private Date getLastSession() {
		Date lastTime = new Date();
		final SharedPreferences prefs = getPreferenceName(context);

			try {
				 lastTime = new Date(prefs.getLong(ApplicationEx.PROPERTY_APP_LASTSESSION, System.currentTimeMillis()));
			} catch (Exception e2) {
			}
			
	

		return lastTime;
	}
	
	private void storeLastSession() {
		final SharedPreferences prefs = getPreferenceName(context);
	
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(ApplicationEx.PROPERTY_APP_LASTSESSION, System.currentTimeMillis());
		editor.commit();
	}
	
	public void startSession(Context context){
		this.context = context;
		new sendSessionInBG().execute(userID);
		
	}
	public void closeSession(Context context){
		this.context = context;
		new sendSessionInBG().execute(userID);
		
	}
	
	
	private long getDeltaTime(){
		lastSession = getLastSession();
		newSession = new Date();
		
	return (newSession.getTime() - lastSession.getTime());
	
	
	}
	 
	private Boolean sendSession(){
		
		handler.post(new Runnable() {
            @Override
            public void run() {
                int deltaTime = 0;
        		
        
                //if from init login session ignore the delta, we always log the first login
                if (loginSend){
        			new AnxaMatsJobImplementer(context,userID ,deltaTime) ;
        		}else{
        			
        			deltaTime = (int)getDeltaTime();
        			if (deltaTime > MIN_SESSION_TIME)
        			new AnxaMatsJobImplementer(context,userID ,deltaTime) ;
        		}
            }
        });
		return true;
			
		}
		
	 private class sendSessionInBG extends AsyncTask<String, Void, Boolean> {
	        @Override
	        protected Boolean doInBackground(String... params) {
	                try {
	                    Thread.sleep(1);
	                  return sendSession();
	                  
	                } catch (InterruptedException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            return false;
	        }

	        @Override
	        protected void onPostExecute(Boolean result) {
	        	//update time 
	        	storeLastSession();
	        	
	        }

	        @Override
	        protected void onPreExecute() {
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {
	        }
	 }

}
