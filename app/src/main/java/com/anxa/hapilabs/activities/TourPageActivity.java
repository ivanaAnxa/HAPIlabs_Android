package com.anxa.hapilabs.activities;

import com.hapilabs.R;
import com.anxa.hapilabs.ui.adapters.TourPagerAdapter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by jas on 21/01/2016.
 */
public class TourPageActivity extends HAPIActivity implements OnPageChangeListener, OnClickListener {

    ImageView page1Dot, page2Dot, page3Dot;
    final byte PAGE1_SCROLL = 80;
    byte currentPage = 0;
    TextView tourText;
    TextView tourText_sub;

    private static final int NUM_PAGES = 3;

    int page1ScrollIndex = 0;
    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private ViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.tour_page);

        page1ScrollIndex = 0;
        currentPage = PAGE1_SCROLL;

        getIntent();

        page1Dot = (ImageView) findViewById(R.id.pager1);
        page2Dot = (ImageView) findViewById(R.id.pager2);
        page3Dot = (ImageView) findViewById(R.id.pager3);
        tourText = (TextView) findViewById(R.id.tourTextView);
        tourText_sub = (TextView) findViewById(R.id.tourTextView_sub);

        // Instantiate a ViewPager and a PagerAdapter.
        TourPagerAdapter adapter = new TourPagerAdapter();

        mPager = (ViewPager) findViewById(R.id.tour_page1);
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(this);
        mPager.setCurrentItem(page1ScrollIndex);

        setPage(page1ScrollIndex);

        updateFooterButton(getResources().getString(R.string.TOUR_NEXT), this);
    }

    private void setPage(int page) {

        switch (page) {

            case 0:
                page1Dot.setPressed(true);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                tourText.setText(getResources().getString(R.string.TOUR_TITLE_1));
                tourText_sub.setText(getResources().getString(R.string.TOUR_SUBTITLE_1));
                updateFooterButton(getResources().getString(R.string.TOUR_NEXT), this);

                break;
            case 1:
                page1Dot.setPressed(false);
                page2Dot.setPressed(true);
                page3Dot.setPressed(false);
                tourText.setText(getResources().getString(R.string.TOUR_TITLE_2));
                tourText_sub.setText(getResources().getString(R.string.TOUR_SUBTITLE_2));
                updateFooterButton(getResources().getString(R.string.TOUR_NEXT), this);

                break;
            case 2:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(true);
                tourText.setText(getResources().getString(R.string.TOUR_TITLE_3));
                tourText_sub.setText(getResources().getString(R.string.TOUR_SUBTITLE_3));
                updateFooterButton(getResources().getString(R.string.TOUR_START), this);

                break;
            default:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                tourText.setText(getResources().getString(R.string.TOUR_TITLE_1));
                tourText_sub.setText(getResources().getString(R.string.TOUR_SUBTITLE_1));
                updateFooterButton(getResources().getString(R.string.TOUR_NEXT), this);

                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
            setPage(page1ScrollIndex);
        } else {
            setPage(page1ScrollIndex);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        page1ScrollIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerbutton) {
            if (footerButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.TOUR_NEXT))){
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }else {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }
}