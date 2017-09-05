package com.anxa.hapilabs.controllers.messages;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.util.ApplicationEx;

public class MessageController {
    Context context;

    GetMessageImplementer msgImplementer;
    PostMessageImplementer postMsgImplementer;
    TimerTask mTimerTask;
    Handler timeHandler;
    Timer t;
    Handler handler;

    GetMessagesListener listener;

    public MessageController(Context context, GetMessagesListener listener) {
        this.context = context;
        this.listener = listener;
        handler = new Handler();
    }

//    public void getMessages(String userId) {
//        Date fromDate = AppUtil.getCurrentDate(-120);
//        msgImplementer = new GetMessageImplementer(context, userId, "", String.valueOf(AppUtil.dateToUnixTimestamp(fromDate)), listener);
//    }


    public void getLatest(String after, String limit){
        msgImplementer = new GetMessageImplementer(context, ApplicationEx.getInstance().userProfile.getRegID(), "", after, limit, listener);
    }

    public void getPrevious(String before, String limit){
        msgImplementer = new GetMessageImplementer(context, ApplicationEx.getInstance().userProfile.getRegID(), before, "", limit, listener);
    }

    public void postMessages(String userId, String message) {
        postMsgImplementer = new PostMessageImplementer(context, userId, message, listener);
    }
}
