package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.lang.String;
import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.UserProfile;


/**
 * Created by elaineanxa on 4/5/16.
 */
public class TargetWeightActivity extends HAPIActivity implements SeekBar.OnSeekBarChangeListener
{
    /*Init the variables*/
    private UserProfile userProfile;

    private int targetWeightValue;

    private SeekBar seekBarTop;
    private SeekBar seekBarBottom;

    private final int MIN_WEIGHT_VALUE = 40;
    private final int FONT_SIZE = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_target_weight);

        userProfile = ApplicationEx.getInstance().userProfile;
        updateFooterButton(getResources().getString(R.string.btn_continue), this);


        int startWeightIntValue = 90000;
        /*Get Start Weight Value*/
        if (userProfile.getStart_weight()!=null) {
             startWeightIntValue = Integer.parseInt(userProfile.getStart_weight());
        }else{
            userProfile.setStart_weight(Integer.toString(startWeightIntValue));
        }

        seekBarTop = (SeekBar) findViewById(R.id.seekBarTop);
        seekBarBottom = (SeekBar) findViewById(R.id.seekBarBottom);

        seekBarBottom.setOnSeekBarChangeListener(this);
        seekBarTop.setOnSeekBarChangeListener(this);

        int startWeightProgress = startWeightIntValue/1000;

        if (startWeightProgress <45){
            seekBarTop.setProgress(MIN_WEIGHT_VALUE - 40);
        }else {
            seekBarTop.setProgress(startWeightProgress - 45);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerbutton) {

            int targetWeightInGrams = targetWeightValue;
            targetWeightInGrams = targetWeightInGrams * 1000;
            userProfile.setTarget_weight(Integer.toString(targetWeightInGrams));

            ApplicationEx.getInstance().userProfile = userProfile;

            Intent i = new Intent(getApplicationContext(), MotivationActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        if (seekBar == seekBarTop) {
            seekBarBottom.setProgress(progress);
        } else {
            seekBarTop.setProgress(progress);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slider_value);
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bmp);

        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);

        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, getResources().getDisplayMetrics());

        p.setTextSize(pixel);
        p.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_orange));
//        p.setColor(getResources().getColor(R.color.text_orange));
        String text = Integer.toString(seekBar.getProgress() + MIN_WEIGHT_VALUE);
        int width = (int) p.measureText(text);
        int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
        c.drawText(text, (bmp.getWidth() - width) / 2, yPos-5, p);

        targetWeightValue = seekBar.getProgress() + MIN_WEIGHT_VALUE;
        seekBarTop.setThumb(new BitmapDrawable(getResources(), bmp));

        Double d = seekBarTop.getMeasuredWidth() * 0.105;
        int i = d.intValue();

        seekBarTop.setPadding(i, 0, i, 0);
    }


    public void goBackToPreviousPage(View view) {
        finish();
    }

}
