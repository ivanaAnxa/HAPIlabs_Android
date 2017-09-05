package com.anxa.hapilabs.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Photo;

public class FullScreenImageActivity extends HAPIActivity {


    private ImageView bitmap_iv1;
    private ImageView bitmap_iv2;
    private ImageView bitmap_iv3;
    private ImageView bitmap_iv4;
    private ImageView bitmap_iv5;

    private ImageButton dots1;
    private ImageButton dots2;
    private ImageButton dots3;
    private ImageButton dots4;
    private ImageButton dots5;


    private String[] photoIds;
    int imageCurrentlyDisplayed = 1;
    int maxImage = 1;

    Button closeBtn;


    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.fullscreen_imageview);

        bitmap_iv1 = (ImageView) findViewById(R.id.full_image_view1);
        bitmap_iv2 = (ImageView) findViewById(R.id.full_image_view2);
        bitmap_iv3 = (ImageView) findViewById(R.id.full_image_view3);
        bitmap_iv4 = (ImageView) findViewById(R.id.full_image_view4);
        bitmap_iv5 = (ImageView) findViewById(R.id.full_image_view5);
        closeBtn = (Button) findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(this);

        dots1 = (ImageButton) findViewById(R.id.bottomDots1);
        dots2 = (ImageButton) findViewById(R.id.bottomDots2);
        dots3 = (ImageButton) findViewById(R.id.bottomDots3);
        dots4 = (ImageButton) findViewById(R.id.bottomDots4);
        dots5 = (ImageButton) findViewById(R.id.bottomDots5);

        bitmap_iv1.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (maxImage > 1) {
                    showImageNumber(imageCurrentlyDisplayed + 1);
                }
            }
            @Override
            public void onSwipeRight() {
                if (maxImage > 1) {
                    showImageNumber(maxImage);
                }
            }
        });

        bitmap_iv2.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (imageCurrentlyDisplayed < maxImage) {
                    showImageNumber(imageCurrentlyDisplayed + 1);
                }else{
                    showImageNumber(1);
                }
            }
            @Override
            public void onSwipeRight() {
                if (imageCurrentlyDisplayed > 1) {
                    showImageNumber(imageCurrentlyDisplayed - 1);
                }else{
                    showImageNumber(maxImage);
                }
            }
        });

        bitmap_iv3.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (imageCurrentlyDisplayed < maxImage) {
                    showImageNumber(imageCurrentlyDisplayed + 1);
                }else{
                    showImageNumber(1);
                }
            }
            @Override
            public void onSwipeRight() {
                if (imageCurrentlyDisplayed > 1) {
                    showImageNumber(imageCurrentlyDisplayed - 1);
                }else{
                    showImageNumber(maxImage);
                }
            }
        });

        bitmap_iv4.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (imageCurrentlyDisplayed < maxImage) {
                    showImageNumber(imageCurrentlyDisplayed + 1);
                }else{
                    showImageNumber(1);
                }
            }
            @Override
            public void onSwipeRight() {
                if (imageCurrentlyDisplayed > 1) {
                    showImageNumber(imageCurrentlyDisplayed - 1);
                }else{
                    showImageNumber(maxImage);
                }
            }
        });

        bitmap_iv5.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (imageCurrentlyDisplayed < maxImage) {
                    showImageNumber(imageCurrentlyDisplayed + 1);
                }else{
                    showImageNumber(1);
                }
            }
            @Override
            public void onSwipeRight() {
                if (imageCurrentlyDisplayed > 1) {
                    showImageNumber(imageCurrentlyDisplayed - 1);
                }else{
                    showImageNumber(maxImage);
                }
            }
        });

        String photoID = getIntent().getStringExtra("PHOTO_ID");
        String mealID = getIntent().getStringExtra("MEAL_ID");
        String title = getIntent().getStringExtra("MEAL_TITLE");


        //get gitmap from bitmap manager using photo id
        Bitmap bitmap = ImageManager.getInstance().findImage(photoID);

        MealDAO dao = new MealDAO(this.getApplicationContext(), null);
        Meal meal = dao.getMealbyID(mealID);

        if (meal == null) {
            meal = ApplicationEx.getInstance().tempList.get(mealID);
            if (meal == null){
                //from community
                meal = ApplicationEx.getInstance().currentMealView;
            }
        }
        //Meal meal = ApplicationEx.getInstance().tempList.get(mealID);

        maxImage = meal.photos.size();

        setupBottomDots();

        if (bitmap == null && mealID != null) {
            //get the meal in the runtime Variable and get the images directt from there

            meal = ApplicationEx.getInstance().tempList.get(mealID);

            if (meal != null && meal.photos != null && meal.photos.size() > 0) {

                for (int i = 0; i < meal.photos.size(); i++) {
                    Photo photo = meal.photos.get(i);

                    if (photo.photo_id.compareTo(photoID) == 0) {
                        bitmap = photo.image;
                        break;
                    }
                }
            }
        }

        bitmap_iv1.setImageBitmap(bitmap);
        showImageNumber(1);

        if (meal.photos!=null) {
            for (int j = 0; j < meal.photos.size(); j++) {

                Photo photo = meal.photos.get(j);

                bitmap = ImageManager.getInstance().findImage(photo.photo_id);
                if (photo.photo_id.compareTo(photoID) == 0) {
                    continue;
                } else {

                    if (bitmap == null) {
                        if (photo.photo_id.compareTo(photoID) == 0) {
                            continue;
                        } else {
                            bitmap = photo.image;
                        }
                    }

                    if (bitmap_iv2.getDrawable() == null) {
                        bitmap_iv2.setImageBitmap(bitmap);
                    } else if (bitmap_iv3.getDrawable() == null) {
                        bitmap_iv3.setImageBitmap(bitmap);
                    } else if (bitmap_iv4.getDrawable() == null) {
                        bitmap_iv4.setImageBitmap(bitmap);
                    } else if (bitmap_iv5.getDrawable() == null) {
                        bitmap_iv5.setImageBitmap(bitmap);
                    }

                }
            }
        }
        //for other image views
        bitmap = ImageManager.getInstance().findImage(photoID);
        if (bitmap == null && mealID != null) {
            //get the meal in the runtime Variable and get the images directt from there

            meal = ApplicationEx.getInstance().tempList.get(mealID);

            if (meal != null && meal.photos != null && meal.photos.size() > 0) {
                for (int i = 0; i < meal.photos.size(); i++) {
                    Photo photo = meal.photos.get(i);

                    if (photo.photo_id.compareTo(photoID) == 0) {
                        bitmap = photo.image;
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnClose) {
            onBackPressed();
        }
    }


    /**
     * Detects left and right swipes across a view.
     */
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public void onSwipeLeft() {

        }

        public void onSwipeRight() {
            if (imageCurrentlyDisplayed > 1) {
                showImageNumber(imageCurrentlyDisplayed - 1);
            }else{
                showImageNumber(maxImage);
            }
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }

    private void showImageNumber(int imageNum){
        imageCurrentlyDisplayed = imageNum;
        switch (imageNum){
            case 1:
                bitmap_iv1.setVisibility(View.VISIBLE);
                bitmap_iv2.setVisibility(View.GONE);
                bitmap_iv3.setVisibility(View.GONE);
                bitmap_iv4.setVisibility(View.GONE);
                bitmap_iv5.setVisibility(View.GONE);

                dots1.setImageResource(R.drawable.pager_orange);
                dots2.setImageResource(R.drawable.pager_gray);
                dots3.setImageResource(R.drawable.pager_gray);
                dots4.setImageResource(R.drawable.pager_gray);
                dots5.setImageResource(R.drawable.pager_gray);

                break;
            case 2:
                bitmap_iv1.setVisibility(View.GONE);
                bitmap_iv2.setVisibility(View.VISIBLE);
                bitmap_iv3.setVisibility(View.GONE);
                bitmap_iv4.setVisibility(View.GONE);
                bitmap_iv5.setVisibility(View.GONE);

                dots1.setImageResource(R.drawable.pager_gray);
                dots2.setImageResource(R.drawable.pager_orange);
                dots3.setImageResource(R.drawable.pager_gray);
                dots4.setImageResource(R.drawable.pager_gray);
                dots5.setImageResource(R.drawable.pager_gray);

                break;
            case 3:
                bitmap_iv1.setVisibility(View.GONE);
                bitmap_iv2.setVisibility(View.GONE);
                bitmap_iv3.setVisibility(View.VISIBLE);
                bitmap_iv4.setVisibility(View.GONE);
                bitmap_iv5.setVisibility(View.GONE);

                dots1.setImageResource(R.drawable.pager_gray);
                dots2.setImageResource(R.drawable.pager_gray);
                dots3.setImageResource(R.drawable.pager_orange);
                dots4.setImageResource(R.drawable.pager_gray);
                dots5.setImageResource(R.drawable.pager_gray);

                break;
            case 4:
                bitmap_iv1.setVisibility(View.GONE);
                bitmap_iv2.setVisibility(View.GONE);
                bitmap_iv3.setVisibility(View.GONE);
                bitmap_iv4.setVisibility(View.VISIBLE);
                bitmap_iv5.setVisibility(View.GONE);

                dots1.setImageResource(R.drawable.pager_gray);
                dots2.setImageResource(R.drawable.pager_gray);
                dots3.setImageResource(R.drawable.pager_gray);
                dots4.setImageResource(R.drawable.pager_orange);
                dots5.setImageResource(R.drawable.pager_gray);

                break;
            case 5:
                bitmap_iv1.setVisibility(View.GONE);
                bitmap_iv2.setVisibility(View.GONE);
                bitmap_iv3.setVisibility(View.GONE);
                bitmap_iv4.setVisibility(View.GONE);
                bitmap_iv5.setVisibility(View.VISIBLE);

                dots1.setImageResource(R.drawable.pager_gray);
                dots2.setImageResource(R.drawable.pager_gray);
                dots3.setImageResource(R.drawable.pager_gray);
                dots4.setImageResource(R.drawable.pager_gray);
                dots5.setImageResource(R.drawable.pager_orange);

                break;
            default:
                break;

        }
    }

    private void setupBottomDots(){
        switch (maxImage){
            case 1:
                dots1.setVisibility(View.GONE);
                dots2.setVisibility(View.GONE);
                dots3.setVisibility(View.GONE);
                dots4.setVisibility(View.GONE);
                dots5.setVisibility(View.GONE);
                break;
            case 2:
                dots1.setVisibility(View.VISIBLE);
                dots2.setVisibility(View.VISIBLE);
                dots3.setVisibility(View.GONE);
                dots4.setVisibility(View.GONE);
                dots5.setVisibility(View.GONE);
                break;
            case 3:
                dots1.setVisibility(View.VISIBLE);
                dots2.setVisibility(View.VISIBLE);
                dots3.setVisibility(View.VISIBLE);
                dots4.setVisibility(View.GONE);
                dots5.setVisibility(View.GONE);
                break;
            case 4:
                dots1.setVisibility(View.VISIBLE);
                dots2.setVisibility(View.VISIBLE);
                dots3.setVisibility(View.VISIBLE);
                dots4.setVisibility(View.VISIBLE);
                dots5.setVisibility(View.GONE);
                break;
            case 5:
                dots1.setVisibility(View.VISIBLE);
                dots2.setVisibility(View.VISIBLE);
                dots3.setVisibility(View.VISIBLE);
                dots4.setVisibility(View.VISIBLE);
                dots5.setVisibility(View.VISIBLE);
                break;
        }
    }
}
