package com.anxa.hapilabs.ui;


import com.hapilabs.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class Must extends with Dialog
 */

/** Implement onClickListener to dismiss dialog when OK Button is pressed */
public class CustomDialog extends Dialog {


    TextView otherButton;
    TextView yesButton;
    TextView noButton;


    public CustomDialog(Context context,
                        String otherButtonText,
                        String yesButtonText,
                        String noButtonText,
                        Boolean isClose,
                        String message,
                        String title,
                        android.view.View.OnClickListener listener) {
        super(context);
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog);

        if (title == null)
            ((LinearLayout) findViewById(R.id.title_container)).setVisibility(View.GONE);
        else {
            ((LinearLayout) findViewById(R.id.title_container)).setVisibility(View.VISIBLE);

        }

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.messagedetail)).setText(message);

        if (!isClose) { //hide close button
            ((ImageView) findViewById(R.id.CloseButton)).setVisibility(View.GONE);
        } else {
            ((ImageView) findViewById(R.id.CloseButton)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.CloseButton)).setOnClickListener(listener);
        }

        otherButton = (TextView) findViewById(R.id.OtherButton);
        yesButton = (TextView) findViewById(R.id.YesButton);
        noButton = (TextView) findViewById(R.id.NoButton);


        if (otherButtonText != null) {
            otherButton.setOnClickListener(listener);
            otherButton.setText(otherButtonText);
            otherButton.setVisibility(View.VISIBLE);

            yesButton.setVisibility(View.GONE);
            noButton.setVisibility(View.GONE);
            return;
        }


        otherButton.setVisibility(View.GONE);

        if (yesButtonText == null || yesButtonText.length() == 0) {
            yesButton.setVisibility(View.GONE);
        } else {
            yesButton.setOnClickListener(listener);
            yesButton.setText(yesButtonText);
        }


        if (noButtonText == null || noButtonText.length() == 0) {
            noButton.setVisibility(View.GONE);
        } else {
            noButton.setOnClickListener(listener);
            noButton.setText(noButtonText);
        }
    }
}