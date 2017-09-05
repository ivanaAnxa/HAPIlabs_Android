package com.anxa.hapilabs.common.storage;


import java.sql.Timestamp;
import java.util.ArrayList;

import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class NotificationDAO extends DAO{
        

	
		protected static final String createSQL = "CREATE TABLE IF NOT EXISTS NotificationDetails "+
        		"(notificationID INTEGER PRIMARY KEY  NOT NULL , "
        	    + " notificationType INTEGER," 
        	    + " notificationState INTEGER," 
        		+ " ref_id TEXT,"
        		+ " ref_type TEXT,"
            	+ " coach_id TEXT,"
            	+ " coach_avatar_url TEXT,"
        		+ " meal_id TEXT,"
        	    + " timestamp TEXT,"
            	+ " message TEXT);";
        
		
		
        public NotificationDAO(Context appCtx, DatabaseHelper dbHelper) {
                super(appCtx, "NotificationDetails", dbHelper);
        }
        /**
         * Create the table
         */
        public void createTable() {
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.execSQL(createSQL);
                } catch( Exception ex ) {
                }
        }

        public void delete(int notificationID){
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, "notificationID = ?", new String[] {String.valueOf(notificationID)});
                }
                        catch( Exception ex ) {
                        }
                
        }
        
    	
        
       public boolean insert(Notification notification) {
                boolean status=true;
                try {
                	
                        ContentValues values = new ContentValues();
                        
                        values.put("notificationID", notification.notificationID);
                        
                        try{
                        	values.put("notificationType", notification.notificationType.getValue());
                        }catch(Exception e){}
                        
                       
                        try{
                        	values.put("notificationState", notification.notificationState.getValue());
                		}catch(Exception e){}
                
                        try{
                        	values.put("ref_id",notification.ref_id);
                        }catch(Exception e){}
                
                        try{
                        	values.put("ref_type", notification.ref_type);
                		}catch(Exception e){}
                
                        try{
                        	values.put("coach_id", notification.coachID);
       					}catch(Exception e){}
       					
                        try{
                        	values.put("coach_avatar_url", notification.coachAvatarURL);
                        }catch(Exception e){}
       					
                        
                        try{
                        	values.put("meal_id", notification.mealID);
                        }catch(Exception e){}
       					
                        try{
                        	values.put("timestamp", String.valueOf(notification.timestamp.getTime()));
                        }catch(Exception e){}
       						 
                        try{
                        	values.put("message",notification.coachMessage);
                        }catch(Exception e){}
       					
                        
                        status = this.insertTable(values, "notificationID=?", new String[] {String.valueOf(notification.notificationID)});
                        
                        
                } catch( Exception ex ) {
                        status=false;
                }finally{
                }
                
                return status;
        }
        
       public boolean update(Notification notification) {
           boolean status=true;
           SQLiteDatabase db = null;
           try {
                   db = this.getDatabase();
                   
                   
                   ContentValues values = new ContentValues();
                   values.put("notificationID", notification.notificationID);
                   
                   try{
                   	values.put("notificationType", notification.notificationType.getValue());
                   }catch(Exception e){}
                   
                  
                   try{
                   	values.put("notificationState", notification.notificationState.getValue());
           		}catch(Exception e){}
           
                   try{
                   	values.put("ref_id",notification.ref_id);
                   }catch(Exception e){}
           
                   try{
                   	values.put("ref_type", notification.ref_type);
           		}catch(Exception e){}
           
                   try{
                   	values.put("coach_id", notification.coachID);
  					}catch(Exception e){}
  					
                   try{
                   	values.put("coach_avatar_url", notification.coachAvatarURL);
                   }catch(Exception e){}
  					
                   
                   try{
                   	values.put("meal_id", notification.mealID);
                   }catch(Exception e){}
  					
                   try{
                   	values.put("timestamp", String.valueOf(notification.timestamp.getTime()));
                   }catch(Exception e){}
  						 
                   try{
                   	values.put("message",notification.coachMessage);
                   }catch(Exception e){}
                  
                   db.update(this.tableName, values, "notificationID = ?", new String [] {String.valueOf(notification.notificationID)} );
           } catch( Exception ex ) {
                   status=false;
           }finally{
           }
           
           return status;
   }
       
       //get all photos regardless of meal ID
    	public ArrayList<Notification> getAllNotifications(){

    		ArrayList<Notification> newListofEntries = new ArrayList<Notification>();
    		
    		Cursor cur = this.getAllEntriesNotification();
    		
    		if (this.cursorHasRows(cur)) {
    			if (cur.moveToFirst()) {
    				do {
    					Notification newObjActivity = new Notification();
    				
    						newObjActivity.notificationID = (cur.getInt(cur.getColumnIndex("notificationID")));
    						newObjActivity.notificationType = Notification.getNOTIFTYPEValue(cur.getInt(cur.getColumnIndex("notificationType")));
    						newObjActivity.notificationState = Notification.getNOTIFSTATEValue(cur.getInt(cur.getColumnIndex("notificationState")));
    						newObjActivity.ref_id = (cur.getString(cur.getColumnIndex("ref_id")));
    						newObjActivity.ref_type = (cur.getString(cur.getColumnIndex("ref_type")));
    						newObjActivity.coachID = (cur.getInt(cur.getColumnIndex("coach_id")));
    						newObjActivity.coachAvatarURL = (cur.getString(cur.getColumnIndex("coach_avatar_url")));
    						newObjActivity.mealID = (cur.getString(cur.getColumnIndex("meal_id")));
    						newObjActivity.timestamp = AppUtil.toDate(new Timestamp(cur.getLong(cur.getColumnIndex("timestamp"))));
    						newObjActivity.coachMessage = (cur.getString(cur.getColumnIndex("message")));
        					
    						//add the meal to the array list
    						newListofEntries.add(newObjActivity);
    						
    						
    						
    				} while (cur.moveToNext());
    			}
    		}
    		
    		cur.close();
    		return newListofEntries;
    	}//get all photos regardless of meal ID
    	
    	public ArrayList<Notification> getAllNotificationsWithMealID(String mealID){

    		ArrayList<Notification> newListofEntries = new ArrayList<Notification>();
    		
    		Cursor cur = this.getAllEntriesNotificationWithMealID(mealID);
    		
    		if (this.cursorHasRows(cur)) {
    			if (cur.moveToFirst()) {
    				do {
    						Notification newObjActivity = new Notification();
    						newObjActivity.notificationID = (cur.getInt(cur.getColumnIndex("notificationID")));
    						newObjActivity.notificationType = Notification.getNOTIFTYPEValue(cur.getInt(cur.getColumnIndex("notificationType")));
    						newObjActivity.notificationState = Notification.getNOTIFSTATEValue(cur.getInt(cur.getColumnIndex("notificationState")));
    						newObjActivity.ref_id = (cur.getString(cur.getColumnIndex("ref_id")));
    						newObjActivity.ref_type = (cur.getString(cur.getColumnIndex("ref_type")));
    						newObjActivity.coachID = (cur.getInt(cur.getColumnIndex("coach_id")));
    						newObjActivity.coachAvatarURL = (cur.getString(cur.getColumnIndex("coach_avatar_url")));
        					newObjActivity.mealID = (cur.getString(cur.getColumnIndex("meal_id")));
    						newObjActivity.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));
    						newObjActivity.coachMessage = (cur.getString(cur.getColumnIndex("message")));
        					//add the meal to the array list
    						newListofEntries.add(newObjActivity);
    						
    						
    				} while (cur.moveToNext());
    			}
    		}
    		
    		cur.close();
    		return newListofEntries;
    	}//get all photos regardless of meal ID\
    	
    	public ArrayList<Notification> getAllNotificationsWithNotifID(String NotifID){

    		ArrayList<Notification> newListofEntries = new ArrayList<Notification>();
    		
    		Cursor cur = this.getAllEntriesNotificationWithNotifID(NotifID);
    		
    		if (this.cursorHasRows(cur)) {
    			if (cur.moveToFirst()) {
    				do {
    						Notification newObjActivity = new Notification();
    						newObjActivity.notificationID = (cur.getInt(cur.getColumnIndex("notificationID")));
    						newObjActivity.notificationType = Notification.getNOTIFTYPEValue(cur.getInt(cur.getColumnIndex("notificationType")));
    						newObjActivity.notificationState = Notification.getNOTIFSTATEValue(cur.getInt(cur.getColumnIndex("notificationState")));
    						newObjActivity.ref_id = (cur.getString(cur.getColumnIndex("ref_id")));
    						newObjActivity.ref_type = (cur.getString(cur.getColumnIndex("ref_type")));
    						newObjActivity.coachID = (cur.getInt(cur.getColumnIndex("coach_id")));
    						newObjActivity.coachAvatarURL = (cur.getString(cur.getColumnIndex("coach_avatar_url")));
        					newObjActivity.mealID = (cur.getString(cur.getColumnIndex("meal_id")));
    						newObjActivity.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));
    						newObjActivity.coachMessage = (cur.getString(cur.getColumnIndex("message")));
        					//add the meal to the array list
    						newListofEntries.add(newObjActivity);
    						
    						
    				} while (cur.moveToNext());
    			}
    		}
    		
    		cur.close();
    		return newListofEntries;
    	}//get all photos regardless of meal ID
    	
    
    
       
   
}
