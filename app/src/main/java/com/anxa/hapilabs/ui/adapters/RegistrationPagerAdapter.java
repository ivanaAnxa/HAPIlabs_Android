package com.anxa.hapilabs.ui.adapters;

import com.hapilabs.R;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;



public class RegistrationPagerAdapter extends PagerAdapter {
	 
    public int getCount() {
        return 5;
    }

    public Object instantiateItem(View collection, int position) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int resId = 0;
        
        
        switch (position) {
        case 0:
            resId = R.layout.register_tutorial_page1;
            break;
        case 1:
            resId = R.layout.register_tutorial_page2;
            break;
        case 2:
            resId = R.layout.register_tutorial_page3;
            break;
        case 3:
            resId = R.layout.register_tutorial_page4;
            break;
        case 4:
            resId = R.layout.register_tutorial_page5;
            break;
//        case 5:
//            resId = R.layout.register_tutorial_page6;
//            break;
        default:
        	 resId = R.layout.register_tutorial_page1;
             break;
        }

        View view = inflater.inflate(resId, null);

        ((ViewPager) collection).addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

	
}