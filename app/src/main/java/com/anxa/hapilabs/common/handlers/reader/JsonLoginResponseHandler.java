package com.anxa.hapilabs.common.handlers.reader;

import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;


/****
* Notes:
* - Return =  UserProfileObj for successful request
* - MessageObj for failed request
*/
public class JsonLoginResponseHandler extends JsonDefaultResponseHandler {

	protected Handler handler;
	protected boolean isError = false;

	String OutputData = "";
    JSONObject jsonResponse;
    String  strJson;
    
    Message msg = new Message();
	
	/****** STEP1:
     * PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * { {
     "api_response" =     {
     status = Successful;
     };
     authticket =     {
     "expiry_date_utc" = "2014-10-30 02:33:31";
     value = C9A2E3E2C0857208D6393970B5B54DEE5BB6C952265AA34ACB51F2AE1C399DFCDC6FDFD5AA1EF812A9D2D63C051D190B0ACF2CED32026BB513CDF8A4949EEBCD80F0B9A0F4C0F9E63050203FB9EF74C41FDF467643D1253CB914D2EC58AA71207E62C1FA8FD1EDE0122FAD79EE38625DC0FA484377DF46ABE485E4E108DDF65B401622F12BA6C9049A16899D894A321C49633BDC393F8447175F6EA620990E13D0875D4B688767080A8E356BDC75471FE40C6F7D5EE55E2F682640D15448C87A77BACA286B65279F4585587466A9AA1BE1EEC78CE932A19E018607DEF6A3BF20715CB330AEB86BEFCF5F630D6AB5101926C4B4DB503366DF7A4A4C70448EA501C51F0DFDD9A1B61B60F2AED967BE5A32C149EC87D9EA322AC832FD5593EE5AB4CA6C45F5442361B3896A6CB54C75BD041F215BEB05BA28B83D416857CF6DD5D2D2B48CED3561F1CE8F2D9C291911DD2FF8C06C3E3539D81B27FE05362626FC0B367770DDAB32F36468E791B85E02DC649731804CDC9D0D6EBADD09875A645ED293739CEB4DC055224F9007A3F237ECFE5579B2DD1F4F01FED75694D35413E46EA421D52D556DF31DC918B7993936AA69B281D88F40CCCAD03DE9D81380188C8917A968A88C18F53905F5A181D15A58F17025BCE54920F9D81BD98EBF8BBD0E221E7A43E863D92E2EC997983D85458CEBBF38411EDC588DB111AA47AAB0A480CEE6844E64B797F494827CBAA463D31D186AE11772;
     };
     user =     {
     "plan_profile" =         {
     coach =             {
     "avatar_url" = "http://img.hapilabs.com/hapicoach/coach/coach_delphine_michel_default@2x.png";
     "coach_id" = 1;
     en =                 {
     about = "Delphine acquired her dietitian license in 2002, after which she taught Nutrition and Food Microbiology until 2008.";
     coachingstyle = "Specialties: Weight loss, balanced diet
     \nPersonality: Encouraging
     \nInterests: Diving, theater";
     profile = "Delphine also ran her own weight loss coaching business before joining HAPIcoach as the head of dietitian services.
     \nShe is a regular guest for various health-oriented TV shows in France.
     \nChan says, \"I lost 24 lbs in 3 months, without feeling like I was on a diet. I even had a few cheat days, with Delphine's guidance. It's great, I'm very satisfied!\"";
     title = Nutritionist;
     };
     firstname = Delphine;
     fr =                 {
     about = "Delphine a mont\U00e9 son propre cabinet de consultations di\U00e9t\U00e9tiques avant de rejoindre HAPIcoach en qualit\U00e9 de responsable du p\U00f4le nutritionnel. Elle participe r\U00e9guli\U00e8rement \U00e0 des \U00e9missions de t\U00e9l\U00e9vision sur les cha\U00eenes historiques et celles de la TNT.";
     coachingstyle = "Sp\U00e9cialit\U00e9s : perte de poids, \U00e9quilibre alimentaire
     \nPersonnalit\U00e9 : souriante et motivante
     \nInt\U00e9r\U00eats personnels : plong\U00e9e, th\U00e9\U00e2tre";
     profile = "Sylvie t\U00e9moigne : \"J'ai perdu 12 kg en 3 mois sans jamais songer que j'\U00e9tais au r\U00e9gime. Je me suis m\U00eame accord\U00e9 quelques extras. C'est certainement gr\U00e2ce au soutien de Delphine.";
     title = "Di\U00e9t\U00e9ticienne";
     };
     lastname = Michel;
     };
     "current_weight" = 46270;
     "eating_habits" = "I eat balanced meals";
     "goal_type" = 0;
     height = 155;
     membership = 2;
     "membership_expiry_utc" = "2015-08-07 07:03:26";
     "start_weight" = 46270;
     "target_weight" = 44450;
     };
     "user_profile" =         {
     birthday = "1987-05-03";
     "contact_number" = 09164259801;
     country = Cambodia;
     "date_joined_utc" = "2012-12-07";
     email = "giselle@anxateam.com";
     firstname = "Giselle   Nicole";
     gender = Male;
     language = en;
     lastname = Ramirez;
     "picture_url_large" = "http://img.hapilabs.com/mobile/5/9f219721-c53a-4c6a-9886-8ca40eec6580-large.jpeg";
     timezone = "Singapore Standard Time";
     "user_id" = 5;
     "user_login" =             {
     password = 8c80249bc7f8a6bb95dcd728a9617fdbaeef5ff3;
     username = "giselle@anxateam.com";
     };
     };
     };
     * */
	public JsonLoginResponseHandler(Handler handler){
		super(handler);
		this.handler = handler;
		//this.strJson = strJson;
		/****** STEP2:
	     * FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE on strJson variable
 	     * */

//		strJson = "{\"data\" : { " +
//				"\"hash \": \"a77ad859ee8a3a348deaafda8a16b6a46e420776\"," +
//				"\"login\": null," +
//				"\"user\":  {"+
//						"\"aj_regno\": 989171," +
//						"\"email\": \"karlo@aujourdhui.com\"," + 
//						"\"firstname\": \"Karlo\"," +
//						"\"gender\": 1, " +
//						"\"initial_weight\": 60, " +
//						"\"lastname\": \"GR \"," +
//						"\"profileimageurl\": \"asdf\"," +
//						"\"reg_id\": 590331, " +
//						"\"target_weight\": 50, "+
//						"\"username\": \"karloanxa\" "+
//					"}"+
//		 	"}," + 
//		 	"\"error\": [],"+
//		 	
//  			"\"error_count\": 0, " +
//  			"\"message\": \"Successful\", " +
//  			"\"message_detail\": \"Hello\""+
//  			"}";;
//  			
//  			//this.strJson = strJson;
  		
	}
	
	
	@Override
	public void start(String strJson) {
		this.strJson = strJson;
		start();
	
	}
	@Override
	public void start(){
        try {
        	
        	/****** inform handler that the jsn processing has started; this is optional *********/
        	msg.what = JsonDefaultResponseHandler.START;
        	
        	handler.handleMessage(msg);
            System.out.println("JsonLoginResponseHandler " + strJson);
        	
             /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            jsonResponse = new JSONObject(strJson);
             
             /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
             /*******  Returns null otherwise.  *******/

        	 JSONObject api_response = jsonResponse.getJSONObject("api_response");
        	 String requestStatus = api_response.optString("message");

        	 //for failed request
        	 if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")){
        		 
        		 //set message obj for the failed request
        		 MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
        		
        		 //transfer the message obj to the response handler
        			 setResponseObj(messageObj);
        		 //set json response handler to completed
        		 msg.what = JsonDefaultResponseHandler.COMPLETED;
             	 
        		 //set the handler for the waiting class
        		 handler.handleMessage(msg);
        		 return;
        		 
        	 }
        	 JSONObject data = null;
        	 
        	 if (jsonResponse.has("user")) {
        		 data = jsonResponse.getJSONObject("user");
        	 }
        	 
        	 if (data!=null){
        		
        		 //set message obj for the successfull request
         		 UserProfile userProfile = JsonUtil.getUserProfile(data);
        		
         		 //transfer the message obj to the response handler
         		setResponseObj(userProfile);
        	    
         		 //set json response handler to completed
        		 msg.what = JsonDefaultResponseHandler.COMPLETED;
             	 
        		 //set the handler for the waiting class
        		 
        		 handler.handleMessage(msg);
        		 return;
        		
        	 	
        	 }else {
        		 if (jsonResponse.has("api_response")) {

        			 JSONObject api_response2 = jsonResponse.getJSONObject("api_response");
     				 String status = api_response2.optString("status");
     				// for failed request
     				if (status == null || status.equalsIgnoreCase("Failed")) {
     					MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response2);
     					setResponseObj(msgObj);
     					msg.what = JsonDefaultResponseHandler.COMPLETED;
     					handler.handleMessage(msg);
     					return;
     				}
        		 }
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
	