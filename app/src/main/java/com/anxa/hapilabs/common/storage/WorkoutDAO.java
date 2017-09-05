package com.anxa.hapilabs.common.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anxa.hapilabs.models.Workout;

/**
 * Created by elaineanxa on 18/08/2016.
 */
public class WorkoutDAO extends DAO
{
    protected static final String createSQL = "CREATE TABLE IF NOT EXISTS workoutDetails " +
            "(activity_id TEXT PRIMARY KEY  NOT NULL , "
            + " coreData_id DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " exercise_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " exercise_date TEXT,"
            + " device_name TEXT,"
            + " duration INTEGER,"
            + " steps INTEGER,"
            + " calories INTEGER,"
            + " distance INTEGER,"
            + " workout_desc TEXT,"
            + " workout_image TEXT,"
            + " exercise_state INTEGER,"
            + " exercise_type INTEGER,"
            + " command TEXT,"
            + " isChecked BOOLEAN);";

    public WorkoutDAO(Context appCtx, DatabaseHelper dbHelper)
    {
        super(appCtx, "workoutDetails", dbHelper);
    }

    /**
     * Create the table
     */
    public void createTable()
    {
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            db.execSQL(createSQL);
        } catch (Exception ex) {
        }
    }

    public void delete(String activity_id)
    {
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();
            db.delete(this.tableName, "activity_id = ?", new String[]{(activity_id)});
        } catch (Exception ex) {
        }
    }

    public boolean insert(Workout workout)
    {
        boolean status = true;
        try {
            ContentValues values = new ContentValues();

            if (workout.activity_id != null)
                values.put("activity_id", workout.activity_id);

            if (workout.coreData_id != null)
            {
                values.put("coreData_id", workout.coreData_id);
            }

            values.put("exercise_datetime", Workout.setDatetoString(workout.exercise_datetime));

            values.put("exercise_date", workout.exercise_date);

            if (workout.device_name != null)
            {
                values.put("device_name", workout.device_name);
            }

            values.put("duration", workout.duration);

            values.put("steps", workout.steps);

            values.put("calories", workout.calories);

            values.put("distance", workout.distance);

            if (workout.workout_desc != null)
                values.put("workout_desc", workout.workout_desc);

            if (workout.exercise_type.getValue() == 1)
            {
                values.put("workout_image", "exercise_display_run");
            }
            else if (workout.exercise_type.getValue() == 2)
            {
                values.put("workout_image", "exercise_display_bike");
            }
            else if (workout.exercise_type.getValue() == 4)
            {
                values.put("workout_image", "exercise_display_walk");
            }
            else if (workout.exercise_type.getValue() == 10)
            {
                values.put("workout_image", "exercise_display_swim");
            }
            else if (workout.exercise_type.getValue() == 35)
            {
                values.put("workout_image", "exercise_display_workout");
            }
            else if (workout.exercise_type.getValue() == -1)
            {
                values.put("workout_image", "exercise_display_steps");
            }
            else
            {
                values.put("workout_image", "exercise_display_other");
            }

            if (workout.exercise_state != null)
            {
                values.put("exercise_state", workout.exercise_state.getValue());
            }

            values.put("exercise_type", workout.exercise_type.getValue());

            values.put("command", workout.command);

            values.put("isChecked", (workout.isChecked == true ? 1 : 0)); //one for true 0 for false

            status = this.insertTable(values, "activity_id=?", new String[]{String.valueOf(workout.activity_id)});

        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        } finally {
        }

        return status;
    }

    public boolean update(Workout workout)
    {
        boolean status = true;
        SQLiteDatabase db = null;
        try {
            db = this.getDatabase();

            ContentValues values = new ContentValues();

            if (workout.activity_id != null)
                values.put("activity_id", workout.activity_id);

            if (workout.coreData_id != null)
            {
                values.put("coreData_id", workout.coreData_id);
            }

            values.put("exercise_datetime", Workout.setDatetoString(workout.exercise_datetime));

            values.put("exercise_date", workout.exercise_date);

            if (workout.device_name != null)
            {
                values.put("device_name", workout.device_name);
            }

            values.put("duration", workout.duration);

            values.put("steps", workout.steps);

            values.put("calories", workout.calories);

            values.put("distance", workout.distance);

            if (workout.workout_desc != null)
                values.put("workout_desc", workout.workout_desc);

            if (workout.exercise_type.getValue() == 1)
            {
                values.put("workout_image", "exercise_display_run");
            }
            else if (workout.exercise_type.getValue() == 2)
            {
                values.put("workout_image", "exercise_display_bike");
            }
            else if (workout.exercise_type.getValue() == 4)
            {
                values.put("workout_image", "exercise_display_walk");
            }
            else if (workout.exercise_type.getValue() == 10)
            {
                values.put("workout_image", "exercise_display_swim");
            }
            else if (workout.exercise_type.getValue() == 35)
            {
                values.put("workout_image", "exercise_display_workout");
            }
            else if (workout.exercise_type.getValue() == -1)
            {
                values.put("workout_image", "exercise_display_steps");
            }
            else
            {
                values.put("workout_image", "exercise_display_other");
            }

            if (workout.exercise_state != null)
            {
                values.put("exercise_state", workout.exercise_state.getValue());
            }

            values.put("exercise_type", workout.exercise_type.getValue());

            values.put("command", workout.command);

            values.put("isChecked", (workout.isChecked == true ? 1 : 0)); //one for true 0 for false

            db.update(this.tableName, values, "activity_id = ?", new String[]{String.valueOf(workout.activity_id)});

        } catch (Exception ex) {
            status = false;
        } finally {
        }

        return status;
    }

    public Workout getWorkoutByActivityID(String activity_id)
    {
        Cursor cur = this.getAllWorkoutWithActivityID(activity_id);

        if (this.cursorHasRows(cur))
        {
            if (cur.moveToFirst())
            {
                do {
                    Workout workout = new Workout();

                    if (cur.getColumnIndex("activity_id") > -1)
                        workout.activity_id = (cur.getString(cur.getColumnIndex("activity_id")));

                    if (cur.getColumnIndex("coreData_id") > -1)
                        workout.coreData_id =  (cur.getString(cur.getColumnIndex("coreData_id")));

                    if (cur.getColumnIndex("exercise_datetime") > -1)
                        workout.exercise_datetime = Workout.setStringtoDate(cur.getString(cur.getColumnIndex("exercise_datetime")));

                    if (cur.getColumnIndex("exercise_date") > -1)
                        workout.exercise_date =  (cur.getString(cur.getColumnIndex("exercise_date")));

                    if (cur.getColumnIndex("device_name") > -1)
                        workout.device_name =  (cur.getString(cur.getColumnIndex("device_name")));

                    if (cur.getColumnIndex("duration") > -1)
                        workout.duration = cur.getInt(cur.getColumnIndex("duration"));

                    if (cur.getColumnIndex("steps") > -1)
                        workout.steps = cur.getInt(cur.getColumnIndex("steps"));

                    if (cur.getColumnIndex("calories") > -1)
                        workout.calories = cur.getInt(cur.getColumnIndex("calories"));

                    if (cur.getColumnIndex("distance") > -1)
                        workout.distance = cur.getDouble(cur.getColumnIndex("distance"));

                    if (cur.getColumnIndex("workout_desc") > -1)
                        workout.workout_desc =  (cur.getString(cur.getColumnIndex("workout_desc")));

                    if (cur.getColumnIndex("workout_image") > -1)
                        workout.workout_image =  (cur.getString(cur.getColumnIndex("workout_image")));

                    if (cur.getColumnIndex("duration") > -1)
                        workout.exercise_state = Workout.getSTATEValue(cur.getInt(cur.getColumnIndex("duration")));

                    if (cur.getColumnIndex("exercise_type") > -1)
                        workout.exercise_type = Workout.getEXERCISETYPEValue(cur.getInt(cur.getColumnIndex("exercise_type")));

                    if (cur.getColumnIndex("command") > -1)
                        workout.command =  (cur.getString(cur.getColumnIndex("command")));

                    if (cur.getColumnIndex("isChecked") > -1)
                        workout.isChecked = (cur.getInt(cur.getColumnIndex("isChecked"))) == 1 ? true : false; // (1 = true, 0 = false)

                    return workout; //return first workout fetch

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
