package com.anxa.hapilabs.ui.adapters;

/**
 * Created by aprilanxa on 19/07/2016.
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hapilabs.R;

public class FullScreenImageAdapter extends PagerAdapter {

    int photoCount = 0;
    ImageView imageView;
    private Context mContext;
    private ArrayList<Bitmap> mResources;

    public int getCount() {
        return mResources.size();
    }

    public FullScreenImageAdapter(Context mContext, ArrayList<Bitmap> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.fullscreen_image, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imgDisplay);
        imageView.setImageBitmap(mResources.get(position));
//        imageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}