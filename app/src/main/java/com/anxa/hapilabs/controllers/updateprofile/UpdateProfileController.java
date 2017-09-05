package com.anxa.hapilabs.controllers.updateprofile;

import android.app.Activity;
import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.UpdateProfileListener;
import com.anxa.hapilabs.models.UserProfile;

/**
 * Created by aprilanxa on 4/11/2016.
 */
public class UpdateProfileController implements ProgressChangeListener{

    protected ProgressChangeListener progressChangeListener;

    Context context;
    UpdateProfileImplementer updateProfileImplementer;
    UpdateProfileListener updateProfileListener;
    UserProfile userProfile;

    public UpdateProfileController(Context context, ProgressChangeListener progressChangeListener, UpdateProfileListener updateProfileListener){
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.updateProfileListener = updateProfileListener;
    }

    public void updateProfile(final UserProfile userProfile){
        this.userProfile = userProfile;

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProfileImplementer = new UpdateProfileImplementer(context, userProfile, progressChangeListener, updateProfileListener);
            }
        });
    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopProgress() {
        // TODO Auto-generated method stub
    }

}
