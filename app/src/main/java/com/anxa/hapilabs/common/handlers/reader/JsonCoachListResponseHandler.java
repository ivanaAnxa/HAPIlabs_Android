package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonCoachListResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    /******
     * {"coach":[{"avatar_url":
     * "http://img.hapilabs.com/hapicoach/coach/coach_delphine_michel_default@2x.png"
     * ,"coach_id":"1","en":{"about":
     * "Delphine acquired her dietitian license in 2002"
     * ,"coachingstyle":"Weight loss and balanced diet"
     * ,"profile":"Delphine also ran her own weight "
     * ,"title":"Nutritionist"},"firstname"
     * :"Delphine","fr":{"about":"Delphine a mont� son propre "
     * ,"coachingstyle":"Perte de poids"
     * ,"profile":"Sylvie t�moigne ","title":"Di�t�ticienne"
     * },"lastname":"Michel"}, {"avatar_url":
     * "http://img.hapilabs.com/hapicoach/coach/coach_lisa_leber_default@2x.png"
     * ,"coach_id":"2","en":{"about":
     * "Lisa acquired her dietitian license in 2002"
     * ,"coachingstyle":"Weight loss and balanced diet"
     * ,"profile":"Lisa also ran her own weight "
     * ,"title":"Nutritionist"},"firstname"
     * :"Lisa","fr":{"about":"Lisa a mont� son propre "
     * ,"coachingstyle":"Perte de poids"
     * ,"profile":"Sylvie t�moigne ","title":"Di�t�ticienne"
     * },"lastname":"Leber"}],"count":2}
     */

    public JsonCoachListResponseHandler(Handler handler/*, String strJson*/) {
        super(handler);
        this.handler = handler;
    }

    //	@Override
//	public void handleMessage(Message msg) {
//		handler.sendMessage(msg);
//	}
//	
    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {

        /** STEP3: START JSON PARSING HERE: */

        try {

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            ApplicationEx.language = Locale.getDefault().getLanguage();

            Log.i("JsonCoachReHandler: ", strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("coach")) {
                JSONArray coaches = jsonResponse.getJSONArray("coach");
                List<Coach> coachList = new ArrayList<Coach>();

                if (coaches != null && coaches.length() > 0) {
                    for (int i = 0; i < coaches.length(); i++) {

                        JSONObject coachJson = coaches.getJSONObject(i);
                        Coach coach = JsonUtil.getCoach(coachJson);

                        if (coach != null) {
                            if (!ApplicationEx.language.equals("fr")) {
                                coachList.add(coach);
                            } else {
                                if (coach.coach_profile_fr != null) {
                                    coachList.add(coach);
                                }
                            }
                        }
                    }

                    //if no coachlist, try other language
                    if (coachList.size() <= 0) {
                        for (int i = 0; i < coaches.length(); i++) {

                            JSONObject coachJson = coaches.getJSONObject(i);
                            Coach coach = JsonUtil.getCoach(coachJson);

                            if (coach != null) {
                                coachList.add(coach);
                            }
                        }
                    }


                    setResponseObj(coachList);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }
            }

            String error_count = jsonResponse.optString("error_count");
            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED,
                        jsonResponse);
                setResponseObj(msgObj);
                msg.what = JsonDefaultResponseHandler.COMPLETED;
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
