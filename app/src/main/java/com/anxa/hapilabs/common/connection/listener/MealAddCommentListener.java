package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.MessageObj;

public interface MealAddCommentListener {

    public void uploadMealCommentSuccess(String response);

    public void uploadMealCommentFailedWithError(MessageObj response, String entryID);

    public void uploadMealCommentRefresh(Comment comment);

    public void uploadMealCommentDelete(Comment comment);
}