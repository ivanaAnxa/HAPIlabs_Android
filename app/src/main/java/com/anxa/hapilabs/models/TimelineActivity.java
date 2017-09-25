package com.anxa.hapilabs.models;

import java.util.List;

/**
 * Created by aprilanxa on 08/03/2017.
 */

public class TimelineActivity {

    public String activity_id;
    public String user_id;
    public String activitytypeid;
    public String activity_date;
    public String rating;
    public boolean isdeleted;

    public Meal meal;
    public HapiMoment hapiMoment;
    public Water water;
    public Workout workout;
    public Steps steps;
    public Weight weight;

    public List<Comment> comments;
    public List<HAPI4U> hapi4Us;

    public CommentUser commentUser;
}
