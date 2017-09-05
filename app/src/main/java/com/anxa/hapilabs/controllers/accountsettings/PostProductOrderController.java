package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PostProductOrderListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 19/08/2016.
 */
public class PostProductOrderController {
    Context context;

    protected ProgressChangeListener progressChangeListener;
    PostProductOrderImplementer postProductOrderImplementer;
    PostProductOrderListener postProductOrderListener;

    public PostProductOrderController(Context context,
                                         ProgressChangeListener progressChangeListener,
                                         PostProductOrderListener postProductOrderListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.postProductOrderListener = postProductOrderListener;
    }


    public void postProductOrder(String orderId) {

        postProductOrderImplementer = new PostProductOrderImplementer(context, orderId, progressChangeListener, postProductOrderListener);

    }
}
