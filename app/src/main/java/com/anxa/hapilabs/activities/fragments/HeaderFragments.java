package com.anxa.hapilabs.activities.fragments;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Context;

/*WE SHOULD NOT BE TOUCHING THIS FILE*/


@SuppressLint("NewApi")
public class HeaderFragments extends RelativeLayout implements OnClickListener {

    Context context;

    OnClickListener listener;

    View leftHeader;
    View rightHeader;
    View centerHeader;
    View header;

    RelativeLayout badgeLayout;

    public HeaderFragments(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView(null);

    }

    public HeaderFragments(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        onCreateView(null);
    }


    public HeaderFragments(Context context) {
        super(context);
        this.context = context;
        onCreateView(null);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;

    }

    public void onCreateView(ViewGroup container) {

        View v = inflate(context, R.layout.fragment_main_header, null);

        try {
            removeView(v);
        } catch (Exception e) {
        }

        addView(v);
    }

    public View getHeader() {
        return header;


    }

    public void setHeader(String textLeft, String textRight, String textCenter, int imgLeft, int imgRight, int imgCenter, OnClickListener onclick) {

        /***RIGHT TEXT***/
        if (textRight != null) {//textView

            //set text
            rightHeader = (TextView) findViewById(R.id.header_right_tv);//textview
            ((TextView) rightHeader).setText(textRight);
            ((TextView) rightHeader).setVisibility(View.VISIBLE);
            ((TextView) rightHeader).setOnClickListener(onclick);
            //hide image
            ((ImageView) findViewById(R.id.header_right)).setVisibility(View.GONE);

        } else if (imgRight > -1) {//Image only
            //set image
            rightHeader = (ImageView) findViewById(R.id.header_right); //imageview
            ((ImageView) rightHeader).setImageResource(imgRight);
            ((ImageView) rightHeader).setVisibility(View.VISIBLE);
            ((ImageView) rightHeader).setOnClickListener(onclick);

            //hide text
            ((TextView) findViewById(R.id.header_right_tv)).setVisibility(View.GONE);

        } else {
            ((TextView) findViewById(R.id.header_right_tv)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_right)).setVisibility(View.INVISIBLE);//textview
        }

        /***LEFT TEXT***/
        if ((textLeft != null) && (imgLeft > -1)) {//textView & image

            //set text
            leftHeader = (TextView) findViewById(R.id.header_left_tv);//textview
            ((TextView) leftHeader).setText(textLeft);
            ((TextView) leftHeader).setVisibility(View.VISIBLE);
            ((TextView) leftHeader).setOnClickListener(onclick);

            //set image
            leftHeader = (ImageView) findViewById(R.id.header_left); //imageview
            ((ImageView) leftHeader).setImageResource(imgLeft);
            ((ImageView) leftHeader).setVisibility(View.VISIBLE);
            ((ImageView) leftHeader).setOnClickListener(onclick);


        } else if (textLeft != null) {//textView

            //set text
            leftHeader = (TextView) findViewById(R.id.header_left_tv);//textview
            ((TextView) leftHeader).setText(textLeft);
            ((TextView) leftHeader).setVisibility(View.VISIBLE);
            ((TextView) leftHeader).setOnClickListener(onclick);


            //hide image
            ((ImageView) findViewById(R.id.header_left)).setVisibility(View.GONE);

        } else if (imgLeft > -1) {//Image only
            //set image
            leftHeader = (ImageView) findViewById(R.id.header_left); //imageview
            ((ImageView) leftHeader).setImageResource(imgLeft);
            ((ImageView) leftHeader).setVisibility(View.VISIBLE);
            ((ImageView) leftHeader).setOnClickListener(onclick);


            //hide text
            ((TextView) findViewById(R.id.header_left_tv)).setVisibility(View.GONE);

            if (textCenter!=null) {
                if (textCenter.equalsIgnoreCase(getResources().getString(R.string.MYMEALS_TITLE))) {
                    if (ApplicationEx.getInstance().unreadNotifications>0){
                        TextView textCount = (TextView)findViewById(R.id.notif_count);
                        textCount.setText(""+ApplicationEx.getInstance().unreadNotifications);

                        findViewById(R.id.badge_notif).setVisibility(View.VISIBLE);
                        findViewById(R.id.badge_notif).setOnClickListener(onclick);
                        leftHeader.setVisibility(View.GONE);

                    }else{
                        findViewById(R.id.badge_notif).setVisibility(View.GONE);
                        //set image
                        leftHeader = (ImageView) findViewById(R.id.header_left);
                        ((ImageView) leftHeader).setImageResource(imgLeft);
                        leftHeader.setVisibility(View.VISIBLE);
                        leftHeader.setOnClickListener(onclick);
                    }


                } else {
                    findViewById(R.id.badge_notif).setVisibility(View.GONE);
                    leftHeader.setVisibility(View.VISIBLE);
                }
            }

        } else {
            ((TextView) findViewById(R.id.header_left_tv)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_left)).setVisibility(View.INVISIBLE);//textview
        }

        /***CENTER TEXT***/
        if (textCenter != null) {//textView
            //set text
            centerHeader = (TextView) findViewById(R.id.header_title);//textview
            ((TextView) centerHeader).setText(textCenter);
            ((TextView) centerHeader).setVisibility(View.VISIBLE);

            //hide image
            ((ImageView) findViewById(R.id.header_title_iv)).setVisibility(View.GONE);

        } else if (imgCenter > -1) {//Image only
            //set image
            centerHeader = (ImageView) findViewById(R.id.header_title_iv); //imageview
            ((ImageView) centerHeader).setImageResource(imgCenter);
            ((ImageView) centerHeader).setVisibility(View.VISIBLE);

            //hide text
            ((TextView) findViewById(R.id.header_title)).setVisibility(View.GONE);

        } else {
            ((TextView) findViewById(R.id.header_title)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_title_iv)).setVisibility(View.INVISIBLE);//textview

        }

    }

    public void setLeftHeader(String text, int resId, OnClickListener onclick) {


        if ((text != null) && (resId > -1)) {//textView & image
            //set text
            leftHeader = (TextView) findViewById(R.id.header_left_tv);//textview
            ((TextView) leftHeader).setText(text);
            ((TextView) leftHeader).setVisibility(View.VISIBLE);
            ((TextView) leftHeader).setOnClickListener(onclick);

            //set image
            leftHeader = (ImageView) findViewById(R.id.header_left); //imageview
            ((ImageView) leftHeader).setImageResource(resId);
            ((ImageView) leftHeader).setVisibility(View.VISIBLE);
            ((ImageView) leftHeader).setOnClickListener(onclick);


        } else if (text != null) {//textView

            //set text
            leftHeader = (TextView) findViewById(R.id.header_left_tv);//textview
            ((TextView) leftHeader).setText(text);
            ((TextView) leftHeader).setVisibility(View.VISIBLE);
            ((TextView) leftHeader).setOnClickListener(onclick);


            //hide image
            ((ImageView) findViewById(R.id.header_left)).setVisibility(View.GONE);

        } else if (resId > -1) {//Image only
            //set image
            leftHeader = (ImageView) findViewById(R.id.header_left); //imageview
            ((ImageView) leftHeader).setImageResource(resId);
            ((ImageView) leftHeader).setVisibility(View.VISIBLE);
            ((ImageView) leftHeader).setOnClickListener(onclick);


            //hide text
            ((TextView) findViewById(R.id.header_left_tv)).setVisibility(View.GONE);

        } else {
            ((TextView) findViewById(R.id.header_left_tv)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_left)).setVisibility(View.INVISIBLE);//textview


        }


    }

    public void setRightHeader(String text, int resId, OnClickListener onclick) {

        if (text != null) {//textView

            //set text
            rightHeader = (TextView) findViewById(R.id.header_right_tv);//textview
            ((TextView) rightHeader).setText(text);
            ((TextView) rightHeader).setVisibility(View.VISIBLE);

            ((TextView) rightHeader).setOnClickListener(onclick);

            //hide image
            ((ImageView) findViewById(R.id.header_right)).setVisibility(View.GONE);

        } else if (resId > -1) {//Image only
            //set image
            rightHeader = (ImageView) findViewById(R.id.header_right); //imageview
            ((ImageView) rightHeader).setImageResource(resId);
            ((ImageView) rightHeader).setVisibility(View.VISIBLE);
            ((ImageView) rightHeader).setOnClickListener(onclick);


            //hide text
            ((TextView) findViewById(R.id.header_right_tv)).setVisibility(View.GONE);

        } else {
            ((TextView) findViewById(R.id.header_right_tv)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_right)).setVisibility(View.INVISIBLE);//textview


        }

    }

    public void setCenterHeader(String text, int resId, OnClickListener onclick) {
        if (text != null) {//textView
            //set text
            centerHeader = (TextView) findViewById(R.id.header_title);//textview
            ((TextView) centerHeader).setText(text);
            ((TextView) centerHeader).setVisibility(View.VISIBLE);

            //hide image
            ((ImageView) findViewById(R.id.header_title_iv)).setVisibility(View.GONE);

        } else if (resId > -1) {//Image only
            //set image
            centerHeader = (ImageView) findViewById(R.id.header_title_iv); //imageview
            ((ImageView) centerHeader).setImageResource(resId);
            ((ImageView) centerHeader).setVisibility(View.VISIBLE);

            //hide text
            ((TextView) findViewById(R.id.header_title)).setVisibility(View.GONE);

        } else {
            ((TextView) findViewById(R.id.header_title)).setVisibility(View.INVISIBLE);//textview
            ((ImageView) findViewById(R.id.header_title_iv)).setVisibility(View.INVISIBLE);//textview
        }
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }


}