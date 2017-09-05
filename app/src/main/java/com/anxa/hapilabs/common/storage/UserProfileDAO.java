package com.anxa.hapilabs.common.storage;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.UserProfile;
//import com.google.android.gms.internal.cu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class UserProfileDAO extends DAO {

	protected static final String createSQL = "CREATE TABLE IF NOT EXISTS userDetails "
			+ "(regID TEXT PRIMARY KEY  NOT NULL , "
			+ " password TEXT,"
			+ " passwordplain TEXT,"
			+ " firstname TEXT,"
			+ " lastname TEXT,"
			+ " email TEXT,"
			+ " bday TEXT,"
			+ " gender INTEGER,"
			+ " membertype INTEGER,"
			+ " memberexpiry TEXT,"
			+ " currentweight TEXT,"
			+ " targetweight TEXT,"
			+ " startweight TEXT,"
			+ " height TEXT,"
			+ " coachid TEXT,"
			+ " coachname TEXT,"
			+ " countrycode TEXT DEFAULT \'ph\',"
			+ " country TEXT DEFAULT \'ph\',"
			+ " language TEXT DEFAULT \'en\',"
			+ " contactnumber TEXT,"
			+ " timezone TEXT,"
			+ " datejoined DATETIME DEFAULT CURRENT_TIMESTAMP,"
			+ " goals TEXT,"
			+ " picurllarge TEXT,"
			+ " userprofilephoto BLOB,"
			+ " goalsindex INTEGER,"
			+ " eatinghabits TEXT,"
			+ " eatinghabitsindex INTEGER,"
			+ " cookie TEXT,"
			+ " cookieexpiry TEXT);";
	Context appCtx;

	public UserProfileDAO(Context appCtx, DatabaseHelper dbHelper) {
		super(appCtx, "userDetails", dbHelper);
		this.appCtx = appCtx;
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

	public void delete(String regID) {
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			db.delete(this.tableName, "regID = ?", new String[] { (regID) });
		} catch (Exception ex) {
		}
	}

	public boolean insert(UserProfile userprofile) {
		boolean status = true;
		try {
			ContentValues values = new ContentValues();

			values.put("regID", userprofile.getRegID());

			if (userprofile.getPassword() != null)
				values.put("password", userprofile.getPassword());

			if (userprofile.getPasswordPlain() != null)
				values.put("passwordplain", userprofile.getPasswordPlain());

			if (userprofile.getFirstname() != null)
				values.put("firstname", userprofile.getFirstname());

			if (userprofile.getLastname() != null)
				values.put("lastname", userprofile.getLastname());

			if (userprofile.getEmail() != null)
				values.put("email", userprofile.getEmail());

			if (userprofile.getBday() != null)
				values.put("bday", userprofile.getBday());

			values.put("gender", userprofile.getGender());

			values.put("membertype", userprofile.getMember_type().getValue());

			values.put("memberexpiry", userprofile.getMember_expiry());

			if (userprofile.getCurrent_weight() != null)
				values.put("currentweight", userprofile.getCurrent_weight());

			if (userprofile.getTarget_weight() != null)
				values.put("targetweight", userprofile.getTarget_weight());

			if (userprofile.getStart_weight() != null)
				values.put("startweight", userprofile.getStart_weight());

			values.put("height", userprofile.getHeight());

			if (userprofile.getCoach() != null) {
				if (userprofile.getCoach().coach_id != null) {

					values.put("coachid", userprofile.getCoach().coach_id);
					if (userprofile.getCoach().firstname != null)
						values.put("coachname",
								userprofile.getCoach().firstname);
				}

			}

			if (userprofile.getCountry_code() != null)
				values.put("countrycode", userprofile.getCountry_code());

			if (userprofile.getCountry() != null)
				values.put("country", userprofile.getCountry());

			if (userprofile.getLanguage() != null)
				values.put("language", userprofile.getLanguage());

			if (userprofile.getTimezone() != null)
				values.put("timezone", userprofile.getTimezone());

			if (userprofile.getDate_joined() != null)
				values.put("datejoined",
						Meal.setDatetoString(userprofile.getDate_joined()));

			if (userprofile.getGoals() != null)
				values.put("goals", userprofile.getGoals());

			if (userprofile.getPic_url_large() != null)
				values.put("picurllarge", userprofile.getPic_url_large());

			if (userprofile.getUserProfilePhoto() != null)
				values.put("userprofilephoto", DBBitmapUtil
						.getBytes(userprofile.getUserProfilePhoto()));

			values.put("goalsindex", userprofile.getGoals_index());

			if (userprofile.getEating_habits() != null)
				values.put("eatinghabits", userprofile.getEating_habits());

			values.put("eatinghabitsindex",
					userprofile.getEating_habits_index());

			values.put("cookie", userprofile.getCookie());

			values.put("cookieexpiry", userprofile.getCookie_expiry());

			status = this.insertTable(values, "regID=?",
					new String[] { String.valueOf(userprofile.getRegID()) });

		} catch (Exception ex) {
			status = false;

			ex.printStackTrace();

		} finally {
		}

		return status;
	}

	public boolean update(UserProfile userprofile) {
		boolean status = true;
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();

			ContentValues values = new ContentValues();
			// values.put("regID", userprofile.getRegID());
			// values.put("password", userprofile.getPassword());
			// values.put("passwordplain", userprofile.getPasswordPlain());
			// values.put("firstname", userprofile.getFirstname());
			// values.put("lastname", userprofile.getLastname());
			// values.put("email", userprofile.getEmail());
			// values.put("bday", userprofile.getBday());
			// values.put("gender", userprofile.getGender().getValue());
			// values.put("membertype",
			// userprofile.getMember_type().getValue());
			// values.put("memberexpiry", userprofile.getMember_expiry());
			// values.put("currentweight", userprofile.getCurrent_weight());
			// values.put("targetweight", userprofile.getTarget_weight());
			// values.put("height", userprofile.getHeight());
			// values.put("coachid", userprofile.getCoach().coach_id);
			// values.put("coachname", userprofile.getCoach().firstname);
			// values.put("countrycode", userprofile.getCountry_code());
			// values.put("language", userprofile.getLanguage());
			// values.put("timezone", userprofile.getTimezone());
			// values.put("datejoined",Meal.setDatetoString(userprofile.getDate_joined()));
			// values.put("goals", userprofile.getGoals());
			// values.put("picurllarge", userprofile.getPic_url_large());
			// values.put("userprofilephoto",
			// DBBitmapUtil.getBytes(userprofile.getUserProfilePhoto()));
			// values.put("goalsindex", userprofile.getGoals_index());
			// values.put("eatinghabits", userprofile.getEating_habits());
			// values.put("eatinghabitsindex",
			// userprofile.getEating_habits_index());
			// values.put("cookie", userprofile.getCookie());
			// values.put("cookieexpiry", userprofile.getCookie_expiry());

			values.put("regID", userprofile.getRegID());

			if (userprofile.getPassword() != null)
				values.put("password", userprofile.getPassword());

			if (userprofile.getPasswordPlain() != null)
				values.put("passwordplain", userprofile.getPasswordPlain());

			if (userprofile.getFirstname() != null)
				values.put("firstname", userprofile.getFirstname());

			if (userprofile.getLastname() != null)
				values.put("lastname", userprofile.getLastname());

			if (userprofile.getEmail() != null)
				values.put("email", userprofile.getEmail());

			if (userprofile.getBday() != null)
				values.put("bday", userprofile.getBday());

			values.put("gender", userprofile.getGender());

			values.put("membertype", userprofile.getMember_type().getValue());

			values.put("memberexpiry", userprofile.getMember_expiry());

			if (userprofile.getCurrent_weight() != null)
				values.put("currentweight", userprofile.getCurrent_weight());

			if (userprofile.getStart_weight() != null)
				values.put("startweight", userprofile.getStart_weight());

			if (userprofile.getTarget_weight() != null)
				values.put("targetweight", userprofile.getTarget_weight());

			values.put("height", userprofile.getHeight());

			if (userprofile.getCoach() != null) {
				if (userprofile.getCoach().coach_id != null) {

					values.put("coachid", userprofile.getCoach().coach_id);
					if (userprofile.getCoach().firstname != null)
						values.put("coachname",
								userprofile.getCoach().firstname);
				}

			}

			if (userprofile.getCountry_code() != null)
				values.put("countrycode", userprofile.getCountry_code());

			if (userprofile.getCountry() != null)
				values.put("country", userprofile.getCountry());

			if (userprofile.getLanguage() != null)
				values.put("language", userprofile.getLanguage());

			if (userprofile.getTimezone() != null)
				values.put("timezone", userprofile.getTimezone());

			if (userprofile.getDate_joined() != null)
				values.put("datejoined",
						Meal.setDatetoString(userprofile.getDate_joined()));

			if (userprofile.getGoals() != null)
				values.put("goals", userprofile.getGoals());

			if (userprofile.getPic_url_large() != null)
				values.put("picurllarge", userprofile.getPic_url_large());

			if (userprofile.getUserProfilePhoto() != null)
				values.put("userprofilephoto", DBBitmapUtil
						.getBytes(userprofile.getUserProfilePhoto()));

			values.put("goalsindex", userprofile.getGoals_index());

			if (userprofile.getEating_habits() != null)
				values.put("eatinghabits", userprofile.getEating_habits());

			values.put("eatinghabitsindex",
					userprofile.getEating_habits_index());

			values.put("cookie", userprofile.getCookie());

			values.put("cookieexpiry", userprofile.getCookie_expiry());

			db.update(this.tableName, values, "regID = ?",
					new String[] { String.valueOf(userprofile.getRegID()) });
		} catch (Exception ex) {
			status = false;
		} finally {
		}

		return status;
	}

	/*
	 * to save a byte array in blob type use following code:
	 * 
	 * ContentValues cv=new ContentValues(); cv.put(CHUNK, buffer); //CHUNK blob
	 * type field of your table long rawId=database.insert(TABLE, null, cv);
	 * //TABLE table name
	 */
	public UserProfile getUserProfile() {

		UserProfile newObjActivity = null;
 try{
		Cursor cur = this.getAllEntriesUserProfile();

		if (cursorHasRows(cur)) {

			if (cur.moveToFirst()) {

				do {

					newObjActivity = new UserProfile();

					if (cur.getColumnIndex("regID") > -1)
						newObjActivity.setRegID((cur.getString(cur
								.getColumnIndex("regID"))));

					if (cur.getColumnIndex("password") > -1)
						newObjActivity.setPassword((cur.getString(cur
								.getColumnIndex("password"))));

					if (cur.getColumnIndex("passwordplain") > -1)
						newObjActivity.setPasswordPlain((cur.getString(cur
								.getColumnIndex("passwordplain"))));

					if (cur.getColumnIndex("firstname") > -1)
						newObjActivity.setFirstname((cur.getString(cur
								.getColumnIndex("firstname"))));

					if (cur.getColumnIndex("lastname") > -1)
						newObjActivity.setLastname((cur.getString(cur
								.getColumnIndex("lastname"))));

					if (cur.getColumnIndex("email") > -1)
						newObjActivity.setEmail((cur.getString(cur
								.getColumnIndex("email"))));

					if (cur.getColumnIndex("bday") > -1)
						newObjActivity.setBday((cur.getString(cur
								.getColumnIndex("bday"))));

					if (cur.getColumnIndex("memberexpiry") > -1)
						newObjActivity.setMember_expiry((cur.getString(cur
								.getColumnIndex("memberexpiry"))));

					if (cur.getColumnIndex("currentweight") > -1)
						newObjActivity.setCurrent_weight((cur.getString(cur
								.getColumnIndex("currentweight"))));

					if (cur.getColumnIndex("startweight") > -1)
						newObjActivity.setStart_weight((cur.getString(cur
								.getColumnIndex("startweight"))));

					if (cur.getColumnIndex("targetweight") > -1)
						newObjActivity.setTarget_weight((cur.getString(cur
								.getColumnIndex("targetweight"))));

					if (cur.getColumnIndex("height") > -1)
						newObjActivity.setHeight((cur.getString(cur
								.getColumnIndex("height"))));

					if (cur.getColumnIndex("coachid") > -1) {
						String coachID = ((cur.getString(cur
								.getColumnIndex("coachid"))));
						String coachname = ((cur.getString(cur
								.getColumnIndex("coachname"))));
						CoachDAO coachdao = new CoachDAO(appCtx, null);
						if (coachID == null) {
							coachID = "1";

						}
						Coach coach = coachdao.getCoachsbyID(coachID);

						if (coach.coach_id == null) {
							if (coachname != null) {
								coach = coachdao.getCoachsbyName(coachname);
								newObjActivity.setCoach(coach);
							}
						} else {
							newObjActivity.setCoach(coach);
						}

					}

					if (cur.getColumnIndex("countrycode") > -1)
						newObjActivity.setCountry_code((cur.getString(cur
								.getColumnIndex("countrycode"))));

					if (cur.getColumnIndex("country") > -1)
						newObjActivity.setCountry((cur.getString(cur
								.getColumnIndex("country"))));

					if (cur.getColumnIndex("language") > -1)
						newObjActivity.setLanguage((cur.getString(cur
								.getColumnIndex("language"))));

					if (cur.getColumnIndex("timezone") > -1)
						newObjActivity.setTimezone((cur.getString(cur
								.getColumnIndex("timezone"))));

					if (cur.getColumnIndex("datejoined") > -1)
						newObjActivity.setDate_joined(Meal.setStringtoDate(cur
								.getString(cur.getColumnIndex("datejoined"))));

					if (cur.getColumnIndex("goals") > -1)
						newObjActivity.setGoals((cur.getString(cur
								.getColumnIndex("goals"))));

					if (cur.getColumnIndex("picurllarge") > -1)
						newObjActivity.setPic_url_large((cur.getString(cur
								.getColumnIndex("picurllarge"))));

					if (cur.getColumnIndex("userprofilephoto") > -1) {
						byte[] imgbyte = null;

						try {
							imgbyte = cur.getBlob(cur
									.getColumnIndex("userprofilephoto"));
						} catch (Exception e) {

						}
						if (imgbyte != null) {
							Bitmap userProfilePhoto = DBBitmapUtil
									.getImage(imgbyte);
							if (userProfilePhoto != null)
								newObjActivity
										.setUserProfilePhoto(userProfilePhoto);
						} else
							newObjActivity.setUserProfilePhoto(null);

					}

					if (cur.getColumnIndex("goalsindex") > -1)
						newObjActivity.setGoals_index((cur.getInt(cur
								.getColumnIndex("goalsindex"))));

					if (cur.getColumnIndex("eatinghabits") > -1)
						newObjActivity.setEating_habits((cur.getString(cur
								.getColumnIndex("eatinghabits"))));

					if (cur.getColumnIndex("eatinghabitsindex") > -1)
						newObjActivity.setEating_habits_index((cur.getInt(cur
								.getColumnIndex("eatinghabitsindex"))));

					if (cur.getColumnIndex("cookie") > -1)
						newObjActivity.setCookie((cur.getString(cur
								.getColumnIndex("cookie"))));

					if (cur.getColumnIndex("cookieexpiry") > -1)
						newObjActivity.setCookie_expiry((cur.getString(cur
								.getColumnIndex("cookieexpiry"))));

					//
					// if ( cur.getColumnIndex("subscriptionstatus") > -1){
					//
					// int tempInt =
					// cur.getInt(cur.getColumnIndex("subscriptionstatus"));
					// newObjActivity.setSubscription(UserProfile.getSTATUSValue(tempInt));
					//
					// }else //set as free
					// newObjActivity.setSubscription(UserProfile.SUBSCRIPTION_STATUS.UNSUBSCRIBED);
					//

					if (cur.getColumnIndex("membertype") > -1) {

						int tempInt = cur.getInt(cur
								.getColumnIndex("membertype"));
						newObjActivity.setMember_type(UserProfile
								.getMEMBERTYPEValue(tempInt));

					} else
						// set as free
						newObjActivity
								.setMember_type(UserProfile.MEMBER_TYPE.FREE);

					if (cur.getColumnIndex("gender") > -1) {

						int tempInt = cur.getInt(cur.getColumnIndex("gender"));
						newObjActivity.setGender(Integer.toString(tempInt));

					} else
						// set as free
						newObjActivity.setGender("0");

				} while (cur.moveToNext());
			}
		}

		cur.close();
 }catch (Exception e){
}
		return newObjActivity;

	}// get all photos regardless of meal ID

}
