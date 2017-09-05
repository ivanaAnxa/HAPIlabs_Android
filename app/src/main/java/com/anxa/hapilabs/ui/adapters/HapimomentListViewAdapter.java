package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.HapimomentAddActivity;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;

import java.util.List;

/**
 * Created by angelaanxa on 8/24/2016.
 */
public class HapimomentListViewAdapter extends ArrayAdapter<HapiMoment> implements View.OnClickListener, BitmapDownloadListener {
    Context context;

    List<HapiMoment> items;
    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    ImageView smileyView;
    ImageView mainPhotoView;
    ImageView hapiMoodAddImage;
    TextView viewTitle;
    TextView description;
    TextView location;
    LinearLayout photoThumbContainer;
    RelativeLayout hapimomentDescriptionContainer;
    LinearLayout approvedIconContainer;
    boolean isHideChecked = true;

    public HapimomentListViewAdapter(Context context,
                                     List<HapiMoment> items) {
        super(context, R.layout.list_item_generic, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.hapimoment_filled_itemcell, parent, false);
        }

        smileyView = ((ImageView) row.findViewById(R.id.hapiMoodImage));
        description = ((TextView) row.findViewById(R.id.hapimomentDescription));
        location = ((TextView) row.findViewById(R.id.hapimomentLocation));
        viewTitle = ((TextView) row.findViewById(R.id.hapimomentViewTitle));
        mainPhotoView = (ImageView) row.findViewById(R.id.hapiphoto);
        photoThumbContainer = (LinearLayout) row.findViewById(R.id.mealphoto_thumbcontainer);
        hapimomentDescriptionContainer = (RelativeLayout) row.findViewById(R.id.hapimomentDescriptionContainer);
        approvedIconContainer = (LinearLayout) row.findViewById(R.id.approvedIconContainer);
        hapiMoodAddImage = ((ImageView) row.findViewById(R.id.hapiMoodAddImage));

        for (HapiMoment h : items) {
            if (h.isChecked) {
                isHideChecked = false;
            }
        }
        refreshUIRow(row, position);

        final int currPosition = position;

        hapiMoodAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewHapimoment(v, items.get(currPosition));
            }
        });


        return row;
    }

    private void refreshUIRow(View row, int position) {
        smileyView = ((ImageView) row.findViewById(R.id.hapiMoodImage));
        description = ((TextView) row.findViewById(R.id.hapimomentDescription));
        location = ((TextView) row.findViewById(R.id.hapimomentLocation));
        viewTitle = ((TextView) row.findViewById(R.id.hapimomentViewTitle));
        mainPhotoView = (ImageView) row.findViewById(R.id.hapiphoto);
        mainPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoThumbContainer = (LinearLayout) row.findViewById(R.id.mealphoto_thumbcontainer);
        hapimomentDescriptionContainer = (RelativeLayout) row.findViewById(R.id.hapimomentDescriptionContainer);
        approvedIconContainer = (LinearLayout) row.findViewById(R.id.approvedIconContainer);
        hapiMoodAddImage = ((ImageView) row.findViewById(R.id.hapiMoodAddImage));
        updateHapiMood(items.get(position).moodValue, items.get(position).mood_datetime.getTime());

        description.setText(items.get(position).description);
        if (position != 0) {
            hapiMoodAddImage.setVisibility(View.GONE);
        }
        if (!items.get(position).location.isEmpty() && items.get(position).location != null && items.get(position).location != "null") {
            location.setText(" - at " + items.get(position).location);
        }
        if ((items.get(position).location.isEmpty() || items.get(position).location == null || items.get(position).location == "null")
                && items.get(position).description.isEmpty()) {
            hapimomentDescriptionContainer.setVisibility(View.GONE);
        }

        if ((items.size() - 1 == position) && !isHideChecked) {
            approvedIconContainer.setVisibility(View.VISIBLE);
        } else {
            approvedIconContainer.setVisibility(View.GONE);
        }
        // set the selected image
        if (items.get(position).photos == null || items.get(position).photos.isEmpty()) {
            mainPhotoView.setBackgroundResource(R.drawable.hapi_default_image);
            photoThumbContainer.setVisibility(View.GONE);
        } else {
            List<Photo> photos;
            photos = items.get(position).photos;
            Bitmap bmp = getHapiPhoto(items.get(position).photos.get(0));
            mainPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (bmp == null) {
                mainPhotoView.setBackgroundResource(R.drawable.hapi_default_image);
                mainPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else {
                mainPhotoView.setImageBitmap(bmp);
            }

            if (items.get(position).photos.size() > 1) {
                for (int i = 0; i < items.get(position).photos.size(); i++) {
                    bmp = getHapiPhoto(items.get(position).photos.get(i));
                    if (bmp != null) {
                        if (i == 0) {
                            ((ImageView) row.findViewById(R.id.mealphoto_thumb1)).setImageBitmap(bmp);
                            ((LinearLayout) row.findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.VISIBLE);
                        }else if (i == 1) {
                            ((ImageView) row.findViewById(R.id.mealphoto_thumb2)).setImageBitmap(bmp);
                            ((LinearLayout) row.findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.VISIBLE);
                        }else if (i == 2) {
                            ((ImageView) row.findViewById(R.id.mealphoto_thumb3)).setImageBitmap(bmp);
                            ((LinearLayout) row.findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.VISIBLE);
                        }else if (i == 3) {
                            ((ImageView) row.findViewById(R.id.mealphoto_thumb4)).setImageBitmap(bmp);
                            ((LinearLayout) row.findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.VISIBLE);
                        }else if (i == 4) {
                            ((ImageView) row.findViewById(R.id.mealphoto_thumb5)).setImageBitmap(bmp);
                            ((LinearLayout) row.findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                photoThumbContainer.setVisibility(View.GONE);
            }
        }
    }

    private void addNewHapimoment(View v, HapiMoment h) {

        Intent mainIntent = new Intent(v.getContext(), HapimomentAddActivity.class);
        mainIntent.putExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_ADD);
        v.getContext().startActivity(mainIntent);

      /*  ApplicationEx.getInstance().currentHapiMoment = h;

        Intent mainIntent;
        mainIntent = new Intent(v.getContext(), HapiMomentViewActivity.class);
        mainIntent.putExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_VIEW);
        v.getContext().startActivity(mainIntent);*/
    }

    private void updateHapiMood(int moodValue, long time) {
        if (moodValue == 1) {
            smileyView.setImageDrawable(context.getResources().getDrawable(R.drawable.mood1));
            viewTitle.setText(context.getString(R.string.HAPIMOMENT_GREAT) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 2) {
            smileyView.setImageDrawable(context.getResources().getDrawable(R.drawable.mood2));
//            smileyView.setBackgroundResource(R.drawable.mood2);
            viewTitle.setText(context.getString(R.string.HAPIMOMENT_GOOD) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 3) {
            smileyView.setImageDrawable(context.getResources().getDrawable(R.drawable.mood3));
//            smileyView.setBackgroundResource(R.drawable.mood3);
            viewTitle.setText(context.getString(R.string.HAPIMOMENT_OKAY) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 4) {
//            smileyView.setBackgroundResource(R.drawable.mood4);
            smileyView.setImageDrawable(context.getResources().getDrawable(R.drawable.mood4));
            viewTitle.setText(context.getString(R.string.HAPIMOMENT_NOTGOOD) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 5) {
//            smileyView.setBackgroundResource(R.drawable.mood5);
            smileyView.setImageDrawable(context.getResources().getDrawable(R.drawable.mood5));
            viewTitle.setText(context.getString(R.string.HAPIMOMENT_BAD) + " @ " + AppUtil.getTimeOnly12(time));
        }
    }


    @Override
    public int getCount() {
        return items.size();// more than zero
    }


    @Override
    public void onClick(View v) {

    }

    private Bitmap getHapiPhoto(Photo mealPhoto) {
        Bitmap mealPhotoBMP = null;

        mealPhotoBMP = ImageManager.getInstance().findImage(mealPhoto.photo_id);

        if (mealPhotoBMP == null) {
            download(mealPhoto.photo_id, mealPhoto.photo_url_large, "0");
        }

        // if (mealPhotoBMP == null)
        //  mealPhotoBMP = BitmapFactory.decodeResource(this.getResources(), AppUtil.getPhotoResource(meal_type), ApplicationEx.getInstance().options_Avatar);

        return mealPhotoBMP;
    }

    private void download(String photoId, String url, String mealId) {
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(context,
                url,
                photoId,
                mealId, this);
    }

    private void updatePhotoInRow(View row, int photoIndex) {

    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
      /*  int index = 0;
        for(HapiMoment h : items)
        {
            if(h.photos != null && !h.photos.isEmpty())
            {
                for(Photo p : h.photos)
                {
                    int photoIndex = 0;
                    if(p.photo_id == photoId)
                    {
                        RefreshUIRow(null, index);
                    }
                    else
                    {
                        photoIndex++;
                    }
                }
            }
            index++;
        }*/
        notifyDataSetChanged();

    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
    }
}
