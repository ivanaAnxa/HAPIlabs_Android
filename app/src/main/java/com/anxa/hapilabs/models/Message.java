package com.anxa.hapilabs.models;

import java.util.Date;


public class Message {

//	//comment message
//	public String message;
//
//	//comment comment_id
//	public String comment_id;
//
//	public Date timestamp;
//
//	
//	public String avatar_url;
//
//	public STATUS status;
//	  //message types
//	  //2 = coach comment
//	 	//3 = user comment
//	
//	public int comment_type;
//
//	
//	public static enum STATUS{
//		ONGOING_COMMENTUPLOAD,
//		SYNC_COMMENT,
//		FAILED_COMMENTUPLOAD
//	}

    public Message() {

    }

    public STATUS status;
    public String message_body;
    public String message_id;
    public String mediaUrl;
    public Date timestamp;
    public Coach coach;
    public String coachID;
    public Boolean is_coach_message = false;//default is false

    public static enum STATUS {
        ONGOING_COMMENTUPLOAD(1), SYNC_COMMENT(2), FAILED_COMMENTUPLOAD(3);
        private final int value;

        private STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;


    public static STATUS getSTATUSValue(int value) {
        STATUS returnvalue = STATUS.ONGOING_COMMENTUPLOAD; // default returnvalue
        for (final STATUS type : STATUS.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }


}
