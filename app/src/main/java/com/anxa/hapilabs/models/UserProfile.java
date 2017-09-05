package com.anxa.hapilabs.models;

import java.util.Date;
import java.util.Locale;


import android.graphics.Bitmap;

import com.anxa.hapilabs.common.util.ApplicationEx;

public class UserProfile {

    private String username;

    private String password; // hashed value of the password

    private String passwordPlain; // hashed value of the password

    private String regID;

    public static enum SUBSCRIPTION_STATUS {

        SUBSCRIBED(1), UNSUBSCRIBED(2), EXPIRED(3);

        private final int value;

        private SUBSCRIPTION_STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;

    public static SUBSCRIPTION_STATUS getSTATUSValue(int value) {
        SUBSCRIPTION_STATUS returnvalue = SUBSCRIPTION_STATUS.SUBSCRIBED; // default returnvalue
        for (final SUBSCRIPTION_STATUS type : SUBSCRIPTION_STATUS.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }


    private SUBSCRIPTION_STATUS subscription;

    private String firstname;

    private String lastname;

    private String email;

    private String bday;


    public static enum GENDER {

        MALE(1), FEMALE(2);

        private final int value;

        private GENDER(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;

    public static GENDER getGENDERValue(int value) {
        GENDER returnvalue = GENDER.FEMALE; // default returnvalue
        for (final GENDER type : GENDER.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    private String gender;


    public static enum MEMBER_TYPE {

        FREE(1), PAID_NORMAL(2), PAID_PREMIUM(3);

        private final int value;

        private MEMBER_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;


    public static MEMBER_TYPE getMEMBERTYPEValue(int value) {
        MEMBER_TYPE returnvalue = MEMBER_TYPE.FREE; // default returnvalue
        for (final MEMBER_TYPE type : MEMBER_TYPE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    private MEMBER_TYPE member_type;

    private String member_expiry;

    private String current_weight;

    private String target_weight;

    private String start_weight;

    private String height;

    private Coach coach;

    private String country_code;
    private String country;

    private String language;

    private String contact_number;

    private String timezone;

    private Date date_joined;

    private String goals;

    private String survey_answer;
    private int has_answered_optin;
    private String motivation_level;
    private String profession;
    private String time_to_spend;

    public Boolean getReceive_newsletter() {
        return receive_newsletter;
    }

    public void setReceive_newsletter(Boolean receive_newsletter) {
        this.receive_newsletter = receive_newsletter;
    }

    private Boolean receive_newsletter;


    private String pic_url_large;

    private Bitmap userProfilePhoto;

    private int goals_index;

    private String eating_habits;

    private int eating_habits_index;

    private String cookie;

    private String cookie_expiry;

    public UserProfile() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    public SUBSCRIPTION_STATUS getSubscription() {
        return subscription;
    }

    public void setSubscription(SUBSCRIPTION_STATUS subscription) {
        this.subscription = subscription;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public MEMBER_TYPE getMember_type() {
        return member_type;
    }

    public void setMember_type(MEMBER_TYPE member_type) {
        this.member_type = member_type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMember_expiry() {
        return member_expiry;
    }

    public void setMember_expiry(String member_expiry) {
        this.member_expiry = member_expiry;
    }

    public String getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(String current_weight) {
        this.current_weight = current_weight;
    }

    public String getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(String target_weight) {
        this.target_weight = target_weight;
    }

    public String getCookie_expiry() {
        return cookie_expiry;
    }

    public void setCookie_expiry(String cookie_expiry) {
        this.cookie_expiry = cookie_expiry;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getEating_habits_index() {
        return eating_habits_index;
    }

    public void setEating_habits_index(int eating_habits_index) {
        this.eating_habits_index = eating_habits_index;
    }

    public String getEating_habits() {
        return eating_habits;
    }

    public void setEating_habits(String eating_habits) {
        this.eating_habits = eating_habits;
    }

    public int getGoals_index() {
        return goals_index;
    }

    public void setGoals_index(int goals_index) {
        this.goals_index = goals_index;
    }

    public Bitmap getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(Bitmap userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public String getStart_weight() {
        return start_weight;
    }

    public void setStart_weight(String start_weight) {
        this.start_weight = start_weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        try {
            if (Locale.getDefault().getLanguage() != null) {
                ApplicationEx.language = Locale.getDefault().getLanguage();
                if (!ApplicationEx.language.equals("fr")) {
                    ApplicationEx.language = "en"; //if its anything but french then default it to english
                }
            } else
                ApplicationEx.language = "en";

        } catch (Exception e) {

        }
        return ApplicationEx.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPic_url_large() {
        return pic_url_large;
    }

    public void setPic_url_large(String pic_url_large) {
        this.pic_url_large = pic_url_large;
    }

    public String getSurvey_answer() {
        return survey_answer;
    }

    public void setSurvey_answer(String survey_answer) {
        this.survey_answer = survey_answer;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public Date getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public static MEMBER_TYPE setMemberType(int intType) {

        switch (intType) {
            case 1:
                return MEMBER_TYPE.FREE;
            case 2:
                return MEMBER_TYPE.PAID_NORMAL;
            default:
                return MEMBER_TYPE.PAID_PREMIUM;
        }
    }

    public int getHas_answered_optin() {
        return has_answered_optin;
    }

    public void setHas_answered_optin(int has_answered_optin) {
        this.has_answered_optin = has_answered_optin;
    }

    public String getMotivation_level() {
        return motivation_level;
    }

    public void setMotivation_level(String motivation_level) {
        this.motivation_level = motivation_level;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }



    public String getTime_to_spend() {
        return time_to_spend;
    }

    public void setTime_to_spend(String time_to_spend) {
        this.time_to_spend = time_to_spend;
    }
}
