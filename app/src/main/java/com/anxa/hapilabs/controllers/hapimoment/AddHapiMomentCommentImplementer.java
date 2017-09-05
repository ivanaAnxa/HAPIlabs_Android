package com.anxa.hapilabs.controllers.hapimoment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.anxa.hapilabs.activities.HapiMomentViewActivity;
import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonCommentResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonCommunityCommentResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

import java.util.List;

/**
 * Created by aprilanxa on 03/03/2017.
 */

public class AddHapiMomentCommentImplementer {

    JsonCommentResponseHandler jsonResponseHandler;
    JsonCommunityCommentResponseHandler jsonCommunityCommentResponseHandler;
    Handler responseHandler;
    protected ProgressChangeListener progresslistener;
    Context context;
    String moodID = "";
    String tempCommentID;
    Comment comment;
    MealAddCommentListener listener;

    private static String APIKey = "An2x A3ct h9p1m36";

    public AddHapiMomentCommentImplementer(Context context, String username, String moodID, Comment comment, ProgressChangeListener progresslistener, MealAddCommentListener listener, String command) {
        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;
        this.moodID = moodID;

        jsonResponseHandler = new JsonCommentResponseHandler(hapimomentCommentAdd);

        jsonCommunityCommentResponseHandler = new JsonCommunityCommentResponseHandler(hapimomentCommunityCommentAdd);

        if (command.equalsIgnoreCase("community")) {
            tempCommentID = comment.comment_id;

            this.comment = comment;
            String data = JsonRequestWriter.getInstance().createCommentCommunityJson(comment, username, moodID);
            addCommunityCommentService(username, data, jsonCommunityCommentResponseHandler);

        } else if (command.equalsIgnoreCase("meal")) {
            tempCommentID = comment.comment_id;
            this.comment = comment;

            String data = JsonRequestWriter.getInstance().createCommentJson(comment);
            addCommentService(username, data, jsonResponseHandler);

        }else if (command.equalsIgnoreCase("hapi4u")) {
            String data = JsonRequestWriter.getInstance().createHAPI4UJson(username, moodID);
            postHAPI4UService(username, data, jsonCommunityCommentResponseHandler);

        }
    }

    //where data = xml string format post data
    public void addCommunityCommentService(String username, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_ACTIVITY_COMMENT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("Id", moodID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_ACTIVITY_COMMENT) + username + moodID, APIKey));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    public void addCommentService(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.UPLOAD_COMMENT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("meal_id", moodID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.UPLOAD_COMMENT) + username + moodID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    public void postHAPI4UService(String username, String data, Handler responseHandler) {

        System.out.println("hapi4u json: " + data);

        String url = WebServices.getURL(WebServices.SERVICES.POST_HAPI4U);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_HAPI4U) + username, APIKey));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }


    final Handler hapimomentCommunityCommentAdd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    HapiMoment hapiMomentWComment = new HapiMoment();
                    hapiMomentWComment = ApplicationEx.getInstance().currentHapiMoment;

                    if(moodID.length()>0) {
                        //find the hapimoment
                        for (int i = 0; i < ApplicationEx.getInstance().hapiMomentList.size(); i++) {
                            HapiMoment hapiMoment = ApplicationEx.getInstance().hapiMomentList.get(i);
                            if (hapiMoment.mood_id == Integer.parseInt(moodID)) {
                                if (hapiMomentWComment != null) {
                                    //UPDATE COMMENT STATUS
                                    List<Comment> comments = hapiMomentWComment.comments;
                                    for (int j = 0; j < comments.size(); j++) {
                                        Comment commentSuccess = comments.get(j);
                                        if (tempCommentID == commentSuccess.comment_id) {
                                            commentSuccess.status = Comment.STATUS.SYNC_COMMENT;
                                            hapiMomentWComment.comments.remove(j);
                                            hapiMomentWComment.comments.add(commentSuccess);
                                            j = comments.size() + 1;
                                        }
                                    }
                                }
                                ApplicationEx.getInstance().hapiMomentList.set(i, hapiMomentWComment);
                            }
                        }
                    }

                    listener.uploadMealCommentSuccess("SUCCESS");

                    break;
                case JsonDefaultResponseHandler.ERROR:

                    HapiMoment hapiMomentWComment_e = new HapiMoment();

                    //find the hapimoment
                    for (int i = 0; i < ApplicationEx.getInstance().hapiMomentList.size(); i++) {
                        HapiMoment hapiMoment = ApplicationEx.getInstance().hapiMomentList.get(i);
                        if (hapiMoment.mood_id == Integer.parseInt(moodID)) {
                            if (hapiMomentWComment_e != null) {
                                //UPDATE COMMENT STATUS
                                List<Comment> comments = hapiMomentWComment_e.comments;
                                for (int j = 0; j < comments.size(); j++) {
                                    Comment commentSuccess = comments.get(j);
                                    if (tempCommentID == commentSuccess.comment_id) {
                                        commentSuccess.status = Comment.STATUS.FAILED_COMMENTUPLOAD;
                                        hapiMomentWComment_e.comments.remove(j);
                                        hapiMomentWComment_e.comments.add(commentSuccess);
                                        j = comments.size() + 1;
                                    }
                                }
                            }
                            ApplicationEx.getInstance().hapiMomentList.set(i, hapiMomentWComment_e);
                        }
                    }


                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonCommunityCommentResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonCommunityCommentResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.uploadMealCommentFailedWithError(mesObj, moodID);

                    //update listener

                    break;
            }//end Switch
        }
    };

    final Handler hapimomentCommentAdd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    HapiMoment hapiMomentWComment = new HapiMoment();
                    hapiMomentWComment = ApplicationEx.getInstance().currentHapiMoment;

                    if(moodID.length()>0) {
                        //find the hapimoment
                        for (int i = 0; i < ApplicationEx.getInstance().hapiMomentList.size(); i++) {
                            HapiMoment hapiMoment = ApplicationEx.getInstance().hapiMomentList.get(i);
                            if (hapiMoment.mood_id == Integer.parseInt(moodID)) {
                                if (hapiMomentWComment != null) {
                                    //UPDATE COMMENT STATUS
                                    List<Comment> comments = hapiMomentWComment.comments;
                                    for (int j = 0; j < comments.size(); j++) {
                                        Comment commentSuccess = comments.get(j);
                                        if (tempCommentID == commentSuccess.comment_id) {
                                            commentSuccess.status = Comment.STATUS.SYNC_COMMENT;
                                            hapiMomentWComment.comments.remove(j);
                                            hapiMomentWComment.comments.add(commentSuccess);
                                            j = comments.size() + 1;
                                        }
                                    }
                                }
                                ApplicationEx.getInstance().hapiMomentList.set(i, hapiMomentWComment);
                            }
                        }

                        //update listener
                        listener.uploadMealCommentSuccess("SUCCESS");
                    }

                    break;
                case JsonDefaultResponseHandler.ERROR:

                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.uploadMealCommentFailedWithError(mesObj, moodID);

                    //update listener

                    break;
            }//end Switch
        }
    };

}
