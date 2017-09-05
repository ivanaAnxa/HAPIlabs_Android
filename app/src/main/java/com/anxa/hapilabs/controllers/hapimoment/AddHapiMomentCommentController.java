package com.anxa.hapilabs.controllers.hapimoment;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Comment;

/**
 * Created by aprilanxa on 03/03/2017.
 */

public class AddHapiMomentCommentController {

    Context context;
    AddHapiMomentCommentImplementer addHapiMomentCommentImplementer;
    protected ProgressChangeListener progresslistener;
    MealAddCommentListener listener;

    public AddHapiMomentCommentController(Context context, ProgressChangeListener progresslistener, MealAddCommentListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.listener = listener;
    }

    public void uploadMealCommunityComment(String moodID, Comment comment, String username) {
        addHapiMomentCommentImplementer = new AddHapiMomentCommentImplementer(context, username, moodID, comment, progresslistener, listener, "community");
    }

    public void uploadMealComment(String moodID, Comment comment, String username) {
        addHapiMomentCommentImplementer = new AddHapiMomentCommentImplementer(context, username, moodID, comment, progresslistener, listener, "meal");
    }

    public void postHAPI4U(String moodID, Comment comment, String username) {
        addHapiMomentCommentImplementer = new AddHapiMomentCommentImplementer(context, username, moodID, comment, progresslistener, listener, "hapi4u");
    }
}
