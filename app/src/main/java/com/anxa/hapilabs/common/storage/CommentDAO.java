package com.anxa.hapilabs.common.storage;

import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Meal;
//import com.google.android.gms.internal.cm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



public class CommentDAO extends DAO{
        
	

		protected static final String createSQL = "CREATE  TABLE IF NOT EXISTS commentDetails "+
        		"(commentID TEXT PRIMARY KEY  NOT NULL , "
        		+ " meal_id TEXT,"
        		+ " coach_id TEXT,"

            	+ " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
        		+ " comment_type INTEGER,"
        		+ " ishapi INTEGER," 
        		+ " status INTEGER," 
        		+ " message TEXT);";
        
        public CommentDAO(Context appCtx, DatabaseHelper dbHelper) {
                super(appCtx, "commentDetails", dbHelper);
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

        public void delete(String commentID){
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, "commentID = ?", new String [] {commentID} );
                } catch( Exception ex ) {
                }
        }
        
       public boolean insert(Comment comment ) {
                boolean status=true;
                try {
                	
                        ContentValues values = new ContentValues();
                        
                        if (comment.comment_id!=null)
                        	values.put("commentID", comment.comment_id);
                        
                        if (comment.meal_id!=null)
                            values.put("meal_id", comment.meal_id);
                        
                        if (comment.coach!=null && comment.coach.coach_id!=null)
                            values.put("coach_id", comment.coach.coach_id);
                       
                        
                        if (comment.timestamp!=null)
                            values.put("timestamp",Meal.setDatetoString(comment.timestamp));
                        values.put("comment_type", comment.comment_type);
                        
                        values.put("ishapi", (comment.isHAPI == true ? 1 : 0)); //one for true 0 for false
                        
                        values.put("status", comment.status.getValue());
                        
                        if (comment.message!=null)
                        	values.put("message",comment.message);
                        
                        status = this.insertTable(values, "commentID=?", new String[] {String.valueOf(comment.comment_id)});
                        
                        
                } catch( Exception ex ) {
                        status=false;
                }finally{
                }
                
                return status;
        }
        
       public boolean update(Comment comment ) {
           boolean status=true;
           SQLiteDatabase db = null;
           try {
                   db = this.getDatabase();
                   
                   ContentValues values = new ContentValues();
                   if (comment.comment_id!=null)
                   	values.put("commentID", comment.comment_id);
                   
                   if (comment.meal_id!=null)
                       values.put("meal_id", comment.meal_id);
                   
                   if (comment.coach!=null && comment.coach.coach_id!=null)
                       values.put("coach_id", comment.coach.coach_id);
                 
                   if (comment.timestamp!=null)
                       values.put("timestamp",Meal.setDatetoString(comment.timestamp));
                   
                   values.put("comment_type", comment.comment_type);
                   
                   values.put("ishapi", (comment.isHAPI == true ? 1 : 0)); //one for true 0 for false
                   
                   values.put("status", comment.status.getValue());
                   
                   if (comment.message!=null)
                   	values.put("message",comment.message);
                   
                   db.update(this.tableName, values, "commentID = ?", new String [] {String.valueOf(comment.comment_id)} );
           } catch( Exception ex ) {
                   status=false;
           }finally{
           }
           
           return status;
   }
       
   
}
