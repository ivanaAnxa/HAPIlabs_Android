package com.anxa.hapilabs.controllers.login;


import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonLoginResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.storage.CommentDAO;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.PhotoDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;

public class LoginImplementer {

    JsonLoginResponseHandler jsonResponseHandler;

    // Handler responseHandler;
    protected ProgressChangeListener progresslistener;
    String password;

    LoginListener loginListener;

    Context context;

    public LoginImplementer(Context context, String username, String password, ProgressChangeListener progresslistener, LoginListener loginListener, Boolean fbConnect) {
        this.context = context;
        this.loginListener = loginListener;
        this.progresslistener = progresslistener;

        String data = JsonRequestWriter.getInstance().createLoginRequest(username, password, fbConnect);

        jsonResponseHandler = new JsonLoginResponseHandler(loginHandler);
        loginServices(username, password, data, jsonResponseHandler);
        this.password = password;
    }

    // where data = xml string format post data
    public void loginServices(String username, String password, String data, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.LOGIN);
        Connection connection = new Connection(responseHandler);
        connection.addParam("username", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.LOGIN) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    // STEP 1: stop progress here

                    if (jsonResponseHandler.getResponseObj() != null
                            && jsonResponseHandler.getResponseObj() instanceof UserProfile) {
                        // STEP 2: save user profile @ runtime for now

                        // this will add a progress bar on the initial mymeallist
                        // laoding
                        ApplicationEx.getInstance().isLoginSync = true;

                        // set login flag to true for auto login
                        ApplicationEx.getInstance().setIsLogin(context,
                                ApplicationEx.getInstance().isLoginSync);

                        UserProfile userProfile = (UserProfile) jsonResponseHandler.getResponseObj();

                        userProfile.setPasswordPlain(password);


                        // check if there is an existing user profile DB
                        UserProfileDAO userDao = new UserProfileDAO(context, null);

                        UserProfile Dbprofile = userDao.getUserProfile();


                        if (Dbprofile != null && Dbprofile.getRegID() != null) {

                            if (Dbprofile.getRegID().equals(userProfile.getRegID())) { // user
                                // is
                                // the
                                // same
                                // as
                                // save
                                // ,
                                // update
                                // the
                                // profile


                                DaoImplementer implDao = new DaoImplementer(userDao, context);
                                ApplicationEx.getInstance().userProfile = userProfile;
                                implDao.deleteprofile(userProfile);
                                implDao.add(userProfile);

                            } else { // user profile from Db is not the same as the
                                // logged in user

                                // delete all existing meals
                                // sequence of clearing DB is important clear all
                                // comments first


                                //reset sync dates too


                                ApplicationEx.getInstance().setSyncDate(context);
                                ApplicationEx.getInstance().toDateSyncCurrent = ApplicationEx.getInstance().getToDate(context);
                                ApplicationEx.getInstance().fromDateSyncCurrent = ApplicationEx.getInstance().fromDate(context);


                                CommentDAO commentDAO = new CommentDAO(context,
                                        null);
                                commentDAO.clearTable();
                                commentDAO.createTable();

                                MessageDAO messageDao = new MessageDAO(context, null);
                                messageDao.clearTable();
                                messageDao.createTable();
                                //

                                PhotoDAO photoDAO = new PhotoDAO(context, null);
                                photoDAO.clearTable();
                                photoDAO.createTable();

                                CoachDAO coachDAO = new CoachDAO(context, null);
                                coachDAO.clearTable();
                                coachDAO.createTable();

                                MealDAO mealDAO = new MealDAO(context, null);
                                mealDAO.clearTable();
                                mealDAO.createTable();
                                //

                                // delete existing DB's and update the user profile
                                UserProfileDAO userProfileDAO = new UserProfileDAO(context, null);
                                userProfileDAO.clearTable();
                                userProfileDAO.createTable();


                                NotificationDAO notificationDAO = new NotificationDAO(context, null);
                                notificationDAO.clearTable();
                                notificationDAO.createTable();

                                WorkoutDAO workoutDAO = new WorkoutDAO(context, null);
                                workoutDAO.clearTable();
                                workoutDAO.createTable();

                                DaoImplementer implDao = new DaoImplementer(userProfileDAO, context);
                                ApplicationEx.getInstance().userProfile = userProfile;
                                implDao.add(userProfile);
                            }
                        } else { // no profile save update the DB

                            //reset sync dates too
                            ApplicationEx.getInstance().setSyncDate(context);
                            ApplicationEx.getInstance().toDateSyncCurrent = ApplicationEx.getInstance().getToDate(context);
                            ApplicationEx.getInstance().fromDateSyncCurrent = ApplicationEx.getInstance().fromDate(context);

                            UserProfileDAO userProfileDAO = new UserProfileDAO(context, null);
                            userProfileDAO.clearTable();
                            userProfileDAO.createTable();

                            ApplicationEx.getInstance().userProfile = userProfile;
                            // save profile to db
                            Boolean done = userProfileDAO.insert(userProfile);
                        }
                        loginListener.loginSuccess(jsonResponseHandler
                                .getResultCode()
                                + " "
                                + jsonResponseHandler.getResultMessage());

                    } else if (jsonResponseHandler.getResponseObj() != null
                            && jsonResponseHandler.getResponseObj() instanceof MessageObj) {
                        // STEP 3: login failed
                        MessageObj mesObj = (MessageObj) jsonResponseHandler
                                .getResponseObj();
                        mesObj.setType(MESSAGE_TYPE.FAILED);
                        loginListener.loginFailedWithError(mesObj);
                    }

                    // STEP 3:

                    break;
                case JsonDefaultResponseHandler.ERROR:

                    // stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);
                    loginListener.loginFailedWithError(mesObj);
                    // dismiss progress here
                    break;
            }// end Switch
        }
    };

}
