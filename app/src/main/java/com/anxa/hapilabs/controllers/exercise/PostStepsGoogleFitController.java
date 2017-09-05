package com.anxa.hapilabs.controllers.exercise;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PostStepsGoogleFitListener;
import com.anxa.hapilabs.common.connection.listener.PostWorkoutListener;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Workout;

import java.util.ArrayList;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public class PostStepsGoogleFitController {
    Context context;

    PostStepsGoogleFitImplementer postStepsGoogleFitImplementer;

    PostStepsGoogleFitListener listener;

    public PostStepsGoogleFitController(Context context, PostStepsGoogleFitListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void postSteps(String userId, ArrayList<Workout> stepsDataArray) {
        postStepsGoogleFitImplementer = new PostStepsGoogleFitImplementer(context, userId, stepsDataArray, listener);
    }
}
