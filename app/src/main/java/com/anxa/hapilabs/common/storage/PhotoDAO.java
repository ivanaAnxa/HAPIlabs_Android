package com.anxa.hapilabs.common.storage;

import java.util.ArrayList;

import com.anxa.hapilabs.models.Photo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class PhotoDAO extends DAO{
        
	
		protected static final String createSQL = "CREATE  TABLE IF NOT EXISTS photoDetails "+
        		"(photoID TEXT PRIMARY KEY  NOT NULL , "
        		+ " mealID TEXT,"
        		+ " tempId TEXT,"
        		+ " image BLOB,"
        		+ " photourllarge TEXT,"
        		+ " state INTEGER);";
        
        public PhotoDAO(Context appCtx, DatabaseHelper dbHelper) {
                super(appCtx, "photoDetails", dbHelper);
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

        public void delete(String photoID){
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, "photoID = ?", new String [] {photoID} );
                } catch( Exception ex ) {
                }
        }
        
       public boolean insert(Photo photo,String mealID ) {
                boolean status=true;
                try {
                		ContentValues values = new ContentValues();
                		values.put("photoID", photo.photo_id);
                        values.put("tempId", photo.tempId);
                		values.put("mealID", mealID);
                        
                		try{
                        	if (photo.image!=null)
                        		values.put("image", DBBitmapUtil.getBytes(photo.image));
                        }catch(Exception e){}
                        
                		values.put("photourllarge", photo.photo_url_large);
                		values.put("state", photo.state.getValue());
                        status = this.insertTable(values, "photoID=?", new String[] {String.valueOf(photo.photo_id)});
                		
                        
                } catch( Exception ex ) {
                        status=false;
                }finally{
                }
                
                return status;
        }
        
       public boolean update(Photo photo,String mealID  ) {
           boolean status=true;
           SQLiteDatabase db = null;
           try {
                   db = this.getDatabase();
                   
                      ContentValues values = new ContentValues();
                   
                   
                   values.put("photoID", photo.photo_id);
                   
           			values.put("tempId", photo.tempId);
                   
            		values.put("mealID", mealID);
                
            		try{
                		if (photo.image!=null){
                		
                			//make sure the photo is 
                			values.put("image", DBBitmapUtil.getBytes(photo.image));
                		
                		}
            		}catch(Exception e){}
            		
            		
            		values.put("photourllarge", photo.photo_url_large);
                   
            		values.put("state", photo.state.getValue());
                   
            		db.update(this.tableName, values, "photoID = ?", new String [] {String.valueOf(photo.photo_id)} );
           } catch( Exception ex ) {
                   status=false;
           }finally{
           }
           
           return status;
   }
       
       public ArrayList<Photo> getPhotos(String mealID){

   		ArrayList<Photo> newListofEntries = new ArrayList<Photo>();
   		
   		Cursor cur = this.getAllEntriesPhotosWithMealID(mealID);
   		
   		if (this.cursorHasRows(cur)) {
   			if (cur.moveToFirst()) {
   				do {
   						String id = cur.getString(cur.getColumnIndex("mealID"));
   						if (id == mealID)
   							{
   						Photo newObjActivity = new Photo();
   						newObjActivity.photo_id = (cur.getString(cur.getColumnIndex("photoID")));
   						newObjActivity.tempId = (cur.getString(cur.getColumnIndex("tempId")));
   						
                    
   						try{
   								newObjActivity.image = DBBitmapUtil.getImage(cur.getBlob(cur.getColumnIndex("image")));
   						}catch(Exception e){}
             
   						newObjActivity.photo_url_large = (cur.getString(cur.getColumnIndex("photourllarge")));
   						newObjActivity.state = Photo.getPHOTOSTATUSvalue((cur.getInt(cur.getColumnIndex("state"))));
   						
   						newListofEntries.add(newObjActivity);
                     }
   				} while (cur.moveToNext());
   			}
   		}
   		
   		cur.close();
   		return newListofEntries;
   	}
       
       //get all photos regardless of meal ID
   	public ArrayList<Photo> getPhotos(){

   		ArrayList<Photo> newListofEntries = new ArrayList<Photo>();
   		
   		Cursor cur = this.getAllEntriesAllPhotos();
   		
   		if (this.cursorHasRows(cur)) {
   			if (cur.moveToFirst()) {
   				do {
   						Photo newObjActivity = new Photo();
   						newObjActivity.photo_id = (cur.getString(cur.getColumnIndex("photoID")));
   						newObjActivity.tempId = (cur.getString(cur.getColumnIndex("tempId")));
   						try{
								newObjActivity.image = DBBitmapUtil.getImage(cur.getBlob(cur.getColumnIndex("image")));
						}catch(Exception e){}
   						newObjActivity.photo_url_large = (cur.getString(cur.getColumnIndex("photourllarge")));
   						newObjActivity.state = Photo.getPHOTOSTATUSvalue((cur.getInt(cur.getColumnIndex("state"))));
   						
   						newListofEntries.add(newObjActivity);
   						
   				} while (cur.moveToNext());
   			}
   		}
   		
   		cur.close();
   		return newListofEntries;
   	}
   
       
   
}
