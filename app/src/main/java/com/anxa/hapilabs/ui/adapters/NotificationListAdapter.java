package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_STATE;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_TYPE;
import com.anxa.hapilabs.ui.RoundedImageView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends ArrayAdapter<Notification> implements OnClickListener, BitmapDownloadListener {
    private final Context context;
    private List<Notification> items = new ArrayList<Notification>();

    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;
    CoachDAO coachDAO;

    final int COLOG_BG_UNREAD_MES = Color.parseColor("#F5DEB3");
    final int COLOG_BG_READ_MES = Color.parseColor("#FFFFFF");

    OnClickListener listener;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    MainActivityCallBack MainListener;


    public NotificationListAdapter(Context context, List<Notification> items, OnClickListener listener) {
        super(context, R.layout.listitem_notifications, items);

        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.listener = listener;
        coachDAO = new CoachDAO(this.context, null);

    }

    public void updateItems(List<Notification> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.listitem_notifications, parent, false);
        }

        Notification notification = (Notification) items.get(position);
        row.setTag(R.id.notif_refid, notification.ref_id);
        row.setTag(R.id.notif_pos, position);
        row.setTag(R.id.notif_id, notification.notificationID);
        row.setOnClickListener(this);

        //coach
        RoundedImageView imgCoachAvatar = (RoundedImageView) row.findViewById(R.id.imgCoachAvatar);

        Coach coach = coachDAO.getCoachsbyID("" + notification.coachID);

        //coach profile photo
        if (coach != null) {
            coachDAO = new CoachDAO(context, null);

            try {
                if (coach != null && coach.coach_id != null) {
//                    imgCoachAvatar.setImageBitmap(getAvatar(coach));

//                    System.out.println("coach: " + coach.coach_id + " name: " + coach.firstname);
                    Bitmap bmp = ImageManager.getInstance().findImage(coach.coach_id);
                    if (bmp == null) {
                        new DownloadImageTask(imgCoachAvatar, Integer.parseInt(coach.coach_id)).execute(coach.avatar_url);
                    } else
                        imgCoachAvatar.setImageBitmap(bmp);
                } else {
                    if (notification.coachID > 0) {
                        Coach newCoach = new Coach();
                        newCoach.coach_id = Integer.toString(notification.coachID);
                        newCoach.avatar_url = notification.coachAvatarURL;
//                        imgCoachAvatar.setImageBitmap(getAvatar(newCoach));

                        Bitmap bmp = ImageManager.getInstance().findImage(newCoach.coach_id);
                        if (bmp == null) {
                            new DownloadImageTask(imgCoachAvatar, Integer.parseInt(newCoach.coach_id)).execute(newCoach.avatar_url);
                        } else
                            imgCoachAvatar.setImageBitmap(bmp);
                    }else{
                        System.out.println("NotificationListAdapter: " + notification.coachAvatarURL);
                        String userID;

                        if (notification.coachAvatarURL.contains("website")){
                            System.out.println("NotificationListAdapter: contains website");
                            String parameters[] = notification.coachAvatarURL.substring(notification.coachAvatarURL.indexOf("website")).split("/");
                            userID = parameters[1];
//                            parameters = notification.coachAvatarURL.substring(notification.coachAvatarURL.indexOf("mobile")).split("/");
                        }else{
                            String parameters[] = notification.coachAvatarURL.substring(notification.coachAvatarURL.indexOf("mobile")).split("/");
                            userID = parameters[1];
                        }

                        System.out.println("NotificationListAdapter userID: " + userID);

                        Bitmap bmp = ImageManager.getInstance().findImage(userID);
                        if (bmp == null) {
                            new DownloadImageTask(imgCoachAvatar, Integer.parseInt(userID)).execute(notification.coachAvatarURL);
                        } else
                            imgCoachAvatar.setImageBitmap(bmp);
                        new DownloadImageTask(imgCoachAvatar, 0).execute(notification.coachAvatarURL);

                    }
                }

            } catch (Exception e) {
                imgCoachAvatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
            }
        } else {

            Coach newCoach = new Coach();
            newCoach.coach_id = Integer.toString(notification.coachID);
            newCoach.avatar_url = notification.coachAvatarURL;

//            System.out.println("3 coach: " + newCoach.coach_id + " name: " + newCoach.firstname);

            try {
//                imgCoachAvatar.setImageBitmap(getAvatar(newCoach));
                Bitmap bmp = ImageManager.getInstance().findImage(newCoach.coach_id);
                if (bmp == null) {
                    new DownloadImageTask(imgCoachAvatar, Integer.parseInt(newCoach.coach_id)).execute(newCoach.avatar_url);
                } else
                    imgCoachAvatar.setImageBitmap(bmp);
            } catch (Exception e) {
                imgCoachAvatar.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
            }
        }

        //notif icon
        ImageView imgIcon = (ImageView) row.findViewById(R.id.imgNotifIcon);
        if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_APPROVAL_COACH) {
            imgIcon.setImageResource(R.drawable.meal_approved_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_COMMENT_COACH) {
            imgIcon.setImageResource(R.drawable.meal_commented_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MESSAGE_COACH) {
            imgIcon.setImageResource(R.drawable.notif_message_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_COMMENT_RATING){
            imgIcon.setImageResource(R.drawable.star_rating_display_yellow);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_MOMENT){
            imgIcon.setImageResource(R.drawable.meal_approved_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_EXERCISE){
            imgIcon.setImageResource(R.drawable.meal_approved_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_WATER){
            imgIcon.setImageResource(R.drawable.meal_approved_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_HAPI4U_COMMUNITY){
            imgIcon.setImageResource(R.drawable.notification_like_icon);
        } else if (notification.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_COMMENT_COMMUNITY){
            imgIcon.setImageResource(R.drawable.meal_commented_icon);
        }


        //notif state -- read unread
        if (notification.notificationState == NOTIFICATION_STATE.READ) {
            row.setBackgroundColor(COLOG_BG_READ_MES);
        } else {
            row.setBackgroundColor(COLOG_BG_UNREAD_MES);
        }

        //display message

        TextView txtNotifMessage = (TextView) row.findViewById(R.id.txtNotifMessage);
        TextView txtTimeStamp = (TextView) row.findViewById(R.id.txtTimestamp);
        txtNotifMessage.setText(notification.coachMessage);

        //timestamp
        String timeStamp = sdf.format(notification.timestamp) + " at " + AppUtil.getTimeOnly24(notification.timestamp.getTime());
        txtTimeStamp.setText(timeStamp);
        return row;
    }

    private void refreshUI() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //if an item is click , set its value to unread and pass the position to the main activity
        if (v != null) {

            if (items != null && items.size() > 0) {
                int pos = (Integer) v.getTag(R.id.notif_pos);
                items.get(pos).notificationState = NOTIFICATION_STATE.READ;
                refreshUI();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        }
    }


    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub

        refreshUI();
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;
        String photoid = "";

        public DownloadImageTask(ImageView bmImage, int photoID) {
            this.bmImage = bmImage;
            this.photoid = Integer.toString(photoID);
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            ImageManager.getInstance().addImage(photoid, result);

        }
    }

    /** **/
}