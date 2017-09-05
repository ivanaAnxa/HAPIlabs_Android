package com.anxa.hapilabs.common.storage;

import java.util.ArrayList;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Message;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;




public class MessageDAO extends DAO{
        
	
		protected static final String createSQL = "CREATE TABLE IF NOT EXISTS messageDetails "+
        		"(messageID TEXT PRIMARY KEY NOT NULL , "
        		+ " coach_id TEXT,"
            	+ " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
        		+ " status INTEGER," 
        		+ " message_body TEXT);";
        
        public MessageDAO(Context appCtx, DatabaseHelper dbHelper) {
                super(appCtx, "messageDetails", dbHelper);
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

        public void delete(String messageID){
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, "messageID = ?", new String [] {messageID} );
                } catch( Exception ex ) {
                }
        }
     
       public boolean insert(Message message ) {
                boolean status=true;
                try {
                	
                        ContentValues values = new ContentValues();
                        values.put("messageID", message.message_id);
                        values.put("coach_id", message.coachID);
                        values.put("timestamp",Meal.setDatetoString(message.timestamp));
                        values.put("status", message.status.getValue());
                        values.put("message_body",message.message_body);
                        status = this.insertTable(values, "messageID=?", new String[] {(message.message_id)});
                        
                } catch( Exception ex ) {
                        status=false;
                }finally{
                }
                
                return status;
        }
        
       public boolean update(Message message ) {
           boolean status=true;
           SQLiteDatabase db = null;
           try {
                   db = this.getDatabase();
                   
                   ContentValues values = new ContentValues();
                   
                   values.put("messageID", message.message_id);
                   values.put("coach_id", message.coachID);
                   values.put("timestamp",Meal.setDatetoString(message.timestamp));
                   values.put("status", message.status.getValue());
                   values.put("message_body",message.message_body);
                 
                    
                   db.update(this.tableName, values, "messageID = ?", new String [] {message.message_id} );
           } catch( Exception ex ) {
                   status=false;
           }finally{
           }
           
           return status;
   }
       
       //get all photos regardless of meal ID
     	public ArrayList<Object> getAllMessages(){

     		ArrayList<Object> newListofEntries = new ArrayList<Object>();
     		
     		Cursor cur = this.getAllEntriesMessages();
     		
     		if (this.cursorHasRows(cur)) {
     			if (cur.moveToFirst()) {
     				do {
     						Message newObjActivity = new Message();
     						newObjActivity.coachID = (cur.getString(cur.getColumnIndex("coach_id")));
     						newObjActivity.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));
     						
     						newObjActivity.message_id = (cur.getString(cur.getColumnIndex("messageID")));
     						newObjActivity.message_body = (cur.getString(cur.getColumnIndex("message_body")));
     						newObjActivity.status = Message.getSTATUSValue(cur.getInt(cur.getColumnIndex("status")));
     						
     						
     						//add the meal to the array list
     						newListofEntries.add(newObjActivity);
     						
     						
     						
     				} while (cur.moveToNext());
     			}
     		}
     		
     		cur.close();
     		return newListofEntries;
     	}//get all photos regardless of meal ID
     	
     
       
   
}
