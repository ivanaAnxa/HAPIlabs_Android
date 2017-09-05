package com.anxa.hapilabs.common.storage;

import java.util.ArrayList;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.Photo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class CoachDAO extends DAO{
        
	
		protected static final String createSQL = "CREATE  TABLE IF NOT EXISTS coachDetails "+
        		"(coachID TEXT PRIMARY KEY  NOT NULL , "
        		+ " firstname TEXT,"
        		+ " lastname TEXT,"
        		+ " avatarurl TEXT,"
        		+ " photo BLOB,"
        		+ " coachprofileen TEXT,"
        		+ " coachtitleen TEXT,"
        		+ " coachstyleen TEXT,"
        		+ " bitmap BLOB,"
        		+ " coachprofilefr TEXT,"
        		+ " coachtitlefr TEXT,"
        		+ " coachstylefr TEXT);";
		
        
        public CoachDAO(Context appCtx, DatabaseHelper dbHelper) {
                super(appCtx, "coachDetails", dbHelper);
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

        public void delete(String coachID){
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, "coachID = ?", new String [] {(coachID)} );
                } catch( Exception ex ) {
                }
        }
        
       public boolean insert(Coach coach ) {
                boolean status=true;
              
                try {
                	
                	
                		
                        ContentValues values = new ContentValues();
                        if (coach.coach_id!=null)
                            values.put("coachID", coach.coach_id);
                        
                        if (coach.firstname!=null)
                            values.put("firstname", coach.firstname);
                        
                        if (coach.lastname!=null)
                            values.put("lastname", coach.lastname);
                        
                        if (coach.avatar_url!=null)
                        	values.put("avatarurl", coach.avatar_url);
                        
                        
                        if(coach.coach_photo != null)
                        {
                        	   if (coach.coach_photo.image!=null)
                               	values.put("photo", DBBitmapUtil.getBytes(coach.coach_photo.image));
                        }
                     
                        
                        if (coach.coach_profile_en!=null)
                            values.put("coachprofileen", coach.coach_profile_en);
                        
                        if (coach.coach_title_en!=null)
                            values.put("coachtitleen", coach.coach_title_en);
                        
                        if (coach.coach_style_en!=null)
                            values.put("coachstyleen", coach.coach_style_en);
                        
                        if (coach.coach_profile_fr!=null)
                            values.put("coachprofilefr", coach.coach_profile_fr);
                        
                        if (coach.coach_title_fr!=null)
                            values.put("coachtitlefr", coach.coach_title_fr);
                        
                        if (coach.coach_style_fr!=null)
                             values.put("coachstylefr", coach.coach_style_fr);
                        
                        
                        status = this.insertTable(values, "coachID=?", new String[] {String.valueOf(coach.coach_id)});
                        
                        
                } catch( Exception ex ) {
                        status=false;
                        ex.printStackTrace();
                        
                       
                }finally{
                }
                
                return status;
        }
        
       public boolean update(Coach coach ) {
           boolean status=true;
           SQLiteDatabase db = null;
           try {
                   db = this.getDatabase();
                   
                   ContentValues values = new ContentValues();
                   if (coach.coach_id!=null)
                       values.put("coachID", coach.coach_id);
                   
                   if (coach.firstname!=null)
                       values.put("firstname", coach.firstname);
                   
                   if (coach.lastname!=null)
                       values.put("lastname", coach.lastname);
                   
                   if (coach.avatar_url!=null)
                   	values.put("avatarurl", coach.avatar_url);
                   
                   if (coach.coach_photo.image!=null)
                   	values.put("photo", DBBitmapUtil.getBytes(coach.coach_photo.image));
                   
                   if (coach.coach_profile_en!=null)
                       values.put("coachprofileen", coach.coach_profile_en);
                   
                   if (coach.coach_title_en!=null)
                       values.put("coachtitleen", coach.coach_title_en);
                   
                   if (coach.coach_style_en!=null)
                       values.put("coachstyleen", coach.coach_style_en);
                   
                   if (coach.coach_profile_fr!=null)
                       values.put("coachprofilefr", coach.coach_profile_fr);
                   
                   if (coach.coach_title_fr!=null)
                       values.put("coachtitlefr", coach.coach_title_fr);
                   
                   if (coach.coach_style_fr!=null)
                        values.put("coachstylefr", coach.coach_style_fr);
                    
                   db.update(this.tableName, values, "coachID = ?", new String [] {String.valueOf(coach.coach_id)} );
           } catch( Exception ex ) {
                   status=false;
           }finally{
           }
           
           return status;
   }
       
       public ArrayList<Coach> getAllCoachs(){

   		ArrayList<Coach> newListofEntries = new ArrayList<Coach>();
   		
try{
   		Cursor cur = this.getAllEntriesCoach();
   		
   		if (this.cursorHasRows(cur)) {
   			if (cur.moveToFirst()) {
   				do {
   						Coach newObjActivity = new Coach();
   						
   						newObjActivity.coach_id = (cur.getString(cur.getColumnIndex("coachID")));
   						newObjActivity.firstname = (cur.getString(cur.getColumnIndex("firstname")));
   						newObjActivity.lastname = (cur.getString(cur.getColumnIndex("lastname")));
   						newObjActivity.avatar_url = (cur.getString(cur.getColumnIndex("avatarurl")));
   						newObjActivity.coach_title_en = (cur.getString(cur.getColumnIndex("coachtitleen")));
   						newObjActivity.coach_profile_en = (cur.getString(cur.getColumnIndex("coachprofileen")));
   						newObjActivity.coach_style_en = (cur.getString(cur.getColumnIndex("coachstyleen")));
   						newObjActivity.coach_title_fr = (cur.getString(cur.getColumnIndex("coachtitlefr")));
   						newObjActivity.coach_profile_fr = (cur.getString(cur.getColumnIndex("coachprofilefr")));
   						newObjActivity.coach_style_fr = (cur.getString(cur.getColumnIndex("coachstylefr")));
   						
   						Photo photo =  new Photo();
   						
   						try{
   							photo.photo_id = newObjActivity.coach_id;
   							photo.photo_url_large = newObjActivity.avatar_url;
   						try{
   							
   							photo.image = DBBitmapUtil.getImage(cur.getBlob(cur.getColumnIndex("photo")));
   						
   						}catch(Exception e){
   							
   						}
   						}catch(Exception ee){
   							
   						}
   						
   					 	newObjActivity.coach_photo = photo;
   					 	
   						//add the meal to the array list
   						newListofEntries.add(newObjActivity);
   						
   						
   				} while (cur.moveToNext());
   			}
   		}
   		
   		cur.close();
   		
} catch( Exception ex ) {

}finally{
}
   		
   		return newListofEntries;
   	}//get all photos regardless of meal ID
     
       public Coach getCoachsbyID(String coachID){
    	   Coach newObjActivity = new Coach();
			
    	   try{
      		Cursor cur = this.getAllEntriesCoachWithCoachID(coachID);
      		
      		if (this.cursorHasRows(cur)) {
      			if (cur.moveToFirst()) {
      				do {
      						
      						newObjActivity.coach_id = (cur.getString(cur.getColumnIndex("coachID")));
      						newObjActivity.firstname = (cur.getString(cur.getColumnIndex("firstname")));
      						newObjActivity.lastname = (cur.getString(cur.getColumnIndex("lastname")));
      						newObjActivity.avatar_url = (cur.getString(cur.getColumnIndex("avatarurl")));
      						newObjActivity.coach_title_en = (cur.getString(cur.getColumnIndex("coachtitleen")));
      						newObjActivity.coach_profile_en = (cur.getString(cur.getColumnIndex("coachprofileen")));
      						newObjActivity.coach_style_en = (cur.getString(cur.getColumnIndex("coachstyleen")));
      						newObjActivity.coach_title_fr = (cur.getString(cur.getColumnIndex("coachtitlefr")));
      						newObjActivity.coach_profile_fr = (cur.getString(cur.getColumnIndex("coachprofilefr")));
      						newObjActivity.coach_style_fr = (cur.getString(cur.getColumnIndex("coachstylefr")));
      						
      						Photo photo =  new Photo();
       						
       						try{
       							photo.photo_id = newObjActivity.coach_id;
       							photo.photo_url_large = newObjActivity.avatar_url;
       						try{
       							
       							photo.image = DBBitmapUtil.getImage(cur.getBlob(cur.getColumnIndex("photo")));
       						
       						}catch(Exception e){
       							
       						}
       						}catch(Exception ee){
       							
       						}
      					 	newObjActivity.coach_photo = photo;
      					 	
      						
      						
      				} while (cur.moveToNext());
      			}
      		}
      		
      		cur.close();
      	
       } catch( Exception ex ) {

       }finally{
       }
         
      		
      		return newObjActivity;
          	
       }//get all photos regardless of meal ID
      	
       
       public Coach getCoachsbyName(String coachName){
    	   Coach newObjActivity = new Coach();
			
    	   try{	
      		
    		   Cursor cur = this.getAllEntriesCoachByCoachName(coachName);
      		
      		if (this.cursorHasRows(cur)) {
      			if (cur.moveToFirst()) {
      				do {
      						
      						newObjActivity.coach_id = (cur.getString(cur.getColumnIndex("coachID")));
      						newObjActivity.firstname = (cur.getString(cur.getColumnIndex("firstname")));
      						newObjActivity.lastname = (cur.getString(cur.getColumnIndex("lastname")));
      						newObjActivity.avatar_url = (cur.getString(cur.getColumnIndex("avatarurl")));
      						newObjActivity.coach_title_en = (cur.getString(cur.getColumnIndex("coachtitleen")));
      						newObjActivity.coach_profile_en = (cur.getString(cur.getColumnIndex("coachprofileen")));
      						newObjActivity.coach_style_en = (cur.getString(cur.getColumnIndex("coachstyleen")));
      						newObjActivity.coach_title_fr = (cur.getString(cur.getColumnIndex("coachtitlefr")));
      						newObjActivity.coach_profile_fr = (cur.getString(cur.getColumnIndex("coachprofilefr")));
      						newObjActivity.coach_style_fr = (cur.getString(cur.getColumnIndex("coachstylefr")));
      						
      						Photo photo =  new Photo();
       						
       						try{
       							photo.photo_id = newObjActivity.coach_id;
       							photo.photo_url_large = newObjActivity.avatar_url;
       						try{
       							
       							photo.image = DBBitmapUtil.getImage(cur.getBlob(cur.getColumnIndex("photo")));
       						
       						}catch(Exception e){
       							
       						}
       						}catch(Exception ee){
       							
       						}
      					 	newObjActivity.coach_photo = photo;
      					 	
      						
      						
      				} while (cur.moveToNext());
      			}
      		}
      		
      		cur.close();
      	
      	  	
			} catch( Exception ex ) {

			}finally{
			}
			  
    		return newObjActivity;
            
       }//get all photos regardless of meal ID
   
}
