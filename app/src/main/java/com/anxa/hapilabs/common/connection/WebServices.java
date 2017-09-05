package com.anxa.hapilabs.common.connection;


import com.anxa.hapilabs.common.util.ApplicationEx;

import java.util.Locale;

public class WebServices {

    public WebServices() {

    }

//        public static CONNECTION ConnectionType = CONNECTION.STAGING;
    public static CONNECTION ConnectionType = CONNECTION.LIVE;
    public static final String COOKIE_NAME = "hapicoach";
    public static final String DOMAIN_NAME = ".hapi.com";
    public static final String ANXAMATS_URL = "http://api.anxa.com/anxamats";


    public static class URL {
        public static final String qcDtsURLString = "http://qc.hapicoach.com/";
        public static final String liveDtsURLString = "http://www.hapicoach.com/";
        public static final String liveFRDtsURLString = "http://www.savoirmanger.fr/";


        public static final String qcDomainURLString = "http://qc.api.hapilabs.com/hapicoach/";
        public static final String liveDomainURLString = "https://api.hapilabs.com/hapicoach/";
        public static final String liveDomainURLStringv2 = "https://api.hapilabs.com/";

        public static final String qcDomainSettingsURLString = "http://qc.api.hapilabs.com/website/";
        public static final String liveDomainSettingsURLString = "https://api.hapilabs.com/website/";

        //shared key used for all API, this is project specific
        public static final String sharedKey_API = "An39 C046h 809ile";

        public static final String qcDomainURLString_AnxaPunc = "http://qc.api.anxa.com/AnxaPuNC/v1/";
        public static final String liveDomainURLString_AnxaPunc = "http://api.anxa.com/AnxaPuNC/v1/";

        public static final String qcDomainURLString_AnxaMats = "http://qc.api.anxa.com/";
        public static final String liveDomainURLString_AnxaMats = "http://api.anxa.com/";

        //shared keys used for anxapunc and session; this is the same for all project
        public static final String sharedKey_AnxaPunc = "0YeMPngXK8B3x1nQGJKR";
        //static NSString * language;


        public static final Boolean isLive = true;

        static final String loginURLString = "login?lang=" + ApplicationEx.language + "&command=post_hapicoach_mobilelogin";
        static final String registrationURLString = "register?lang=" + ApplicationEx.language + "&command=post_hapicoach_registration";//&username=%@&signature=%@";
        static final String forgotURLString = "login?lang=" + ApplicationEx.language + "&command=post_hapicoach_forgotpassword";//&username=%@&signature=%@";
        static final String uploadMealURLString = "meal?lang=" + ApplicationEx.language + "&command=post_meals";//&userid=;%@&signature=%@";
        static final String updateProfileURLString = "user?lang=" + ApplicationEx.language + "&command=update_userprofile"; //&userid=%@&signature=%@";
        static final String getSyncURLString = "meal?lang=" + ApplicationEx.language + "&command=get_sync&";
        static final String uploadPhotoURLString = "uploadphoto?command=post_mobile_photo";

        static final String getMealUrlString = "meal?lang=" + ApplicationEx.language + "&command=get_meal_v2&";//&userid=%@&meal_id=%@&signature=%@";
        static final String getAllMealsUrlString = "meals?";

        static final String uploadCommentURLString = "meal?lang=" + ApplicationEx.language + "&command=post_meal_comment";//&userid=%@&meal_id=%@&signature=%@";
        static final String uploadCommentHapiURLString = "meal?lang=" + ApplicationEx.language + "&command=post_meal_comment_hapi";//&userid=%@&comment_id=%@&signature=%@";
        static final String coachListURLString = "coach?lang=" + ApplicationEx.language + "&userid=%@&command=get_coachlist&";//&signature=%@";
        static final String getCoachProgramURL = "coach?command=get_coachprogram_bycoach&";

        static final String paymentURLWebString = "webkit/v1/payment?userId=%s&coachid=%@";
        static final String paymentUpgradeURLWebString = "webkit/v1/payment?userId=%s";
        static final String questionURLWebString = "webkit/v1/Survey/Question?userId=%@";
        static final String postAnswerURLWebString = "survey?lang=" + ApplicationEx.language + "&command=post_survey_answer";

        static final String getMessagesUrlString = "message?lang=" + ApplicationEx.language + "&command=get_coachmessages&";//&userid=%@&signature=%@";
        static final String getMessagesPagedUrlString = "message.json?lang=" + ApplicationEx.language + "&command=get_coachmessages_paged&";//&userid=%@&signature=%@";
        static final String postMessagesUrlString = "message?lang=" + ApplicationEx.language + "&command=post_coachmessage&";
        static final String getNotificationUrlString = "notification.json?lang=" + ApplicationEx.language + "&command=get_notifications_v2&";
        static final String postProductOrderUrlString = "subscription?lang=" + ApplicationEx.language + "&command=post_productorder";
        static final String sendAccessCodeUrlString =  "subscription?lang=" + ApplicationEx.language + "&command=send_accesscode";
        static final String validateAccessCodeUrlString = "subscription?lang=" + ApplicationEx.language + "&command=validate_accesscode";
        static final String getWorkoutUrlString = "meal?lang=" + ApplicationEx.language + "&command=get_hapicoach_workout&";
        static final String postWorkoutUrlString = "meal?lang=" + ApplicationEx.language + "&command=post_hapi_workout";

        static final String getWaterURLString = "meal?lang=" + ApplicationEx.language + "&command=get_water&";
        static final String getHAPIMomentURLString = "meal?lang=" + ApplicationEx.language + "&command=get_hapi_moment_v2&";

        static final String pushURL = "Register/UserDevice?";

        static final String anxaMats = "anxamats/logger";
        static final String getSurveyURL = "survey?command=get_survey_question&lang=" + ApplicationEx.language + "&";
        static final String markAsReadURL = "notification.json?command=post_markasread_notifications";
        static final String markAllAsReadURL = "notification.json?command=post_markallasread_notifications";
        static final String clearAllURL = "notification.json?command=post_clear_notifications";
        static final String postHAPIWaterURL = "meal?lang=" + ApplicationEx.language + "&command=post_hapi_water";

        static final String postCommandSettingsURL = "settings?command=post_account_settings";
        static final String postHapiMomentURL = "meal?command=post_hapi_moment";

        static final String getWeightGraph = "graph?Command=get_weight&";
        static final String postWeightGraph = "weight?command=post_weight";
        static final String updateWeightGraph = "weight?command=update_weight";
        static final String deleteWeightGraph = "weight?command=delete_weight";

        static final String getStepsGraph = "graph?Command=get_steps&";
        static final String postStepsGraph = "steps?command=post_steps";
        static final String updateStepsGraph = "steps?command=update_steps";
        static final String deleteStepsGraph = "steps?command=delete_steps";


        static final String URLWeeklySummary = "progress/my-weekly-summary";

        public static final String URLProfile = "/webkit/v1/profile?userId=";

        public static final String URLWeightGraph = "/webkit/v1/weightgraph?lang=" + ApplicationEx.language + "&userId=";
        public static final String URLStepsGraph = "/webkit/v1/stepgraph?lang=" + ApplicationEx.language + "&userId=";
        public static final String URLCoach = "/webkit/v1/weightgraph?lang=" + ApplicationEx.language + "&userId=";
        public static final String URLSubscription = "/webkit/v1/subscription?lang=" + ApplicationEx.language + "&userid=";
        public static final String URLHelpAbout = "/webkit/v1/help/about?lang=" + ApplicationEx.language;
        public static final String URLHelpTerms = "/webkit/v1/help/termsofservice?lang=" + ApplicationEx.language;
        public static final String URLHelpPrivacy = "/webkit/v1/help/privacypolicy?lang=" + ApplicationEx.language;
        public static final String URLHelpContactUs = "/webkit/v1/help/contactus?lang=" + ApplicationEx.language;

        public static final String URLDtsWeightGraph = "/my-graph/weight-graph";
        public static final String URLDtsStepsGraph = "/my-graph/step-graph";

        public static final String URLWeightGraphNew = "webkit/v1/weightgraph?userId=%@" + "&showBy=1&culture=%l";
        public static final String URLStepGraphNew = "webkit/v1/stepgraph?userId=%@" + "&culture=%l";

        public static final String communityURLString = "community";
        public static final String settingsURLString = "user/account-settings";

        public static final String postActivityCommentURL = "activity?command=post_activitycomment";
        public static final String postHAPI4uURL = "activity?command=post_hapi4u";

        //api.hapilabs.com/hapicoach/activity?&command=get_timelineactivity&UserId=%@&Id=%@&signature=%@
        public static final String getTimelineActivity = "activity?&command=get_timelineactivity&";
        public static final String registerDevice = "v2/device/register?command=v2/device/register";;
        public static final String postStepsGoogleFit = "v2/activity/steps?command=v2/activity/steps";

        public static final String googleFitSupportURL = "https://support.google.com/accounts/answer/6098255";
    }



    public static class COMMAND {

        public static final String LOGIN = "post_hapicoach_mobilelogin";
        public static final String FORGOTPASS = "post_hapicoach_forgotpassword";
        public static final String REGISTRATION = "post_hapicoach_registration";
        public static final String GET_SYNC = "get_sync";
        public static final String UPLOAD_MEAL = "post_meals";
        public static final String UPLOAD_COMMENT = "post_meal_comment";
        public static final String UPLOAD_COMMENT_HAPI = "post_meal_comment_hapi";
        public static final String UPLOAD_PHOTO = "post_mobile_photo";
        public static final String UPDATE_PROFILE = "update_userprofile";
        public static final String COACH_LIST = "get_coachlist";
        public static final String GET_MEAL = "get_meal_v2";
        public static final String GET_HAPIMOMENT = "get_hapi_moment_v2";
//        public static final String GET_MEAL = "get_meal";
        public static final String GET_MEALS_ALL = "get_meals_all";
        public static final String GET_MEALS_HEALTHY = "get_meals_healthy";
        public static final String GET_MEALS_OK = "get_meals_ok";
        public static final String GET_MEALS_UNHEALTHY = "get_meals_unhealthy";
        public static final String PUSH_REG = "get_meal";
        public static final String GET_MESSAGES = "get_coachmessages";
        public static final String GET_MESSAGES_PAGED = "get_coachmessages_paged";
        public static final String POST_MESSAGES = "post_coachmessage";
        public static final String GET_NOTIFICATIONS = "get_notifications_v2";
        public static final String GET_WORKOUT = "get_hapicoach_workout";
        public static final String POST_WORKOUT = "post_hapi_workout";
        public static final String ANXAMATS = "anxa_mats";
        public static final String GET_SURVEY_QUESTION = "get_survey_question";
        public static final String POST_SURVEY_ANSWER = "post_survey_answer";
        public static final String POST_MARK_AS_READ = "post_markasread_notifications";
        public static final String POST_MARK_ALL_AS_READ = "post_markallasread_notifications";
        public static final String POST_CLEAR_ALL = "post_clear_notifications";
        public static final String GET_HAPI_WATER = "get_water";
        public static final String POST_HAPI_WATER = "post_hapi_water";
        public static final String POST_ACCOUNT_SETTINGS = "post_account_settings";
        public static final String GET_COACH_PROGRAM = "get_coachprogram_bycoach";
        public static final String POST_PRODUCT_ORDER = "post_productorder";
        public static final String SEND_ACCESS_CODE = "send_accesscode";
        public static final String VALIDATE_ACCESS_CODE = "validate_accesscode";
        public static final String POST_HAPI_MOMENT = "post_hapi_moment";
        public static final String GET_HAPI_MOMENT = "get_hapi_moment_v2";
        public static final String GET_WEIGHT_GRAPH_NATIVE = "get_weight";
        public static final String POST_WEIGHT_DATA = "post_weight";
        public static final String UPDATE_WEIGHT_DATA = "update_weight";
        public static final String DELETE_WEIGHT_DATA = "delete_weight";
        public static final String GET_STEPS_GRAPH_NATIVE = "get_steps";
        public static final String POST_STEPS_DATA = "post_steps";
        public static final String UPDATE_STEPS_DATA = "update_steps";
        public static final String DELETE_STEPS_DATA = "delete_steps";
        public static final String POST_ACTIVITY_COMMENT = "post_activitycomment";
        public static final String POST_HAPI4U = "post_hapi4u";
        public static final String GET_TIMELINE_ACTIVITY = "get_timelineactivity";
        public static final String REGISTER_DEVICE = "v2/device/register";
        public static final String POST_ACTIVITY_STEPS = "v2/activity/steps";
    }

    public static class ACTIVITYSTATUS {

        public static final String ADDED = "added";
        public static final String UPDATED = "updated";
    }


    public enum CONNECTION {
        STAGING,
        LIVE
    }

    public enum SERVICES {
        LOGIN,
        REGISTRATION,
        FORGOTPASSWORD,
        GET_SYNC,
        UPLOAD_MEAL,
        UPLOAD_COMMENT,
        UPLOAD_COMMENT_HAPI,
        UPLOAD_PHOTO,
        UPDATE_PROFILE,
        COACH_LIST,
        GET_MEAL,
        GET_MEALS_ALL,
        GET_MEALS_HEALTHY,
        GET_MEALS_OK,
        GET_MEALS_UNHEALTHY,
        GET_PAYMENTKIT,
        GET_QUESTIONKIT,
        PUSH_REG,
        URL_PROFILE,
        URL_WEIGHTGRAPH,
        URL_STEPSGRAPH,
        URL_COACH,
        URL_SUBSCR,
        URL_HELP_ABOUT,
        URL_HELP_TERMS,
        URL_HELP_PRIVACY,
        URL_HELP_CONTACT,
        GET_MESSAGES,
        GET_MESSAGES_PAGED,
        POST_MESSAGES,
        GET_NOTIFICATIONS,
        GET_WORKOUT,
        POST_WORKOUT,
        ANXAMATS,
        GET_SURVEY_QUESTION,
        POST_SURVEY_ANSWER,
        POST_MARK_AS_READ,
        POST_MARK_ALL_AS_READ,
        POST_CLEAR_ALL,
        POST_HAPI_WATER,
        GET_HAPI_WATER,
        POST_ACCOUNT_SETTINGS,
        GET_WEIGHT_GRAPH,
        GET_STEP_GRAPH,
        GET_COACH_PROGRAM,
        POST_PRODUCT_ORDER,
        SEND_ACCESS_CODE,
        VALIDATE_ACCESS_CODE,
        POST_HAPI_MOMENT,
        GET_HAPI_MOMENT,
        GET_COMMUNITY,
        GET_USER_SETTINGS,
        GET_WEIGHT_GRAPH_NATIVE,
        POST_WEIGHT_DATA,
        UPDATE_WEIGHT_DATA,
        DELETE_WEIGHT_DATA,
        GET_STEPS_GRAPH_NATIVE,
        POST_STEPS_DATA,
        UPDATE_STEPS_DATA,
        DELETE_STEPS_DATA,
        URL_WEEKLY_SUMMARY,
        POST_ACTIVITY_COMMENT,
        POST_HAPI4U,
        GET_TIMELINE_ACTIVITY,
        REGISTER_DEVICE,
        POST_ACTIVITY_STEPS,
        GOOGLE_FIT_SUPPORT
    }

    public static String getCommand(SERVICES service) {
        String command = null;

        switch (service) {
            case UPDATE_PROFILE:
                command = COMMAND.UPDATE_PROFILE;
                break;
            case LOGIN:
                command = COMMAND.LOGIN;
                break;
            case FORGOTPASSWORD:
                command = COMMAND.FORGOTPASS;
                break;
            case REGISTRATION:
                command = COMMAND.REGISTRATION;
                break;
            case GET_SYNC:
                command = COMMAND.GET_SYNC;
                break;
            case UPLOAD_MEAL:
                command = COMMAND.UPLOAD_MEAL;
                break;
            case UPLOAD_COMMENT:
                command = COMMAND.UPLOAD_COMMENT;
                break;
            case UPLOAD_COMMENT_HAPI:
                command = COMMAND.UPLOAD_COMMENT_HAPI;
                break;
            case UPLOAD_PHOTO:
                command = COMMAND.UPLOAD_PHOTO;
                break;
            case COACH_LIST:
                command = COMMAND.COACH_LIST;
                break;
            case GET_MEAL:
                command = COMMAND.GET_MEAL;
                break;
            case GET_MEALS_ALL:
                command = COMMAND.GET_MEALS_ALL;
                break;
            case GET_MEALS_HEALTHY:
                command = COMMAND.GET_MEALS_HEALTHY;
                break;
            case GET_MEALS_OK:
                command = COMMAND.GET_MEALS_OK;
                break;
            case GET_MEALS_UNHEALTHY:
                command = COMMAND.GET_MEALS_UNHEALTHY;
                break;
            case PUSH_REG:
                command = COMMAND.PUSH_REG;
                break;
            case GET_MESSAGES:
                command = COMMAND.GET_MESSAGES;
                break;
            case GET_MESSAGES_PAGED:
                command = COMMAND.GET_MESSAGES_PAGED;
                break;
            case POST_MESSAGES:
                command = COMMAND.POST_MESSAGES;
                break;
            case GET_NOTIFICATIONS:
                command = COMMAND.GET_NOTIFICATIONS;
                break;
            case GET_SURVEY_QUESTION:
                command = COMMAND.GET_SURVEY_QUESTION;
                break;
            case POST_SURVEY_ANSWER:
                command = COMMAND.POST_SURVEY_ANSWER;
                break;
            case POST_MARK_AS_READ:
                command = COMMAND.POST_MARK_AS_READ;
                break;
            case POST_MARK_ALL_AS_READ:
                command = COMMAND.POST_MARK_ALL_AS_READ;
                break;
            case POST_CLEAR_ALL:
                command = COMMAND.POST_CLEAR_ALL;
                break;
            case POST_HAPI_WATER:
                command = COMMAND.POST_HAPI_WATER;
                break;
            case GET_HAPI_WATER:
                command = COMMAND.GET_HAPI_WATER;
                break;
            case POST_ACCOUNT_SETTINGS:
                command = COMMAND.POST_ACCOUNT_SETTINGS;
                break;
            case POST_HAPI_MOMENT:
                command = COMMAND.POST_HAPI_MOMENT;
                break;
            case GET_HAPI_MOMENT:
                command = COMMAND.GET_HAPI_MOMENT;
                break;
            case GET_COACH_PROGRAM:
                command = COMMAND.GET_COACH_PROGRAM;
                break;
            case POST_PRODUCT_ORDER:
                command = COMMAND.POST_PRODUCT_ORDER;
                break;
            case SEND_ACCESS_CODE:
                command = COMMAND.SEND_ACCESS_CODE;
                break;
            case VALIDATE_ACCESS_CODE:
                command = COMMAND.VALIDATE_ACCESS_CODE;
                break;
            case GET_WORKOUT:
                command = COMMAND.GET_WORKOUT;
                break;
            case POST_WORKOUT:
                command = COMMAND.POST_WORKOUT;
                break;
            case GET_WEIGHT_GRAPH_NATIVE:
                command = COMMAND.GET_WEIGHT_GRAPH_NATIVE;
                break;
            case POST_WEIGHT_DATA:
                command = COMMAND.POST_WEIGHT_DATA;
                break;
            case UPDATE_WEIGHT_DATA:
                command = COMMAND.UPDATE_WEIGHT_DATA;
                break;
            case DELETE_WEIGHT_DATA:
                command = COMMAND.DELETE_WEIGHT_DATA;
                break;
            case GET_STEPS_GRAPH_NATIVE:
                command = COMMAND.GET_STEPS_GRAPH_NATIVE;
                break;
            case POST_STEPS_DATA:
                command = COMMAND.POST_STEPS_DATA;
                break;
            case UPDATE_STEPS_DATA:
                command = COMMAND.UPDATE_STEPS_DATA;
                break;
            case DELETE_STEPS_DATA:
                command = COMMAND.DELETE_STEPS_DATA;
                break;
            case POST_ACTIVITY_COMMENT:
                command = COMMAND.POST_ACTIVITY_COMMENT;
                break;
            case POST_HAPI4U:
                command = COMMAND.POST_HAPI4U;
                break;
            case GET_TIMELINE_ACTIVITY:
                command = COMMAND.GET_TIMELINE_ACTIVITY;
                break;
            case REGISTER_DEVICE:
                command = COMMAND.REGISTER_DEVICE;
                break;
            case POST_ACTIVITY_STEPS:
                command = COMMAND.POST_ACTIVITY_STEPS;
                break;
            default:
                break;
        }
        return command;
    }

    public static String getURL(SERVICES service) {
        String url = null;
        switch (service) {

            case PUSH_REG:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString_AnxaPunc + URL.pushURL) : (URL.qcDomainURLString_AnxaPunc + URL.pushURL);
                break;
            case LOGIN:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.loginURLString) : (URL.qcDomainURLString + URL.loginURLString);
                break;
            case REGISTRATION:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.registrationURLString) : (URL.qcDomainURLString + URL.registrationURLString);
                break;
            case FORGOTPASSWORD:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.forgotURLString) : (URL.qcDomainURLString + URL.forgotURLString);
                break;
            case GET_SYNC:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getSyncURLString) : (URL.qcDomainURLString + URL.getSyncURLString);
                break;
            case UPLOAD_MEAL:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.uploadMealURLString) : (URL.qcDomainURLString + URL.uploadMealURLString);
                break;
            case UPLOAD_COMMENT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.uploadCommentURLString) : (URL.qcDomainURLString + URL.uploadCommentURLString);
                break;
            case UPLOAD_COMMENT_HAPI:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.uploadCommentHapiURLString) : (URL.qcDomainURLString + URL.uploadCommentHapiURLString);
                break;
            case UPLOAD_PHOTO:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.uploadPhotoURLString) : (URL.qcDomainURLString + URL.uploadPhotoURLString);
                break;
            case UPDATE_PROFILE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.updateProfileURLString) : (URL.qcDomainURLString + URL.updateProfileURLString);
                break;
            case COACH_LIST:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.coachListURLString) : (URL.qcDomainURLString + URL.coachListURLString);
                break;
            case GET_MEAL:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getMealUrlString) : (URL.qcDomainURLString + URL.getMealUrlString);
                break;
            case GET_MEALS_ALL:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getAllMealsUrlString) : (URL.qcDomainURLString + URL.getAllMealsUrlString);
                break;
            case GET_PAYMENTKIT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.paymentURLWebString) : (URL.qcDomainURLString + URL.paymentURLWebString);
                break;
            case GET_QUESTIONKIT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.questionURLWebString) : (URL.qcDomainURLString + URL.questionURLWebString);
                break;
            case GET_MESSAGES:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getMessagesUrlString) : (URL.qcDomainURLString + URL.getMessagesUrlString);
                break;
            case GET_MESSAGES_PAGED:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getMessagesPagedUrlString) : (URL.qcDomainURLString + URL.getMessagesPagedUrlString);
                break;
            case POST_MESSAGES:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postMessagesUrlString) : (URL.qcDomainURLString + URL.postMessagesUrlString);
                break;
            case GET_NOTIFICATIONS:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getNotificationUrlString) : (URL.qcDomainURLString + URL.getNotificationUrlString);
                break;
            case ANXAMATS:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString_AnxaMats + URL.anxaMats) : (URL.qcDomainURLString_AnxaMats + URL.anxaMats);
                break;
            case URL_HELP_ABOUT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLHelpAbout) : (URL.qcDomainURLString + URL.URLHelpAbout);
                break;
            case URL_HELP_CONTACT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLHelpContactUs) : (URL.qcDomainURLString + URL.URLHelpContactUs);
                break;
            case URL_HELP_PRIVACY:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLHelpPrivacy) : (URL.qcDomainURLString + URL.URLHelpPrivacy);
                break;
            case URL_HELP_TERMS:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLHelpTerms) : (URL.qcDomainURLString + URL.URLHelpTerms);
                break;
            case URL_PROFILE:
               url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLProfile) : (URL.qcDomainURLString + URL.URLProfile);
                break;
            case URL_SUBSCR:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLSubscription) : (URL.qcDomainURLString + URL.URLSubscription);
                break;
            case URL_WEIGHTGRAPH:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.URLDtsWeightGraph) : (URL.qcDtsURLString + URL.URLDtsWeightGraph);
                break;
            case URL_STEPSGRAPH:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.URLDtsStepsGraph) : (URL.qcDtsURLString + URL.URLDtsStepsGraph);
                break;
            case URL_COACH:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.URLCoach) : (URL.qcDomainURLString + URL.URLCoach);
                break;
            case GET_SURVEY_QUESTION:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getSurveyURL) : (URL.qcDomainURLString + URL.getSurveyURL);
                break;
            case POST_SURVEY_ANSWER:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postAnswerURLWebString) : (URL.qcDomainURLString + URL.postAnswerURLWebString);
                break;
            case POST_MARK_AS_READ:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.markAsReadURL) : (URL.qcDomainURLString + URL.markAsReadURL);
                break;
            case POST_MARK_ALL_AS_READ:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.markAllAsReadURL) : (URL.qcDomainURLString + URL.markAllAsReadURL);
                break;
            case POST_CLEAR_ALL:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.clearAllURL) : (URL.qcDomainURLString + URL.clearAllURL);
                break;
            case GET_WEIGHT_GRAPH:
                if (ApplicationEx.language.equalsIgnoreCase("fr")){
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveFRDtsURLString + URL.URLDtsWeightGraph) : (URL.qcDtsURLString + URL.URLDtsWeightGraph);
                }else {
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.URLDtsWeightGraph) : (URL.qcDtsURLString + URL.URLDtsWeightGraph);
                }
                break;
            case GET_STEP_GRAPH:
                if (ApplicationEx.language.equalsIgnoreCase("fr")){
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveFRDtsURLString + URL.URLDtsStepsGraph) : (URL.qcDtsURLString + URL.URLDtsStepsGraph);
                }else {
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.URLDtsStepsGraph) : (URL.qcDtsURLString + URL.URLDtsStepsGraph);
                }
                break;
            case GET_COACH_PROGRAM:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getCoachProgramURL) : (URL.qcDomainURLString + URL.getCoachProgramURL);
                break;
            case POST_PRODUCT_ORDER:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postProductOrderUrlString) : (URL.qcDomainURLString + URL.postProductOrderUrlString);
                break;
            case SEND_ACCESS_CODE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.sendAccessCodeUrlString) : (URL.qcDomainURLString + URL.sendAccessCodeUrlString);
                break;
            case VALIDATE_ACCESS_CODE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.validateAccessCodeUrlString) : (URL.qcDomainURLString + URL.validateAccessCodeUrlString);
                break;
           case GET_COMMUNITY:
                if (ApplicationEx.language.equalsIgnoreCase("fr")){
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveFRDtsURLString + URL.communityURLString) : (URL.qcDtsURLString + URL.communityURLString);
                }else {
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.communityURLString) : (URL.qcDtsURLString + URL.communityURLString);
                }
                break;
            case GET_USER_SETTINGS:
                if (ApplicationEx.language.equalsIgnoreCase("fr")) {
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveFRDtsURLString + URL.settingsURLString) : (URL.qcDtsURLString + URL.settingsURLString);
                }else{
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.settingsURLString) : (URL.qcDtsURLString + URL.settingsURLString);
                }
                break;
            case POST_HAPI_MOMENT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postHapiMomentURL) : (URL.qcDomainURLString + URL.postHapiMomentURL);
                break;
            case GET_HAPI_MOMENT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getHAPIMomentURLString) : (URL.qcDomainURLString + URL.getHAPIMomentURLString);
                break;
            case GET_WORKOUT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getWorkoutUrlString) : (URL.qcDomainURLString + URL.getWorkoutUrlString);
                break;
            case POST_WORKOUT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postWorkoutUrlString) : (URL.qcDomainURLString + URL.postWorkoutUrlString);
                break;
            case POST_HAPI_WATER:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postHAPIWaterURL) : (URL.qcDomainURLString + URL.postHAPIWaterURL);
                break;
            case GET_HAPI_WATER:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getWaterURLString) : (URL.qcDomainURLString + URL.getWaterURLString);
                break;
            case GET_WEIGHT_GRAPH_NATIVE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getWeightGraph) : (URL.qcDomainURLString + URL.getWeightGraph);
                break;
            case POST_WEIGHT_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postWeightGraph) : (URL.qcDomainURLString + URL.postWeightGraph);
                break;
            case UPDATE_WEIGHT_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.updateWeightGraph) : (URL.qcDomainURLString + URL.updateWeightGraph);
                break;
            case DELETE_WEIGHT_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.deleteWeightGraph) : (URL.qcDomainURLString + URL.deleteWeightGraph);
                break;
            case GET_STEPS_GRAPH_NATIVE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getStepsGraph) : (URL.qcDomainURLString + URL.getStepsGraph);
                break;
            case POST_STEPS_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.postStepsGraph) : (URL.qcDomainURLString + URL.postStepsGraph);
                break;
            case UPDATE_STEPS_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.updateStepsGraph) : (URL.qcDomainURLString + URL.updateStepsGraph);
                break;
            case DELETE_STEPS_DATA:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.deleteStepsGraph) : (URL.qcDomainURLString + URL.deleteStepsGraph);
                break;
            case URL_WEEKLY_SUMMARY:
                if (ApplicationEx.language.equalsIgnoreCase("fr")) {
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveFRDtsURLString + URL.URLWeeklySummary) : (URL.qcDtsURLString + URL.URLWeeklySummary);
                }else{
                    url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDtsURLString + URL.URLWeeklySummary) : (URL.qcDtsURLString + URL.URLWeeklySummary);
                }
                break;
            case POST_ACTIVITY_COMMENT:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainSettingsURLString + URL.postActivityCommentURL) : (URL.qcDomainSettingsURLString + URL.postActivityCommentURL);
                break;
            case POST_HAPI4U:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainSettingsURLString + URL.postHAPI4uURL) : (URL.qcDomainSettingsURLString + URL.postHAPI4uURL);
                break;
            case GET_TIMELINE_ACTIVITY:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLString + URL.getTimelineActivity) : (URL.qcDomainURLString + URL.getTimelineActivity);
                break;
            case REGISTER_DEVICE:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLStringv2 + URL.registerDevice) : (URL.qcDomainURLString + URL.registerDevice);
                break;
            case POST_ACTIVITY_STEPS:
                url = (ConnectionType == CONNECTION.LIVE) ? (URL.liveDomainURLStringv2 + URL.postStepsGoogleFit) : (URL.qcDomainURLString + URL.postStepsGoogleFit);
                break;
            case GOOGLE_FIT_SUPPORT:
                url = URL.googleFitSupportURL;
                break;
        }
        return url;
    }
}