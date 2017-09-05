package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.*;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.UserProfile;


/**
 * Created by elaineanxa on 4/5/16.
 * Modified by aprilanxa on 4/14/16
 */
public class MotivationActivity extends HAPIActivity implements SeekBar.OnSeekBarChangeListener {
    /*Init the variables*/
    private TextView questionLabel;

    private final int MIN_MOTIVATION_VALUE = 0;
    private final int DEFAULT_MOTIVATION_VALUE = 5;
    private final int FONT_SIZE = 50;

    private int motivationValue;

    private SeekBar seekBarTop;
    private SeekBar seekBarBottom;
    private UserProfile userProfile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_motivation);

        userProfile = ApplicationEx.getInstance().userProfile;

        updateFooterButton(getResources().getString(R.string.btn_continue), this);

        questionLabel = (TextView) findViewById(R.id.reg_questionLabel);

        if (userProfile.getStart_weight() == null) {
            String labelText = "1/2 " + getResources().getString(R.string.REG_QUESTION_MOTIVATION_LEVEL);
            questionLabel.setText(labelText);
        } else {
            String labelText = "3/4 " + getResources().getString(R.string.REG_QUESTION_MOTIVATION_LEVEL);
            questionLabel.setText(labelText);
        }

        seekBarTop = (SeekBar) findViewById(R.id.seekBarTop);
        seekBarBottom = (SeekBar) findViewById(R.id.seekBarBottom);

        seekBarBottom.setOnSeekBarChangeListener(this);
        seekBarTop.setOnSeekBarChangeListener(this);

        seekBarTop.setProgress(DEFAULT_MOTIVATION_VALUE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerbutton) {
            SharedPreferences sharedPreferences = getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putString("motivation_level", Integer.toString(motivationValue));
            editor.commit();

            userProfile.setMotivation_level(Integer.toString(motivationValue));
            ApplicationEx.getInstance().userProfile = userProfile;

            Intent i = new Intent(getApplicationContext(), EngagementActivity.class);
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
        String text = Integer.toString(seekBar.getProgress() + MIN_MOTIVATION_VALUE);
        int width = (int) p.measureText(text);
        int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
        c.drawText(text, (bmp.getWidth() - width) / 2, yPos - 5, p);

        motivationValue = seekBar.getProgress() + MIN_MOTIVATION_VALUE;
        seekBarTop.setThumb(new BitmapDrawable(getResources(), bmp));

        Double d = seekBarTop.getMeasuredWidth() * 0.105;
        int i = d.intValue();

        seekBarTop.setPadding(i, 0, i, 0);
    }


    public void goBackToPreviousPage(View view) {
        finish();
    }

}
