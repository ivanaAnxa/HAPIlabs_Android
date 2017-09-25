package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.TimelineActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by aprilanxa on 08/03/2017.
 */

public class JsonTimelineActivityResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;
    protected boolean isError = false;
    String OutputData = "";
    String strJson;
    JSONObject jsonResponse;
    Message msg = new Message();


    public JsonTimelineActivityResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
    }


    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {
        try {
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);
            jsonResponse = new JSONObject(strJson);

            System.out.println("JsonTimelineActivityResponseHandler: " + jsonResponse);
            if (jsonResponse.has("api_response")) {
                JSONObject api_response = jsonResponse.getJSONObject("api_response");
                String requestStatus = api_response.optString("status");

                // for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {

                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);

                    return;
                }
                JSONArray timeline_activity_array = jsonResponse.getJSONArray("timeline_activity");
                JSONObject timeline_activity_main = timeline_activity_array.getJSONObject(0);

                TimelineActivity timelineActivity = new TimelineActivity();
                timelineActivity.activity_id = timeline_activity_main.optString("activity_id");
                timelineActivity.user_id = timeline_activity_main.optString("user_id");
                timelineActivity.activitytypeid = timeline_activity_main.optString("activitytypeid");
                timelineActivity.activity_date = timeline_activity_main.optString("activity_date");


                JSONArray commentJson = timeline_activity_main.getJSONArray("comment");
                timelineActivity.comments = new ArrayList<Comment>();

                if (commentJson != null && commentJson.length() > 0) {
                    System.out.println("JsonTimelineActivityResponseHandler: " + commentJson);
                    timelineActivity.comments = new ArrayList<Comment>();

                    for (int i = 0; i < commentJson.length(); i++) {
                        JSONObject commentgroup = commentJson.getJSONObject(i);
                        timelineActivity.comments.add(JsonUtil.getComment(commentgroup, Comment.STATUS.SYNC_COMMENT));
                    }
                }

                System.out.println("JsonTimelineActivityResponseHandler comments: " + timelineActivity.comments);

                JSONArray hapi4UJson = timeline_activity_main.getJSONArray("hapi4u");
                timelineActivity.hapi4Us = new ArrayList<HAPI4U>();

                System.out.println("hapi4uJson: " + hapi4UJson);
                if (hapi4UJson != null && hapi4UJson.length() > 0) {

                    for (int i = 0; i < hapi4UJson.length(); i++) {
                        JSONObject commentgroup = hapi4UJson.getJSONObject(i);
                        timelineActivity.hapi4Us.add(JsonUtil.getHAPI4U(commentgroup, Comment.STATUS.SYNC_COMMENT));
                    }
                }

                if (JsonUtil.getMeal(timeline_activity_main.getJSONObject("meal"), 0) != null)
                    timelineActivity.meal = JsonUtil.getMeal(timeline_activity_main.getJSONObject("meal"), 0);

                if (timeline_activity_main.has("mood")) {
                    if (timeline_activity_main.get("mood").equals(null) || (timeline_activity_main.get("mood").toString().equalsIgnoreCase("null"))) {
                    } else {
                        if (JsonUtil.getHapiMoment(timeline_activity_main.getJSONObject("mood")) != null)
                            timelineActivity.hapiMoment = JsonUtil.getHapiMoment(timeline_activity_main.getJSONObject("mood"));
                    }
                }
                if (timeline_activity_main.has("water")) {
                    System.out.println("JsonTimelineActivityResponseHandler haswater: " + timeline_activity_main.get("water"));

                    if (timeline_activity_main.get("water").equals(null) || (timeline_activity_main.get("water").toString().equalsIgnoreCase("water"))) {
                    } else {
                        if (JsonUtil.getWater(timeline_activity_main.getJSONObject("water")) != null) {
                            timelineActivity.water = JsonUtil.getWater(timeline_activity_main.getJSONObject("water"));
                        }
                    }
                }

                if (timeline_activity_main.has("workout")) {
                    System.out.println("JsonTimelineActivityResponseHandler hasworkout: " + timeline_activity_main.get("workout"));

                    if (timeline_activity_main.get("workout").equals(null) || (timeline_activity_main.get("workout").toString().equalsIgnoreCase("workout"))) {
                    } else {
                        if (JsonUtil.getWorkout(timeline_activity_main.getJSONObject("workout")) != null) {
                            timelineActivity.workout = JsonUtil.getWorkout(timeline_activity_main.getJSONObject("workout"));
                        }
                    }
                }

                if (timeline_activity_main.has("steps")) {
                    System.out.println("JsonTimelineActivityResponseHandler steps: " + timeline_activity_main.get("steps"));

                    if (timeline_activity_main.get("steps").equals(null) || (timeline_activity_main.get("steps").toString().equalsIgnoreCase("steps"))) {
                    } else {
                        if (JsonUtil.getStepsTimelineActivity(timeline_activity_main.getJSONObject("steps")) != null) {
                            timelineActivity.steps = JsonUtil.getStepsTimelineActivity(timeline_activity_main.getJSONObject("steps"));
                        }
                    }
                }
                if (timeline_activity_main.has("weight")) {
                    System.out.println("JsonTimelineActivityResponseHandler steps: " + timeline_activity_main.get("weight"));

                    if (timeline_activity_main.get("weight").equals(null) || (timeline_activity_main.get("weight").toString().equalsIgnoreCase("weight"))) {
                    } else {
                        if (JsonUtil.getStepsTimelineActivity(timeline_activity_main.getJSONObject("weight")) != null) {
                            timelineActivity.weight = JsonUtil.getWeightTimelineActivity(timeline_activity_main.getJSONObject("weight"));
                        }
                    }
                }

                if (timeline_activity_main.has("user")) {
                    JSONObject commentUser = timeline_activity_main.optJSONObject("user");
                    CommentUser user = new CommentUser();
                    user.user_id = commentUser.optString("user_id");
                    user.firstname = commentUser.optString("firstname");
                    user.lastname = commentUser.optString("lastname");
                    user.email = commentUser.optString("email");
                    user.picture_url_large = commentUser.optString("picture_url_large");
                    timelineActivity.commentUser = user;
                }

                //transfer the message obj to the response handler
                setResponseObj(timelineActivity);

                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);

                return;
            }
        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }
    }

    @Override
    public void start(String strJson, String id) {
        // TODO Auto-generated method stub
    }

}
