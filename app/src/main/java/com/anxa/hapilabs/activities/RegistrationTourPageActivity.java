package com.anxa.hapilabs.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View.OnClickListener;

import com.hapilabs.R;
import com.anxa.hapilabs.ui.adapters.RegistrationPagerAdapter;

/**
 * Created by aprilanxa on 4/5/2016.
 */
public class RegistrationTourPageActivity extends HAPIActivity implements OnPageChangeListener, OnClickListener {

    ImageView page1Dot, page2Dot, page3Dot, page4Dot, page5Dot;
    final byte PAGE1_SCROLL = 80;
    byte currentPage = 0;
    TextView tourText;

    private static final int NUM_PAGES = 5;

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
        setContentView(R.layout.whatsintheapptour);

        page1ScrollIndex = 0;
        currentPage = PAGE1_SCROLL;

        getIntent();

        page1Dot = (ImageView) findViewById(R.id.pager1);
        page2Dot = (ImageView) findViewById(R.id.pager2);
        page3Dot = (ImageView) findViewById(R.id.pager3);
        page4Dot = (ImageView) findViewById(R.id.pager4);
        page5Dot = (ImageView) findViewById(R.id.pager5);
        // Instantiate a ViewPager and a PagerAdapter.
        RegistrationPagerAdapter adapter = new RegistrationPagerAdapter();
        mPager = (ViewPager) findViewById(R.id.tour_page1);
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(this);
        mPager.setCurrentItem(page1ScrollIndex);

        setPage(page1ScrollIndex);

        updateFooterButton(getResources().getString(R.string.REGISTRATION_IWANTTOSTART),
                this);

    }

    private void setPage(int page) {
        switch (page) {
            case 0:
                page1Dot.setPressed(true);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                page4Dot.setPressed(false);
                page5Dot.setPressed(false);
                break;
            case 1:
                page1Dot.setPressed(false);
                page2Dot.setPressed(true);
                page3Dot.setPressed(false);
                page4Dot.setPressed(false);
                page5Dot.setPressed(false);
                break;
            case 2:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(true);
                page4Dot.setPressed(false);
                page5Dot.setPressed(false);
                break;
            case 3:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                page4Dot.setPressed(true);
                page5Dot.setPressed(false);
                break;
            case 4:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                page4Dot.setPressed(false);
                page5Dot.setPressed(true);
                break;
            default:
                page1Dot.setPressed(false);
                page2Dot.setPressed(false);
                page3Dot.setPressed(false);
                page4Dot.setPressed(false);
                page5Dot.setPressed(false);
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
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }
}
