package com.anxa.hapilabs.models;

import java.util.Date;
import java.util.UUID;


public class Comment {

    //comment message
    public String message;

    //comment comment_id
    public String comment_id;

    //comment reference meal id
    public String meal_id;

    public Date timestamp;

    /*Value can be
    0 - community
    1 = approval
    2 = coach comment
    3 = user comment
    4 - marketing comment

    Any other number(user iD of the person that comment, can be use in the future)
    */
    public int comment_type;

    /**
     * comment isHApi
     **/
    public Boolean isHAPI = false;//default is false
    public Coach coach;
    public STATUS status;
    public CommentUser user;

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


    //generate a temp photo id
    public void UniqueIDgen() {

        UUID uniqueKey = UUID.randomUUID();
        setId(uniqueKey.toString());

    }

    public void setId(String id) {
        this.comment_id = id;
    }


    public Comment() {
        UniqueIDgen();
    }

}


