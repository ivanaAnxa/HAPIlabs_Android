package com.anxa.hapilabs.models;

import java.util.Date;
import java.util.List;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public class Steps {
    public String activity_id;
    public String device_name;
    public String graph_value_type;
    public String steps_count;
    public double steps_distance;
    public double steps_calories;
    public double steps_duration;
    public Date start_datetime;
    public Date end_datetime;
    public Boolean isDeleted;
    public Boolean isChecked;


    public List<Comment> comments;
    public List<HAPI4U> hapi4Us;
}
