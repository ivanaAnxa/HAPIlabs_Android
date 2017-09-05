package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Workout;

import android.os.Handler;
import android.os.Message;


/****
 * Notes:
 * - Return =  UserProfileObj for successful request
 * - MessageObj for failed request
 */
public class JsonGetSyncResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    JSONObject jsonResponse;
    String strJson;

    Message msg = new Message();


    /******
     * STEP1:
     * PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     */
    public JsonGetSyncResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
        //this.strJson = strJson;
        /****** STEP2:
         * FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE on strJson variable
         * */
    }


    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {

        /****** STEP3:
         * START JSON PARSING HERE:
         *
         *
         * */
        try {
            /****** inform handler that the jsn processing has started; this is optional *********/
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            jsonResponse = new JSONObject(strJson);
            System.out.println("JsonGetSyncResponseHandler " + strJson);


            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
            /*******  Returns null otherwise.  *******/
            try {
                JSONObject api_response = jsonResponse.getJSONObject("api_response");

                String requestStatus = api_response.optString("message");

                //for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
                    //set message obj for the failed request
                    MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, jsonResponse);
                    //transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }

                //possible successful request but empty result
                //CHECK MEMBERSHIP FIRST if there is any update
                int _tempStr = jsonResponse.optInt("membership");
                ApplicationEx.getInstance().userProfile.setMember_type(UserProfile.setMemberType(_tempStr));

                ApplicationEx.getInstance().userRatingSetting = jsonResponse.optBoolean("userRating_setting");

                // GET Water ARRAY
                JSONArray _waterArray = jsonResponse.getJSONArray("water");
                ApplicationEx.getInstance().waterList = new ArrayList<Water>();
                List<Water> waterList = new ArrayList<Water>();

                for (int index = 0; index < _waterArray.length(); index++) {
                    JSONObject json = _waterArray.getJSONObject(index);

                    Water toSaveWater = JsonUtil.getWater(json);

                    System.out.println("JsonGetSyncResponseHandler: " + toSaveWater.comments);
                    waterList.add(toSaveWater);

                }

                ApplicationEx.getInstance().waterList = waterList;
                // GET WORKOUT ARRAY
                JSONArray _workoutArray = jsonResponse.getJSONArray("workout");
                ArrayList<Workout> workoutList = new ArrayList<Workout>();

                for (int index = 0; index < _workoutArray.length(); index++)
                {
                    JSONObject json = _workoutArray.getJSONObject(index);

                    Workout workoutObj = JsonUtil.getWorkout(json);
                    workoutList.add(workoutObj);
                }

                // GET Hapimoment ARRAY
                JSONArray _hapiMomentArray = jsonResponse.getJSONArray("mood");
                ApplicationEx.getInstance().hapiMomentList = new ArrayList<HapiMoment>();
                List<HapiMoment> hapiMomentList = new ArrayList<HapiMoment>();

                for (int index = 0; index < _hapiMomentArray.length(); index++) {
                    JSONObject json = _hapiMomentArray.getJSONObject(index);

                    HapiMoment toSaveHapiMoment = JsonUtil.getHapiMoment(json);
                    hapiMomentList.add(toSaveHapiMoment);
                }

                ApplicationEx.getInstance().hapiMomentList = hapiMomentList;

                // GET MEAL ARRAY
                JSONArray _array = jsonResponse.getJSONArray("meal");
                List<Meal> _meallist = new ArrayList<Meal>();

                TimeZone tz = TimeZone.getDefault();
                Calendar cal = GregorianCalendar.getInstance(tz);
                int offsetInMillis = (tz.getOffset(cal.getTimeInMillis())) / 1000;

                for (int index = 0; index < _array.length(); index++) {
                    JSONObject json = _array.getJSONObject(index);
                    if (JsonUtil.getMeal(json, offsetInMillis) != null) {
                        _meallist.add(JsonUtil.getMeal(json, offsetInMillis));
                    }else{
                        System.out.println("JsonUtil.meal == null");
                    }
                }


                ArrayList returnArrayList = new ArrayList<Object>();
                returnArrayList.addAll(_meallist);
                returnArrayList.addAll(hapiMomentList);
                returnArrayList.addAll(workoutList);
                //transfer the message obj to the response handler
                setResponseObj(returnArrayList);

                //transfer the message obj to the response handler
//                setResponseObj(_meallist);
                setResultMessage(requestStatus);

                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            } catch (JSONException e) {
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                e.printStackTrace();
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
