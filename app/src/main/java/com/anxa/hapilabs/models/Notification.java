package com.anxa.hapilabs.models;

import java.util.Date;

public class Notification {

    public static enum NOTIFICATION_TYPE {
        NOTIFICATION_MEAL_APPROVAL_COACH(1),
        NOTIFICATION_MEAL_COMMENT_COACH(2),
        NOTIFICATION_MESSAGE_COACH(3),
        NOTIFICATION_CHECKED_WATER(4),
        NOTIFICATION_CHECKED_EXERCISE(5),
        NOTIFICATION_CHECKED_MOMENT(6),
        NOTIFICATION_MEAL_COMMENT_RATING(7),
        NOTIFICATION_MEAL_COMMENT_COMMUNITY(8),
        NOTIFICATION_MEAL_HAPI4U_COMMUNITY(9),
        NOTIFICATION_SYSTEM_MESSAGE(20);

        private final int value;

        private NOTIFICATION_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static NOTIFICATION_TYPE getNOTIFTYPEValue(int value) {
        NOTIFICATION_TYPE returnvalue = NOTIFICATION_TYPE.NOTIFICATION_MEAL_APPROVAL_COACH; // default returnvalue
        for (final NOTIFICATION_TYPE type : NOTIFICATION_TYPE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }


    public static enum NOTIFICATION_STATE {
        READ(1),
        UNREAD(2),
        NOTFOUND(3);

        private final int value;

        private NOTIFICATION_STATE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public static NOTIFICATION_STATE getNOTIFSTATEValue(int value) {
        NOTIFICATION_STATE returnvalue = NOTIFICATION_STATE.UNREAD; // default returnvalue
        for (final NOTIFICATION_STATE type : NOTIFICATION_STATE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    public NOTIFICATION_TYPE notificationType;

    public NOTIFICATION_STATE notificationState;

    public int coachID;

    public int notificationID;

    public String ref_id;

    public String coachAvatarURL;

    public String ref_type;

    public String coachMessage;

    public String mealID;

    public int badge;

    public Date timestamp;

    public boolean is_community;

    public Notification() {
        notificationState = NOTIFICATION_STATE.UNREAD; //default is unread.
    }


}
