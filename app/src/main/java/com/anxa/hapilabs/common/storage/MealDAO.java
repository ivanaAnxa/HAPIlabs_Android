package com.anxa.hapilabs.common.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//import com.google.android.gms.internal.cm;


public class MealDAO extends DAO {

    protected static final String createSQL = "CREATE  TABLE IF NOT EXISTS mealDetails " +
            "(mealID TEXT PRIMARY KEY  NOT NULL , "
            + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " meal_creation_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " title TEXT,"
            + " meal_description TEXT,"
            + " isAllMealWithRating INTEGER,"
            + " isHealthyMealWithRating INTEGER,"
            + " isJustOkMealWithRating INTEGER,"
            + " isUnhealthyMealWithRating INTEGER,"
            + " isApproved INTEGER,"
            + " isCommented INTEGER,"
            + " hasUpdate INTEGER,"
            + " haspaidSubcription INTEGER,"
            + " state INTEGERS,"
            + " foodgroupcount INTEGER,"
            + " foodgroup TEXT,"
            + " mealTypeIndex INTEGER,"
            + " coachRating INTEGER,"
            + " userRating INTEGER,"
            + " meal_status TEXT);";
    //+ " meal_status TEXT);";

    public MealDAO(Context appCtx, DatabaseHelper dbHelper) {
        super(appCtx, "mealDetails", dbHelper);

    }

    /**
     * Create the table
     */
    public void createTable() {
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            db.execSQL(createSQL);
        } catch (Exception ex) {
        }
    }

    public void deleteMeal(String mealID) {
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            db.delete(this.tableName, "mealID = ?", new String[]{(mealID)});
        } catch (Exception ex) {
        }
    }

    public boolean insertMeal(Meal meal) {
        boolean status = true;
        try {
            ContentValues values = new ContentValues();

            if (meal.meal_id != null)
                values.put("mealID", meal.meal_id);

            values.put("timestamp", Meal.setDatetoString(meal.timestamp));
            values.put("meal_creation_date", Meal.setDatetoString(meal.meal_creation_date));

            if (meal.title != null)
                values.put("title", meal.title);

            if (meal.meal_description != null)
                values.put("meal_description", meal.meal_description);

            if (meal.food_group != null) {
                String keygroup = "";
                for (int i = 0; i < meal.food_group.size(); i++) {
                    keygroup = meal.food_group.get(i).getValue() + ":" + keygroup;
                }
                if (keygroup.length() > 0)
                    values.put("foodgroup", keygroup.substring(0, keygroup.length() - 1));
                values.put("foodgroupcount", meal.food_group.size());

            }

            values.put("isAllMealWithRating", (meal.isAllMealWithRating == true ? 1 : 0));
            values.put("isHealthyMealWithRating", (meal.isHealthyMealWithRating == true ? 1 : 0));
            values.put("isJustOkMealWithRating", (meal.isJustOkMealWithRating == true ? 1 : 0));
            values.put("isUnhealthyMealWithRating", (meal.isUnhealthyMealWithRating == true ? 1 : 0));

            values.put("isApproved", (meal.isApproved == true ? 1 : 0)); //one for true 0 for false

            values.put("isCommented", (meal.isCommented == true ? 1 : 0)); //one for true 0 for false

            values.put("hasUpdate", (meal.hasUpdate == true ? 1 : 0)); //one for true 0 for false

            values.put("haspaidSubcription", (meal.haspaidSubcription == true ? 1 : 0)); //one for true 0 for false

            values.put("state", meal.state.getValue());

            values.put("mealTypeIndex", meal.meal_type.getValue());

            values.put("meal_status", meal.meal_status);

            values.put("userRating", meal.userRating);
            values.put("coachRating", meal.coachRating);

            status = this.insertTable(values, "mealID=?", new String[]{String.valueOf(meal.meal_id)});


        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        } finally {
        }

        return status;
    }

    public boolean updateMeal(Meal meal) {
        boolean status = true;
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();

            ContentValues values = new ContentValues();
            if (meal.meal_id != null)
                values.put("mealID", meal.meal_id);
            values.put("timestamp", Meal.setDatetoString(meal.timestamp));

            values.put("meal_creation_date", Meal.setDatetoString(meal.meal_creation_date));

            if (meal.title != null)
                values.put("title", meal.title);

            if (meal.meal_description != null)
                values.put("meal_description", meal.meal_description);

            if (meal.food_group != null) {
                String keygroup = "";
                for (int i = 0; i < meal.food_group.size(); i++) {
                    keygroup = meal.food_group.get(i).getValue() + ":" + keygroup;
                }
                if (keygroup.length() > 0)
                    values.put("foodgroup", keygroup.substring(0, keygroup.length() - 1));

                values.put("foodgroupcount", meal.food_group.size());

            }

            values.put("isAllMealWithRating", (meal.isAllMealWithRating == true ? 1 : 0));
            values.put("isHealthyMealWithRating", (meal.isHealthyMealWithRating == true ? 1 : 0));
            values.put("isJustOkMealWithRating", (meal.isJustOkMealWithRating == true ? 1 : 0));
            values.put("isUnhealthyMealWithRating", (meal.isUnhealthyMealWithRating == true ? 1 : 0));

            values.put("isApproved", (meal.isApproved == true ? 1 : 0)); //one for true 0 for false
            values.put("isCommented", (meal.isCommented == true ? 1 : 0)); //one for true 0 for false
            values.put("hasUpdate", (meal.hasUpdate == true ? 1 : 0)); //one for true 0 for false
            values.put("haspaidSubcription", (meal.haspaidSubcription == true ? 1 : 0)); //one for true 0 for false
            values.put("state", meal.state.getValue());
            values.put("mealTypeIndex", meal.meal_type.getValue());
            values.put("meal_status", meal.meal_status);
            values.put("userRating", meal.userRating);
            values.put("coachRating", meal.coachRating);

            db.update(this.tableName, values, "mealID = ?", new String[]{String.valueOf(meal.meal_id)});

        } catch (Exception ex) {
            status = false;
        } finally {
        }

        return status;
    }


    //get all photos regardless of meal ID
    public ArrayList<Meal> getAllMeals() {

        ArrayList<Meal> newListofEntries = new ArrayList<Meal>();

        Cursor cur = this.getAllEntriesMeals();

        if (cur != null)

            if (this.cursorHasRows(cur)) {
                if (cur.moveToFirst()) {
                    do {
                        Meal newObjActivity = new Meal();

                        if (cur.getColumnIndex("mealID") > -1)
                            newObjActivity.meal_id = (cur.getString(cur.getColumnIndex("mealID")));

                        if (cur.getColumnIndex("timestamp") > -1)
                            newObjActivity.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        format.setTimeZone(TimeZone.getTimeZone("GMT"));
                        Date meal_creation_date = null;
                        try {
                            meal_creation_date = format.parse(cur.getString(cur.getColumnIndex("meal_creation_date")));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (cur.getColumnIndex("meal_creation_date") > -1)
                            newObjActivity.meal_creation_date = meal_creation_date;//Meal.setStringtoDate(cur.getString(cur.getColumnIndex("meal_creation_date")));

                        if (cur.getColumnIndex("title") > -1)
                            newObjActivity.title = (cur.getString(cur.getColumnIndex("title")));

                        if (cur.getColumnIndex("foodgroupcount") > -1) {
                            int foodcount = (cur.getInt(cur.getColumnIndex("foodgroupcount")));
                            String foodgroupstring = null;
                            if (foodcount > 0) {

                                newObjActivity.food_group = new ArrayList<Meal.FOODGROUP>();

                                foodgroupstring = (cur.getString(cur.getColumnIndex("foodgroup")));

                                String[] stringFG = foodgroupstring.split(":");

                                for (int i = 0; i < stringFG.length; i++) {
                                    int fgInt = Integer.parseInt(stringFG[i]);
                                    newObjActivity.food_group.add(Meal.getFGValue(fgInt));
                                }
                            }
                        }

                        if (cur.getColumnIndex("meal_description") > -1)
                            newObjActivity.meal_description = (cur.getString(cur.getColumnIndex("meal_description")));

                        if (cur.getColumnIndex("isAllMealWithRating") > -1)
                            newObjActivity.isAllMealWithRating = (cur.getInt(cur.getColumnIndex("isAllMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("isHealthyMealWithRating") > -1)
                            newObjActivity.isHealthyMealWithRating = (cur.getInt(cur.getColumnIndex("isHealthyMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("isJustOkMealWithRating") > -1)
                            newObjActivity.isJustOkMealWithRating = (cur.getInt(cur.getColumnIndex("isJustOkMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("isUnhealthyMealWithRating") > -1)
                            newObjActivity.isUnhealthyMealWithRating = (cur.getInt(cur.getColumnIndex("isUnhealthyMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("isApproved") > -1)
                            newObjActivity.isApproved = (cur.getInt(cur.getColumnIndex("isApproved"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("isCommented") > -1)
                            newObjActivity.isCommented = (cur.getInt(cur.getColumnIndex("isCommented"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("hasUpdate") > -1)
                            newObjActivity.hasUpdate = (cur.getInt(cur.getColumnIndex("hasUpdate"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("haspaidSubcription") > -1)
                            newObjActivity.isApproved = (cur.getInt(cur.getColumnIndex("haspaidSubcription"))) == 1 ? true : false; // (1 = true, 0 = false)

                        if (cur.getColumnIndex("state") > -1)
                            newObjActivity.state = Meal.getSTATEValue(cur.getInt(cur.getColumnIndex("state")));

                        if (cur.getColumnIndex("meal_status") > -1)
                            newObjActivity.meal_status = cur.getString(cur.getInt(cur.getColumnIndex("meal_status")));

                        if (cur.getColumnIndex("mealTypeIndex") > -1)
                            newObjActivity.meal_type = Meal.MEAL_TYPE.getMealType(cur.getInt(cur.getColumnIndex("mealTypeIndex")));

                        if (cur.getColumnIndex("userRating") > -1)
                            newObjActivity.userRating = cur.getInt(cur.getInt(cur.getColumnIndex("userRating")));

                        if (cur.getColumnIndex("coachRating") > -1)
                            newObjActivity.coachRating = cur.getInt(cur.getInt(cur.getColumnIndex("coachRating")));

                        //get all photos for that meal
                        try {
                            Cursor cursorPhoto = this.getAllEntriesPhotosWithMealID(newObjActivity.meal_id);

                            if (this.cursorHasRows(cursorPhoto)) {

                                List<Photo> photos = new ArrayList<Photo>();

                                if (cursorPhoto.moveToFirst()) {

                                    do {

                                        try {

                                            Photo photo = new Photo();

                                            if (cursorPhoto.getColumnIndex("photoID") > -1) {
                                                photo.photo_id = (cursorPhoto.getString(cursorPhoto.getColumnIndex("photoID")));
                                            }

                                            if (cursorPhoto.getColumnIndex("tempId") > -1)
                                                photo.tempId = (cursorPhoto.getString(cursorPhoto.getColumnIndex("tempId")));

                                            if (cursorPhoto.getColumnIndex("image") > -1) {
                                                try {
                                                    byte[] blobImg = cursorPhoto.getBlob(cursorPhoto.getColumnIndex("image"));
                                                    if (blobImg != null)
                                                        photo.image = DBBitmapUtil.getImage(blobImg);
                                                } catch (Exception e) {

                                                }
                                            }

                                            if (cursorPhoto.getColumnIndex("photourllarge") > -1)
                                                photo.photo_url_large = (cursorPhoto.getString(cursorPhoto.getColumnIndex("photourllarge")));

                                            if (cursorPhoto.getColumnIndex("state") > -1)
                                                photo.state = Photo.getPHOTOSTATUSvalue(cursorPhoto.getInt(cursorPhoto.getColumnIndex("state")));

                                            photos.add(photo);
                                        } catch (Exception e) {
                                            // TODO: handle exception

                                        }
                                    } while (cursorPhoto.moveToNext());
                                }

                                if (photos != null && photos.size() > 0) {
                                    newObjActivity.photos = new ArrayList<Photo>();
                                    newObjActivity.photos = photos;
                                }
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        //get all comment for that meal
                        try {
                            Cursor cursorComment = this.getAllEntriesCommentWithCommentID(newObjActivity.meal_id);

                            if (this.cursorHasRows(cursorComment)) {
                                List<Comment> comments = new ArrayList<Comment>();

                                if (cursorComment.moveToFirst()) {

                                    do {
                                        try {
                                            Comment comment = new Comment();

                                            if (cursorComment.getColumnIndex("commentID") > -1)
                                                comment.comment_id = (cursorComment.getString(cursorComment.getColumnIndex("commentID")));

                                            if (cursorComment.getColumnIndex("meal_id") > -1)
                                                comment.meal_id = (cursorComment.getString(cursorComment.getColumnIndex("meal_id")));

                                            if (cursorComment.getColumnIndex("message") > -1)
                                                comment.message = (cursorComment.getString(cursorComment.getColumnIndex("message")));

                                            comment.coach = new Coach();

                                            if (cursorComment.getColumnIndex("coach_id") > -1)
                                                comment.coach.coach_id = (cursorComment.getString(cursorComment.getColumnIndex("coach_id")));

                                            if (cursorComment.getColumnIndex("timestamp") > -1)
                                                comment.timestamp = Meal.setStringtoDate(cursorComment.getString(cursorComment.getColumnIndex("timestamp")));

                                            if (cursorComment.getColumnIndex("comment_type") > -1)
                                                comment.comment_type = cursorComment.getInt(cursorComment.getInt(cursorComment.getColumnIndex("comment_type")));

                                            if (cursorComment.getColumnIndex("status") > -1)
                                                comment.status = Comment.getSTATUSValue(cursorComment.getInt(cur.getColumnIndex("status")));


                                            if (cursorComment.getColumnIndex("ishapi") > -1)
                                                comment.isHAPI = (cursorComment.getInt(cursorComment.getColumnIndex("ishapi"))) == 1 ? true : false; // (1 = true, 0 = false)

                                            comments.add(comment);


                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            e.printStackTrace();


                                        }
                                    } while (cursorComment.moveToNext());


                                }

                                newObjActivity.comments = comments;
                            }

                        } catch (Exception e) {
                            // TODO: handle exception

                        }

                        //add the meal to the array list
                        newListofEntries.add(newObjActivity);


                    } while (cur.moveToNext());
                }
            }

        try {
            cur.close();
        } catch (Exception e) {
            ;
        }

        return newListofEntries;
    }//get all photos regardless of meal ID

    public Meal getMealbyID(String mealID) {

        Cursor cur = this.getAllMealWithMealID(mealID);

        if (this.cursorHasRows(cur)) {

            if (cur.moveToFirst()) {
                do {
                    Meal newObjActivity = new Meal();

                    if (cur.getColumnIndex("mealID") > -1)
                        newObjActivity.meal_id = (cur.getString(cur.getColumnIndex("mealID")));

                    if (cur.getColumnIndex("timestamp") > -1)
                        newObjActivity.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));

                    if (cur.getColumnIndex("meal_creation_date") > -1)
                        newObjActivity.meal_creation_date = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("meal_creation_date")));

                    if (cur.getColumnIndex("title") > -1)
                        newObjActivity.title = (cur.getString(cur.getColumnIndex("title")));

                    if (cur.getColumnIndex("meal_description") > -1)
                        newObjActivity.meal_description = (cur.getString(cur.getColumnIndex("meal_description")));

                    if (cur.getColumnIndex("isAllMealWithRating") > -1)
                        newObjActivity.isAllMealWithRating = (cur.getInt(cur.getColumnIndex("isAllMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("isHealthyMealWithRating") > -1)
                        newObjActivity.isHealthyMealWithRating = (cur.getInt(cur.getColumnIndex("isHealthyMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("isJustOkMealWithRating") > -1)
                        newObjActivity.isJustOkMealWithRating = (cur.getInt(cur.getColumnIndex("isJustOkMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("isUnhealthyMealWithRating") > -1)
                        newObjActivity.isUnhealthyMealWithRating = (cur.getInt(cur.getColumnIndex("isUnhealthyMealWithRating"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("isApproved") > -1)
                        newObjActivity.isApproved = (cur.getInt(cur.getColumnIndex("isApproved"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("isCommented") > -1)
                        newObjActivity.isCommented = (cur.getInt(cur.getColumnIndex("isCommented"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("hasUpdate") > -1)
                        newObjActivity.hasUpdate = (cur.getInt(cur.getColumnIndex("hasUpdate"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("haspaidSubcription") > -1)
                        newObjActivity.isApproved = (cur.getInt(cur.getColumnIndex("haspaidSubcription"))) == 1 ? true : false; // (1 = true, 0 = false)

                    if (cur.getColumnIndex("state") > -1)
                        newObjActivity.state = Meal.getSTATEValue(cur.getInt(cur.getColumnIndex("state")));

                    if (cur.getColumnIndex("meal_status") > -1)
                        newObjActivity.meal_status = (cur.getString(cur.getColumnIndex("meal_status")));

                    if (cur.getColumnIndex("mealTypeIndex") > -1)
                        newObjActivity.meal_type = Meal.MEAL_TYPE.getMealType(cur.getInt(cur.getColumnIndex("mealTypeIndex")));

                    if (cur.getColumnIndex("userRating") > -1)
                        newObjActivity.userRating = cur.getInt(cur.getInt(cur.getColumnIndex("userRating")));

                    if (cur.getColumnIndex("coachRating") > -1)
                        newObjActivity.coachRating = cur.getInt(cur.getInt(cur.getColumnIndex("coachRating")));

                    if (cur.getColumnIndex("foodgroupcount") > -1) {
                        int foodcount = (cur.getInt(cur.getColumnIndex("foodgroupcount")));
                        String foodgroupstring = null;
                        if (foodcount > 0) {

                            newObjActivity.food_group = new ArrayList<Meal.FOODGROUP>();

                            foodgroupstring = (cur.getString(cur.getColumnIndex("foodgroup")));

                            String[] stringFG = foodgroupstring.split(":");

                            for (int i = 0; i < stringFG.length; i++) {
                                int fgInt = Integer.parseInt(stringFG[i]);
                                newObjActivity.food_group.add(Meal.getFGValue(fgInt));

                            }


                        }


                    }


                    //get all photos for that meal
                    Cursor cursorPhoto = this.getAllEntriesPhotosWithMealID(newObjActivity.meal_id);

                    if (this.cursorHasRows(cursorPhoto)) {
                        List<Photo> photos = new ArrayList<Photo>();
                        if (cursorPhoto.moveToFirst()) {

                            do {
                                try {
                                    Photo photo = new Photo();

                                    if (cursorPhoto.getColumnIndex("photoID") > -1)
                                        photo.photo_id = (cursorPhoto.getString(cursorPhoto.getColumnIndex("photoID")));

                                    if (cursorPhoto.getColumnIndex("tempId") > -1)
                                        photo.tempId = (cursorPhoto.getString(cursorPhoto.getColumnIndex("tempId")));

                                    if (cursorPhoto.getColumnIndex("image") > -1)

                                        try {
                                            byte[] blobImg = cursorPhoto.getBlob(cursorPhoto.getColumnIndex("image"));
                                            if (blobImg != null)
                                                photo.image = DBBitmapUtil.getImage(blobImg);
                                        } catch (Exception e) {

                                        }

                                    if (cursorPhoto.getColumnIndex("photourllarge") > -1)
                                        photo.photo_url_large = (cursorPhoto.getString(cursorPhoto.getColumnIndex("photourllarge")));

                                    if (cur.getColumnIndex("state") > -1)
                                        photo.state = Photo.getPHOTOSTATUSvalue(cursorPhoto.getInt(cursorPhoto.getColumnIndex("state")));

                                    photos.add(photo);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            } while (cursorPhoto.moveToNext());

                        }

                        newObjActivity.photos = photos;

                    }

                    //get all comment for that meal
                    Cursor cursorComment = this.getAllEntriesCommentWithCommentID(newObjActivity.meal_id);

                    if (this.cursorHasRows(cursorComment)) {
                        List<Comment> comments = new ArrayList<Comment>();

                        if (cursorComment.moveToFirst()) {

                            do {
                                try {
                                    Comment comment = new Comment();

                                    if (cur.getColumnIndex("commentID") > -1)
                                        comment.comment_id = (cur.getString(cur.getColumnIndex("commentID")));

                                    if (cur.getColumnIndex("meal_id") > -1)
                                        comment.meal_id = (cur.getString(cur.getColumnIndex("meal_id")));

                                    if (cur.getColumnIndex("message") > -1)
                                        comment.message = (cur.getString(cur.getColumnIndex("message")));

                                    comment.coach = new Coach();

                                    if (cur.getColumnIndex("coach_id") > -1)
                                        comment.coach.coach_id = (cur.getString(cur.getColumnIndex("coach_id")));

                                    if (cur.getColumnIndex("timestamp") > -1)
                                        comment.timestamp = Meal.setStringtoDate(cur.getString(cur.getColumnIndex("timestamp")));

                                    if (cur.getColumnIndex("comment_type") > -1)
                                        comment.comment_type = cur.getInt(cur.getInt(cur.getColumnIndex("comment_type")));

                                    if (cur.getColumnIndex("status") > -1)
                                        comment.status = Comment.getSTATUSValue(cur.getInt(cur.getColumnIndex("status")));

                                    if (cur.getColumnIndex("ishapi") > -1)
                                        comment.isHAPI = (cur.getInt(cur.getColumnIndex("ishapi"))) == 1 ? true : false; // (1 = true, 0 = false)

                                    comments.add(comment);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            } while (cursorComment.moveToNext());
                        }

                        newObjActivity.comments = comments;
                    }


                    //add the meal to the array list

                    return newObjActivity; //return first meal fetch

                } while (cur.moveToNext());
            }

        }

        try {
            cur.close();
        } catch (Exception e) {

        }

        return null;
    }

}
