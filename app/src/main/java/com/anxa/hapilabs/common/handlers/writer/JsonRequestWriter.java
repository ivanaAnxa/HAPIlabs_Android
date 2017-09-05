package com.anxa.hapilabs.common.handlers.writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.SessionTracking;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Workout;

//for post request only
public class JsonRequestWriter {

    private static JsonRequestWriter instance;

    public static JsonRequestWriter getInstance() {
        if (instance == null)
            instance = new JsonRequestWriter();
        return instance;
    }

    /******
     * TODO: ADD INPUT FORMAT HERE input UserProfile Obj
     * <p>
     * output Json format request
     * <p>
     * {"user":{"user_profile":{"contact_number":"09175430863","birthday":
     * "1988-06-23","timezone":"China Standard Time","user_login":{"password":
     * "2a90091c37b4e340988fa91e53f1806333091320"
     * ,"username":"rj_24@hapitest.com"
     * },"email":"rj_24@hapitest.com","gender":"Male"
     * ,"lastname":"Luyun","language"
     * :"en","firstname":"Mon","country":"Philippines"
     * },"plan_profile":{"current_weight"
     * :"65000","target_weight":"61000","start_weight"
     * :"75000","height":"183","goal_index":"1"}}}
     *
     * @return
     */
    public String createRegistrationRequest(Context context,
                                            UserProfile userprofile) {

        JSONObject json = new JSONObject();

        try {

            JSONObject user = new JSONObject();
            JSONObject user_profile = new JSONObject();

            user_profile.put("email", userprofile.getEmail());
            user_profile.put("firstname", userprofile.getFirstname());
            user_profile.put("lastname", userprofile.getLastname());
            user_profile.put("birthday", userprofile.getBday());

            user_profile.put("gender", userprofile.getGender());

            user_profile.put("country", userprofile.getCountry());
            user_profile.put("language", userprofile.getLanguage());
            user_profile.put("contact_number", userprofile.getContact_number());
            user_profile.put("timezone", userprofile.getTimezone());
            user_profile.put("weight_unit", "kg"); // static value for now
            user_profile.put("height_unit", "cm");// static value for now


            JSONObject user_login = new JSONObject();

            user_login.put("username", userprofile.getUsername());
            user_login.put("password", AppUtil.shaHashed(userprofile.getPassword()));
            user_profile.put("user_login", user_login);

            user_profile.put("picture_url_large", userprofile.getPic_url_large());

            JSONObject plan_profile = new JSONObject();

            plan_profile.put("goal_type", userprofile.getGoals_index());
            plan_profile.put("height", userprofile.getHeight());
            plan_profile.put("start_weight", userprofile.getStart_weight());
            plan_profile.put("current_weight", userprofile.getCurrent_weight());
            plan_profile.put("target_weight", userprofile.getTarget_weight());
            plan_profile.put("eating_habits", userprofile.getEating_habits());

            user.put("user_profile", user_profile);
            user.put("plan_profile", plan_profile);

            json.put("user", user);

//            Log.i("reg", json.toString());

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /*******
     * input username , password(in hash)
     *
     * @return output Json format request { "username": "iamjenie@gmail.com",
     * "password": "4ca1eaebff939d70f11e17e768199fb652fc10dd" }
     */
    public String createLoginRequest(String username, String password, Boolean fbConnect) {

        JSONObject json = new JSONObject();
        try {

            json.put("username", username);
            if (fbConnect) {
                json.put("password", AppUtil.createSignature(password));
            } else {
                json.put("password", AppUtil.shaHashed(password));
            }

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

	/*	String data = "{ \"appVersion\": \"" + ver + "\", "
                    + "\"isNotificationEnabled\": true,\""
					+ "userFirstName\": \"" + fname + "\",\"" + "userId\": \""
					+ regID + "\",\"" + "userLastName\": \"" + lname + "\"}";
*/

    public String createPushRegRequest(String appVersion, Boolean isNotificationEnabled, String userFirstName, String regID, String userLastName) {

        JSONObject json = new JSONObject();
        try {

            json.put("appVersion", appVersion);
            json.put("isNotificationEnabled", isNotificationEnabled);
            json.put("userFirstName", userFirstName);
            json.put("userId", regID);
            json.put("userLastName", userLastName);

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /*******
     * input username
     *
     * @return output Json format request { "username": "iamjenie@gmail.com", }
     */
    public String createForgotPasswordRequest(String username) {
        JSONObject json = new JSONObject();
        try {

            json.put("username", username);
            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /******
     * TODO: ADD INPUT FORMAT HERE input UserProfile Obj
     * <p>
     * output Json format request
     * <p>
     * { "user_profile" : { "firstname": "Raymunfil", "lastname": "Luyun",
     * "birthday": "1988-06-23", "gender": "Male", "language": "en",
     * "contact_number": "09175430863", "timezone": "China Standard Time" },
     * "plan_profile" : { "goal_index": "2", "height": "183", "start_weight":
     * "75000", "current_weight": "64000", "target_weight": "61000" } } }
     */

    public String createUpdateProfileJson(Context context, UserProfile userprofile) {

        JSONObject json = new JSONObject();

        try {

            JSONObject user_profile = new JSONObject();

            user_profile.put("firstname", userprofile.getFirstname());
            user_profile.put("lastname", userprofile.getLastname());
            user_profile.put("birthday", userprofile.getBday());
//			user_profile.put("gender", userprofile.getGender());
            user_profile.put("country", userprofile.getCountry());
            user_profile.put("language", userprofile.getLanguage());
            user_profile.put("contact_number", userprofile.getContact_number());
            user_profile.put("timezone", userprofile.getTimezone());
            user_profile.put("picture_url_large", userprofile.getPic_url_large());
            user_profile.put("motivation_level", userprofile.getMotivation_level());
            user_profile.put("receive_newsletter", userprofile.getReceive_newsletter());
            user_profile.put("profession", userprofile.getProfession());
            user_profile.put("time_to_spend", userprofile.getTime_to_spend());
//			user_profile.put("gender", userprofile.getGender());
//
            JSONObject plan_profile = new JSONObject();

            plan_profile.put("has_answered_optin", userprofile.getHas_answered_optin());
            plan_profile.put("goal_type", userprofile.getGoals_index());
            plan_profile.put("answer", userprofile.getSurvey_answer());

            json.put("user_profile", user_profile);
            json.put("plan_profile", plan_profile);

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /******
     * TODO: ADD INPUT FORMAT HERE input List<Meal> Obj
     * <p>
     * output Json format request
     * <p>
     * { "upload": { "upload_count": "1", "meals": { "meal": { "command":
     * "Added", "timestamp_utc": "1400555088", "meal_creation_date":
     * "1400555088", "meal_type": "3", "foodgroups": { "foodgroup": [ {
     * "groupid": "1" }, { "groupid": "2" } ] }, "description": "My Meal",
     * "album": { "photos": { "photo": [ { "photo_id": "1", "url":
     * "http://img.hapilabs.com/mobile/1/418b7d22-6cef-4024-a6fb-4ac20e671ff5-large.png"
     * }, { "photo_id": "2", "url":
     * "http://img.hapilabs.com/mobile/1/418b7d22-6cef-4024-a6fb-4ac20e671ff5-large.png"
     * } ] }, "photo_count": "1" } } } } }
     */

    public String createUploadMealJson(List<Meal> meals) {

        JSONObject json = new JSONObject();

        try {

            JSONObject upload = new JSONObject();

            upload.put("upload_count", meals.size());
            JSONArray mealsArr = new JSONArray();

            for (int i = 0; i < meals.size(); i++) {
                Meal meal = meals.get(i);

                JSONObject mealJson = new JSONObject();

                mealJson.put("meal_type", meal.meal_type.ordinal() + 1);
                mealJson.put("command", "Added");
                mealJson.put("timestamp_utc", AppUtil.dateToUnixTimestamp(meal.timestamp));


                mealJson.put("meal_creation_date", ((AppUtil.getOffset(meal.meal_creation_date)).getTime() / 1000)); //no need to convert to utc

                //mealJson.put("meal_creation_date",(meal.meal_creation_date.getTime())); //no need to convert to unix this is just a string time


                mealJson.put("description", meal.meal_description);

                JSONObject foodgroupsObj = new JSONObject();
                JSONArray foodgroupsArr = new JSONArray();

                for (int f = 0; f < meal.food_group.size(); f++) {
                    JSONObject foodgroupObj = new JSONObject();
                    foodgroupObj.put("groupid", meal.food_group.get(f)
                            .ordinal() + 1);
                    foodgroupsArr.put(foodgroupObj);
                }

                // foodgroupsObj.put("foodgroup", foodgroupsArr);

                mealJson.put("foodgroup", foodgroupsArr);

                JSONObject album = new JSONObject();
                album.put("photo_count", meal.photos.size());

                JSONObject photosObj = new JSONObject();
                JSONArray photosArr = new JSONArray();
                for (int p = 0; p < meal.photos.size(); p++) {
                    JSONObject photoObj = new JSONObject();
                    photoObj.put("photo_id", meal.photos.get(p).photo_id);
                    photoObj.put("url", meal.photos.get(p).photo_url_large);
                    photosArr.put(photoObj);
                }

                // photosObj.put("photo", photosArr);

                album.put("photo", photosArr);

                mealJson.put("album", album);

                JSONObject mealObj = new JSONObject();
                mealObj.put("meal", mealJson);
                mealsArr.put(mealObj);
            }

            upload.put("meals", mealsArr);

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /******
     * TODO: ADD INPUT FORMAT HERE input Meal Obj
     * <p>
     * output Json format request
     * <p>
     * { "upload": { "upload_count": "1", "meals": { "meal": { "command":
     * "Added", "timestamp_utc": "1400555088", "meal_creation_date":
     * "1400555088", "meal_type": "3", "foodgroups": { "foodgroup": [ {
     * "groupid": "1" }, { "groupid": "2" } ] }, "description": "My Meal",
     * "album": { "photos": { "photo": [ { "photo_id": "1", "url":
     * "http://img.hapilabs.com/mobile/1/418b7d22-6cef-4024-a6fb-4ac20e671ff5-large.png"
     * }, { "photo_id": "2", "url":
     * "http://img.hapilabs.com/mobile/1/418b7d22-6cef-4024-a6fb-4ac20e671ff5-large.png"
     * } ] }, "photo_count": "1" } } } } }
     */

    public String createUploadMealJson(Meal meal, byte mealCommand) {

        JSONObject json = new JSONObject();

        try {
            /**
             {"meal":[{"meal_id":"87294349","command":"Deleted"}],"upload_count":"1"}

             We currently have 3 commands for post_meals: Added, Deleted, Updated
             */

            JSONArray mealsArr = new JSONArray();

            JSONObject mealJson = new JSONObject();


            if (mealCommand == Meal.MEALSTATE_EDIT)
                mealJson.put("command", "Updated");
            else if (mealCommand == Meal.MEALSTATE_DELETE)
                mealJson.put("command", "Deleted");
            else
                mealJson.put("command", "Added");

            if (mealCommand == Meal.MEALSTATE_DELETE) {
                mealJson.put("meal_id", meal.meal_id);

            } else {
                mealJson.put("meal_id", meal.meal_id);
                mealJson.put("meal_type", meal.meal_type.ordinal() + 1);

                mealJson.put("userRating", meal.userRating);
                mealJson.put("userRating_setting", ApplicationEx.getInstance().userRatingSetting);

                mealJson.put("timestamp_utc", AppUtil.dateToUnixTimestamp(meal.timestamp));

//                mealJson.put("meal_creation_date", ((meal.meal_creation_date)).getTime() / 1000); //no need to convert to utc
                mealJson.put("meal_creation_date", ((AppUtil.getOffset(meal.meal_creation_date)).getTime() / 1000)); //no need to convert to utc

                //mealJson.put("meal_creation_date",(meal.meal_creation_date.getTime()/1000)); //no need to convert to utc

                mealJson.put("description", meal.meal_description);

                JSONObject foodgroupsObj = new JSONObject();
                JSONArray foodgroupsArr = new JSONArray();

                for (int f = 0; f < meal.food_group.size(); f++) {
                    JSONObject foodgroupObj = new JSONObject();
                    foodgroupObj.put("groupid",
                            meal.food_group.get(f).ordinal() + 1);
                    foodgroupsArr.put(foodgroupObj);
                }

                // foodgroupsObj.put("foodgroup", foodgroupsArr);
                mealJson.put("foodgroup", foodgroupsArr);

                JSONObject album = new JSONObject();

                if (meal.photos != null)
                    album.put("photo_count", meal.photos.size());
                else
                    album.put("photo_count", 0);

                // JSONObject photosObj = new JSONObject();
                JSONArray photosArr = new JSONArray();

                if (meal.photos != null) {

                    for (int p = 0; p < meal.photos.size(); p++) {
                        JSONObject photoObj = new JSONObject();
                        photoObj.put("photo_id", meal.photos.get(p).photo_id);
                        photoObj.put("url", meal.photos.get(p).photo_url_large);
                        photosArr.put(photoObj);
                    }

                    // photosObj.put("photo", photosArr);
                    album.put("photo", photosArr);
                    mealJson.put("album", album);
                } else {
                    album.put("photo", photosArr);
                    mealJson.put("album", album);
                }

            }
            mealsArr.put(mealJson);

            JSONObject upload = new JSONObject();
            upload.put("upload_count", "1");
            upload.put("meal", mealsArr);


            return upload.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /******
     * TODO: ADD INPUT FORMAT HERE input Comment Obj
     * <p>
     * output Json format request
     * <p>
     * { "upload_comment": { "comment": { "comment_message": " Great!!! ",
     * "ishapi": "true", "comment_timestamp_utc": "0" } } }
     */

    public String createAddHapi4UJson(Comment comment) {

        //JSONObject json = new JSONObject();

        try {

            JSONObject commentObj = new JSONObject();
            commentObj.put("ishapi", comment.isHAPI.toString());
            JSONObject commentJson = new JSONObject();
            commentJson.put("comment", commentObj);
//			json.put("upload_comment", commentJson);

            return commentJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /******
     * TODO: ADD INPUT FORMAT HERE input SessionTracking Obj
     * <p>
     * output Json format request
     * <p>
     * <p>
     * {"log_details":"2014-10-14T05:38:57","event_id":1,"application_id":"105",
     * "event_value":90601}
     */

    public String createSessionTrackingJson(SessionTracking sessionTracking) {

        JSONObject json = new JSONObject();

        try {

            json.put("event_id", sessionTracking.event_id);
            json.put("application_id", sessionTracking.application_id);
            json.put("event_value", sessionTracking.event_value);
            json.put("log_details", sessionTracking.log_details);

            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /******
     * TODO: ADD INPUT FORMAT HERE input Comment Obj
     * <p>
     * output Json format request
     * <p>
     * { "upload_comment": { "comment": { "comment_message": " Great!!! ",
     * "ishapi": "true", "comment_timestamp_utc": "0" } } }
     */

    public String createCommentJson(Comment comment) {
        try {
            JSONObject commentObj = new JSONObject();
            commentObj.put("comment_message", comment.message);
            commentObj.put("ishapi", comment.isHAPI.toString());
            // commentObj.put("comment_timestamp_utc",
            // AppUtil.dateToUnixTimestamp(comment.timestamp));
            JSONObject commentJson = new JSONObject();
            commentJson.put("comment", commentObj);

            return commentJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String createCommentCommunityJson(Comment comment, String userid, String activityid) {
        JSONObject json = new JSONObject();
        try {

            JSONArray commentArr = new JSONArray();

            JSONObject jsonComment = new JSONObject();
            jsonComment.put("user_id", userid);
            jsonComment.put("activity_id", activityid);
            jsonComment.put("text", comment.message);
            jsonComment.put("date", AppUtil.dateToUnixTimestamp(comment.timestamp));

            commentArr.put(jsonComment);

            json.put("comment", commentArr);
            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String createHAPI4UJson(String userid, String activityid) {
        JSONObject json = new JSONObject();
        try {
            JSONArray commentArr = new JSONArray();

            JSONObject jsonHAPI4U = new JSONObject();
            jsonHAPI4U.put("user_id", userid);
            jsonHAPI4U.put("activity_id", activityid);

            commentArr.put(jsonHAPI4U);

            json.put("hapi4U", commentArr);
            json.put("activity_id", activityid);
            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /******
     * TODO: ADD INPUT FORMAT HERE input Message Obj
     * <p>
     * { "body": "Hello" }
     */

    public String createMessageJson(Message message) {

        try {

            JSONObject msgObj = new JSONObject();

            msgObj.put("body", message.message_body);

            return msgObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }

    /**
     * notification mark as read
     ***/

    public String createMarkAsReadJson(String notifId) {

        System.out.println("createMarkAsReadJson: " + notifId);

        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("notification_id", notifId);
            System.out.println("createMarkAsReadJson: 2 " + notifId);

            return msgObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * water json
     **/

    public String createAddWaterJson(Water water) {

        System.out.println("createAddWaterJson: " + water);

        try {
            JSONObject msgObj = new JSONObject();
            String number_glasses = water.getNumber_glasses();
            msgObj.put("number_glasses", water.getNumber_glasses());


            System.out.println("requestwriter water: inutc: " + AppUtil.getDateinGMT(water.getWater_datetime()));
            String string_date = Long.toString(AppUtil.dateToUnixTimestamp(water.getWater_datetime()));
            msgObj.put("water_datetime", string_date);

            JSONObject waterObj = new JSONObject();
            waterObj.put("water", msgObj);

            return waterObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String createAddHapimomentJson(HapiMoment hapiMoment) {

        System.out.println("createAddHapimomentJson: " + hapiMoment);

        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("moodValue", hapiMoment.moodValue);


            // String string_date = Long.toString(AppUtil.dateToUnixTimestamp(hapiMoment.mood_datetime));
            msgObj.put("mood_datetime", (AppUtil.getOffset(hapiMoment.mood_datetime)).getTime() / 1000);

            msgObj.put("description", hapiMoment.description);
            msgObj.put("command", hapiMoment.command);
            msgObj.put("mood_id", hapiMoment.mood_id);
            JSONObject album = new JSONObject();

            //temporary
            JSONObject photosObj = new JSONObject();
            JSONArray photosArr = new JSONArray();
            if (hapiMoment.photos != null && !hapiMoment.photos.isEmpty()) {
                for (int i = 0; i < hapiMoment.photos.size(); i++) {
                    photosObj = new JSONObject();
                    photosObj.put("photo_id", hapiMoment.photos.get(i).photo_id);
                    photosObj.put("photo_url_large", hapiMoment.photos.get(i).photo_url_large);
                    photosArr.put(photosObj);
                }
            } else {
                photosObj.put("photo_id", 0);
                photosArr.put(photosObj);
            }


            album.put("photo", photosArr);
            album.put("location", hapiMoment.location);
            msgObj.put("album", album);
            JSONObject hapimomentObj = new JSONObject();
            hapimomentObj.put("mood", msgObj);

            return hapimomentObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * workout json
     **/

    public String createExerciseJson(Workout workoutObj, String command) {
        System.out.println("createExerciseJson: " + workoutObj);

        try {
            JSONObject msgObj = new JSONObject();

            msgObj.put("command", command);

            msgObj.put("description", workoutObj.workout_desc);

            msgObj.put("distance", workoutObj.distance);

            msgObj.put("duration", workoutObj.duration);

            msgObj.put("exercise_datetime", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));

            msgObj.put("exercise_type", workoutObj.exercise_type.getValue());

            msgObj.put("steps", workoutObj.steps);

            JSONObject workoutJsonObj = new JSONObject();
            workoutJsonObj.put("workout", msgObj);

            System.out.println("workoutJsonObj: " + workoutJsonObj);

            return workoutJsonObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String editExerciseJson(Workout workoutObj, String command) {
        System.out.println("editExerciseJson: " + workoutObj);

        try {
            JSONObject msgObj = new JSONObject();

            msgObj.put("activity_id", workoutObj.activity_id);
            msgObj.put("client_id", workoutObj.coreData_id);
            msgObj.put("command", command);
            msgObj.put("description", workoutObj.workout_desc);
            msgObj.put("distance", workoutObj.distance);
            msgObj.put("duration", workoutObj.duration);
            msgObj.put("exercise_datetime", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));
            msgObj.put("exercise_type", workoutObj.exercise_type.getValue());
            msgObj.put("steps", workoutObj.steps);
            JSONObject workoutJsonObj = new JSONObject();
            workoutJsonObj.put("workout", msgObj);

            System.out.println("workoutJsonObj: " + workoutJsonObj);

            return workoutJsonObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * account settings json
     **/
    public String createAccountSettingsJson(int acctSettingsType) {

        System.out.println("createAccountSettingsJson: " + acctSettingsType);

        try {
            JSONArray settingsArr = new JSONArray();

            JSONObject msgObj = new JSONObject();
            msgObj.put("account_setting_type", "4");
            msgObj.put("rule", "HapiCoachMealUserRating");
            msgObj.put("value", acctSettingsType);
            settingsArr.put(msgObj);

            JSONObject acctSettingsObj = new JSONObject();
            acctSettingsObj.put("account_setting", settingsArr);

            return acctSettingsObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * send access code json
     **/
    public String createSendAccessCodeJson(String phoneNumber) {

        System.out.println("createSendAccessCodeJson: " + phoneNumber);

        try {
            JSONObject phoneNumberObj = new JSONObject();
            phoneNumberObj.put("phone", phoneNumber);

            return phoneNumberObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String createPostSurveyAnswerJson(String answer, String questionId) {
        JSONObject json = new JSONObject();
        try {

            JSONArray answerArr = new JSONArray();
            answerArr.put(answer);

            json.put("answer", answerArr);
            json.put("questionid", questionId);
            return json.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * userId: User id of the user
     * From: UTC date from in unix
     * To: UTC date to in unix
     **/
    public String createWeightGraphJson(String userId, String fromDate, String toDate) {
        System.out.println("createWeightGraphJson: " + fromDate);

        try {
            JSONObject weightGraphJson = new JSONObject();
            weightGraphJson.put("userId", userId);
            weightGraphJson.put("From", fromDate);
            weightGraphJson.put("To", toDate);

            return weightGraphJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    //    "weight_grams": 900,
//            "date": "1438776643000"
    public String createWeightDataJson(String weight, String date) {

        try {
            JSONObject weightGraphJson = new JSONObject();
            weightGraphJson.put("weight_grams", weight);

            //\/Date(1438776643000)\/
            weightGraphJson.put("date", date);
//            weightGraphJson.put("date", "/Date(" + date + ")/");

            return weightGraphJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String createStepsDataJson(String steps, String date) {
        try {
            JSONObject stepsGraphJson = new JSONObject();
            stepsGraphJson.put("total_steps", steps);
            stepsGraphJson.put("date", date);

            return stepsGraphJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String createRegisterDeviceJson(String UUID, String appName, String appVersion) {
        try {
            JSONObject registerDeviceJson = new JSONObject();
            registerDeviceJson.put("device_code", UUID);
            registerDeviceJson.put("device_type", 53); //GoogleFit
            registerDeviceJson.put("device_id", "1");
            registerDeviceJson.put("hardware_version", Build.BRAND + "/" + Build.MODEL);
            registerDeviceJson.put("name", appName);
            registerDeviceJson.put("software_version", appVersion);

            return registerDeviceJson.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String createStepsJson(Workout workoutObj) {
        System.out.println("createStepsJson: " + workoutObj);

        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("client_id", "");
            msgObj.put("start", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));
            msgObj.put("end", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));
            msgObj.put("total_calories", workoutObj.calories);
            msgObj.put("total_distance_meters", workoutObj.distance);
            msgObj.put("total_duration_seconds", workoutObj.duration*60);
            msgObj.put("raw_steps", "");
            msgObj.put("total_steps", workoutObj.steps);

            JSONArray stepsArr = new JSONArray();
            stepsArr.put(msgObj);

            JSONObject workoutJsonObj = new JSONObject();
            workoutJsonObj.put("steps", stepsArr);
            System.out.println("workoutJsonObj: " + workoutJsonObj);
            return workoutJsonObj.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String createStepsArrayJson(ArrayList<Workout> workoutObjArray) {
        System.out.println("createStepsJson: " + workoutObjArray);
        Log.e("History", "createStepsArrayJson");
        try {
            JSONArray stepsArr = new JSONArray();

            for (Workout workoutObj: workoutObjArray) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("client_id", "");
                msgObj.put("start", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));
                msgObj.put("end", Long.toString(AppUtil.dateToUnixTimestamp(workoutObj.exercise_datetime)));
                msgObj.put("total_calories", workoutObj.calories);
                msgObj.put("total_distance_meters", workoutObj.distance);
                msgObj.put("total_duration_seconds", workoutObj.duration * 60);
                msgObj.put("raw_steps", "");
                msgObj.put("total_steps", workoutObj.steps);

                stepsArr.put(msgObj);
            }

            JSONObject workoutJsonObj = new JSONObject();
            workoutJsonObj.put("steps", stepsArr);
            System.out.println("workoutJsonObj: " + workoutJsonObj);

            return workoutJsonObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /*

     {
       "application_id" = 105;
       "event_id" = 1;
       "event_value" = 4170;
       "log_details" = "2015-08-13T03:58:43";
    }

    REMARKS:


For application_id  106 for HAPIcoach Android

For application_userId   Hapicoach userid of the user that's logged in.

For event_id constant value of 1 for user time spent.

For event_value  time spent in millisecond from the time the app launches to the time it goes to background.

For log_details, special field to send user's time in current timezone, format to sortable time (i.e., 2014-04-12T18:31:58)



    */
    public String createAnxaMats(int appID, String application_userid, String eventid, int eventValue, Date timestamp) {

        try {

            JSONObject msgObj = new JSONObject();

            String str_time = DateFormat.format("yyyy-MM-ddThh:mm:ss", timestamp).toString();

            msgObj.put("application_id", appID);
            msgObj.put("application_userid", application_userid);
            msgObj.put("event_id", eventid);
            msgObj.put("event_value", eventValue);
            msgObj.put("log_details", str_time);

            return msgObj.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";

    }


}