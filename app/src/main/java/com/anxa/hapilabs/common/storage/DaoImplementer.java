package com.anxa.hapilabs.common.storage;

import java.util.List;

import android.content.Context;

import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Workout;
//import com.google.android.gms.internal.cu;
//import com.google.android.gms.internal.ph;
//import com.google.android.gms.internal.pr;


public class DaoImplementer {

    //ProfileDAO profileDAO = new ProfileDAO(context, null);
    DAO dao;
    Context context;

    public DaoImplementer(DAO dao, Context context) {
        this.dao = dao;
        this.context = context;
    }

    public boolean addMeal(Meal meal) {

        //update comment
        if (meal.comments != null && meal.comments.size() > 0) {
            List<Comment> comments = meal.comments;

            for (Comment comment : comments) {
                CommentDAO commentdao = new CommentDAO(context, null);
                Boolean stat = commentdao.insert(comment);
                if (stat)
                    System.out.println("MEAL WITH COMMENTS @ addMeal+ " + meal.meal_id + " " + "SUCCESS");

            }
            comments = null;
            System.gc();
        }

        //update photo
        if (meal.photos != null && meal.photos.size() > 0) {
            PhotoDAO photodao = new PhotoDAO(context, null);
            List<Photo> photos = meal.photos;
            for (Photo photo : photos) {
                photodao.insert(photo, meal.meal_id);
            }
            photos = null;
            System.gc();
        }

        //update meal
        ((MealDAO) dao).insertMeal(meal);
        return true;
    }

    public boolean deleteMeal(Meal meal) {
        //delete photo and comment for that meal too

        //delete comment
        if (meal.comments != null && meal.comments.size() > 0) {
            CommentDAO commentdao = new CommentDAO(context, null);
            List<Comment> comments = meal.comments;
            for (Comment comment : comments) {
                commentdao.delete(comment.comment_id);
            }
        }

        //delete photo
        if (meal.photos != null && meal.photos.size() > 0) {
            PhotoDAO photodao = new PhotoDAO(context, null);
            List<Photo> photos = meal.photos;
            for (Photo photo : photos) {
                photodao.delete(photo.photo_id);
            }
        }

        //delete notification associated with the meal
//		NotificationDAO notifDao = new NotificationDAO(context, null);
//		List<Photo> photos = meal.photos;
//		notifDao.getAllEntriesNotificationWithMealID(meal.meal_id)
//	

        //delete meal
        ((MealDAO) dao).deleteMeal(meal.meal_id);

        System.gc();
        return true;
    }

    public boolean updatedMeal(Meal meal) {
        //update photo and comment for that meal too

        //update comment
        if (meal.comments != null && meal.comments.size() > 0) {

            List<Comment> comments = meal.comments;
            for (Comment comment : comments) {
                CommentDAO commentdao = new CommentDAO(context, null);
                commentdao.update(comment);
            }
            comments = null;

        }

        //update photo
        if (meal.photos != null && meal.photos.size() > 0) {
            PhotoDAO photodao = new PhotoDAO(context, null);
            List<Photo> photos = meal.photos;
            for (Photo photo : photos) {
                photodao.update(photo, meal.meal_id);
            }

        }
        ((MealDAO) dao).updateMeal(meal);


        return true;
    }

    public boolean updateMealWithRating(Meal meal, String command, String dbCommand) {
        if (dbCommand == "add") {
            if (command == "get_meals_all") {
                meal.isAllMealWithRating = true;
                meal.isHealthyMealWithRating = false;
                meal.isJustOkMealWithRating = false;
                meal.isUnhealthyMealWithRating = false;
            } else if (command == "get_meals_healthy") {
                meal.isAllMealWithRating = false;
                meal.isHealthyMealWithRating = true;
                meal.isJustOkMealWithRating = false;
                meal.isUnhealthyMealWithRating = false;
            } else if (command == "get_meals_ok") {
                meal.isAllMealWithRating = false;
                meal.isHealthyMealWithRating = false;
                meal.isJustOkMealWithRating = true;
                meal.isUnhealthyMealWithRating = false;
            } else if (command == "get_meals_unhealthy") {
                meal.isAllMealWithRating = false;
                meal.isHealthyMealWithRating = false;
                meal.isJustOkMealWithRating = false;
                meal.isUnhealthyMealWithRating = true;
            }

            addMeal(meal);
        } else if (dbCommand == "update") {
            if (command == "get_meals_all") {
                meal.isAllMealWithRating = true;

                MealDAO dao = new MealDAO(context, null);
                Meal tempMeal = dao.getMealbyID(meal.meal_id);

                if (tempMeal.isHealthyMealWithRating) {
                    meal.isHealthyMealWithRating = true;
                } else {
                    meal.isHealthyMealWithRating = false;
                }

                if (tempMeal.isJustOkMealWithRating) {
                    meal.isJustOkMealWithRating = true;
                } else {
                    meal.isJustOkMealWithRating = false;
                }

                if (tempMeal.isUnhealthyMealWithRating) {
                    meal.isUnhealthyMealWithRating = true;
                } else {
                    meal.isUnhealthyMealWithRating = false;
                }
            } else if (command == "get_meals_healthy") {
                meal.isHealthyMealWithRating = true;

                MealDAO dao = new MealDAO(context, null);
                Meal tempMeal = dao.getMealbyID(meal.meal_id);

                if (tempMeal.isAllMealWithRating) {
                    meal.isAllMealWithRating = true;
                } else {
                    meal.isAllMealWithRating = false;
                }

                if (tempMeal.isJustOkMealWithRating) {
                    meal.isJustOkMealWithRating = true;
                } else {
                    meal.isJustOkMealWithRating = false;
                }

                if (tempMeal.isUnhealthyMealWithRating) {
                    meal.isUnhealthyMealWithRating = true;
                } else {
                    meal.isUnhealthyMealWithRating = false;
                }
            } else if (command == "get_meals_ok") {
                meal.isJustOkMealWithRating = true;

                MealDAO dao = new MealDAO(context, null);
                Meal tempMeal = dao.getMealbyID(meal.meal_id);

                if (tempMeal.isAllMealWithRating) {
                    meal.isAllMealWithRating = true;
                } else {
                    meal.isAllMealWithRating = false;
                }

                if (tempMeal.isHealthyMealWithRating) {
                    meal.isHealthyMealWithRating = true;
                } else {
                    meal.isHealthyMealWithRating = false;
                }

                if (tempMeal.isUnhealthyMealWithRating) {
                    meal.isUnhealthyMealWithRating = true;
                } else {
                    meal.isUnhealthyMealWithRating = false;
                }
            } else if (command == "get_meals_unhealthy") {
                meal.isUnhealthyMealWithRating = true;

                MealDAO dao = new MealDAO(context, null);
                Meal tempMeal = dao.getMealbyID(meal.meal_id);

                if (tempMeal.isAllMealWithRating) {
                    meal.isAllMealWithRating = true;
                } else {
                    meal.isAllMealWithRating = false;
                }

                if (tempMeal.isHealthyMealWithRating) {
                    meal.isHealthyMealWithRating = true;
                } else {
                    meal.isHealthyMealWithRating = false;
                }

                if (tempMeal.isJustOkMealWithRating) {
                    meal.isJustOkMealWithRating = true;
                } else {
                    meal.isJustOkMealWithRating = false;
                }
            }

            ((MealDAO) dao).updateMeal(meal);
        }

        return true;
    }

    public boolean add(Coach coach) {
        ((CoachDAO) dao).insert(coach);
        return true;
    }

    public boolean deletecoach(String coachid) {
        ((CoachDAO) dao).delete(coachid);
        return true;
    }

    public boolean update(Coach coach) {
        ((CoachDAO) dao).update(coach);
        return true;
    }

    public boolean add(Comment comment) {
        ((CommentDAO) dao).insert(comment);
        return true;
    }

    public boolean deletecomment(String commentid) {
        ((CommentDAO) dao).delete(commentid);
        return true;
    }

    public boolean update(Comment comment) {
        ((CommentDAO) dao).insert(comment);
        return true;
    }

    public boolean add(UserProfile profile) {
        //add coach to DB too


        if (profile.getCoach() != null) {

            CoachDAO coachDao = new CoachDAO(context, null);
            Boolean inserysuccess = coachDao.insert(profile.getCoach());


        }


        boolean status = ((UserProfileDAO) dao).insert(profile);

        return status;
    }


    public boolean deleteprofile(UserProfile profile) {
        //delete coach too
        if (profile.getCoach() != null) {
            CoachDAO coachDao = new CoachDAO(context, null);
            coachDao.delete(profile.getCoach().coach_id);
        }

        ((UserProfileDAO) dao).delete(profile.getRegID());
        return true;
    }

    public boolean update(UserProfile profile) {
        //update coach too;
        if (profile.getCoach() != null) {
            CoachDAO coachDao = new CoachDAO(context, null);
            coachDao.update(profile.getCoach());
        }
        ((UserProfileDAO) dao).update(profile);

        return true;
    }


    public boolean add(Photo photo, String mealID) {
        ((PhotoDAO) dao).insert(photo, mealID);
        return true;
    }

    public boolean deletephoto(String photoid) {
        ((PhotoDAO) dao).delete(photoid);
        return true;
    }

    public boolean update(Photo photo, String mealID) {
        ((PhotoDAO) dao).update(photo, mealID);
        return true;
    }


    public boolean add(Notification notification) {
        ((NotificationDAO) dao).insert(notification);
        return true;
    }

    public boolean deleteNotif(int notifId) {
        ((NotificationDAO) dao).delete(notifId);
        return true;
    }

    public boolean update(Notification notification) {
        ((NotificationDAO) dao).update(notification);
        return true;
    }


    public boolean add(Message message) {
        ((MessageDAO) dao).insert(message);
        return true;
    }

    public boolean deleteMessage(String id) {
        ((MessageDAO) dao).delete(id);
        return true;
    }

    public boolean update(Message message) {
        ((MessageDAO) dao).update(message);
        return true;
    }

    public boolean add(Workout workoutObj) {
        ((WorkoutDAO) dao).insert(workoutObj);
        return true;
    }

    public boolean update(Workout workoutObj) {
        ((WorkoutDAO) dao).update(workoutObj);
        return true;
    }

    public boolean delete(String activity_id) {
        ((WorkoutDAO) dao).delete(activity_id);
        return true;
    }

}