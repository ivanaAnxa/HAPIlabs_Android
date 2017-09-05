package com.anxa.hapilabs.ui.adapters;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Comment.STATUS;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.ui.CustomDialog;
//import com.google.android.gms.internal.co;
//import com.google.android.gms.internal.oi;

//import com.google.android.gms.internal.ie;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class CommentViewAdapter extends ArrayAdapter<Object> implements OnClickListener {
    Context context;

    List<Object> items;
    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    String currentDate;
    CustomDialog dialog;
    Comment selectedComment = null;
    MealAddCommentListener commentlistener;
    OnClickListener listener;
    MainActivityCallBack MainListener;
    String message;
    String title;

    String currentMediaURL;

    Boolean isSoundOn = true;
    Boolean isMediaPlaying = false;
    Boolean isMediaPlaybackCompleted = false;
    private String durationText = "";
    private MediaPlayer mediaPlayer;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    Message selectedAudioMessage;

    CoachDAO coachDAO;

    public CommentViewAdapter(Context context,
                              List<Object> items,
                              MainActivityCallBack MainListener,
                              OnClickListener listener,
                              MealAddCommentListener commentlistener) {
        super(context, R.layout.comment_item, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.MainListener = MainListener;
        this.listener = listener;
        this.commentlistener = commentlistener;

        message = context.getResources().getString(R.string.COMMENT_FAILED_TITLE);
        title = context.getResources().getString(R.string.ERROR);
        coachDAO = new CoachDAO(context, null);
    }


    public void update(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.comment_item, parent, false);
        }

        if (items != null && items.size() > 0 && getCount() > 0) {
            if (items.get(position) instanceof Comment) {
                Comment comment = (Comment) items.get(position);
                String date = AppUtil.getMonthDay(comment.timestamp);
                String time = AppUtil.getTimeOnly24(comment.timestamp.getTime());

                if (currentDate == null || position == 0) {
                    currentDate = date;
                    ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.date)).setText(date);

                } else if (currentDate.contentEquals(date)) {
                    ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.GONE);
                } else {
                    ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.date)).setText(date);
                    currentDate = date;
                }

                ((TextView) row.findViewById(R.id.timestamp)).setText(time);

                //comment types
                //0 - community
                //1 = approval
                //2 = coach comment
                //3 = user comment
                //4 - marketing comment

                if (comment.comment_type == 1) {
                    //coach
                    Bitmap bmp = ImageManager.getInstance().findImage(comment.coach.coach_id);
                    if (bmp == null) {
                        new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(comment.coach.coach_id)).execute(comment.coach.avatar_url);
                    } else
                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);
                    ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_coach);

                    String approvedMessage = comment.message;
                    String replace = "";
                    if (comment.coach != null) {
                        replace = comment.coach.firstname + " " + comment.coach.lastname;
                    }

                    String newString = approvedMessage.replace("$coach", replace);
                    ((TextView) row.findViewById(R.id.chat_message)).setText(newString);

                } else if (comment.comment_type == 0) {
                    ((ImageView) row.findViewById(R.id.chat_status)).setVisibility(View.INVISIBLE);

                    //community
                    if (comment.user != null) {
                        if (comment.user.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID())) {

                            LayoutParams params1 = (RelativeLayout.LayoutParams) ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).getLayoutParams();
                            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                            LayoutParams params2 = (RelativeLayout.LayoutParams) ((TextView) row.findViewById(R.id.timestamp)).getLayoutParams();
                            params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                            LayoutParams params3 = (RelativeLayout.LayoutParams) ((LinearLayout) row.findViewById(R.id.chat_message_cont)).getLayoutParams();
                            params3.addRule(RelativeLayout.RIGHT_OF, R.id.timestamp);
                            params3.addRule(RelativeLayout.LEFT_OF, R.id.chat_avatar);

                            ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setLayoutParams(params1);
                            ((TextView) row.findViewById(R.id.timestamp)).setLayoutParams(params2);
                            ((LinearLayout) row.findViewById(R.id.chat_message_cont)).setLayoutParams(params3);

                            ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar));
                            ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_user_inverted);
                            ((TextView) row.findViewById(R.id.chat_message)).setText(comment.message);

                            Bitmap bmp = ImageManager.getInstance().findImage(comment.user.user_id);
                            if (bmp == null) {
                                new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(comment.user.user_id)).execute(comment.user.picture_url_large);
                            } else
                                ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);

                        } else {
                            LayoutParams params1 = (RelativeLayout.LayoutParams) (((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar))).getLayoutParams();
                            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                            LayoutParams params2 = (RelativeLayout.LayoutParams) ((TextView) row.findViewById(R.id.timestamp)).getLayoutParams();
                            params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                            LayoutParams params3 = (RelativeLayout.LayoutParams) ((LinearLayout) row.findViewById(R.id.chat_message_cont)).getLayoutParams();
                            params3.addRule(RelativeLayout.LEFT_OF, R.id.timestamp);
                            params3.addRule(RelativeLayout.RIGHT_OF, R.id.chat_avatar);

                            ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setLayoutParams(params1);
                            ((TextView) row.findViewById(R.id.timestamp)).setLayoutParams(params2);
                            ((LinearLayout) row.findViewById(R.id.chat_message_cont)).setLayoutParams(params3);


                            String newString = comment.user.firstname + ": " + comment.message;
                            ((TextView) row.findViewById(R.id.chat_message)).setText(newString);
                            ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_coach);
                            // try getting on the ImageManager
                            Bitmap bmp = ImageManager.getInstance().findImage(comment.user.user_id);
                            if (bmp == null) {
                                new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(comment.user.user_id)).execute(comment.user.picture_url_large);
                            } else
                                ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);
                        }
                    } else {
                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar));
                    }

                } else if (comment.comment_type == 2) { //coach comment use coach comment here
                    ((TextView) row.findViewById(R.id.chat_message)).setText(comment.message);

                    Bitmap bmp = ImageManager.getInstance().findImage(comment.coach.coach_id);
                    if (bmp == null) {
                        new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(comment.coach.coach_id)).execute(comment.coach.avatar_url);
                    } else
                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);
                    ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_coach);
                } else if (comment.comment_type == 3) { //user comment use user comment here
                    ((TextView) row.findViewById(R.id.chat_message)).setText(comment.message);

                    Bitmap bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());
                    if (bmp == null) {
                        if (bmp == null)
                            new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(comment.user.user_id)).execute(comment.user.picture_url_large);
                    } else
                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);
                    ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.content_bubble_user);

                } else {
                }


                if (comment.status == com.anxa.hapilabs.models.Comment.STATUS.SYNC_COMMENT) {

                    if (comment.comment_type == 1) { //approval use coach icon here
                        ((ImageView) row.findViewById(R.id.chat_status)).setImageResource(R.drawable.meal_dietapproved);
                    } else if (comment.comment_type == 2) {
                        if (comment.isHAPI) {
                            ((ImageView) row.findViewById(R.id.chat_status)).setImageResource(R.drawable.meal_smiley_orange);
                        } else {
                            ((ImageView) row.findViewById(R.id.chat_status)).setImageResource(R.drawable.meal_smiley_gray);
                            ((ImageView) row.findViewById(R.id.chat_status)).setOnClickListener(this);
                            ((ImageView) row.findViewById(R.id.chat_status)).setTag(comment);
                            ((ImageView) row.findViewById(R.id.chat_status)).setTag(R.id.commentid, (Object) position);

                        }
                    } else if (comment.comment_type == 3) {     //user comment no isHapiIcon
                        ((ImageView) row.findViewById(R.id.chat_status)).setVisibility(View.INVISIBLE);
                    }
                } else if (comment.status == com.anxa.hapilabs.models.Comment.STATUS.ONGOING_COMMENTUPLOAD) {

                    ((ImageView) row.findViewById(R.id.chat_status)).setImageResource(R.drawable.loader_small_gray01);
                    ((ImageView) row.findViewById(R.id.chat_status)).setVisibility(View.VISIBLE);

                } else if (comment.status == com.anxa.hapilabs.models.Comment.STATUS.FAILED_COMMENTUPLOAD) {

                    ((ImageView) row.findViewById(R.id.chat_status)).setImageResource(R.drawable.loading_error);
                    ((ImageView) row.findViewById(R.id.chat_status)).setVisibility(View.VISIBLE);
                    ((ImageView) row.findViewById(R.id.chat_status)).setOnClickListener(this);
                    ((ImageView) row.findViewById(R.id.chat_status)).setTag(R.id.commentid, (Object) position);
                    ((ImageView) row.findViewById(R.id.chat_status)).setTag(comment);
                }

            } else if (items.get(position) instanceof Message) {
                final Message message = (Message) items.get(position);

                String date = AppUtil.getMonthDay(message.timestamp);
                String time = AppUtil.getTimeOnly24(message.timestamp.getTime());

                if (currentDate == null) {
                    currentDate = date;
                    ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.date)).setText(date);

                } else if (currentDate.contentEquals(date)) {
                    if (items.size() > 1 && position > 0) {
                        if (AppUtil.getMonthDay(((Message) items.get(position - 1)).timestamp).equalsIgnoreCase(date)) {
                            ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.GONE);
                        } else {
                            ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.VISIBLE);
                            ((TextView) row.findViewById(R.id.date)).setText(date);
                            currentDate = date;
                        }
                    } else {
                        ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.GONE);
                    }
                } else {
                    ((RelativeLayout) row.findViewById(R.id.datecontainer)).setVisibility(View.VISIBLE);
                    ((TextView) row.findViewById(R.id.date)).setText(date);
                    currentDate = date;
                }

                ((TextView) row.findViewById(R.id.timestamp)).setText(time);

                //if text message
                ((TextView) row.findViewById(R.id.chat_message)).setText(message.message_body);

                final RelativeLayout audio_message_rl = (RelativeLayout) row.findViewById(R.id.audio_message);
                final ImageButton play_button = (ImageButton) row.findViewById(R.id.playButton);
                final ImageButton sound_button = (ImageButton) row.findViewById(R.id.soundButton);
                final SeekBar audio_seekbar = (SeekBar) row.findViewById(R.id.audioSeekBar);
                final TextView audio_textView = (TextView) row.findViewById(R.id.audioTextView);

                audio_seekbar.setMax((int) finalTime);
                audio_seekbar.setClickable(false);


                boolean isMediaURLNull = true;
                try {
                    isMediaURLNull = message.mediaUrl.equalsIgnoreCase("null") ? true : false;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

//                if (message.message_body.equalsIgnoreCase("Voice message:") && message.mediaUrl.length() > 0) {
                if (!isMediaURLNull) {

                    if (message.mediaUrl.length() > 0) {
                        (row.findViewById(R.id.chat_message)).setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            audio_seekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.text_orangedark), PorterDuff.Mode.SRC_ATOP));
                        }
                        audio_message_rl.setVisibility(View.VISIBLE);
                        play_button.setOnClickListener(this);
                        play_button.setTag(message);

                        sound_button.setOnClickListener(this);
                        sound_button.setTag(message);


                        if (selectedAudioMessage == message) {
                            if (isMediaPlaying) {
                                play_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_pause));
                                if (mediaPlayer != null) {
                                    audio_seekbar.setMax(mediaPlayer.getDuration());
                                    audio_textView.setText(durationText);
                                    audio_seekbar.setProgress((int) timeElapsed);
                                }

                            } else {
                                play_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_play));
                                if (isMediaPlaybackCompleted) {
                                    audio_seekbar.setProgress((int) finalTime);
                                    String totalString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime));
//                                String totalString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toSeconds((long)finalTime), ((long)finalTime % 1000) / 10);
                                    audio_textView.setText(totalString + "/" + totalString);
                                }
                            }

                            if (isSoundOn)
                                sound_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_sound_on));
                            else
                                sound_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_sound_off));

                        }
                    }

                } else {
                    (row.findViewById(R.id.chat_message)).setVisibility(View.VISIBLE);
                    (row.findViewById(R.id.audio_message)).setVisibility(View.GONE);
                }


                //if audio message
                //message types
                //2 = coach comment
                //3 = user comment


                if (message.coachID != null) { //coach comment use coach comment here

                    LayoutParams params1 = (RelativeLayout.LayoutParams) (((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar))).getLayoutParams();
                    params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                    LayoutParams params2 = (RelativeLayout.LayoutParams) ((TextView) row.findViewById(R.id.timestamp)).getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                    LayoutParams params3 = (RelativeLayout.LayoutParams) ((LinearLayout) row.findViewById(R.id.chat_message_cont)).getLayoutParams();
                    params3.addRule(RelativeLayout.LEFT_OF, R.id.timestamp);
                    params3.addRule(RelativeLayout.RIGHT_OF, R.id.chat_avatar);

                    ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setLayoutParams(params1);
                    ((TextView) row.findViewById(R.id.timestamp)).setLayoutParams(params2);
                    ((LinearLayout) row.findViewById(R.id.chat_message_cont)).setLayoutParams(params3);

                    Bitmap bmp = ImageManager.getInstance().findImage(message.coachID);
                    if (bmp == null) {
                        new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(message.coachID)).execute(message.coach.avatar_url);
                    } else
                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);

                    ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_coach);

                } else {

                    LayoutParams params1 = (RelativeLayout.LayoutParams) ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).getLayoutParams();
                    params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                    LayoutParams params2 = (RelativeLayout.LayoutParams) ((TextView) row.findViewById(R.id.timestamp)).getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                    LayoutParams params3 = (RelativeLayout.LayoutParams) ((LinearLayout) row.findViewById(R.id.chat_message_cont)).getLayoutParams();
                    params3.addRule(RelativeLayout.RIGHT_OF, R.id.timestamp);
                    params3.addRule(RelativeLayout.LEFT_OF, R.id.chat_avatar);

                    ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setLayoutParams(params1);
                    ((TextView) row.findViewById(R.id.timestamp)).setLayoutParams(params2);
                    ((LinearLayout) row.findViewById(R.id.chat_message_cont)).setLayoutParams(params3);

//                    ((RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(avatar);
                    Bitmap bmp = ImageManager.getInstance().findImage(message.coachID);
                    if (message.coachID == null) {
                        if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() != null)
                            bmp = ApplicationEx.getInstance().userProfile.getUserProfilePhoto();
                        else if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null)
                            bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());

                        if (bmp == null)
                            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);

                        ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);

                    } else {
                        if (bmp == null) {
                            new DownloadImageTask(((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)), Integer.parseInt(message.coachID)).execute(message.coach.avatar_url);
                        } else
                            ((com.anxa.hapilabs.ui.RoundedImageView) row.findViewById(R.id.chat_avatar)).setImageBitmap(bmp);
                    }

                    ((TextView) row.findViewById(R.id.chat_message)).setBackgroundResource(R.drawable.comment_bubble_user_inverted);
                }

                ((ImageView) row.findViewById(R.id.chat_status)).setVisibility(View.INVISIBLE);
            }
        }
        return row;
    }


    @Override
    public int getCount() {
        return items.size();// more than zero
    }

    private void showDialog(Comment comment) {
        selectedComment = comment;

        if (dialog == null)
            dialog = new CustomDialog(context, null, context.getResources().getString(R.string.btn_retry), context.getResources().getString(R.string.btn_delete), false, message, title, this);

        dialog.show();
    }

    /**
     * \] nji
     * private Bitmap getAvatar(Coach coach) {
     * <p>
     * Bitmap avatarBMP = null;
     * <p>
     * if (coach != null) {
     * //comment out first - issue#6
     * avatarBMP = ImageManager.getInstance().findImage(coach.coach_id);
     * <p>
     * if (avatarBMP == null) {
     * //download the image first
     * MainListener.download(coach.coach_id, coach.avatar_url, "0");
     * }
     * <p>
     * if (avatarBMP == null)
     * avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);
     * <p>
     * return avatarBMP;
     * <p>
     * } else { //user comment use his image instead
     * if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() != null)
     * avatarBMP = ApplicationEx.getInstance().userProfile.getUserProfilePhoto();
     * else if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null)
     * avatarBMP = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());
     * <p>
     * if (avatarBMP == null)
     * avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);
     * }
     * return avatarBMP;
     * <p>
     * }
     **/

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.chat_status) {

            if (v.getTag() != null && v.getTag() instanceof Comment) {

                Comment comment = (Comment) v.getTag();
                int position = (Integer) v.getTag(R.id.commentid);

                //triger a ishapicomment click is comment is already sync, comment is from coach and comment is not yet ishapi

                if (comment.status == STATUS.SYNC_COMMENT && comment.comment_type == 2 && !comment.isHAPI) {
                    //isHAPI

                    //change the icon
                    comment.isHAPI = true;
                    // update the list
                    items.set(position, comment);
                    // update the UI
                    notifyDataSetChanged();
                    if (listener != null)
                        listener.onClick(v);

                } else if (comment.status == STATUS.FAILED_COMMENTUPLOAD) { //delete confirmation dialog
                    showDialog(comment);
                }
            }

        } else if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (v.getId() == R.id.YesButton) {
                //yes is click in this case retry
                commentlistener.uploadMealCommentRefresh(selectedComment);

            } else if (v.getId() == R.id.NoButton) {
                //delete is clicked
                commentlistener.uploadMealCommentDelete(selectedComment);
            }
        } else if (v.getId() == R.id.soundButton) {
            Message selectedMsg = (Message) v.getTag();

            selectedAudioMessage = selectedMsg;

            muteMediaPlayer();
            notifyDataSetChanged();
        } else if (v.getId() == R.id.playButton) {
            Message selectedMsg = (Message) v.getTag();

            selectedAudioMessage = selectedMsg;
            currentMediaURL = selectedMsg.mediaUrl;

            playMedia(currentMediaURL);

            notifyDataSetChanged();
        }
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set time remaining
            double timeRemaining = finalTime - timeElapsed;

            //repeat yourself that again in 100 miliseconds
            String elapsedString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed));
            String totalString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()), TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()));
            durationText = elapsedString + "/" + totalString;

            notifyDataSetChanged();

            durationHandler.postDelayed(this, 100);
        }
    };

    private void playMedia(String mediaUrl) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            isMediaPlaybackCompleted = false;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {

//                    Toast.makeText(context, "Media Completed", Toast.LENGTH_SHORT).show();
                    mediaPlayer.release();
                    mediaPlayer = null;

                    isMediaPlaying = false;
                    durationHandler.removeCallbacks(updateSeekBarTime);

                    timeElapsed = finalTime;

                    isMediaPlaybackCompleted = true;

                    notifyDataSetChanged();
                }
            });
        }


        try {
            mediaPlayer.setDataSource(mediaUrl);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (IOException e) {
            System.out.println("IOException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        }

        if (isMediaPlaying) {
            isMediaPlaying = false;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                durationHandler.removeCallbacks(updateSeekBarTime);

            }
        } else {
            isMediaPlaying = true;

            //play the audio
            if (mediaPlayer != null) {
                mediaPlayer.start();
                durationText = "00:00";
                finalTime = mediaPlayer.getDuration();
                timeElapsed = mediaPlayer.getCurrentPosition();

                durationHandler.postDelayed(updateSeekBarTime, 100);
            }
            System.out.println("play music");
        }
    }

    private void muteMediaPlayer() {
        try {
            if (isSoundOn) {
                isSoundOn = false;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0, 0);
                }
            } else {
                isSoundOn = true;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(1, 1);
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        }
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
}
