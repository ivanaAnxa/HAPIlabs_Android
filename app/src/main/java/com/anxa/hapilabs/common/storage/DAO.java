package com.anxa.hapilabs.common.storage;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;


/**
 * Base data access class
 */
public abstract class DAO {
    /**
     * Database helper
     */
    protected DatabaseHelper dbHelper = null;
    /**
     * Table name
     */
    protected String tableName;
    /**
     * Application Context
     */
    protected Context appCtx;
    private boolean canUpgrade = true;

    /**
     * Abstract function called to create the table
     */
    protected abstract void createTable();

    public DAO(Context appCtx, String tablename, DatabaseHelper helper) {
        this.appCtx = appCtx;
        this.tableName = tablename;
        this.dbHelper = helper;
        this.initialiseDatabase();
    }

    public DAO(Context appCtx, String tablename, DatabaseHelper helper, boolean canUpgrade) {
        this.appCtx = appCtx;
        this.tableName = tablename;
        this.dbHelper = helper;
        this.canUpgrade = canUpgrade;
        this.initialiseDatabase();
    }

    /**
     * Initialise the database
     *
     * @param createString
     */
    protected void initialiseDatabase() {
        if (this.dbHelper == null)
            this.dbHelper = DatabaseHelper.getInstance(appCtx);
//                        this.dbHelper = new DatabaseHelper(appCtx);

        if (this.dbHelper.requireUpgrade() && canUpgrade) {
            String query = "DROP TABLE " + this.tableName + ";";
            SQLiteDatabase db = null;
            try {
                db = this.getDatabase();
                db.execSQL(query);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        this.createTable();
    }

    /**
     * Get the database instance
     *
     * @return
     */
    protected SQLiteDatabase getDatabase() {
        return this.dbHelper.getDatabase();
    }

    /**
     * Check if the cursor has rows
     *
     * @param cursor
     * @return
     */
    protected boolean cursorHasRows(Cursor cursor) {

        return (cursor != null && cursor.getCount() > 0);
    }

    public void clearTable() {
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            db.delete(this.tableName, null, null);
        } catch (Exception ex) {
        }
    }


    public boolean insertTable(ContentValues values, String whereClause, String[] whereArgs) {

//        Log.d("insertTable @table name", tableName);

        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            if (db.update(tableName, values, whereClause, whereArgs) <= 0)
                db.insertOrThrow(tableName, null, values);
            db.close();
            return true;
        } catch (Exception ex) {
        }
        return false;
        //DBInsertHandler.getInstance(dbHelper).addQueue(tableName, values, whereClause, whereArgs);
    }


    Cursor getSelectedEntry(int passID, String orderBy) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();

        try {
            cur = db.query(tableName, null, null, null, null, null, orderBy);

        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesPerTable() {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from " + tableName, new String[]{});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    /**
     * returns all photo in the SQL table
     */
    Cursor getAllEntriesAllPhotos() {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from photoDetails", new String[]{});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesComments() {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();

        try {
            cur = db.rawQuery("SELECT * from commentDetails", new String[]{});

        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesCoach() {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from coachDetails", new String[]{});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


    Cursor getAllEntriesMeals() {
        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();


        try {
            cur = db.rawQuery("SELECT * from mealDetails", new String[]{});
        } catch (Exception e) {
            //no such table exist
        }


        return cur;

    }

    Cursor getAllEntriesMessages() {
        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from messageDetails", new String[]{});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesNotification() {
        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from NotificationDetails", new String[]{});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesUserProfile() {

        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();

        List<Pair<String, String>> pairs = db.getAttachedDbs();


        Pair<String, String> pair = pairs.get(0);


        try {
            cur = db.rawQuery("SELECT * from userDetails", new String[]{});


        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


    Cursor getAllEntriesPhotosWithMealID(String mealID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from photoDetails where mealID = ?", new String[]{mealID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesPhotosWithPhotoID(String photoID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from photoDetails where photoID = ?", new String[]{photoID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


    Cursor getAllMealWithMealID(String mealID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from mealDetails where mealID = ?", new String[]{mealID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllWorkoutWithActivityID(String activity_id) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from workoutDetails where activity_id = ?", new String[]{activity_id});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesCommentWithMealID(String mealID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from commentDetails where meal_id = ?", new String[]{mealID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesCommentWithCommentID(String commentID) {
        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from commentDetails where commentID = ?", new String[]{commentID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesMessageWithMessageID(String messageID) {
        Cursor cur = null;

        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from messageDetails where messageID = ?", new String[]{messageID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


    Cursor getAllEntriesNotificationWithNotifID(String notificationID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from NotificationDetails where notificationID = ?", new String[]{notificationID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesNotificationWithMealID(String mealID) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from NotificationDetails where meal_id = ?", new String[]{mealID});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


    Cursor getAllEntriesCoachWithCoachID(String coachId) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from coachDetails where coachID = ?", new String[]{coachId});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getAllEntriesCoachByCoachName(String coachName) {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from coachDetails where firstname = ?", new String[]{coachName});
        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }

    Cursor getSettings() {
        Cursor cur = null;
        SQLiteDatabase db = this.getDatabase();
        try {
            cur = db.rawQuery("SELECT * from appsettings", new String[]{});

        } catch (Exception e) {
            //no such table exist
        }
        return cur;
    }


}