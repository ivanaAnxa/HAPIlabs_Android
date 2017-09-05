package com.anxa.hapilabs.controllers.addmeal;


import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonCommentResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonCommunityCommentResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Comment.STATUS;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;

public class AddMealCommentImplementer {

    JsonCommentResponseHandler jsonResponseHandler;
    JsonCommunityCommentResponseHandler jsonCommunityCommentResponseHandler;

    Handler responseHandler;
    protected ProgressChangeListener progresslistener;

    MealAddCommentListener listener;

    Context context;

    String mealID = "";
    String tempCommentID;
    Comment comment;

    private static String APIKey = "An2x A3ct h9p1m36";


    public AddMealCommentImplementer(Context context, String username, String mealid, Comment comment, ProgressChangeListener progresslistener, MealAddCommentListener listener, String command) {

        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;
        mealID = mealid;


        jsonResponseHandler = new JsonCommentResponseHandler(mealCommentAdd);
        jsonCommunityCommentResponseHandler = new JsonCommunityCommentResponseHandler(mealCommunityCommentAdd);

        if (command.equalsIgnoreCase("community")){
            tempCommentID = comment.comment_id;

            this.comment = comment;
            String data = JsonRequestWriter.getInstance().createCommentCommunityJson(comment, username, mealid);
            addCommunityCommentService(username, data, jsonCommunityCommentResponseHandler);

        }else if (command.equalsIgnoreCase("hapi4u")){
            String data = JsonRequestWriter.getInstance().createHAPI4UJson(username, mealid);
            postHAPI4UService(username, data, jsonCommunityCommentResponseHandler);

        }else {
            tempCommentID = comment.comment_id;

            this.comment = comment;
            String data = JsonRequestWriter.getInstance().createCommentJson(comment);
            addCommentService(username, data, jsonResponseHandler);
        }
    }

    //where data = xml string format post data
    public void addCommentService(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.UPLOAD_COMMENT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("meal_id", mealID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.UPLOAD_COMMENT) + username + mealID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    public void addCommunityCommentService(String username, String data, Handler responseHandler) {

        System.out.println("communitycomment json: " + data);

        String url = WebServices.getURL(SERVICES.POST_ACTIVITY_COMMENT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("Id", mealID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.POST_ACTIVITY_COMMENT) + username + mealID, APIKey));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    public void postHAPI4UService(String username, String data, Handler responseHandler) {

        System.out.println("hapi4u json: " + data);

        String url = WebServices.getURL(SERVICES.POST_HAPI4U);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.POST_HAPI4U) + username , APIKey));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }


    final Handler mealCommentAdd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    //find the meal

                    Meal mealWComment = ApplicationEx.getInstance().tempList.get(mealID);

                    if (mealWComment != null) {

                        //upDATE COMMENT STATUS
                        List<Comment> comments = mealWComment.comments;
                        for (int i = 0; i < comments.size(); i++) {

                            Comment commentSuccess = comments.get(i);
                            if (tempCommentID == commentSuccess.comment_id) {
                                commentSuccess.status = STATUS.SYNC_COMMENT;
                                mealWComment.comments.remove(i);
                                mealWComment.comments.add(commentSuccess);

                                i = comments.size() + 1;

                            }
                        }


                        //update templist
                        ApplicationEx.getInstance().tempList.put(mealID, mealWComment);

                        //update listener
                        listener.uploadMealCommentSuccess("SUCCESS");
                    }

                    break;
                case JsonDefaultResponseHandler.ERROR:

                    //update the UI for error display
                    Meal mealWCommentFailed = ApplicationEx.getInstance().tempList.get(mealID);
                    if (mealWCommentFailed != null) {
                        //upDATE COMMENT STATUS
                        List<Comment> comments = mealWCommentFailed.comments;
                        for (int i = 0; i < comments.size(); i++) {
                            Comment commentFail = comments.get(i);

                            if (tempCommentID == commentFail.comment_id) {
                                commentFail.status = STATUS.FAILED_COMMENTUPLOAD;
                                mealWCommentFailed.comments.remove(i);
                                mealWCommentFailed.comments.add(commentFail);
                                i = comments.size() + 1;

                            }
                        }

                    }

                    ApplicationEx.getInstance().tempList.put(mealID, mealWCommentFailed);


                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.uploadMealCommentFailedWithError(mesObj, mealID);

                    //update listener

                    break;
            }//end Switch
        }
    };

    final Handler mealCommunityCommentAdd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    //find the meal
                    Meal mealWComment = ApplicationEx.getInstance().tempList.get(mealID);
                    if (mealWComment != null) {
                        //UPDATE COMMENT STATUS
                        List<Comment> comments = mealWComment.comments;
                        for (int i = 0; i < comments.size(); i++) {
                            Comment commentSuccess = comments.get(i);
                            if (tempCommentID == commentSuccess.comment_id) {
                                commentSuccess.status = STATUS.SYNC_COMMENT;
                                mealWComment.comments.remove(i);
                                mealWComment.comments.add(commentSuccess);
                                i = comments.size() + 1;
                            }
                        }

                        //update templist
                        ApplicationEx.getInstance().tempList.put(mealID, mealWComment);
                        //update listener
                        listener.uploadMealCommentSuccess("SUCCESS");
                    }

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //update the UI for error display
                    Meal mealWCommentFailed = ApplicationEx.getInstance().tempList.get(mealID);
                    if (mealWCommentFailed != null) {
                        //upDATE COMMENT STATUS
                        List<Comment> comments = mealWCommentFailed.comments;
                        for (int i = 0; i < comments.size(); i++) {
                            Comment commentFail = comments.get(i);

                            if (tempCommentID == commentFail.comment_id) {
                                commentFail.status = STATUS.FAILED_COMMENTUPLOAD;
                                mealWCommentFailed.comments.remove(i);
                                mealWCommentFailed.comments.add(commentFail);
                                i = comments.size() + 1;
                            }
                        }
                    }

                    ApplicationEx.getInstance().tempList.put(mealID, mealWCommentFailed);

                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.uploadMealCommentFailedWithError(mesObj, mealID);

                    //update listener

                    break;
            }//end Switch
        }
    };


}
