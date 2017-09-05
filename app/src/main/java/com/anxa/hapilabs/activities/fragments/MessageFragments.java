package com.anxa.hapilabs.activities.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hapilabs.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.anxa.hapilabs.activities.MainActivity;
import com.anxa.hapilabs.activities.UpgradeActivity;
import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.messages.MessageController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.CustomDialog;


@SuppressLint("NewApi")
public class MessageFragments extends Fragment implements OnClickListener,
        MealAddCommentListener, GetMessagesListener {

    Context context;

    CommentListLayout commentList;
    ProgressBar progressBar;
    TextView submit_tv;
    EditText comment_et;

    Button loadMore_btn;
    RelativeLayout loadMore_layout;

    MainActivityCallBack mainListener;
    MessageController controller;

    List<Object> items;
    MessageDAO dao;
    ImageView header_left;
    View rootView;

    String DEFAULT_LIMIT = "10";
    String previousDate;
    String beforeDate;
    String afterDate;

    String textToPost;
    Message messageToPost;

    CustomDialog dialog;
    boolean loadPrevious = false;

    public MessageFragments() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
            MainActivity.stopWork();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        loadPrevious = false;
        //clear add message text view
        comment_et.setText("");
        //getlatest
        getLatestMessages();

        ((MainActivity) getActivity()).updateHeader(2, this);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mainListener = (MainActivityCallBack) getActivity();
        this.context = getActivity();

        items = new ArrayList<Object>(ApplicationEx.getInstance().messageList.values());

        IntentFilter filter = new IntentFilter();
//        filter.addAction(context.getResources().getString(R.string.meallist_getavatar));
        filter.addAction("END_OF_LINE");
        filter.addAction("SCROLL_STARTED");
        filter.addAction("TOP_OF_LINE");
        context.getApplicationContext().registerReceiver(the_receiver, filter);

        dao = new MessageDAO(context, null);

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_messages, container, false);
            submit_tv = (TextView) rootView.findViewById(R.id.btnSubmit);
            submit_tv.setOnClickListener(this);
            comment_et = (EditText) rootView.findViewById(R.id.comment_et);
            comment_et.setHint(R.string.message_hint);
            commentList = (CommentListLayout) rootView.findViewById(R.id.commentlist);
            loadMore_btn = (Button) rootView.findViewById(R.id.loadMoreButton);
            loadMore_btn.setOnClickListener(this);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            progressBar.setIndeterminate(true);
            loadMore_layout = (RelativeLayout) rootView.findViewById(R.id.loadMoreLayout);

            if (items != null && items.size() > 1) {
                sort(items);
            }

            commentList.initData(items, context, null, null, mainListener);
//            header_left = (ImageView) container.getRootView().findViewById(R.id.header_left);
//            header_left.setOnClickListener(this);
        }

        return rootView;
    }

    private void getPreviousMessages() {

        if (controller == null) {
            controller = new MessageController(context, this);
        }
        startProgress();
        controller.getPrevious(previousDate, DEFAULT_LIMIT);
    }

    /**
     * for future use
     **/
    private void getNextMessages() {
        startProgress();
        if (controller == null) {
            controller = new MessageController(context, this);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
        controller.getLatest(sharedPreferences.getString("cursor_next", beforeDate), DEFAULT_LIMIT);
    }

    /**
     * for future use, if push notifications is already implemented
     **/
    private void getLatestMessages() {
//        startProgress();
        stopProgress();

        if (controller == null) {
            controller = new MessageController(context, this);
        }

        try {
            if (ApplicationEx.getInstance().userProfile.getRegID() != null && ApplicationEx.getInstance().userProfile.getRegID().length() > 0) {
                afterDate = String.valueOf(AppUtil.dateToUnixTimestamp(AppUtil.getCurrentDateinDate()));
                controller.getPrevious(afterDate, DEFAULT_LIMIT);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    private void createNewComment(String comment_message) {
        startProgress();

        textToPost = comment_message;

        messageToPost = new Message();
        messageToPost.is_coach_message = false;// user comment
        messageToPost.message_body = comment_et.getText().toString();
        messageToPost.timestamp = AppUtil.getCurrentDateinDate();
        messageToPost.status = com.anxa.hapilabs.models.Message.STATUS.ONGOING_COMMENTUPLOAD;

        items.add(messageToPost); //add message on the adapter data

//        ApplicationEx.getInstance().messageList.put(messageToPost.message_id, messageToPost);

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    comment_et.setText("");

                    if (items != null && items.size() > 1) ;
                    sort(items);
                    commentList.updateDataFromRefresh(items); //update list
                }
            });

        } catch (Exception ex) {
        }


        if (controller == null) {
            controller = new MessageController(context, this);
        }

        controller.postMessages(
                ApplicationEx.getInstance().userProfile.getRegID(),
                comment_message);

    }

    @Override
    public void onClick(View v) {
        if (v == submit_tv) {

            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);

            // check if the text view has something
            if (comment_et != null && comment_et.getText() != null) {
                if (comment_et.getText().toString().trim().length() > 0) {
                    String comment_message = comment_et.getText().toString();
                    createNewComment(comment_message);
                }
            }
        } else if (v == loadMore_btn) {
            loadPrevious = true;
            getPreviousMessages();
        } else if (v.getId() == R.id.CloseButton) {
            if (dialog != null)
                dialog.dismiss();
        } else if (v.getId() == R.id.header_right) {
            Intent intent = new Intent(getActivity(), UpgradeActivity.class);
            this.getActivity().startActivity(intent);
        }
    }

    @Override
    public void uploadMealCommentSuccess(String response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uploadMealCommentFailedWithError(MessageObj response,
                                                 String entryID) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uploadMealCommentRefresh(Comment comment) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uploadMealCommentDelete(Comment comment) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getMessagesSuccess(String response) {

        // TODO Auto-generated method stub
        //update message list
        items = new ArrayList<Object>(ApplicationEx.getInstance().messageList.values());

        if (items != null && items.size() > 1) ;
        sort(items);

        if (loadPrevious) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopProgress();
                    hideLoadMoreButton();
                    commentList.updateData(items);
                }
            });

        } else {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    stopProgress();
                    hideLoadMoreButton();

                    commentList.updateDataFromRefresh(items);
                }
            });
        }

    }

    public void postMessagesSuccess(String response) {

        stopProgress();

        if (items != null) {
            int index = items.indexOf(messageToPost);

            messageToPost.status = Message.STATUS.SYNC_COMMENT;
            messageToPost.message_id = response;

            items.set(index, messageToPost);

            dao.insert(messageToPost);
        }
    }

    public void postMessagesError(MessageObj response) {

        stopProgress();

        if (items != null) {
            int index = items.indexOf(messageToPost);

            messageToPost.status = Message.STATUS.FAILED_COMMENTUPLOAD;
            messageToPost.message_id = "";

            items.remove(index);
        }

        final String messageDialog = getResources().getString(R.string.ALERTMESSAGE_OFFLINE);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                comment_et.setText(textToPost);
                showCustomDialog(messageDialog);
            }
        });
    }

    public void sort(final List<Object> items) {
        if (items != null && items.size() > 0) {

            Collections.sort(items, new Comparator<Object>() {
                public int compare(Object notif1, Object notif2) {
                    if (notif1 instanceof Comment) {
                        return ((Comment) notif1).timestamp.compareTo(((Comment) notif2).timestamp);
                    } else {
                        return ((Message) notif1).timestamp.compareTo(((Message) notif2).timestamp);
                    }
                }
            });
        }
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() == context.getResources().getString(R.string.meallist_getavatar)) {// update timeline only if this
//                // is the content on Show
//                if (commentList != null && items != null && items.size() > 0)
//                    commentList.updateData(items); //no need to sort if its an avatar update
//            } else if (intent.getAction() == "END_OF_LINE") {
////                getLatestMessages();
//
//            } else

            if (intent.getAction() == "TOP_OF_LINE") {
                SharedPreferences sharedPreferences = context.getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
                previousDate = sharedPreferences.getString("cursor_previous", beforeDate);
                if (previousDate == null || previousDate.length() < 1 || previousDate.equalsIgnoreCase("")) {
                    hideLoadMoreButton();
                } else {
                    showLoadMoreButton();
                }

            } else if (intent.getAction() == "SCROLL_STARTED") {
//                System.out.println("SCROLL_STARTED");
            }
        }
    };

    @Override
    public void getMessagesFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        //ignore failed message fetching, we will fetch again next time user go to this tab
        stopProgress();

        final String messageDialog = getResources().getString(R.string.ALERTMESSAGE_OFFLINE);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showCustomDialog(messageDialog);
            }
        });
    }

    private void startProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void hideLoadMoreButton() {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            loadMore_layout.setVisibility(View.GONE);
            loadMore_btn.setVisibility(View.GONE);
        }
    }

    private void showLoadMoreButton() {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            loadMore_layout.setVisibility(View.VISIBLE);
            loadMore_btn.setVisibility(View.VISIBLE);
            loadMore_btn.setText(getResources().getString(R.string.LOAD_MORE_BTN));
        }
    }

    private void showCustomDialog(String message) {

        dialog = new CustomDialog(context, null, null, null, true, message, null, this);
        dialog.show();
    }
}