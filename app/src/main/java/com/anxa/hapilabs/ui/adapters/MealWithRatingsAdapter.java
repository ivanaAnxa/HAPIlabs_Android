package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by elaineanxa on 05/08/2016.
 */
public class MealWithRatingsAdapter extends ArrayAdapter<Meal> implements BitmapDownloadListener
{
    private final Context context;
    private ArrayList<Meal> mealArrayList;
    LayoutInflater layoutInflater;

    public MealWithRatingsAdapter(Context context, ArrayList<Meal> mealTempArrayList)
    {
        super(context, R.layout.progress_mealwithoutpic_item, mealTempArrayList);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        mealArrayList = mealTempArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int xml_type;

        Meal currentMeal =  mealArrayList.get(position);

        if (currentMeal.photos != null)
        {
            xml_type = R.layout.progress_mealwithpicture_item;
        }
        else
        {
            xml_type = R.layout.progress_mealwithoutpic_item;
        }

        View rowView = layoutInflater.inflate(xml_type, parent, false);

        ((TextView)rowView.findViewById(R.id.mealType)).setText(AppUtil.getMealTitle(context, currentMeal.meal_type));
        ((TextView)rowView.findViewById(R.id.mealDate)).setText(getMealTime(currentMeal.meal_creation_date));

        if (currentMeal.photos != null)
        {
            if (currentMeal.photos.size() == 1)
            {
                ((LinearLayout) rowView.findViewById(R.id.mealPhotoView)).setVisibility(LinearLayout.INVISIBLE);
                ((ImageView)rowView.findViewById(R.id.mealPhoto))
                        .setImageBitmap(getMealPhoto(currentMeal.photos.get(0), currentMeal.meal_type));
            }
            else if (currentMeal.photos.size() > 1)
            {
                ((LinearLayout) rowView.findViewById(R.id.mealPhotoView)).setVisibility(LinearLayout.VISIBLE);
                ImageView photoMain = ((ImageView)rowView.findViewById(R.id.mealPhoto));
                photoMain.setImageBitmap(getMealPhoto(currentMeal.photos.get(0), currentMeal.meal_type));

                for(int i=1; i<=currentMeal.photos.size(); i++)
                {
                    ((ImageView)rowView.findViewById(context.getResources().getIdentifier("mealPhoto" + i, "id", context.getPackageName())))
                            .setImageBitmap(getMealPhoto(currentMeal.photos.get(i-1), currentMeal.meal_type));
                }
            }
        }

        return rowView;
    }

    public String getMealTime(Date mealDate)
    {
        if (ApplicationEx.language.equals("fr"))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM 'à' HH:mm");
            return sdf.format(mealDate);
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' hh:mm a");
            return sdf.format(mealDate);
        }
    }

    private Bitmap getMealPhoto(Photo mealPhoto, Meal.MEAL_TYPE meal_type)
    {
        Bitmap mealPhotoBMP = null;

        mealPhotoBMP = ImageManager.getInstance().findImage(mealPhoto.photo_id);

        if (mealPhotoBMP == null)
        {
            download(mealPhoto.photo_id, mealPhoto.photo_url_large, "0");
        }

        if (mealPhotoBMP == null)
            mealPhotoBMP = BitmapFactory.decodeResource(context.getResources(), AppUtil.getPhotoResource(meal_type), ApplicationEx.getInstance().options_Avatar);

        return mealPhotoBMP;
    }

    private void download(String photoId, String url, String mealId)
    {
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(context,
                url,
                photoId,
                mealId, this);
    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId)
    {
        // TODO Auto-generated method stub

        refreshUI();
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response)
    {
        // TODO Auto-generated method stub
    }

    private void refreshUI()
    {
        notifyDataSetChanged();
    }
}