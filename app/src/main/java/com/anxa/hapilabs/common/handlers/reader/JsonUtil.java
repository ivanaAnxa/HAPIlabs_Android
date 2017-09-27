package com.anxa.hapilabs.common.handlers.reader;

import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.CoachProgram;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.STATE;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_STATE;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_TYPE;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.SurveyOption;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Meal.FOODGROUP;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.UserProfile.GENDER;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;

public class JsonUtil {

    public static MessageObj getMessageObj(MessageObj.MESSAGE_TYPE type,
                                           JSONObject json) {

        MessageObj response = null;


        if (json.optString("message_detail") != null) {
            response = new MessageObj();
            response.setMessage_string(json.optString("message_detail"));
            response.setType(type);
        }
        return response;

    }

    public static MessageObj getMessageObj(MessageObj.MESSAGE_TYPE type,
                                           String message) {

        MessageObj response = new MessageObj();
        response.setMessage_string(message);
        response.setType(type);
        return response;

    }

    public static Coach getCoach(JSONObject json) {

        try {
            Coach coach = new Coach();

            //fi
            String coach_avatar = json.optString("avatar_url").toString();

            if (coach_avatar != null)
                coach.avatar_url = coach_avatar;

            String coach_fname = json.optString("firstname").toString();
            if (coach_fname != null)
                coach.firstname = coach_fname;

            String coach_lname = json.optString("lastname").toString();
            if (coach_lname != null)
                coach.lastname = coach_lname;

            String coach_id = json.optString("coach_id").toString();
            coach.coach_id = coach_id;

            // depending on the current language get either the
            // enlish or french profile
            if (json.has("en")) {
                JSONObject coachprofile_EN = json.getJSONObject("en");

                if (coachprofile_EN != null) {

                    String coach_about = coachprofile_EN.optString("about").toString();


                    if (coach_about != null)
                        coach.coach_profile_en = coach_about;

                    String coach_profile_en = coachprofile_EN.optString("profile")
                            .toString();

                    if (coach_profile_en != null)
                        coach.coach_profile_en = coach.coach_profile_en + coach_profile_en;

                    String coach_style_en = coachprofile_EN.optString("coachingstyle").toString();

                    if (coach_style_en != null)
                        coach.coach_style_en = coach_style_en;

                    String coach_title_en = coachprofile_EN.optString("title").toString();

                    if (coach_title_en != null)
                        coach.coach_title_en = coach_title_en;
                }
            }

            if (json.has("fr")) {
                JSONObject coachprofile_FR = json.getJSONObject("fr");
//                Log.i("coachprofile_FR", String.valueOf(coachprofile_FR));
                if (coachprofile_FR != null) {

                    String coach_about_fr = coachprofile_FR.optString("about")
                            .toString();

                    if (coach_about_fr != null)
                        coach.coach_profile_fr = coach_about_fr;

                    String coach_profile_fr = coachprofile_FR.optString("profile")
                            .toString();

                    if (coach_profile_fr != null)
                        coach.coach_profile_fr = coach.coach_profile_fr
                                + coach_profile_fr;

                    String coach_style_fr = coachprofile_FR.optString(
                            "coachingstyle").toString();

                    if (coach_style_fr != null)
                        coach.coach_style_fr = coach_style_fr;

                    String coach_title_fr = coachprofile_FR.optString("title")
                            .toString();

                    if (coach_title_fr != null)
                        coach.coach_title_fr = coach_title_fr;
                }

            }


            return coach;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static CoachProgram getCoachProgram(JSONObject json) {

        CoachProgram coachProgram = new CoachProgram();

        coachProgram.coachprogram_code = ((json.optString("code") == null) ? "" : json.optString("code"));
        coachProgram.coachprogram_id = json.optInt("coachprogram_id", 0);
        coachProgram.coachprogram_type = ((json.optString("coachprogram_type") == null) ? "" : json.optString("coachprogram_type"));
        coachProgram.coachprofile_id = json.optInt("coachprofile_id", 0);
        coachProgram.coachprogram_title = ((json.optString("title") == null) ? "" : json.optString("title"));
        coachProgram.teaser = ((json.optString("teaser") == null) ? "" : json.optString("teaser"));
        coachProgram.paymentstatus = ((json.optString("paymentstatus") == null) ? "" : json.optString("paymentstatus"));
        coachProgram.coachprogramregistrationtype = ((json.optString("coachprogramregistrationtype") == null) ? "" : json.optString("coachprogramregistrationtype"));
        coachProgram.startdate_utc = ((json.optString("startdate_utc") == null) ? "" : json.optString("startdate_utc"));
        coachProgram.enddate_utc = ((json.optString("enddate_utc") == null) ? "" : json.optString("enddate_utc"));
        coachProgram.isenabled = json.optBoolean("isenabled");
        coachProgram.haspromo = json.optBoolean("haspromo");
        coachProgram.coachprogram_index = json.optInt("index", 0);
        coachProgram.subscriptionDays = json.optInt("subscriptionDays", 0);
        coachProgram.brandId = json.optInt("brandId", 0);

        return coachProgram;
    }

    public static UserProfile getUserProfile(JSONObject user) {
        UserProfile userProfile = new UserProfile();
        // /******* Fetch node values **********/
        try {

            if (user == null)
                return null;


            //JSONObject user = data.getJSONObject("user");

            JSONObject plan_profile = user.getJSONObject("plan_profile");
            JSONObject user_profile = user.getJSONObject("user_profile");
            JSONObject user_login = user_profile.getJSONObject("user_login");


            if (plan_profile != null) {// start coach parsing

                JSONObject coachObj = plan_profile.optJSONObject("coach");


                if (coachObj != null) {
                    userProfile.setCoach(getCoach(coachObj));
                } else {
                }
                String current_weight = plan_profile
                        .optString("current_weight").toString();
                if (current_weight != null)
                    userProfile.setCurrent_weight(current_weight);

                String eating_habits = plan_profile.optString("eating_habits")
                        .toString();
                if (eating_habits != null)
                    userProfile.setEating_habits(eating_habits);

                userProfile.setEating_habits_index(1);

                Boolean hasAnsweredQuestionaire = plan_profile.optBoolean("has_answered_questionnaire");
                //may need to remove this direct value here
                ApplicationEx.getInstance().hasWelcome = hasAnsweredQuestionaire;

                //TODO: eating habits indes
                String gt = plan_profile.optString("goal_type").toString();
                if (gt != "null") {
                    int goal_type = 1;//Integer.parseInt(gt);
                    userProfile.setGoals_index(goal_type);
                }


                String height = plan_profile.optString("height").toString();
                if (height != null)
                    userProfile.setHeight(height);

                // TODO set to enum
                int membership = 0;
                membership = Integer.parseInt(plan_profile.optString("membership").toString());
                userProfile.setMember_type(UserProfile.setMemberType(membership));


                String membership_expiry_utc = plan_profile.optString(
                        "membership_expiry_utc").toString();
                if (membership_expiry_utc != null)
                    userProfile.setMember_expiry(membership_expiry_utc);

                String start_weight = plan_profile.optString("start_weight")
                        .toString();
                if (start_weight != null)
                    userProfile.setStart_weight(start_weight);

                String target_weight = plan_profile.optString("target_weight")
                        .toString();
                if (target_weight != null)
                    userProfile.setTarget_weight(target_weight);

            }// plan profile

            // start user_profile
            if (user_profile != null) {
                String pic_url = user_profile.optString("picture_url_large").toString();
                if (pic_url != null)
                    userProfile.setPic_url_large(pic_url);

                // TODO: settodate format
                String birthday = user_profile.optString("birthday").toString();
                if (birthday != null)
                    userProfile.setBday(birthday);

                String contact_number = user_profile
                        .optString("contact_number").toString();
                if (contact_number != null)
                    userProfile.setContact_number(contact_number);

                // TODO; set country code
                String country = user_profile.optString("country").toString();
                if (country != null)
//					userProfile.setCountry_code(country);
                    userProfile.setCountry(country);
                // TODO: settodate format

                try {
                    String date_joined_utc = user_profile.optString("date_joined_utc").toString();
                    if (date_joined_utc != null)
                        userProfile.setDate_joined(AppUtil.stringToDateFormat(date_joined_utc, ApplicationEx.getInstance().defaultTimeFormat));
                } catch (Exception e) {

                }

                String email = user_profile.optString("email").toString();
                if (email != null)
                    userProfile.setEmail(email);

                String firstname = user_profile.optString("firstname")
                        .toString();
                if (firstname != null)
                    userProfile.setFirstname(firstname);

                // TODO: SET gender to enum

                String gender = user_profile.optString("gender").toString().toLowerCase(Locale.ENGLISH);
//				if (gender !=null ){
//					if (gender.compareTo("female") ==0){
//						userProfile.setGender(GENDER.FEMALE);
//					} else if (gender.compareTo("male") == 0){
//						userProfile.setGender(GENDER.MALE);
//
//					}
//				}


                // TODO set language to language code
                String language = user_profile.optString("language").toString();
                if (language != null)
                    userProfile.setLanguage(language);

                String lastname = user_profile.optString("lastname").toString();
                if (lastname != null)
                    userProfile.setLastname(lastname);

                // TODO get timezone from phone setting instead
                String timezone = user_profile.optString("timezone").toString();
                if (timezone != null)
                    userProfile.setTimezone(timezone);

                String user_id = user_profile.optString("user_id").toString();
                if (user_id != null) {
                    userProfile.setRegID(user_id);
                    ApplicationEx.sharedUserId = user_id;
                } else {
                    ApplicationEx.sharedUserId = "";
                }

            }

            // start user login
            if (user_login != null) {
                String passwordHash = user_login.optString("password").toString();
                if (passwordHash != null)
                    userProfile.setPassword(passwordHash);
                String username = user_login.optString("username").toString();
                if (username != null)
                    userProfile.setUsername(username);

            }

            //JsonUtil.log(userProfile);
            return userProfile;

        } catch (JSONException e) {

            return null;
        }
    }

    public static Meal getMeal(JSONObject json, long offsetInMillis) {
        Meal meal = new Meal();
        meal.meal_id = json.optString("meal_id");
        //set a default meal state
        meal.state = STATE.SYNC;
        if (json.has("meal_type")) {
            Integer meal_type = json.optInt("meal_type");
            if (meal_type != null) {
                try {
                    meal.meal_type = MEAL_TYPE.getMealType(meal_type);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        meal.meal_description = json.optString("description");
        meal.haspaidSubcription = json.optBoolean("haspaidsubscription");

        Long timestamp_utc = json.optLong("timestamp_utc");
        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        meal.timestamp = AppUtil.toDate(timestamp_utc);

        Long meal_creation_date = json.optLong("meal_creation_date");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(meal_creation_date * 1000);
        meal.meal_creation_date = AppUtil.getOffsetOnSync(calendar1.getTime());
        meal.meal_status = json.optString("status");
        meal.isApproved = json.optBoolean("isapproved");
        meal.isCommented = json.optBoolean("iscommented");

        if (json.has("coachRating")) {
            meal.coachRating = json.optInt("coachRating");
        }

        if (json.has("userRating")) {
            meal.userRating = json.optInt("userRating");
        }

        JSONObject album;
        try {
            try {
                album = json.getJSONObject("album");
                Object photosObj = new JSONTokener(album.getString("photo")).nextValue();
                if (photosObj instanceof JSONObject) {
                    JSONObject photosJson = album.getJSONObject("photo");
                    JSONObject photoJson = photosJson.getJSONObject("photo");
                    meal.photos = new ArrayList<Photo>();
                    meal.photos.add(getPhoto(photoJson));
                } else if (photosObj instanceof JSONArray) {
                    JSONArray photosArr = album.optJSONArray("photo");
                    if (photosArr != null && photosArr.length() > 0) {
                        meal.photos = new ArrayList<Photo>();
                        for (int i = 0; i < photosArr.length(); i++) {
                            JSONObject photoJson = photosArr.getJSONObject(i);
                            meal.photos.add(getPhoto(photoJson));
                        }
                    }
                }
            } catch (Exception e) {
            }
            try {
                Object foodgroupsObj = new JSONTokener(json.getString("foodgroup")).nextValue();

                if (foodgroupsObj instanceof JSONObject) {
                    JSONObject foodgroupsJson = (JSONObject) foodgroupsObj;
                    Integer groupid = foodgroupsJson.optInt("groupid");
                    meal.food_group = new ArrayList<FOODGROUP>();
                    meal.food_group.add(FOODGROUP.getFoodGroup(groupid));
                } else if (foodgroupsObj instanceof JSONArray) {
                    JSONArray foodgroupsArr = (JSONArray) foodgroupsObj;
                    meal.food_group = new ArrayList<FOODGROUP>();
                    for (int i = 0; i < foodgroupsArr.length(); i++) {
                        JSONObject foodgroup;
                        try {
                            foodgroup = foodgroupsArr.getJSONObject(i);
                            Integer groupid = foodgroup.optInt("groupid");
                            meal.food_group.add(FOODGROUP.getFoodGroup(groupid));
                        } catch (JSONException e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
            if (json.has("commentgroup")) {
                JSONObject commentGroupObj = json.getJSONObject("commentgroup");
                if (commentGroupObj != null && commentGroupObj.length() > 0) {
                    meal.commentsCount = commentGroupObj.optInt("comment_count");
                    Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                    if (commentsObj instanceof JSONArray) {
                        JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                        meal.comments = new ArrayList<Comment>();

                        for (int i = 0; i < commentArr.length(); i++) {
                            JSONObject commentgroup = commentArr.getJSONObject(i);
                            meal.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                    Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                    if (hapi4uObj instanceof JSONArray) {
                        JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                        meal.hapi4Us = new ArrayList<HAPI4U>();

                        for (int i = 0; i < hapi4uArr.length(); i++) {
                            JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                            meal.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return meal;
    }

    public static Water getWater(JSONObject json) {

        Water water = new Water();

        water.water_id = json.optString("water_id");
        water.number_glasses = json.optString("number_glasses");
        water.isChecked = json.optBoolean("isChecked");

        Long timestamp_utc = json.optLong("water_datetime");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(timestamp_utc * 1000);
        water.water_datetime = AppUtil.getOffsetOnSync(calendar1.getTime());

        try{
            if (json.has("commentgroup")) {
                JSONObject commentGroupObj = json.getJSONObject("commentgroup");
                if (commentGroupObj != null && commentGroupObj.length() > 0) {
                    Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                    if (commentsObj instanceof JSONArray) {
                        JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                        water.comments = new ArrayList<Comment>();

                        for (int i = 0; i < commentArr.length(); i++) {
                            JSONObject commentgroup = commentArr.getJSONObject(i);
                            water.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                    Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                    if (hapi4uObj instanceof JSONArray) {
                        JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                        water.hapi4Us = new ArrayList<HAPI4U>();

                        for (int i = 0; i < hapi4uArr.length(); i++) {
                            JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                            water.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                }
            }
        } catch (Exception e) {

        }

        return water;
    }

    public static HapiMoment getHapiMoment(JSONObject json) {

        HapiMoment hapiMoment = new HapiMoment();
        hapiMoment.moodValue = json.optInt("moodValue");
        hapiMoment.description = json.optString("description");
        hapiMoment.mood_id = json.optInt("mood_id");
        hapiMoment.isChecked = json.optBoolean("isChecked", false);
        Long timestamp_utc = json.optLong("mood_datetime");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        hapiMoment.mood_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        JSONObject album;
        try {
            try {
                album = json.getJSONObject("album");
                hapiMoment.location = album.getString("location");

                Object photosObj = new JSONTokener(album.getString("photo"))
                        .nextValue();

                if (photosObj instanceof JSONObject) {
                    JSONObject photosJson = album.getJSONObject("photo");
                    JSONObject photoJson = photosJson.getJSONObject("photo");
                    hapiMoment.photos = new ArrayList<Photo>();
                    hapiMoment.photos.add(getPhoto(photoJson));
                } else if (photosObj instanceof JSONArray) {
                    JSONArray photosArr = album.optJSONArray("photo");
                    if (photosArr != null && photosArr.length() > 0) {
                        hapiMoment.photos = new ArrayList<Photo>();
                        for (int i = 0; i < photosArr.length(); i++) {
                            JSONObject photoJson = photosArr.getJSONObject(i);
                            hapiMoment.photos.add(getPhoto(photoJson));

                        }
                    }
                }

                if (json.has("commentgroup")) {
                    JSONObject commentGroupObj = json.getJSONObject("commentgroup");
                    if (commentGroupObj != null && commentGroupObj.length() > 0) {
                        Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                        if (commentsObj instanceof JSONArray) {
                            JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                            hapiMoment.comments = new ArrayList<Comment>();

                            for (int i = 0; i < commentArr.length(); i++) {
                                JSONObject commentgroup = commentArr.getJSONObject(i);
                                hapiMoment.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                            }
                        }

                        Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                        if (hapi4uObj instanceof JSONArray) {
                            JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                            hapiMoment.hapi4Us = new ArrayList<HAPI4U>();

                            for (int i = 0; i < hapi4uArr.length(); i++) {
                                JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                                hapiMoment.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                            }
                        }

                    }
                }


            } catch (Exception e) {

            }
        } catch (Exception e) {

        }
        return hapiMoment;
    }

    public static Workout getWorkout(JSONObject json) {
        Workout workoutObj = new Workout();

        workoutObj.activity_id = json.optString("activity_id");
        workoutObj.coreData_id = json.optString("client_id");
        workoutObj.calories = json.optInt("calories");
        workoutObj.workout_desc = json.optString("description");
        workoutObj.device_name = json.optString("device_name");
        workoutObj.distance = json.optDouble("distance") / 1000;
        workoutObj.duration = json.optInt("duration");
        workoutObj.dateString = json.optString("exercise_datetime");

        Long timestamp_utc = json.optLong("exercise_datetime");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(timestamp_utc * 1000);
        workoutObj.exercise_datetime = AppUtil.getOffsetOnSync(calendar1.getTime());

        workoutObj.exercise_date = new SimpleDateFormat("yyyy-MM-dd").format(AppUtil.getOffsetOnSync(calendar1.getTime()));

        Integer exercise_type = json.optInt("exercise_type");

        if (exercise_type != null) {
            try {
                workoutObj.exercise_type = Workout.EXERCISE_TYPE.getExerciseType(exercise_type);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (workoutObj.exercise_type.getValue() == 1) {
            workoutObj.workout_image = "exercise_display_run";
        } else if (workoutObj.exercise_type.getValue() == 2) {
            workoutObj.workout_image = "exercise_display_bike";
        } else if (workoutObj.exercise_type.getValue() == 4) {
            workoutObj.workout_image = "exercise_display_walk";
        } else if (workoutObj.exercise_type.getValue() == 10) {
            workoutObj.workout_image = "exercise_display_swim";
        } else if (workoutObj.exercise_type.getValue() == 35) {
            workoutObj.workout_image = "exercise_display_workout";
        } else if (workoutObj.exercise_type.getValue() == -1) {
            workoutObj.workout_image = "exercise_display_steps";
        } else {
            workoutObj.workout_image = "exercise_display_other";
        }

        workoutObj.isChecked = json.optBoolean("isChecked");
        workoutObj.steps = json.optInt("steps");

        try{
            if (json.has("commentgroup")) {
                JSONObject commentGroupObj = json.getJSONObject("commentgroup");
                if (commentGroupObj != null && commentGroupObj.length() > 0) {
                    Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                    if (commentsObj instanceof JSONArray) {
                        JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                        workoutObj.comments = new ArrayList<Comment>();

                        for (int i = 0; i < commentArr.length(); i++) {
                            JSONObject commentgroup = commentArr.getJSONObject(i);
                            workoutObj.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                    Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                    if (hapi4uObj instanceof JSONArray) {
                        JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                        workoutObj.hapi4Us = new ArrayList<HAPI4U>();

                        for (int i = 0; i < hapi4uArr.length(); i++) {
                            JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                            workoutObj.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                }
            }
        } catch (Exception e) {
            workoutObj.hapi4Us = new ArrayList<HAPI4U>();
            workoutObj.comments = new ArrayList<Comment>();
        }
        return workoutObj;
    }

    public static Weight getWeight(JSONObject json) {
        Weight weightObj = new Weight();

        weightObj.activity_id = json.optString("activity_id");
        weightObj.device_name = json.optString("device_name");
        weightObj.graph_values_type = json.optString("graph_value_type");
        weightObj.isChecked = json.optBoolean("isChecked", false);
        Long timestamp_utc = json.optLong("start_datetime");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        weightObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        timestamp_utc = json.optLong("end_datetime");

        //convert timestamp  based on user timezone
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        weightObj.end_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        weightObj.isDeleted = json.optBoolean("is_deleted");
        try {
            JSONArray graph_value = json.getJSONArray("graph_value");

            for (int index = 0; index < graph_value.length(); index++) {
                try {
                    JSONObject value_json = graph_value.getJSONObject(index);
                    if (value_json.optString("value_type").equalsIgnoreCase("CurrentWeightGrams")) {
                        weightObj.currentWeightGrams = value_json.optDouble("value");
                    } else if (value_json.optString("value_type").equalsIgnoreCase("BMI")) {
                        weightObj.BMI = value_json.optDouble("value");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weightObj;
    }

    public static Steps getSteps(JSONObject json) {
        Steps stepsObj = new Steps();

        stepsObj.activity_id = json.optString("activity_id");
        stepsObj.device_name = json.optString("device_name");
        stepsObj.graph_value_type = json.optString("graph_value_type");

        Long timestamp_utc = json.optLong("start_datetime");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        stepsObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        timestamp_utc = json.optLong("end_datetime");

        //convert timestamp  based on user timezone
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        stepsObj.end_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        stepsObj.isDeleted = json.optBoolean("is_deleted");
        stepsObj.isChecked = json.optBoolean("isChecked",false);

        try {
            JSONArray graph_value = json.getJSONArray("graph_value");
            for (int index = 0; index < graph_value.length(); index++) {
                try {
                    JSONObject value_json = graph_value.getJSONObject(index);
                    if (value_json.optString("value_type").equalsIgnoreCase("Steps")) {
                        stepsObj.steps_count = value_json.optString("value");
                    } else if (value_json.optString("value_type").equalsIgnoreCase("Distance")) {
                        stepsObj.steps_distance = value_json.optDouble("value");
                    } else if (value_json.optString("value_type").equalsIgnoreCase("Calories")) {
                        stepsObj.steps_calories = value_json.optDouble("value");
                    } else if (value_json.optString("value_type").equalsIgnoreCase("Duration")) {
                        stepsObj.steps_duration = value_json.optDouble("value");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stepsObj;
    }

    public static Weight getWeightView(JSONObject json) {
        Weight weightObj = new Weight();

        weightObj.activity_id = json.optString("activity_id");
        weightObj.device_name = json.optString("device_name");
        weightObj.isChecked = json.optBoolean("isChecked", false);
        Long timestamp_utc = json.optLong("activity_date");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        weightObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());
        weightObj.currentWeightGrams = json.optDouble("weight");
        weightObj.BodyFatRatio = json.optDouble("body_fat_ratio", 0);
        weightObj.BodyWaterRatio = json.optDouble("body_water_ratio", 0);
        weightObj.BoneWeightGrams = json.optDouble("bone_weight_grams", 0);
        weightObj.LeanMassRatio = json.optDouble("lean_mass_ratio", 0);
        weightObj.BMI = json.optDouble("bmi", 0);
   try
   {
        if (json.has("comment_group")) {
            JSONObject commentGroupObj = json.getJSONObject("comment_group");
            if (commentGroupObj != null && commentGroupObj.length() > 0) {
                Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                if (commentsObj instanceof JSONArray) {
                    JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                    weightObj.comments = new ArrayList<Comment>();

                    for (int i = 0; i < commentArr.length(); i++) {
                        JSONObject commentgroup = commentArr.getJSONObject(i);
                        weightObj.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                    }
                }

                Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                if (hapi4uObj instanceof JSONArray) {
                    JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                    weightObj.hapi4Us = new ArrayList<HAPI4U>();

                    for (int i = 0; i < hapi4uArr.length(); i++) {
                        JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                        weightObj.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                    }
                }

            }
        }
   } catch (JSONException e) {
       e.printStackTrace();
   }
        return weightObj;
    }

    public static Steps getStepsView(JSONObject json) {
        Steps stepsObj = new Steps();

        stepsObj.activity_id = json.optString("activity_id");
        stepsObj.device_name = json.optString("device_name");

        Long timestamp_utc = json.optLong("activity_date");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        stepsObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());
        stepsObj.steps_count = String.valueOf(json.optInt("steps", 0));
        stepsObj.steps_duration = json.optDouble("duration", 0);
        stepsObj.steps_distance = json.optDouble("distance", 0);
        stepsObj.steps_calories = json.optDouble("calories", 0);
        stepsObj.isChecked = json.optBoolean("isChecked",false);

        try
        {
            if (json.has("comment_group")) {
                JSONObject commentGroupObj = json.getJSONObject("comment_group");
                if (commentGroupObj != null && commentGroupObj.length() > 0) {
                    Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                    if (commentsObj instanceof JSONArray) {
                        JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                        stepsObj.comments = new ArrayList<Comment>();

                        for (int i = 0; i < commentArr.length(); i++) {
                            JSONObject commentgroup = commentArr.getJSONObject(i);
                            stepsObj.comments.add(getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                    Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();
                    if (hapi4uObj instanceof JSONArray) {
                        JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                        stepsObj.hapi4Us = new ArrayList<HAPI4U>();

                        for (int i = 0; i < hapi4uArr.length(); i++) {
                            JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                            stepsObj.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stepsObj;
    }


    public static Steps getStepsTimelineActivity(JSONObject json) {
        Steps stepsObj = new Steps();

        stepsObj.activity_id = json.optString("activity_id");
        stepsObj.device_name = json.optString("device_name");
        stepsObj.steps_duration = json.optDouble("duration");
        stepsObj.steps_distance = json.optDouble("distance");
        stepsObj.steps_count = json.optString("steps");
        stepsObj.steps_calories = json.optDouble("calories");

        Long timestamp_utc = json.optLong("activity_date");

        //convert timestamp  based on user timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        stepsObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        stepsObj.isDeleted = json.optBoolean("is_deleted");
        stepsObj.isChecked = json.optBoolean("isChecked",false);

        return stepsObj;
    }
    public static Weight getWeightTimelineActivity(JSONObject json) {
        Weight weightObj = new Weight();

        weightObj.activity_id = json.optString("activity_id");
        weightObj.device_name = json.optString("device_name");
        weightObj.currentWeightGrams = json.optDouble("weight");
        weightObj.BodyFatRatio = json.optDouble("body_fat_ratio", 0);
        weightObj.BodyWaterRatio = json.optDouble("body_water_ratio", 0);
        weightObj.BoneWeightGrams = json.optDouble("bone_weight_grams", 0);
        weightObj.LeanMassRatio = json.optDouble("lean_mass_ratio", 0);
        weightObj.BMI = json.optDouble("bmi", 0);
        weightObj.isChecked = json.optBoolean("isChecked", false);
        Long timestamp_utc = json.optLong("activity_date");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp_utc * 1000);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        weightObj.start_datetime = AppUtil.getOffsetOnSync(calendar.getTime());

        return weightObj;
    }
    public static Photo getPhoto(JSONObject json, String tempID) {

        Photo photo = new Photo();

        photo.photo_id = json.optString("photo_id");
        photo.photo_url_large = json.optString("url");
        photo.tempId = tempID;
        return photo;

    }

    public static GraphMeal getGraphMeal(JSONObject json) {

        GraphMeal graphMeal = new GraphMeal();

        String strDate = json.optString("date");

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            graphMeal.date = format.parse(strDate);
//            System.out.println(graphMeal.date);
            graphMeal.mealcount = json.optInt("posted_count");

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            graphMeal = null;
        }

        return graphMeal;
    }

    public static Photo getPhoto(JSONObject json) {

        Photo photo = new Photo();
        photo.photo_id = json.optString("photo_id");
        photo.photo_url_large = json.optString("url");

        return photo;

    }

    public static Notification getNotification(JSONObject json) {
        Notification notification = new Notification();
        notification.coachAvatarURL = json.optString("avatar_url");
        notification.coachID = json.optInt("coach_id");

        if (json.optBoolean("is_read") == false)
            notification.notificationState = NOTIFICATION_STATE.UNREAD;
        else
            notification.notificationState = NOTIFICATION_STATE.READ;

        notification.is_community = json.optBoolean("is_community");

        notification.notificationID = json.optInt("notification_id");
        notification.coachMessage = json.optString("notification_message");
        try {
            notification.notificationType = NOTIFICATION_TYPE.values()[json.optInt("notification_type") - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("ArrayIndexOutOfBoundsException notificationType: " + json.optInt("notification_type"));
            return null;
        }
        notification.ref_id = json.optString("ref_id");
        notification.ref_type = json.optString("ref_type");

        Long timestamp_utc = json.optLong("notification_timestamp_utc");
        notification.timestamp = AppUtil.toDate(timestamp_utc);

        return notification;
    }

    public static Comment getComment(JSONObject json, Comment.STATUS status) {
        Comment comment = new Comment();
        comment.status = status;
        comment.comment_id = json.optString("comment_id");
        comment.message = json.optString("comment_message");
        comment.comment_type = json.optInt("comment_type");
        comment.isHAPI = json.optBoolean("ishapi");

        Long comment_timestamp_utc = json.optLong("comment_timestamp_utc");

        comment.timestamp = AppUtil.toDate(comment_timestamp_utc);

        if (json.has("coach")) {
            JSONObject commentcoach = json.optJSONObject("coach");
            Coach coach = new Coach();
            coach.avatar_url = commentcoach.optString("avatar_url");
            coach.coach_id = commentcoach.optString("coach_id");
            coach.firstname = commentcoach.optString("firstname");
            coach.lastname = commentcoach.optString("lastname");
            comment.coach = coach;
        }

        if (json.has("user")) {
            JSONObject commentUser = json.optJSONObject("user");
            CommentUser user = new CommentUser();
            user.user_id = commentUser.optString("user_id");
            user.firstname = commentUser.optString("firstname");
            user.lastname = commentUser.optString("lastname");
            user.email = commentUser.optString("email");
            user.picture_url_large = commentUser.optString("picture_url_large");
            comment.user = user;
        }

        return comment;
    }

    public static HAPI4U getHAPI4U(JSONObject json, Comment.STATUS status) {
        HAPI4U hapi4U = new HAPI4U();
        hapi4U.hapi4u_id = json.optString("hapi4u_id");
        hapi4U.activity_id = json.optString("activity_id");
        hapi4U.userid = json.optString("user_id");

        if (json.has("user")) {
            JSONObject commentUser = json.optJSONObject("user");
            CommentUser user = new CommentUser();
            user.user_id = commentUser.optString("user_id");
            user.firstname = commentUser.optString("firstname");
            user.lastname = commentUser.optString("lastname");
            user.email = commentUser.optString("email");
            user.picture_url_large = commentUser.optString("picture_url_large");
            hapi4U.user = user;
        }

        return hapi4U;
    }

    public static Comment getComment(JSONObject json) {

        Comment comment = new Comment();
        comment.comment_id = json.optString("comment_id");
        comment.message = json.optString("comment_message");
        comment.comment_type = json.optInt("comment_type");
        comment.isHAPI = json.optBoolean("ishapi");

        Long comment_timestamp_utc = json.optLong("comment_timestamp_utc");
        comment.timestamp = AppUtil.toDate(comment_timestamp_utc);

        if (json.has("coach")) {
            JSONObject commentcoach = json.optJSONObject("coach");
            Coach coach = new Coach();
            coach.avatar_url = commentcoach.optString("avatar_url");
            coach.coach_id = commentcoach.optString("coach_id");
            coach.firstname = commentcoach.optString("firstname");
            coach.lastname = commentcoach.optString("lastname");
            comment.coach = coach;
        }

        return comment;
    }

    public static Message getMessage(JSONObject json) {

        Message message = new Message();

        message.message_body = json.optString("body");
        message.message_id = json.optString("message_id");
        JSONObject coachObj = json.optJSONObject("coach");

        if (coachObj != null) {
            message.coachID = coachObj.optString("coach_id");
            message.coach = new Coach();
            message.coach.avatar_url = coachObj.optString("avatar_url");
            message.coach.coach_id = coachObj.optString("coach_id");
            message.coach.firstname = coachObj.optString("firstname");
            message.coach.lastname = coachObj.optString("lastname");
        }

        message.timestamp = AppUtil.toDate(json.optLong("message_timestamp_utc"));
        message.is_coach_message = json.optBoolean("is_coach_message");
        message.mediaUrl = json.optString("mediaUrl");

        return message;
    }

    public static SurveyOption getSurveyQuestions(JSONObject json) {

        SurveyOption option = new SurveyOption();

        option.option_displaytext = json.optString("displaytext");
        option.option_id = json.optString("optionid");
        option.option_index = json.optInt("index");
        option.option_text = json.optString("text");

        return option;
    }

    public static void log(Meal meal, String Tag) {
//		System.out.println(Tag+"syncSuccess: "+"MEAL "+meal.meal_id);
//		System.out.println(Tag+"syncSuccess: CREATION DATE: "+meal.meal_creation_date);
//		System.out.println(Tag+"syncSuccess: TIMESTAMP: "+meal.timestamp);
//		System.out.println(Tag+"syncSuccess: "+"###################");

    }

}
