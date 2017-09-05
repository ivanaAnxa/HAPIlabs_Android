package com.anxa.hapilabs.controllers.addmeal;


import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.Comment;


public class AddMealCommentController {

    Context context;
    AddMealCommentImplementer addMealImpl;
    protected ProgressChangeListener progresslistener;
    MealAddCommentListener listener;

    public AddMealCommentController(Context context, ProgressChangeListener progresslistener, MealAddCommentListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.listener = listener;
    }

    public void uploadMealComment(String mealid, Comment comment, String username) {
        addMealImpl = new AddMealCommentImplementer(context, username, mealid, comment, progresslistener, listener, "meal");

    }

    public void uploadMealCommunityComment(String mealid, Comment comment, String username) {
        addMealImpl = new AddMealCommentImplementer(context, username, mealid, comment, progresslistener, listener, "community");
    }

    public void postHAPI4U(String mealid, Comment comment, String username) {
        addMealImpl = new AddMealCommentImplementer(context, username, mealid, comment, progresslistener, listener, "hapi4u");
    }
}
